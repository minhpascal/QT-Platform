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
 * Weighted moving average.
 * 
 * @author Miquel Sas
 */
public class WeightedMovingAverage extends MovingAverage {

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public WeightedMovingAverage(Session session) {
		super(session);

		// Indicator info to be configured.
		IndicatorInfo info = getIndicatorInfo();

		// Name and title.
		info.setName("WMA");
		info.setTitle("Weighted moving average");

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
		int periodParameter = getIndicatorInfo().getParameter(ParamPeriod).getValue().getInteger();

		// Applied period.
		int period = Math.min(periodParameter, index + 1);
		int startIndex = index - period + 1;
		int endIndex = index;

		// Must be calculated for all the period each time.
		int numIndexes = getNumIndexes();
		double[] averages = new double[numIndexes];
		double[] weights = new double[numIndexes];
		Arrays.fill(averages, 0);
		Arrays.fill(weights, 0);
		double weight = 1;
		for (int i = startIndex; i <= endIndex; i++) {
			int averageIndex = 0;
			for (IndicatorSource source : indicatorSources) {
				DataList dataList = source.getDataList();
				List<Integer> indexes = source.getIndexes();
				for (Integer dataIndex : indexes) {
					averages[averageIndex] += (dataList.get(i).getValue(dataIndex) * weight);
					weights[averageIndex] += weight;
					averageIndex++;
				}
			}
			weight += 1;
		}
		for (int i = 0; i < averages.length; i++) {
			averages[i] = averages[i] / weights[i];
		}
		Data data = new Data();
		data.setData(averages);
		data.setTime(indicatorSources.get(0).getDataList().get(index).getTime());
		return data;
	}
}
