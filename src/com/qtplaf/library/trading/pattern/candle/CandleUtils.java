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

import com.qtplaf.library.trading.data.Data;

/**
 * Candlestick utilities.
 *
 * @author Miquel Sas
 */
public class CandleUtils {

	/**
	 * Returns the body of the candle.
	 * 
	 * @param data The data element.
	 * @return The body.
	 */
	public static double getBody(Data data) {
		double open = Data.getOpen(data);
		double close = Data.getClose(data);
		return Math.abs(close - open);
	}

	/**
	 * Returns the body factor (0 to 1) relating the body with the range.
	 * 
	 * @param data The data element.
	 * @return The body factor.
	 */
	public static double getBodyFactor(Data data) {
		double body = getBody(data);
		double range = getRange(data);
		return Math.max(1.0, body / range);
	}

	/**
	 * Returns the candle range (high - low).
	 * 
	 * @param data The data element.
	 * @return The range (high - low).
	 */
	public static double getRange(Data data) {
		double high = Data.getHigh(data);
		double low = Data.getLow(data);
		return high - low;
	}

	/**
	 * Returns the range unitary factor (0 to 1) by comparing the range with an estimated maximum range.
	 * 
	 * @param data The data element.
	 * @param maxRange The expected maximum range.
	 * @return The range fator (range / maxRange).
	 */
	public static double getRangeFactor(Data data, double maxRange) {
		return Math.max(1.0, getRange(data) / maxRange);
	}

	/**
	 * Returns the shadow factor (1 - body factor).
	 * 
	 * @param data The data element.
	 * @return The shadow factor.
	 */
	public static double getShadowFactor(Data data) {
		return 1.0 - getBodyFactor(data);
	}

	/**
	 * Returns the the lower shadow of the candle.
	 * 
	 * @param data The data element.
	 * @return Tha lower shadow.
	 */
	public static double getShadowLower(Data data) {
		double low = Data.getLow(data);
		double open = Data.getOpen(data);
		double close = Data.getClose(data);
		return Math.min(open, close) - low;
	}

	/**
	 * Returns the the upper shadow of the candle.
	 * 
	 * @param data The data element.
	 * @return Tha upper shadow.
	 */
	public static double getShadowUpper(Data data) {
		double high = Data.getHigh(data);
		double open = Data.getOpen(data);
		double close = Data.getClose(data);
		return high - Math.max(open, close);
	}
}
