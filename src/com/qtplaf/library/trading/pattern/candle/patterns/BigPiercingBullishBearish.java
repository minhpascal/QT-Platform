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

package com.qtplaf.library.trading.pattern.candle.patterns;

import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.pattern.candle.CandlePattern;

/**
 * Big piercing bullish-bearish pattern.
 *
 * @author Miquel Sas
 */
public class BigPiercingBullishBearish extends CandlePattern {

	/**
	 * Constructor.
	 */
	public BigPiercingBullishBearish() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Big piercing bullish-bearish pattern");
		setLookBackward(1);
	}

	/**
	 * Check if the pattern can be identified at the current data and index.
	 * 
	 * @param dataList The data list.
	 * @param index The current index.
	 * @return A boolean indicating that the pattern has been identified.
	 */
	@Override
	public boolean isPattern(DataList dataList, int index) {

		// Index 0 no sense.
		if (index == 0) {
			return false;
		}

		// Big bullish previous
		if (isPattern(new MediumBullishMediumBody(), dataList, index - 1)) {
			// Big bearish current
			if (isPattern(new MediumBearishMediumBody(), dataList, index)) {
				Data prev = dataList.get(index - 1);
				Data curr = dataList.get(index);
				// High curr >= body high prev
				if (getHigh(curr) >= getBodyHigh(prev)) {
					// Body low curr <= body center prev.
					if (getBodyLow(curr) <= getBodyCenter(prev)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
