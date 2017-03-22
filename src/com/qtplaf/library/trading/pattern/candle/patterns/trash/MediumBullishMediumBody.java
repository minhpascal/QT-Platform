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

package com.qtplaf.library.trading.pattern.candle.patterns.trash;

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.pattern.candle.CandlePattern;

/**
 * Big bullish.
 *
 * @author Miquel Sas
 */
public class MediumBullishMediumBody extends CandlePattern {

	/**
	 * Constructor.
	 */
	public MediumBullishMediumBody() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Big bullish candle with medium or greater body");
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
		Data data = dataList.get(index);
		Control sizeControl = getControl();
		double rangeFactor = getRangeFactor(data, getMaximumRange());
		double bodyFactor = getBodyFactor(data);
		if (isBullish(data)) {
			if (sizeControl.checkGE(rangeFactor, Size.Medium)) {
				if (sizeControl.checkGE(bodyFactor, Size.Medium)) {
					return true;
				}
			}
		}
		return false;
	}

}
