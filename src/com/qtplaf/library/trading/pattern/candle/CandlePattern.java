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

package com.qtplaf.library.trading.pattern.candle;

import com.qtplaf.library.trading.pattern.Pattern;

/**
 * Root class of candlestick patterns. To correctly calculate proportions and ranges, it receives the average range
 * (hig-low) and its standard deviation.
 *
 * @author Miquel Sas
 */
public abstract class CandlePattern extends Pattern {

	/** Parameters. */
	private CandleParameters parameters;

	/**
	 * 
	 */
	public CandlePattern() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the candlestick patterns recognition parameters.
	 * 
	 * @return The candlestick patterns recognition parameters.
	 */
	public CandleParameters getParameters() {
		return parameters;
	}

	/**
	 * Set the candlestick patterns recognition parameters.
	 * 
	 * @param parameters The candlestick patterns recognition parameters.
	 */
	public void setParameters(CandleParameters parameters) {
		this.parameters = parameters;
	}

}
