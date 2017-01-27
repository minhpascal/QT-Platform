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
 * Minimizes the Euclidean distance between the two indicator sources:
 * <ul>
 * <li>First data source: output translate to input to minimize the mean squared, normally an average.</li>
 * <li>Second data source: input or reference, the price or another average..</li>
 * </ul>
 * Has one parameter that is the period to retrieve backward data.
 * 
 * @author Miquel Sas
 */
public class MeanSquaredTranslation extends PeriodIndicator {
	
	/** Output source index. */
	public static final int Output = 0;
	/** Input source index. */
	public static final int Input = 1;
	/** Index of result. */
	public static final int Index = 0;

	/**
	 * @param session
	 */
	public MeanSquaredTranslation(Session session) {
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
	private void validate(List<IndicatorSource> indicatorSources) {
		// Two and only two data sources.
		if (indicatorSources.size() != 2) {
			throw new IllegalArgumentException("Two and only two data sources are required.");
		}
		// One index per data source.
		for (int i = 0; i < indicatorSources.size(); i++) {
			IndicatorSource source = indicatorSources.get(i);
			if (source.getIndexes().size() != 1) {
				throw new IllegalArgumentException("Source " + i + " must have only one index.");
			}
		}
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	public void start(List<IndicatorSource> indicatorSources) {
		
		// Validate sources.
		validate(indicatorSources);

		// Calculate the number of indexes for later use.
		calculateNumIndexes(indicatorSources);

		// Fill aditional info
		IndicatorInfo info = getIndicatorInfo();

		// Instrument, period and scale from the output source.
		DataInfo dataInfoInput = indicatorSources.get(Output).getDataList().getDataInfo();
		info.setInstrument(dataInfoInput.getInstrument());
		info.setPeriod(dataInfoInput.getPeriod());
		info.setPipScale(dataInfoInput.getPipScale());
		info.setTickScale(dataInfoInput.getTickScale());

		// One output info refered to output and input, with index 0.
		int period = info.getParameter(ParamPeriodName).getValue().getInteger();
		IndicatorSource output = indicatorSources.get(Output);
		IndicatorSource input = indicatorSources.get(Input);
		StringBuilder name = new StringBuilder();
		name.append(output.getDataList().getDataInfo().getName());
		name.append("-");
		name.append(input.getDataList().getDataInfo().getName());
		name.append(" (");
		name.append(period);
		name.append(")");
		info.addOutput(name.toString(), name.toString(), Index);

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
		
		// Output and input.
		IndicatorSource outputSource = indicatorSources.get(Output);
		IndicatorSource inputSource = indicatorSources.get(Input);
		int outputIndex = outputSource.getIndexes().get(0);
		int inputIndex = inputSource.getIndexes().get(0);
		DataList outputData = outputSource.getDataList();
		DataList inputData = inputSource.getDataList();
		double[] output = new double[period];
		double[] input = new double[period];
		int arrayIndex = 0;
		int startIndex = index - period + 1;
		for (int i = startIndex; i <= index; i++) {
			output[arrayIndex] = outputData.get(i).getValue(outputIndex);
			input[arrayIndex] = inputData.get(i).getValue(inputIndex);
			arrayIndex++;
		}		
		double[] meanSquaredValues = Calculator.meanSquaredMinimum(output, input, 0.01, 0.00000000001, 1000000);
		
		// Set indicator data up to index - 1.
		arrayIndex = 0;
		for (int i = startIndex; i < index; i++) {
			indicatorData.get(i).setValue(Index, meanSquaredValues[arrayIndex]);
			arrayIndex++;
		}

		// The result data.
		Data result = new Data();
		result.setValue(Index, meanSquaredValues[arrayIndex]);
		return result;
	}

}
