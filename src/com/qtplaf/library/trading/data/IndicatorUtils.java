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

package com.qtplaf.library.trading.data;

import java.awt.Color;
import java.util.Arrays;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.indicators.SimpleMovingAverage;

/**
 * Indicator utilities.
 * 
 * @author Miquel Sas
 */
public class IndicatorUtils {
	/**
	 * Returns a simple SMA configurated indicator data list.
	 * 
	 * @param dataList The source data list.
	 * @param period The period of the SMA.
	 * @param index The index in the data of the source to calculate the average.
	 * @param color Plot color.
	 * @return The indicator data list.
	 */
	public static IndicatorDataList getSMA(DataList dataList, int period, int index, Color color) {
		Session session = dataList.getSession();
		SimpleMovingAverage sma = new SimpleMovingAverage(session);
		sma.getIndicatorInfo().getParameter(SimpleMovingAverage.ParamPeriodIndex).getValue().setInteger(period);
		IndicatorSource source = new IndicatorSource(dataList, OHLCV.Index.Close.getIndex());
		IndicatorDataList avgList = new IndicatorDataList(session, sma, sma.getIndicatorInfo(), Arrays.asList(source));
		avgList.getPlotProperties(0).setColorBullishEven(color);
		avgList.getPlotProperties(0).setColorBearishEven(color);
		avgList.getPlotProperties(0).setColorBullishOdd(color);
		avgList.getPlotProperties(0).setColorBearishOdd(color);
		return avgList;
	}
}
