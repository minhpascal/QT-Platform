package trash.jforex.examples.strategy.historical_data;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.dukascopy.api.*;

/**
 * The strategy retrieves first historical data for:
 * - daily candles
 * - ticks
 * - renko bars
 */
public class FirstDataTimes implements IStrategy {
    
    @SuppressWarnings("serial")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") 
    	{ {setTimeZone(TimeZone.getTimeZone("GMT")); } };
    
    private IDataService dataService;
    private IConsole console;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        dataService = context.getDataService();

        long timeCandle = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.DAILY);
        long timeRenko = dataService.getTimeOfFirstRenko(Instrument.EURUSD, PriceRange.FIVE_PIPS);
        long timeTick =dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.TICK);
        
        print("daily candle=%s tick=%s renko=%s ", sdf.format(timeCandle), sdf.format(timeTick), sdf.format(timeRenko) );
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
