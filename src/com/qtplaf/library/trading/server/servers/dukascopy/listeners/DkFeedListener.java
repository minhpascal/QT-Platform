/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy.listeners;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.server.feed.FeedListenerAdapter;
import com.qtplaf.library.trading.server.feed.OHLCVSubscription;
import com.qtplaf.library.trading.server.feed.TickSubscription;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;

/**
 * Dukascopy feed listener.
 * 
 * @author Miquel Sas
 */
public class DkFeedListener extends FeedListenerAdapter {

	/**
	 * Constructor.
	 */
	public DkFeedListener() {
		super();
	}

	/**
	 * Adds a subscription to current bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 */
	public void addCurrentOHLCVSubscription(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = DkUtilities.fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = DkUtilities.fromDkOfferSide(dkOfferSide);
		OHLCVSubscription subscription = new OHLCVSubscription(instrument, period, offerSide);
		addCurrentOHLCVSubscription(subscription);
	}

	/**
	 * Adds a subscription to comppleted bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 */
	public void addOHLCVSubscription(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = DkUtilities.fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = DkUtilities.fromDkOfferSide(dkOfferSide);
		OHLCVSubscription subscription = new OHLCVSubscription(instrument, period, offerSide);
		addOHLCVSubscription(subscription);
	}

	/**
	 * Adds a subscription to comppleted bar data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 */
	public void addTickSubscription(Instrument dkInstrument) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		TickSubscription subscription = new TickSubscription(instrument);
		addTickSubscription(subscription);
	}
}
