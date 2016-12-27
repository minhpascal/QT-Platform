package trash.jforex.examples.strategy.historical_data;

import java.awt.Color;
import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;
import com.dukascopy.api.drawings.IVerticalLineChartObject;
/**
 * The strategy on its start gets last 10 bars over the designated period
 * and gets the maximum High price and minimum Low price of those bars.
 * 
 * The strategy also draws two price markers that correspond to the prices.
 * It also draws two time markers to show the time interval
 *
 */
public class HistoricalHighLowWithLog2 implements IStrategy {

    @Configurable("Period")
    public Period period = Period.ONE_MIN;
    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;
    
    private IConsole console;
    private IHistory history;
    private IChart chart;
    
    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        
        long lastTickTime = history.getLastTick(instrument).getTime();
        long lastBarTime = history.getBarStart(period, lastTickTime);
        List<IBar> bars = history.getBars(instrument, period, OfferSide.BID, Filter.NO_FILTER, 10, lastBarTime, 0);

        double maxHigh = Double.MIN_VALUE;
        double minLow = Double.MAX_VALUE;
        for(IBar bar : bars){
            if(maxHigh < bar.getHigh()){
                maxHigh = bar.getHigh();
            }
            if(minLow > bar.getLow()){
                minLow = bar.getLow();
            }
            console.getOut().println(bar);
        }

        console.getOut().println(String.format("over last %s bars - high=%.5f, low=%.5f", bars.size(), maxHigh, minLow));
        
        chart = context.getChart(instrument);
        if(chart == null){
            console.getWarn().println("Chart is not open, will not plot lines");
            return;
        }
        
        IChartObjectFactory factory = chart.getChartObjectFactory();
        
        IHorizontalLineChartObject highLine = factory.createPriceMarker("highLine", maxHigh);
        IHorizontalLineChartObject lowLine = factory.createPriceMarker("minLow", minLow);
        IVerticalLineChartObject oldestLine = factory.createTimeMarker("oldestBarTime", bars.get(0).getTime());
        IVerticalLineChartObject latestLine = factory.createTimeMarker("latestBarTime", bars.get(bars.size() - 1).getTime());
        
        highLine.setColor(Color.GREEN);
        lowLine.setColor(Color.RED);
        
        oldestLine.setLineWidth(3);  
        oldestLine.setOpacity(0.5f);
        oldestLine.setLineStyle(LineStyle.DASH);
        latestLine.setLineWidth(3);
        latestLine.setOpacity(0.5f);
        latestLine.setLineStyle(LineStyle.DASH);
        
        chart.addToMainChart(highLine);
        chart.addToMainChart(lowLine);
        chart.addToMainChart(oldestLine);
        chart.addToMainChart(latestLine);
        
        
          
    }
    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
    @Override
    public void onMessage(IMessage message) throws JFException {}
    @Override
    public void onAccount(IAccount account) throws JFException {}
    @Override
    public void onStop() throws JFException {
        chart.removeAll();        
    }
}
