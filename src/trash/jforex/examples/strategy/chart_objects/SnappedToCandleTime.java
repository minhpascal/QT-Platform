package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.drawings.IVerticalLineChartObject;

/**
 * The strategy on its start creates two horizontal lines:
 * - one which get snapped on the chart to candle time,
 * - one which does not.
 *
 */
public class SnappedToCandleTime implements IStrategy {

    private IChart chart;
    private IHistory history;
    private IConsole console;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    
    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();
        chart = context.getChart(instrument);
        
        if(chart == null){
            console.getErr().println("No chart opened for " + instrument);
            context.stop(); //stop the strategy
        }
        
        IChartObjectFactory factory = chart.getChartObjectFactory();     
        IBar bar1 = history.getBar(instrument, chart.getSelectedPeriod(), chart.getSelectedOfferSide(), 1);
        
        IVerticalLineChartObject vLineUnsnapped = factory.createVerticalLine("vLineUnsnapped", bar1.getTime());
        vLineUnsnapped.setStickToCandleTimeEnabled(false);
        vLineUnsnapped.setColor(Color.BLUE);
        chart.addToMainChart(vLineUnsnapped);
        
        IVerticalLineChartObject vLineSnapped = factory.createVerticalLine("vLineSnapped", bar1.getTime());
        vLineSnapped.setColor(Color.MAGENTA);
        chart.addToMainChart(vLineSnapped);
        
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
