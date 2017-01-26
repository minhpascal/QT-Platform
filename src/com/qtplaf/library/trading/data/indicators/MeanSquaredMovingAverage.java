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
import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;

/**
 * A moving average, SMA, EMA or WMA that minimizes the euclidean distance between the average and the source values.
 * Generates two output values for each input value.
 * 
 * @author Miquel Sas
 */
public class MeanSquaredMovingAverage extends MovingAverage {

	/**
	 * @param session
	 */
	public MeanSquaredMovingAverage(Session session) {
		super(session);

		// Indicator info to be configured.
		IndicatorInfo info = getIndicatorInfo();

		// Name and title.
		info.setName("MSWMA");
		info.setTitle("Mean squared exponential moving average");

		// Instrument, period and scales will be setup at start using those of the unique <i>DataInfo</i> used.

		// Setup input information. Uses an unique input source, with one output value, of any data type.
		info.addInput(getDefaultInputInfo());

		// Setup the input parameter and default value: period.
		info.addParameter(getPeriodParameter());
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	public void start(List<IndicatorSource> indicatorSources) {

		// Calculate the number of indexes for later use.
		calculateNumIndexes(indicatorSources);

		// Fill aditional info
		IndicatorInfo info = getIndicatorInfo();

		// Instrument, period and scale from the first source.
		DataInfo input = indicatorSources.get(0).getDataList().getDataInfo();
		info.setInstrument(input.getInstrument());
		info.setPeriod(input.getPeriod());
		info.setPipScale(input.getPipScale());
		info.setTickScale(input.getTickScale());

		// Output infos
		int numIndexes = getNumIndexes();
		int period = info.getParameter(ParamPeriodName).getValue().getInteger();

		String indicatorName = "WMA";
		for (int i = 0; i < numIndexes; i++) {
			StringBuilder b = new StringBuilder();
			b.append(indicatorName);
			if (numIndexes > 1) {
				b.append("-" + i);
			}
			b.append("(" + period + ")");
			info.addOutput(b.toString(), b.toString(), i);
		}

		indicatorName = "MSWMA";
		for (int i = 0; i < numIndexes; i++) {
			StringBuilder b = new StringBuilder();
			b.append(indicatorName);
			if (numIndexes > 1) {
				b.append("-" + i);
			}
			b.append("(" + period + ")");
			info.addOutput(b.toString(), b.toString(), i);
		}

		// Set look backward to the indicator info.
		info.setLookBackward(period);
	}

	/**
	 * Calculates the indicator data at the given index, for the list of indicator sources.
	 * <p>
	 * This indicator already calculated data is passed as a parameter because some indicators may need previous
	 * calculated values or use them to improve calculation performance.
	 * 
	 * @param index The data index.
	 * @param indicatorSources The list of indicator sources.
	 * @param indicatorData This indicator already calculated data.
	 * @return The result data.
	 */
	@Override
	public Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData) {
		if (index < 0) {
			return null;
		}

		int period = getIndicatorInfo().getParameter(ParamPeriodName).getValue().getInteger();
		period = Math.min(period, index + 1);
		double[] averages = new double[2];

		// Calculate the average and assing it to the averages.
		Data data = getWMA(this, index, indicatorSources, indicatorData);
		averages[0] = data.getValue(0);

		IndicatorSource source = indicatorSources.get(0);
		int sourceIndex = source.getIndexes().get(0);
		DataList sourceData = source.getDataList();

		double[] sourceValues = new double[period];
		int resultIndex = 0;
		int startIndex = index - period + 1;
		for (int i = startIndex; i <= index; i++) {
			sourceValues[resultIndex] = sourceData.get(i).getValue(sourceIndex);
			resultIndex++;
		}

		double[] averageValues = new double[period];
		int averageIndex = 0;
		for (int i = startIndex; i < index; i++) {
			averageValues[averageIndex] = indicatorData.get(i).getValue(0);
			averageIndex++;
		}
		averageValues[period - 1] = averages[0];
		
		double[] output = averageValues;
		double[] input = sourceValues;
		double[] meanSquaredValues = Calculator.meanSquaredMinimum(output, input, 0.01, 0.00000000001, 1000000);

		averages[1] = meanSquaredValues[period - 1];

		int meanSquaredIndex = 0;
		for (int i = startIndex; i < index; i++) {
			indicatorData.get(i).setValue(1, meanSquaredValues[meanSquaredIndex]);
			meanSquaredIndex++;
		}

		Data result = new Data();
		result.setValues(averages);
		return result;
	}

}
