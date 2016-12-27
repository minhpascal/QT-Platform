package trash.jforex.examples.strategy.indicators;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.TimeZone;
import com.dukascopy.api.*;

/**
 * The strategy prints sentiment index for :
 * - EUR/USD instrument,
 * - USD currency, both latest and 24h old one.
 */
public class SentimentIndex implements IStrategy {
    
    @SuppressWarnings("serial")
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") 
        { {setTimeZone(TimeZone.getTimeZone("GMT")); } };
        
    private IDataService dataService;
    private IConsole console;
    private IHistory history;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        dataService = context.getDataService();
 
        long time = history.getLastTick(Instrument.EURUSD).getTime();
          
        IFXSentimentIndex eurUsdIndex = dataService.getFXSentimentIndex(Instrument.EURUSD, time);
        print("%s sentiment index at %s is %.5f with tendency %.5f", 
                Instrument.EURUSD, sdf.format(eurUsdIndex.getIndexTime()), eurUsdIndex.getIndexValue(), eurUsdIndex.getIndexTendency());
        
        IFXSentimentIndex usdIndex = dataService.getFXSentimentIndex(Currency.getInstance("USD"), time);
        print("USD sentiment index at %s is %.5f with tendency %.5f", 
                sdf.format(usdIndex.getIndexTime()), usdIndex.getIndexValue(), usdIndex.getIndexTendency());
        
        usdIndex = dataService.getFXSentimentIndex(Currency.getInstance("USD"), time - Period.DAILY.getInterval());
        print("USD sentiment index at %s is %.5f with tendency %.5f", 
                sdf.format(usdIndex.getIndexTime()), usdIndex.getIndexValue(), usdIndex.getIndexTendency());
    }
    
    private void print (String format, Object...args){
        console.getOut().println(String.format(format, args));
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
