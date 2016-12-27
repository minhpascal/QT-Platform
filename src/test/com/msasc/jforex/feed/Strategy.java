/**
 * 
 */
package test.com.msasc.jforex.feed;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.Unit;

/**
 * Strategy to test on tick, on bar and feed listener.
 * 
 * @author Miquel Sas
 */
public class Strategy implements IStrategy {
	IContext context;
	public Strategy() {
	}
	public void onStart(IContext context) throws JFException {
		this.context = context;
		// Ensure subcribed.
		Instrument instrument = Instrument.EURUSD;
		Instrument[] instrArr = new Instrument[] { instrument };
		Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(instrArr));
		context.setSubscribedInstruments(instruments);
		
		// Subscribe 1min. ASK
		Period period = Period.ONE_MIN;
		OfferSide offerSide = OfferSide.ASK;
		context.subscribeToBarsFeed(instrument, period, offerSide, new BarListener(0));
		context.subscribeToBarsFeed(instrument, period, offerSide, new BarListener(1));
	}
	public void onTick(Instrument instrument, ITick tick) throws JFException {
//		if (tick != null) {
//			Util.print(tick, "STR-TICK");
//		}
//		IBar bar = context.getHistory().getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0);
//		Util.print(Period.ONE_MIN, bar, "STR-TICK-BAR");
	}
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
//		if (askBar != null) {
//			Util.print(period, askBar, "STR-BAR-ASK");
//		}
//		if (bidBar != null) {
//			Util.print(period, bidBar, "STR-BAR-BID");
//		}
	}
	public void onMessage(IMessage message) throws JFException {
	}
	public void onAccount(IAccount account) throws JFException {
	}
	public void onStop() throws JFException {
	}

}
