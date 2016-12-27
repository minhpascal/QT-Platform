/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy.listeners;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IBarFeedListener;
import com.qtplaf.library.trading.server.servers.dukascopy.DkFeedDispatcher;

/**
 * Implementation of the <i>IBarFeedListener</i> interface.
 * 
 * @author Miquel Sas
 */
public class DkBarFeedListener implements IBarFeedListener {

	/**
	 * The Dukascopy dispatcher.
	 */
	private DkFeedDispatcher dispatcher;

	/**
	 * Constructor.
	 * 
	 * @param listener The system listener to forward events.
	 */
	public DkBarFeedListener(DkFeedDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
	}

	/**
	 * The method is being called when next bar arrives and posts the bar  to the dispatcher.
	 * 
	 * @param dkInstrument Instrument.
	 * @param dkPeriod Period.
	 * @param dkOfferSide Offer side.
	 * @param dkBar Bar.
	 */
	public void onBar(Instrument dkInstrument, Period dkPeriod, OfferSide dkOfferSide, IBar dkBar) {
		dispatcher.addBar(dkInstrument, dkPeriod, dkOfferSide, dkBar);
	}

}
