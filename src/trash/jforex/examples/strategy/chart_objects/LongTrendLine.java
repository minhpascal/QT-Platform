package trash.jforex.examples.strategy.chart_objects;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.ILongLineChartObject;

/**
 * The strategy draws a long line between points:
 * - (last bar, last tick price)
 * - (fourth to last bar, last tick price - 10 pips)
 */
public class LongTrendLine implements IStrategy {

    private IChart chart;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.chart = context.getChart(Instrument.EURUSD);
        this.history = context.getHistory();
        
        ITick tick = history.getLastTick(Instrument.EURUSD);
        long firstBarTime = history.getBarStart(chart.getSelectedPeriod(), tick.getTime());
        long scndBarTime = firstBarTime - chart.getSelectedPeriod().getInterval() * 5;
        double max = tick.getBid();
        double min = max - Instrument.EURUSD.getPipValue() * 10;
        
        ILongLineChartObject line = chart.getChartObjectFactory().createLongLine("shortLine", firstBarTime, max, scndBarTime, min);
        chart.addToMainChart(line);    
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
    public void onStop() throws JFException {}

}
