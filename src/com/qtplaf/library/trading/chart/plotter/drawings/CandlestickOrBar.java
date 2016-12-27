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
package com.qtplaf.library.trading.chart.plotter.drawings;

import com.qtplaf.library.trading.data.OHLCV;

/**
 * Base class of a bar or candlestick drawing.
 * 
 * @author Miquel Sas
 */
public abstract class CandlestickOrBar extends Drawing {

	/**
	 * The data index.
	 */
	private int index;
	/**
	 * The OHLCV.
	 */
	private OHLCV ohlcv;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param ohlcv The OHLCV.
	 */
	public CandlestickOrBar(int index, OHLCV ohlcv) {
		this.index = index;
		this.ohlcv = ohlcv;
	}

	/**
	 * Returns the data index.
	 * 
	 * @return The data index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the OHLCV.
	 * 
	 * @return The OHLCV.
	 */
	public OHLCV getOHLCV() {
		return ohlcv;
	}

	/**
	 * Check if this bar or candlestick is bullish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bullish.
	 */
	public boolean isBullish() {
		return ohlcv.isBullish();
	}

	/**
	 * Check if this bar or candlestick is bearish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bearish.
	 */
	public boolean isBearish() {
		return ohlcv.isBearish();
	}
}
