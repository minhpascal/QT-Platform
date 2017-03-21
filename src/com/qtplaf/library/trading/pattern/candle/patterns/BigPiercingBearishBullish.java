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
 * Big piercing bearish-bullish pattern
 *
 * @author Miquel Sas
 */
public class BigPiercingBearishBullish extends CandlePattern {

	/**
	 * Constructor.
	 */
	public BigPiercingBearishBullish() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Big piercing bearish-bullish pattern");
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

		// Big bearish previous
		if (isPattern(new MediumBearishMediumBody(), dataList, index - 1)) {
			// Big bullish current
			if (isPattern(new MediumBullishMediumBody(), dataList, index)) {
				Data prev = dataList.get(index - 1);
				Data curr = dataList.get(index);
				// Low curr < prev
				if (getLow(curr) <= getBodyLow(prev)) {
					// Body high curr >= body center prev.
					if (getBodyHigh(curr) >= getBodyCenter(prev)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
