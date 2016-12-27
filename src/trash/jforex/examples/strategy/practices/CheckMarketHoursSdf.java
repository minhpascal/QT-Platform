package trash.jforex.examples.strategy.practices;

import com.dukascopy.api.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * The strategy checks on every 10 sec bar if the last tick was within user-defined market hours.
 *
 */
public class CheckMarketHoursSdf implements IStrategy {
	
	private IConsole console;
	private IHistory history;
	
	private Period period = Period.TEN_SECS;
	private Instrument instrument = Instrument.EURUSD;
	
	private SimpleDateFormat gmtSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		history = context.getHistory();

		gmtSdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	//use of string operations
	private boolean isValidTime(int fromHour, int fromMin, int toHour, int toMin) throws JFException {			

		boolean result = false;
		long lastTickTime = history.getLastTick(instrument).getTime();
		//you want to work with the date of the last tick - in a case you are back-testing
		String fromStr = gmtSdf.format(lastTickTime).substring(0, 11) + String.valueOf(fromHour)+":"+String.valueOf(fromMin) + ":00";
		String toStr = gmtSdf.format(lastTickTime).substring(0, 11) + String.valueOf(toHour)+":"+String.valueOf(toMin) + ":00";
		try {
			long from = gmtSdf.parse(fromStr).getTime();
			long to = gmtSdf.parse(toStr).getTime();
			
			print(String.format("calendar: %s - %s last tick: %s", gmtSdf.format(from), gmtSdf.format(to), gmtSdf.format(lastTickTime)));
			result = lastTickTime > from  && lastTickTime < to;			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		if (period != this.period || instrument != this.instrument)
			return;

		print ( "Is valid time? " + isValidTime (10, 0, 18, 0) );
	}

	private void print(Object o) {
		console.getOut().println(o);
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}
	@Override
	public void onMessage(IMessage message) throws JFException {	}
	@Override
	public void onAccount(IAccount account) throws JFException {	}
	@Override
	public void onStop() throws JFException {	}

}
