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
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.indicators.ExponentialMovingAverage;
import com.qtplaf.library.trading.data.indicators.MeanSquaredTranslation;
import com.qtplaf.library.trading.data.indicators.PeriodIndicator;
import com.qtplaf.library.trading.data.indicators.SimpleMovingAverage;
import com.qtplaf.library.trading.data.indicators.WeightedMovingAverage;
import com.qtplaf.library.util.list.ListUtils;

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
	public static IndicatorDataList getExponentialMovingAverage(
		DataList dataList,
		int period,
		int index,
		Color color) {

		Session session = dataList.getSession();
		ExponentialMovingAverage ema = new ExponentialMovingAverage(session);
		ema.getIndicatorInfo().getParameter(PeriodIndicator.ParamPeriodIndex).getValue().setInteger(period);
		IndicatorSource source = new IndicatorSource(dataList, index);
		IndicatorDataList avgList =
			new IndicatorDataList(session, ema, ema.getIndicatorInfo(), ListUtils.asList(source));
		avgList.getPlotProperties(0).setColorBullishEven(color);
		avgList.getPlotProperties(0).setColorBearishEven(color);
		avgList.getPlotProperties(0).setColorBullishOdd(color);
		avgList.getPlotProperties(0).setColorBearishOdd(color);
		return avgList;
	}

	/**
	 * Returns a simple SMA configurated indicator data list.
	 * 
	 * @param dataList The source data list.
	 * @param period The period of the SMA.
	 * @param index The index in the data of the source to calculate the average.
	 * @param color Plot color.
	 * @return The indicator data list.
	 */
	public static IndicatorDataList getSimpleMovingAverage(
		DataList dataList,
		int period,
		int index,
		Color color) {

		Session session = dataList.getSession();
		SimpleMovingAverage sma = new SimpleMovingAverage(session);
		sma.getIndicatorInfo().getParameter(PeriodIndicator.ParamPeriodIndex).getValue().setInteger(period);
		IndicatorSource source = new IndicatorSource(dataList, index);
		IndicatorDataList avgList =
			new IndicatorDataList(session, sma, sma.getIndicatorInfo(), ListUtils.asList(source));
		avgList.getPlotProperties(0).setColorBullishEven(color);
		avgList.getPlotProperties(0).setColorBearishEven(color);
		avgList.getPlotProperties(0).setColorBullishOdd(color);
		avgList.getPlotProperties(0).setColorBearishOdd(color);
		return avgList;
	}

	/**
	 * Returns a simple WMA configurated indicator data list.
	 * 
	 * @param dataList The source data list.
	 * @param period The period of the SMA.
	 * @param index The index in the data of the source to calculate the average.
	 * @param color Plot color.
	 * @return The indicator data list.
	 */
	public static IndicatorDataList getWeightedMovingAverage(
		DataList dataList,
		int period,
		int index,
		Color color) {

		Session session = dataList.getSession();
		WeightedMovingAverage sma = new WeightedMovingAverage(session);
		sma.getIndicatorInfo().getParameter(PeriodIndicator.ParamPeriodIndex).getValue().setInteger(period);
		IndicatorSource source = new IndicatorSource(dataList, index);
		IndicatorDataList avgList =
			new IndicatorDataList(session, sma, sma.getIndicatorInfo(), ListUtils.asList(source));
		avgList.getPlotProperties(0).setColorBullishEven(color);
		avgList.getPlotProperties(0).setColorBearishEven(color);
		avgList.getPlotProperties(0).setColorBullishOdd(color);
		avgList.getPlotProperties(0).setColorBearishOdd(color);
		return avgList;
	}

	/**
	 * Returns a mean squared translation.
	 * 
	 * @param outputList Output data list.
	 * @param outputIndex Output index in the data.
	 * @param inputList Input data list.
	 * @param inputIndex Input index in the data.
	 * @param period Period.
	 * @param color Color.
	 * @return The indicator data list.
	 */
	public static IndicatorDataList getMeanSquaredTranslation(
		DataList outputList,
		int outputIndex,
		DataList inputList,
		int inputIndex,
		int period,
		Color color) {

		Session session = outputList.getSession();
		MeanSquaredTranslation ms = new MeanSquaredTranslation(session);
		ms.getIndicatorInfo().getParameter(PeriodIndicator.ParamPeriodIndex).getValue().setInteger(period);
		IndicatorSource outputSource = new IndicatorSource(outputList, outputIndex);
		IndicatorSource inputSource = new IndicatorSource(inputList, inputIndex);
		List<IndicatorSource> sources = ListUtils.asList(outputSource, inputSource);
		IndicatorDataList msList = new IndicatorDataList(session, ms, ms.getIndicatorInfo(), sources);
		msList.getPlotProperties(0).setColorBullishEven(color);
		msList.getPlotProperties(0).setColorBearishEven(color);
		msList.getPlotProperties(0).setColorBullishOdd(color);
		msList.getPlotProperties(0).setColorBearishOdd(color);
		return msList;
	}
}
