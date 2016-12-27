package trash.jforex.examples.strategy.historical_data;

import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.util.DateUtils;

/**
 * The following strategy retrieve history ticks by:
 * - shift
 * - time interval
 * 
 * The strategy logs the results such that user can check that 
 * the corresponding ones match by using different approaches.
 * 
 * For the sake of atomicity, each test on purpose uses 
 * as little as possible global variables.
 *
 */
public class HistoryTicksSynch implements IStrategy {
    
    private IHistory history;
    private IConsole console;

    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        console = context.getConsole();   
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);

        getTickByShift();
        getTicksByTimeInterval();

    }
    
    private void getTickByShift() throws JFException{  
        ITick tick0 = history.getTick(Instrument.EURUSD, 0);
        ITick tick1 = history.getTick(Instrument.EURUSD, 1);
        console.getOut().println(String.format("last tick: %s; previous tick: %s", tick0, tick1));        
    }
      
    private void getTicksByTimeInterval() throws JFException{            
        ITick lastTick = history.getLastTick(Instrument.EURUSD);
        List<ITick> ticks = history.getTicks(Instrument.EURUSD, lastTick.getTime() - 10 * 1000, lastTick.getTime());
        int last = ticks.size() - 1;
        console.getOut().println(String.format(
            "Tick count=%s; Latest bid price=%.5f, time=%s; Oldest bid price=%.5f, time=%s", 
            ticks.size(), ticks.get(last).getBid(), DateUtils.format(ticks.get(last).getTime()), ticks.get(0).getBid(), DateUtils.format(ticks.get(last).getTime())));
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
