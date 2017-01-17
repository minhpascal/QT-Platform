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
package com.qtplaf.library.trading.server.servers.dukascopy;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OHLCVIterator;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.ServerException;

/**
 * @author Miquel Sas
 */
public class DkOHLCVIterator implements OHLCVIterator {

	/**
	 * History manager.
	 */
	private DkHistoryManager historyManager;
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
	 * Filter.
	 */
	private Filter filter;
	/**
	 * From time.
	 */
	private long from;
	/**
	 * To time.
	 */
	private long to;
	/**
	 * The buffer where temporarily the data is loaded.
	 */
	private List<OHLCV> buffer = new ArrayList<>();
	/**
	 * The buffer size.
	 */
	private int bufferSize = 1000;
	/**
	 * The last time loaded.
	 */
	private long lastTimeLoaded = -1;

	/**
	 * Constructor assigning fields.
	 * 
	 * @param historyManager The history manager.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @param filter The filter to apply.
	 * @param from From time.
	 * @param to To time.
	 */
	public DkOHLCVIterator(
		DkHistoryManager historyManager,
		Instrument instrument,
		Period period,
		OfferSide offerSide,
		Filter filter,
		long from,
		long to) {
		super();
		this.historyManager = historyManager;
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
		this.filter = filter;
		this.from = from;
		this.to = to;
	}

	/**
	 * Closes the iterator and any related resources.
	 * 
	 * @throws ServerException
	 */
	public void close() throws ServerException {
	}

	/**
	 * Returns a boolean indicating if there are remaining elements to retrieve.
	 * 
	 * @return A boolean indicating if there are remaining elements to retrieve.
	 * @throws ServerException
	 */
	public boolean hasNext() throws ServerException {
		requestData();
		return !buffer.isEmpty();
	}

	/**
	 * Returns the next element or throws an exception if there are no more elements.
	 * 
	 * @return The next element or throws an exception if there are no more elements.
	 * @throws ServerException
	 */
	public OHLCV next() throws ServerException {
		try {
			return buffer.remove(0);
		} catch (Exception cause) {
			throw new ServerException("No more data to download", cause);
		}
	}

	/**
	 * Request data from the history manager and load the buffer.
	 * 
	 * @throws ServerException
	 */
	private void requestData() throws ServerException {
		// If the buffer is not empty, do nothing.
		if (!buffer.isEmpty()) {
			return;
		}
		// If last time loaded is greater than or equal to final to time, do nothing.
		if (lastTimeLoaded >= to) {
			return;
		}
		// The last time loaded.
		if (lastTimeLoaded == -1) {
			lastTimeLoaded = from;
		}
		
		// Calculate the buffer size to not pass the to limit.
		int execBufferSize = 0;
		long nextTime = lastTimeLoaded;
		for (int i = 0; i < bufferSize; i++) {
			nextTime += period.getTime();
			if (nextTime > to) {
				break;
			}
			execBufferSize++;
		}
		
		// Load data.
		List<OHLCV> ohlcvData = historyManager.getOHLCVData(
			instrument, period, offerSide, filter, lastTimeLoaded, 0, execBufferSize);
		
		// Tranfer loaded data to buffer with the limit of to.
		for (OHLCV ohlcv : ohlcvData) {
			long time = ohlcv.getTime();
			
			// Skipt last loaded uppon buffer loads.
			if (time == lastTimeLoaded) {
				continue;
			}

			// if time is greater than to, register the last loaded time to do nothing from now.
			if (time > to) {
				lastTimeLoaded = time;
				break;
			}

			// Add data to the buffer and register the last loaded time.
			buffer.add(ohlcv);
			lastTimeLoaded = time;
		}
	}
}
