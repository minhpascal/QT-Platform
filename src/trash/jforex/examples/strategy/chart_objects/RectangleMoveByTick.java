package trash.jforex.examples.strategy.chart_objects;

import com.dukascopy.api.*;
import com.dukascopy.api.drawings.*;

/**
 * The strategy on its start creates a rectangle and on every tick
 * moves it to the last tick's coordinates - time and price.
 *
 */
@RequiresFullAccess
public class RectangleMoveByTick implements IStrategy {

    private IChart chart;
    private IHistory history;
    private IRectangleChartObject rectangle;
    private Instrument instrument = Instrument.EURUSD;
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.chart = context.getChart(instrument);
        this.history = context.getHistory();
        ITick tick = history.getLastTick(instrument);    
        
        long barWidth = chart.getSelectedPeriod().getInterval();
        double tenPips = instrument.getPipValue() * 10;
        rectangle = chart.getChartObjectFactory().createRectangle("rectangle", 
            tick.getTime(), tick.getBid(), 
            tick.getTime() - barWidth * 10, tick.getBid() - tenPips
        );
        chart.addToMainChart(rectangle);
        
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {  
    	if(instrument != this.instrument){
    		return;
    	}
    	rectangle.move(tick.getTime(), tick.getBid());
    }

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
