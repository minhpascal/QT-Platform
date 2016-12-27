package trash.jforex.examples.strategy.historical_data;

import java.util.List;

import com.dukascopy.api.*;

public class HistoryBarsFilter1Week implements IStrategy {

    private IConsole console;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        
        IBar weeklyCurr = history.getBar(Instrument.EURUSD, Period.WEEKLY, OfferSide.BID, 0);
        IBar weeklyPrev = history.getBar(Instrument.EURUSD, Period.WEEKLY, OfferSide.BID, 1);
        List<IBar> barsNoWeekends = history.getBars(Instrument.EURUSD, Period.ONE_HOUR, OfferSide.BID, Filter.WEEKENDS, weeklyPrev.getTime(), weeklyCurr.getTime());
        List<IBar> barsNoFilter = history.getBars(Instrument.EURUSD, Period.ONE_HOUR, OfferSide.BID, Filter.NO_FILTER, weeklyPrev.getTime(), weeklyCurr.getTime());
        print(String.format("No-weekend bar count: %s, No-filter bar count: %s", barsNoWeekends.size(), barsNoFilter.size()));
        for(IBar bar : barsNoWeekends){
            print("No-weekend bar: " + bar);
        }        
        for(IBar bar : barsNoFilter){
            print("No-filter bar: " + bar);
        }
        barsNoFilter.removeAll(barsNoWeekends);
        for(IBar bar : barsNoFilter){
            print("Weekend-only bar: " + bar);
        }
        
        
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
    
    private void print(Object o){
        console.getOut().println(o);
    }

}
