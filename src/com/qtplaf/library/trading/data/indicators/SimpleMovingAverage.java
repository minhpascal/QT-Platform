/**
 * 
 */
package com.qtplaf.library.trading.data.indicators;

import java.util.Arrays;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
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
	 * @param indicatorData This indicator already calculated data.
	 * @return The result data.
	 */
	public Data calculate(int index, List<IndicatorSource> indicatorSources, DataList indicatorData) {

		// If index < 0 do nothing.
		if (index < 0) {
			return null;
		}

		// The unique data list and the index of the data.
		int period = getIndicatorInfo().getParameter(ParamPeriod).getValue().getInteger();

		// If index < period, calculate the mean from scratch.
		if (index < period) {
			return getAverage(index, indicatorSources);
		}

		// Improved performance calculation retrieving from the last calculated average the first value of the series
		// (divided by the period) and adding the new value of the series (also divided bythe period).
		int numIndexes = getNumIndexes();
		double[] averages = new double[numIndexes];
		Arrays.fill(averages, 0);
		int averageIndex = 0;
		for (IndicatorSource source : indicatorSources) {
			DataList dataList = source.getDataList();
			List<Integer> indexes = source.getIndexes();
			for (Integer dataIndex : indexes) {
				double lastAverage = indicatorData.get(index - 1).getValue(averageIndex);
				double firstValue = dataList.get(index - period).getValue(dataIndex) / period;
				double nextValue = dataList.get(index).getValue(dataIndex) / period;
				double average = lastAverage - firstValue + nextValue;
				averages[averageIndex] += average;
				averageIndex++;
			}
		}
		
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}
}
