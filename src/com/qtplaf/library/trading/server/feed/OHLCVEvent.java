/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.trading.server.feed;

import java.util.EventObject;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * OHLCV feed event.
 * 
 * @author Miquel Sas
 */
public class OHLCVEvent extends EventObject {

	/**
	 * Instrument.
	 */
	private Instrument instrument;
	/**
	 * Period.
	 */
	private Period period;
	/**
	 * Offer side.
	 */
	private OfferSide offerSide;
	/**
	 * The OHLCV data.
	 */
	private OHLCV ohlcv;

	/**
	 * Constructor assigning fields.
	 * 
	 * @param source
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide Offer side.
	 * @param ohlcv OHlcv data.
	 */
	public OHLCVEvent(Object source, Instrument instrument, Period period, OfferSide offerSide, OHLCV ohlcv) {
		super(source);
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
		this.ohlcv = ohlcv;
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
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Returns the offer side.
	 * 
	 * @return The offer side.
	 */
	public OfferSide getOfferSide() {
		return offerSide;
	}

	/**
	 * Returns the OHLCV data.
	 * 
	 * @return The OHLCV data.
	 */
	public OHLCV getOHLCV() {
		return ohlcv;
	}

	/**
	 * Returns a string representation of this event.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getSource().getClass().getSimpleName());
		b.append(", ");
		b.append(getInstrument().getDescription());
		b.append(", ");
		b.append(getPeriod());
		b.append(", ");
		b.append(getOfferSide());
		b.append(", ");
		b.append(getOHLCV());
		return b.toString();
	}

}
