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

/**
 * Candlestick parameters used in candle pattern recognition.
 *
 * @author Miquel Sas
 */
public class CandleParameters {

	/**
	 * Average range statistically calculated for the time frame.
	 */
	private double rangeAverage;
	/**
	 * Range standard deviation also statistically calculated for the time frame.
	 */
	private double rangeStdDev;
	/**
	 * Factor to consider the candle a big candle, for instance, a range fator greater than 0.75
	 */
	private double bigCandleFactor = 0.75;
	/**
	 * Factor to consider the candle a small candle, for instance, a range fator less than 0.1
	 */
	private double smallCandleFactor = 0.1;

	/**
	 * 
	 */
	public CandleParameters() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the average range.
	 * 
	 * @return The average range.
	 */
	public double getRangeAverage() {
		return rangeAverage;
	}

	/**
	 * Set the average range.
	 * 
	 * @param rangeAverage The average range.
	 */
	public void setRangeAverage(double rangeAverage) {
		this.rangeAverage = rangeAverage;
	}

	/**
	 * Returns the range standard deviation.
	 * 
	 * @return The range standard deviation.
	 */
	public double getRangeStdDev() {
		return rangeStdDev;
	}

	/**
	 * Set the range standard deviation.
	 * 
	 * @param rangeStdDev The range standard deviation.
	 */
	public void setRangeStdDev(double rangeStdDev) {
		this.rangeStdDev = rangeStdDev;
	}

	/**
	 * Return the big candle factor.
	 * 
	 * @return The big candle factor.
	 */
	public double getBigCandleFactor() {
		return bigCandleFactor;
	}

	/**
	 * Set the big candle factor.
	 * 
	 * @param bigCandleFactor The big candle factor.
	 */
	public void setBigCandleFactor(double bigCandleFactor) {
		this.bigCandleFactor = bigCandleFactor;
	}

	/**
	 * @return the smallCandleFactor
	 */
	public double getSmallCandleFactor() {
		return smallCandleFactor;
	}

	/**
	 * @param smallCandleFactor the smallCandleFactor to set
	 */
	public void setSmallCandleFactor(double smallCandleFactor) {
		this.smallCandleFactor = smallCandleFactor;
	}
}
