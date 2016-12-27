/**
 * 
 */
package com.qtplaf.library.trading.server.feed;

import com.qtplaf.library.trading.data.Instrument;

/**
 * Defines a subscription to an instrument tick data.
 * 
 * @author Miquel Sas
 */
public class TickSubscription {

	/**
	 * The intrument.
	 */
	private Instrument instrument;

	/**
	 * Constructor.
	 * 
	 * @param instrument The instrument.
	 */
	public TickSubscription(Instrument instrument) {
		super();
		this.instrument = instrument;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Check if this subscription should accept ticks from the argument instrument.
	 * 
	 * @param instrument The instrument.
	 * @return A boolean that indicates if this subscription should accept ticks from the argument instrument.
	 */
	public boolean acceptsTick(Instrument instrument) {
		return this.instrument.equals(instrument);
	}
}
