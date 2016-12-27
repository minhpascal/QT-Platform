package trash.jforex.examples.strategy.order;

import java.awt.Color;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;

/**
 * The strategy on its start creates an order.
 * Once the order profit reaches 5 pips, the order stop loss
 * gets moved to the open price.
 * 
 * The strategy draws the break even level line, such that
 * the user can change the break even level by moving the line on the chart.
 *
 */
public class BreakEvenHLine implements IStrategy {
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public OrderCommand orderCommand = OrderCommand.BUY;
    @Configurable("")
    double amount = 0.001;
    @Configurable("")
    public int slippage = 20;
    @Configurable("")
    public int stopLossPips = 10;
    @Configurable("")
    public int takeProfitPips = 40;
    @Configurable("")
    public int breakEvenPips = 5;
    
    private IConsole console;
    private IEngine engine;
    private IHistory history;
    private IOrder order;
    private IChart chart;
    
    IHorizontalLineChartObject breakEvenLine;

    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
        chart = context.getChart(instrument);
                
        ITick tick = history.getLastTick(instrument);
        double stopLossPrice, takeProfitPrice;
        if(orderCommand.isLong()){
            stopLossPrice = tick.getBid() - stopLossPips * instrument.getPipValue();
            takeProfitPrice = tick.getBid() + takeProfitPips * instrument.getPipValue();
        } else {
            stopLossPrice = tick.getAsk() + stopLossPips * instrument.getPipValue();
            takeProfitPrice = tick.getAsk() - takeProfitPips * instrument.getPipValue();
        }
        //for simplicity make order at the last bid price. Change this for use with conditional orders
        double openPrice = tick.getBid(); 
        order = engine.submitOrder("breakEvenOrder", instrument, orderCommand, amount, openPrice, slippage,  stopLossPrice, takeProfitPrice);
        order.waitForUpdate(2000, IOrder.State.OPENED);
        
        if(chart == null){
            console.getErr().println("Can't add break even line, because there is no chart opened for " + instrument);
            context.stop();
            return;
        }
        //add break even line
        double breakEvenPrice = orderCommand.isLong() 
            ? order.getOpenPrice() + breakEvenPips * instrument.getPipValue()
            : order.getOpenPrice() - breakEvenPips * instrument.getPipValue();
        
        breakEvenLine = chart.getChartObjectFactory().createPriceMarker("breakEvenLine", breakEvenPrice);
        breakEvenLine.setColor(Color.RED);
        breakEvenLine.setLineStyle(LineStyle.DASH);
        chart.addToMainChart(breakEvenLine);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument || order.getState() != IOrder.State.FILLED || breakEvenLine == null){
            return;
        }
        if((order.isLong() && tick.getBid() >= breakEvenLine.getPrice(0)) ||
                (!order.isLong() && tick.getAsk() <= breakEvenLine.getPrice(0))){
            order.setStopLossPrice(order.getOpenPrice());
            chart.remove(breakEvenLine);
            breakEvenLine = null;
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
