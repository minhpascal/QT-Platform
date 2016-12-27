package trash.jforex.examples.strategy.historical_data;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;

/**
 * The strategy writes on chart how many ticks are remaining in the current tick bar.
 *
 */
public class TickBarTicksRemaining implements IStrategy {

    @Configurable("instrument")
    public Instrument instrument = Instrument.EURUSD;    
    
    private IConsole console;
    private IChart chart;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {        
        console = context.getConsole();
        history = context.getHistory();
        chart = context.getChart(instrument);

    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {        
        if(instrument != this.instrument){
            return;
        }
         
        if(chart == null || chart.getTickBarSize() == null){
            print("Tick bar chart not opened!");
            return;
        }
        
        ITickBar currentTickBar = history.getTickBar(instrument, OfferSide.BID, chart.getTickBarSize(), 0);
        int remaining =  (int) (chart.getTickBarSize().getSize() - currentTickBar.getFormedElementsCount());
        print("ticks remaining: " + remaining + " currentTickBar: " + currentTickBar);
        chart.comment("Ticks remaining " + remaining);

    }
    
    private void print(Object o){
        console.getOut().println(o);
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException {}

}
