package trash.jforex.examples.strategy.indicators;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Set;
import java.util.HashSet;
import java.util.TimeZone;

import com.dukascopy.api.*;

/**
 * The strategy prints the latest sentiment indices for:
 * - set of Instruments,
 * - array of Currencies.
 * 
 * One has to mind that instrument index retrieval requires instrument to be subscribed.
 * However, currency index retrieval does not.
 */
public class SentimentIndexMulti implements IStrategy {
    
    @SuppressWarnings("serial")
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") 
        { {setTimeZone(TimeZone.getTimeZone("GMT")); } };
    
    private Set<Instrument> instruments = new HashSet<Instrument>(  Arrays.asList(new Instrument[] {
    		Instrument.EURUSD, 
    		Instrument.NZDCAD, 
    		Instrument.EURAUD
    }));  
    
    private Currency[] currencies = new Currency[]{
            Currency.getInstance("USD"),
            Currency.getInstance("NZD"),
            Currency.getInstance("CAD"),
            Currency.getInstance("CHF")
    };
        
    private IDataService dataService;
    private IConsole console;
    private IHistory history;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        dataService = context.getDataService();

        context.setSubscribedInstruments(instruments);
        int i = 5;
        while(!context.getSubscribedInstruments().containsAll(instruments) && i >0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
        }
        
        long time = history.getLastTick(Instrument.EURUSD).getTime();
        
        for(Instrument instrument : instruments){
            getAndPrintIndex(instrument, time);
        }
        
        for(Currency currency : currencies){
            getAndPrintIndex(currency, time);
        }
    }
    
    private void getAndPrintIndex(Instrument instrument, long time){
        printIndex(dataService.getFXSentimentIndex(instrument, time), instrument.toString());
    }
    
    private void getAndPrintIndex(Currency currency, long time){
        printIndex(dataService.getFXSentimentIndex(currency, time), currency.toString());
    }
    
    
    private void printIndex (IFXSentimentIndex index, String comment){
    	console.getOut().println(String.format("%s sentiment index at %s is %.5f with tendency %.5f", 
                comment, sdf.format(index.getIndexTime()), index.getIndexValue(), index.getIndexTendency()));
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
