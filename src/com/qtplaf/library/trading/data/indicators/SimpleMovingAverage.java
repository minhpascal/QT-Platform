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
package com.qtplaf.library.trading.data.indicators;

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.info.IndicatorInfo;

/**
 * Simple moving average.
 * 
 * @author Miquel Sas
 */
public class SimpleMovingAverage extends MovingAverage {

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public SimpleMovingAverage(Session session) {
		super(session);

		// Indicator info to be configured.
		IndicatorInfo info = getIndicatorInfo();

		// Name and title.
		info.setName("SMA");
		info.setTitle("Simple moving average");

		// Instrument, period and scales will be setup at start using those of the unique <i>DataInfo</i> used.

		// Setup input information. Uses an unique input source, with one output value, of any data type.
		info.addInput(getDefaultInputInfo());

		// Setup the input parameter and default value: period.
		info.addParameter(getPeriodParameter());
	}

	/**
	 * Calculates the indicator.
	 * <p>
	 * This indicator already calculated data is used to improve calculation performance.
	 * 
	 * @param index The data index.
	 * @param inputSources The list of input sources.
	 * @param inputIndexes The list of input indexes to be considered.
	 * @return The result data.
	 */
	public Data calculate(int index, List<IndicatorSource> indicatorSources) {
		// If index < 0 do nothing.
		if (index < 0) {
			return null;
		}
		int period = getIndicatorInfo().getParameter(ParamPeriod).getValue().getInteger();
		return getAverage(period, index, indicatorSources);
	}
}
