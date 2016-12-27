/**
 * 
 */
package trash.jforex.database;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IDataService;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.qtplaf.library.util.Timestamp;

/**
 * Strategy to check the first available data (EURUSD)
 * 
 * @author Miquel Sas
 */
public class CheckFirstData implements IStrategy {

	/**
	 * 
	 */
	public CheckFirstData() {
	}

	public void onStart(IContext context) throws JFException {
		IDataService dataService = context.getDataService();
		long timeDaily = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.DAILY);
		long time4Hours = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.FOUR_HOURS);
		long time1Hour = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.ONE_HOUR);
		long time30Minutes = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.THIRTY_MINS);
		long time15Minutes = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.FIFTEEN_MINS);
		long time5Minutes = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.FIVE_MINS);
		long time1Minute = dataService.getTimeOfFirstCandle(Instrument.EURUSD, Period.ONE_MIN);

		System.out.println("Daily: "+new Timestamp(timeDaily));
		System.out.println("4 Hours: "+new Timestamp(time4Hours));
		System.out.println("1 Hour: "+new Timestamp(time1Hour));
		System.out.println("30 Minutes: "+new Timestamp(time30Minutes));
		System.out.println("15 Minutes: "+new Timestamp(time15Minutes));
		System.out.println("5 Minutes: "+new Timestamp(time5Minutes));
		System.out.println("1 Minute: "+new Timestamp(time1Minute));
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onStop() throws JFException {
	}

}
