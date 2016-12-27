package trash.jforex.examples.strategy.strategies;

import java.awt.Color;
import java.awt.Font;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.drawings.IChartDependentChartObject;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.drawings.IOhlcChartObject;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IFeedListener;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * The strategy trades according to SMA cross direction.
 * On fast SMA going below slow SMA it buys, on the opposite cross - sells.
 * 
 * The strategy itself opens a chart (if no such chart has been opened yet)
 * and adds the indicator values to the OHLC index.
 */
public class SmaCrossStrategyVisual implements IStrategy, IFeedListener {

    @Configurable("Feed")
    public IFeedDescriptor feedDescriptor =
            new TimePeriodAggregationFeedDescriptor(
                    Instrument.EURUSD, 
                    Period.TEN_SECS, 
                    OfferSide.ASK, 
                    Filter.NO_FILTER
            );
    @Configurable("Amount")
    public double amount = 0.001;
    @Configurable("Stop loss")
    public int slPips = 10;
    @Configurable("Take profit")
    public int tpPips = 10;
    @Configurable(value="", description="close the existing order on creation of a new order if it has not been closed yet")
    public boolean closePreviousOrder = true;
    @Configurable("")
    public int smaTimePeriodFast = 2;
    @Configurable("")
    public int smaTimePeriodSlow = 5;
    @Configurable("")
    public Color fastColor = Color.GREEN;
    @Configurable("")
    public Color slowColor = Color.RED;
    
    private static int LAST = 1;
    private static int PREV = 0;

    private IEngine engine;
    private IHistory history;
    private IConsole console;
    private IContext context;
    private IOrder order;
    private IIndicators indicators;
    private IChart chart;
  
    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.history = context.getHistory();
        this.console = context.getConsole();
        this.indicators = context.getIndicators();
        this.context = context;
        // subscribe the instrument that we are going to work with
        context.setSubscribedInstruments(java.util.Collections.singleton(feedDescriptor.getInstrument()), true);
        if(feedDescriptor.getDataType() == DataType.TICKS){
            console.getWarn().println("The strategy can't trade according to the tick feed!");
            context.stop();
        }
        context.subscribeToFeed(feedDescriptor, this);
        setupChart();        
        
        IBar prevFeedData = (IBar) history.getFeedData(feedDescriptor, 1);
        submitOrder(prevFeedData.getClose() > prevFeedData.getOpen() );
    }
    
    private void setupChart(){
        for (IChart c : context.getCharts()){
            if(c.getFeedDescriptor().equals(feedDescriptor)){
                chart = c;
                break;
            }
        }
        //no such chart opened yet - we open it now
        if(chart == null){
            chart = context.openChart(feedDescriptor);
        }
        chart.add(indicators.getIndicator("SMA"), new Object[] { smaTimePeriodFast }, new Color[] { fastColor }, new DrawingStyle[] { DrawingStyle.LINE }, new int[] { 2 });
        chart.add(indicators.getIndicator("SMA"), new Object[] { smaTimePeriodSlow }, new Color[] { slowColor }, new DrawingStyle[] { DrawingStyle.LINE }, new int[] { 2 });
        
        IOhlcChartObject ohlc = null;
        for (IChartObject obj : chart.getAll()) {
            if (obj instanceof IOhlcChartObject) {
                ohlc = (IOhlcChartObject) obj;
            }
        }
        if (ohlc == null) {
            ohlc = chart.getChartObjectFactory().createOhlcInformer();
            chart.add(ohlc);
        }
        ohlc.setShowIndicatorInfo(true);
    }
    
    private void addSignal(boolean isLong, IBar previousBar){
        IChartObjectFactory factory = chart.getChartObjectFactory();
        Instrument instrument = feedDescriptor.getInstrument();
        IChartDependentChartObject signal = isLong ? 
                factory.createSignalUp("signalUp_" + System.currentTimeMillis(), previousBar.getTime(), previousBar.getLow() - instrument.getPipValue())
                : factory.createSignalDown("signalDownKey_" + System.currentTimeMillis(), previousBar.getTime(), previousBar.getHigh() + instrument.getPipValue());
        signal.setText("SMA cross", new Font("Monospaced", Font.BOLD, 12));
        signal.setColor(isLong ? fastColor : slowColor);
        signal.setStickToCandleTimeEnabled(false);
        chart.add(signal);  
    }
    
    @Override
    public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {

        try {
			long time = feedData.getTime();
			double[] smaFast = indicators.sma(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(), smaTimePeriodFast).calculate(3, time, 0);
			double[] smaSlow = indicators.sma(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(), smaTimePeriodSlow).calculate(3, time, 0);
            if (smaFast[LAST] < smaFast[PREV] 
                && smaFast[LAST] < smaSlow[LAST] 
                && smaFast[PREV] >= smaSlow[PREV]
            ) { // smaFast falls below smaSlow
                submitOrder(!order.isLong());
                addSignal(!order.isLong(), (IBar) feedData);
            } else if (smaFast[LAST] > smaFast[PREV] 
                    && smaFast[LAST] > smaSlow[LAST] 
                    && smaFast[PREV] <= smaSlow[PREV]
            ) { // smaFast overtakes smaSlow
                submitOrder(order.isLong());
                addSignal(order.isLong(), (IBar) feedData);
            }
        } catch (Exception e) {
            console.getErr().println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(IMessage message) throws JFException {}

    private void submitOrder(boolean isLong) throws JFException {
        double slPrice, tpPrice;
        Instrument instrument = feedDescriptor.getInstrument();
        ITick lastTick = history.getLastTick(instrument);
        OrderCommand orderCmd = isLong ? OrderCommand.BUY : OrderCommand.SELL;
        // Calculating stop loss and take profit prices
        if (isLong) {
            slPrice = lastTick.getAsk() - slPips * instrument.getPipValue();
            tpPrice = lastTick.getAsk() + tpPips * instrument.getPipValue();
        } else {
            slPrice = lastTick.getBid() + slPips * instrument.getPipValue();
            tpPrice = lastTick.getBid() - tpPips * instrument.getPipValue();
        }
        if(closePreviousOrder && order != null && order.getState() == IOrder.State.FILLED){
            //we don't use order.waitForUpdate, since our next actions don't depend on the previous order anymore
            order.close();
        }
        order = engine.submitOrder("cross_"+orderCmd.toString() + System.currentTimeMillis(), instrument, orderCmd, amount, 0, 20, slPrice, tpPrice);
    }
    
    @Override
    public void onStop() throws JFException {
        if(order.getState() == IOrder.State.FILLED || order.getState() == IOrder.State.OPENED){
            order.close();
        }
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

}
