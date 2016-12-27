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

import java.util.Arrays;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Indicator;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.indicators.validators.IntegerValidator;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.library.trading.data.info.InputInfo;
import com.qtplaf.library.trading.data.info.ParameterInfo;

/**
 * Base class for moving averages.
 * 
 * @author Miquel Sas
 */
public abstract class MovingAverage extends Indicator {

	/**
	 * The name of the PERIOD parameter.
	 */
	public static final String ParamPeriod = "PERIOD";

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public MovingAverage(Session session) {
		super(session);
	}

	/**
	 * Returns a suitable period parameter.
	 * 
	 * @return A suitable period parameter.
	 */
	protected ParameterInfo getPeriodParameter() {
		Field period = new Field();
		period.setName(ParamPeriod);
		period.setAlias(ParamPeriod);
		period.setLabel("Period");
		period.setTitle("Average period");
		period.setType(Types.Integer);
		period.setValidator(new IntegerValidator("Period", 1, Integer.MAX_VALUE));
		Value value = period.getDefaultValue();
		value.setInteger(20);
		ParameterInfo parameter = new ParameterInfo(getSession());
		parameter.setField(period);
		parameter.setValue(value);
		return parameter;
	}

	/**
	 * Returns a default input info for Price, Volume and Indicator.
	 * 
	 * @return A default input info for Price, Volume and Indicator.
	 */
	protected InputInfo getDefaultInputInfo() {
		InputInfo inputInfo = new InputInfo();
		inputInfo.addPossibleInputSource(DataType.Price, 1);
		inputInfo.addPossibleInputSource(DataType.Volume, 1);
		inputInfo.addPossibleInputSource(DataType.Indicator, 1);
		return inputInfo;
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
		int period = info.getParameter(ParamPeriod).getValue().getInteger();
		String indicatorName = info.getName();
		for (int i = 0; i < numIndexes; i++) {
			StringBuilder b = new StringBuilder();
			b.append(indicatorName);
			if (numIndexes > 1) {
				b.append("-" + i);
			}
			b.append("(" + period + ")");
			info.addOutput(b.toString(), b.toString(), i);
		}

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
	public abstract Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData);

	/**
	 * Returns the average data calculated from the begining to the argument index, when index is less that period.
	 * 
	 * @param index The index.
	 * @param indicatorSources The list of indicator sources.
	 * @return The average data element.
	 */
	protected Data getAverage(int index, List<IndicatorSource> indicatorSources) {
		int numIndexes = getNumIndexes();
		double[] averages = new double[numIndexes];
		Arrays.fill(averages, 0);
		for (int i = 0; i <= index; i++) {
			int averageIndex = 0;
			for (IndicatorSource source : indicatorSources) {
				DataList dataList = source.getDataList();
				List<Integer> indexes = source.getIndexes();
				for (Integer dataIndex : indexes) {
					averages[averageIndex] += dataList.get(i).getValue(dataIndex);
					averageIndex++;
				}
			}
		}
		for (int i = 0; i < averages.length; i++) {
			averages[i] = averages[i] / (index + 1);
		}
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}

	/**
	 * Returns the source data.
	 * 
	 * @param index The index.
	 * @param indicatorSources The list of indicator sources.
	 * @return The source data element.
	 */
	protected Data getSource(int index, List<IndicatorSource> indicatorSources) {
		int numIndexes = getNumIndexes();
		double[] values = new double[numIndexes];
		Arrays.fill(values, 0);

		int valueIndex = 0;
		for (IndicatorSource source : indicatorSources) {
			DataList dataList = source.getDataList();
			List<Integer> indexes = source.getIndexes();
			for (Integer dataIndex : indexes) {
				values[valueIndex] += dataList.get(index).getValue(dataIndex);
				valueIndex++;
			}
		}
		Data data = new Data();
		data.setData(values);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}
}
