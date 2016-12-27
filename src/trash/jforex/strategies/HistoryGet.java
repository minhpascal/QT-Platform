/**
 * 
 */
package trash.jforex.strategies;

import java.util.List;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.util.Calendar;

/**
 * Fetch history.
 * 
 * @author Miquel Sas
 */
public class HistoryGet implements IStrategy {

	/**
	 * Default constructor
	 */
	public HistoryGet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Called on start of the strategy.
	 */
	public void onStart(IContext context) throws JFException {
		IHistory history = context.getHistory();
		
		Calendar calendarFrom = new Calendar(2013, 1, 1);
		Calendar calendarTo = new Calendar(2015, 5, 31);
		
		List<IBar> bars = 
			history.getBars(
				Instrument.EURUSD, 
				Period.DAILY, 
				OfferSide.ASK, 
				calendarFrom.getTimeInMillis(), 
				calendarTo.getTimeInMillis());
		
		for (IBar bar : bars) {
			System.out.println(bar);
		}
	}

	/**
	 * Called on every tick of every subscribed instrument.
	 */
	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	/**
	 * Called on bar completion.
	 */
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	/**
	 * Called n message reception.
	 */
	public void onMessage(IMessage message) throws JFException {
	}

	/**
	 * Called on new account information reception.
	 */
	public void onAccount(IAccount account) throws JFException {
	}

	/**
	 * Called on stop.
	 */
	public void onStop() throws JFException {
	}

}
