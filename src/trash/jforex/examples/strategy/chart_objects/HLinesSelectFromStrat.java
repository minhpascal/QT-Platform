package trash.jforex.examples.strategy.chart_objects;

import java.util.Random;
import com.dukascopy.api.*;

/**
 * The strategy on its start creates 10 horizontal lines by 2 pip price interval.
 * On every tick the strategy selects and navigates to a random chart object.
 *
 */
public class HLinesSelectFromStrat implements IStrategy {

    private IChart chart;
    private IHistory history;
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("HLine count")
    public int hlineCount = 10;
    @Configurable("HLine step (in pips)")
    public int stepInPips = 2;   
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.chart = context.getChart(instrument);
        this.history = context.getHistory();
        
        double basePrice = history.getLastTick(instrument).getBid(); //base price - the last bid price
        
        //draw HLines
        for(int i=0; i< hlineCount; i++){
            double price = basePrice + instrument.getPipValue() * stepInPips * i;
            chart.addToMainChart(chart.getChartObjectFactory().createHorizontalLine("hLine" + i, price));
        }     
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument){
            return;
        }
        if(chart != null && chart.getAll().size() > 0){ 
	        int objectCount = chart.getAll().size();
	        IChartObject randomObject = chart.getAll().get((new Random()).nextInt(objectCount));
	        chart.navigateAndSelectDrawing(randomObject);        
        }
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
