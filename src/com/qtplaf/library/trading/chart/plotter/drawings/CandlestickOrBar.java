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

import com.qtplaf.library.trading.data.Data;

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
	 * The data.
	 */
	private Data data;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param data The data.
	 */
	public CandlestickOrBar(int index, Data data) {
		this.index = index;
		this.data = data;
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
	 * Returns the data.
	 * 
	 * @return The data.
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Check if this bar or candlestick is bullish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bullish.
	 */
	public boolean isBullish() {
		return Data.isBullish(data);
	}

	/**
	 * Check if this bar or candlestick is bearish.
	 * 
	 * @return A boolean indicating if this bar or candlestick is bearish.
	 */
	public boolean isBearish() {
		return Data.isBearish(data);
	}
}
