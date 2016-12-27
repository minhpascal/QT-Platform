package trash.jforex.examples.strategy.indicators;

import java.util.List;
import com.dukascopy.api.*;

/**
 * The strategy retrieves sentiment index aggregation bars 
 * for the instrument and its both currencies
 *
 */
public class SentimentIndexInterval implements IStrategy {

    private IConsole console;
    private IHistory history;
    private IDataService dataService;

    @Configurable("")
    public Period period = Period.FOUR_HOURS;
    @Configurable("")
    public Instrument instr = Instrument.EURUSD;
    @Configurable("")
    public int barCount = 10;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        dataService = context.getDataService();
        
        long lastTickTime = history.getTimeOfLastTick(instr);
        long from = history.getTimeForNBarsBack(period, lastTickTime, barCount);
        long to = history.getTimeForNBarsBack(period, lastTickTime, 1);
        printIndices(dataService.getFXSentimentIndex(instr, period, from, to), instr);
        printIndices(dataService.getFXSentimentIndex(instr.getPrimaryCurrency(), period, from, to), instr.getPrimaryCurrency());
        printIndices(dataService.getFXSentimentIndex(instr.getSecondaryCurrency(), period, from, to), instr.getSecondaryCurrency());
    }
    
    private void printIndices(List<IFXSentimentIndexBar> sentimentBars, Object key){
        for (IFXSentimentIndexBar sentimentBar : sentimentBars) {
            console.getOut().println(key.toString() + " - " + sentimentBar + ", close price: " + sentimentBar.getClose());
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

}
