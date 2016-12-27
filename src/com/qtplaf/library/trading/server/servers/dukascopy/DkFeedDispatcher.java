/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.server.feed.FeedDispatcher;

/**
 * Dukascopy feed dispatcher.
 * 
 * @author Miquel Sas
 */
public class DkFeedDispatcher extends FeedDispatcher {

	/**
	 * Constructor.
	 */
	public DkFeedDispatcher() {
		super();
	}

	/**
	 * Adds a current OHLCV data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 * @param dkBar The Dukascopy bar.
	 */
	public void addCurrentBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = DkUtilities.fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = DkUtilities.fromDkOfferSide(dkOfferSide);
		OHLCV ohlcv = DkUtilities.fromDkBar(dkBar);
		addCurrentOHLCV(instrument, period, offerSide, ohlcv);
	}

	/**
	 * Adds a comppleted OHLCV data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkPeriod The Dukascopy period.
	 * @param dkOfferSide The Dukascopy offer side.
	 * @param dkBar The Dukascopy bar.
	 */
	public void addBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		com.qtplaf.library.trading.data.Period period = DkUtilities.fromDkPeriod(dkPeriod);
		com.qtplaf.library.trading.server.OfferSide offerSide = DkUtilities.fromDkOfferSide(dkOfferSide);
		OHLCV ohlcv = DkUtilities.fromDkBar(dkBar);
		addOHLCV(instrument, period, offerSide, ohlcv);
	}
	
	/**
	 * Add a tick data event to the input queue, from Dukascopy data.
	 * 
	 * @param dkInstrument The Dukascopy instrument.
	 * @param dkTick The Dukascopy tick.
	 */
	public void addTick(Instrument dkInstrument, ITick dkTick) {
		com.qtplaf.library.trading.data.Instrument instrument = DkUtilities.fromDkInstrument(dkInstrument);
		Tick tick = DkUtilities.fromDkTick(dkTick);
		addTick(instrument, tick);
	}
}
