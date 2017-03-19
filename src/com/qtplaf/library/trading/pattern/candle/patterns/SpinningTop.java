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

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.pattern.candle.CandlePattern;
import com.qtplaf.library.trading.pattern.candle.CandleUtils;

/**
 * Spinnin top, spinning with short shadows.
 *
 * @author Miquel Sas
 */
public class SpinningTop extends CandlePattern {

	/**
	 * Constructor.
	 */
	public SpinningTop() {
		super();
		setFamily("Candles");
		setId(getClass().getSimpleName());
		setDescription("Spinning with short shadows");
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
		Control sizeControl = getSizeControl();
		Control positionControl = getPositionControl();
		double rangeFactor = CandleUtils.getRangeFactor(data, getMaximumRange());
		double bodyFactor = CandleUtils.getBodyFactor(data);
		double bodyCenter = CandleUtils.getBodyCenterFactor(data);
		if (sizeControl.checkLE(rangeFactor, Size.Small)) {
			if (sizeControl.checkLE(bodyFactor, Size.Small)) {
				if (positionControl.checkEQ(bodyCenter, Position.Middle)) {
					return true;
				}
			}
		}
		return false;
	}

}