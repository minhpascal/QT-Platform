package trash.jforex.examples.strategy.practices;

import com.dukascopy.api.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * The strategy checks on every 10 sec bar if the last tick was within user-defined market hours.
 *
 */
public class CheckMarketHoursCalendar implements IStrategy {
	
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
	
	//use of calendar
	private boolean isValidTime(int fromHour, int fromMin, int toHour, int toMin) throws JFException {
		
		long lastTickTime = history.getLastTick(instrument).getTime();
		Calendar calendar = Calendar.getInstance();
		//you want to work with the date of the last tick - in a case you are back-testing
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTimeInMillis(lastTickTime);
		calendar.set(Calendar.HOUR_OF_DAY, fromHour);
		calendar.set(Calendar.MINUTE, fromMin);
		calendar.set(Calendar.SECOND, 0);
		long from = calendar.getTimeInMillis();
		
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.setTimeInMillis(lastTickTime);
		calendar.set(Calendar.HOUR_OF_DAY, toHour);
		calendar.set(Calendar.MINUTE, toMin);
		calendar.set(Calendar.SECOND, 0);
		long to = calendar.getTimeInMillis();
		
		print(String.format("calendar: %s - %s last tick: %s", gmtSdf.format(from), gmtSdf.format(to), gmtSdf.format(lastTickTime)));
		
		boolean result = lastTickTime > from  && lastTickTime < to;
		return result;
	}



	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		if (period != this.period || instrument != this.instrument)
			return;

		print ( "Is valid time? " + isValidTime(10, 0, 18, 0));
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
