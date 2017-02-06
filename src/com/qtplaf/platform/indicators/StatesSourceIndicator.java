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

package com.qtplaf.platform.indicators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.Indicator;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.IndicatorSource;
import com.qtplaf.library.trading.data.IndicatorUtils;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.library.trading.data.info.PriceInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.statistics.StatesSource;
import com.qtplaf.platform.statistics.StatesSource.Average;
import com.qtplaf.platform.statistics.StatesSource.Fields;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Indicator to calculate the states source values.
 *
 * @author Miquel Sas
 */
public class StatesSourceIndicator extends Indicator {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** Data list price key. */
	private static final String KeyPrice = "price";

	/** Underlying states source statistics. */
	private StatesSource statesSource;
	/** Caching of data lists. */
	private Map<String, DataList> mapDataLists = new HashMap<>();
	/** This indicator data list. */
	private IndicatorDataList indicatorDataList;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param statesSource The underlying states source statistics.
	 */
	public StatesSourceIndicator(Session session, StatesSource statesSource) {
		super(session);
		this.statesSource = statesSource;

		// Fill indicator info.
		IndicatorInfo info = getIndicatorInfo();

		// Output informations.
		List<Field> fields = statesSource.getIndicatorOutputFields();
		for (int index = 0; index < fields.size(); index++) {
			Field field = fields.get(index);
			info.addOutput(field.getName(), field.getTitle(), index);
		}

	}

	/**
	 * Returns the underlying states source statistics.
	 * 
	 * @return The underlying states source statistics.
	 */
	public StatesSource getStatesSource() {
		return statesSource;
	}

	/**
	 * Returns this indicator data list conveniently configurated.
	 * 
	 * @return The data list.
	 */
	public IndicatorDataList getDataList() {
		if (indicatorDataList == null) {

			List<IndicatorSource> sources = new ArrayList<>();

			// Price (High, Low, Close).
			sources.add(new IndicatorSource(
				getDataListPrice(),
				OHLCV.Index.High.getIndex(),
				OHLCV.Index.Low.getIndex(),
				OHLCV.Index.Close.getIndex()));

			// Averages.
			List<Average> averages = statesSource.getAverages();
			for (Average average : averages) {
				sources.add(new IndicatorSource(getDataListAverage(average), 0));
			}

			// This indicator data list.
			indicatorDataList = new IndicatorDataList(getSession(), this, getIndicatorInfo(), sources);
		}
		return indicatorDataList;
	}

	/**
	 * Returns the data list for a given average.
	 * 
	 * @param average The average.
	 * @return The data list for the average.
	 */
	public IndicatorDataList getDataListAverage(Average average) {
		DataList dataList = mapDataLists.get(average.getName());
		if (dataList == null) {
			dataList = IndicatorUtils.getSmoothedSimpleMovingAverage(
				getDataListPrice(),
				OHLCV.Index.Close.getIndex(),
				average.getPeriod(),
				average.getSmooths());
			mapDataLists.put(average.getName(), dataList);
		}
		return (IndicatorDataList) dataList;
	}

	/**
	 * Returns the price data list.
	 * 
	 * @return The price data list.
	 */
	public PersistorDataList getDataListPrice() {
		DataList price = mapDataLists.get(KeyPrice);
		if (price == null) {
			try {
				// Server, instrument, period.
				Server server = statesSource.getServer();
				Instrument instrument = statesSource.getInstrument();
				Period period = statesSource.getPeriod();

				// The first input source is a persistor data list on the price (instrument-period).
				Record record = RecordUtils.getRecordTicker(getSession(), server, instrument, period);
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				DataInfo infoPrice = new PriceInfo(getSession(), instrument, period);
				Persistor persistor = PersistorUtils.getPersistorOHLCV(getSession(), server, tableName);
				PersistorDataList priceTmp = new PersistorDataList(getSession(), infoPrice, persistor);
				priceTmp.setCacheSize(-1);

				price = priceTmp;
				mapDataLists.put(KeyPrice, price);
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
		return (PersistorDataList) price;
	}

	/**
	 * Returns the list of indicator data lists to perform the calculations.
	 * 
	 * @return The list of indicator data lists.
	 */
	public List<IndicatorDataList> getIndicatorDataListsToCalculate() {
		List<DataList> dataLists = new ArrayList<>(mapDataLists.values());
		return DataList.getIndicatorDataListsToCalculate(dataLists);
	}

	/**
	 * Returns the data price for a given index.
	 * 
	 * @param index The index.
	 * @return The <tt>Data</tt>,
	 */
	private Data getInputDataPrice(int index) {
		DataList dataList = mapDataLists.get(KeyPrice);
		return dataList.get(index);
	}

	/**
	 * Returns the average <tt>Data</tt> for a given indeX
	 * 
	 * @param index The index.
	 * @param average The average.
	 * @return The <tt>Data</tt>,
	 */
	private Data getInputDataAverage(int index, Average average) {
		DataList dataList = mapDataLists.get(average.getName());
		return dataList.get(index);
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	public void start(List<IndicatorSource> indicatorSources) {
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

		IndicatorInfo info = getIndicatorInfo();
		double[] values = new double[info.getOutputCount()];

		// Price values.
		Data price = getInputDataPrice(index);
		double high = price.getValue(OHLCV.Index.High.getIndex());
		double low = price.getValue(OHLCV.Index.Low.getIndex());
		double close = price.getValue(OHLCV.Index.Close.getIndex());
		values[info.getOutputIndex(StatesSource.Fields.High)] = high;
		values[info.getOutputIndex(StatesSource.Fields.Low)] = low;
		values[info.getOutputIndex(StatesSource.Fields.Close)] = close;

		// Percentual range.
		double range = (high - low) / low;
		values[info.getOutputIndex(StatesSource.Fields.Range)] = range;

		// Averages.
		List<Average> averages = statesSource.getAverages();
		for (Average average : averages) {
			Data data = getInputDataAverage(index, average);
			values[info.getOutputIndex(average.getName())] = data.getValue(0);
		}

		// Price spreads vs the fastest average.
		{
			Average fastAvg = averages.get(0);
			Data data = getInputDataAverage(index, fastAvg);
			double avgValue = data.getValue(0);
			// High spread.
			{
				double spread = (high / avgValue) - 1;
				values[info.getOutputIndex(Fields.spreadPriceName(Fields.High, fastAvg.getPeriod()))] = spread;
			}
			// Low spread.
			{
				double spread = (low / avgValue) - 1;
				values[info.getOutputIndex(Fields.spreadPriceName(Fields.Low, fastAvg.getPeriod()))] = spread;
			}
			// Close spread.
			{
				double spread = (close / avgValue) - 1;
				values[info.getOutputIndex(Fields.spreadPriceName(Fields.Close, fastAvg.getPeriod()))] = spread;
			}
		}

		// Spreads between averages.
		for (int i = 0; i < averages.size(); i++) {
			Average averageFast = averages.get(i);
			int periodFast = averageFast.getPeriod();
			Data dataFast = getInputDataAverage(index, averageFast);
			double valueFast = dataFast.getValue(0);
			for (int j = i + 1; j < averages.size(); j++) {
				Average averageSlow = averages.get(j);
				int periodSlow = averageSlow.getPeriod();
				Data dataSlow = getInputDataAverage(index, averageSlow);
				double valueSlow = dataSlow.getValue(0);
				double spread = (valueFast / valueSlow) - 1;
				values[info.getOutputIndex(Fields.spreadAvgName(periodFast, periodSlow))] = spread;
			}
		}

		// Speed (tangent) percentual of averages.
		if (index > 0) {
			for (int i = 0; i < averages.size(); i++) {
				Data dataCurr = getInputDataAverage(index, averages.get(i));
				double valueCurr = dataCurr.getValue(0);
				Data dataPrev = getInputDataAverage(index - 1, averages.get(i));
				double valuePrev = dataPrev.getValue(0);
				double speed = (valueCurr / valuePrev) - 1;
				values[info.getOutputIndex(Fields.speedName(averages.get(i).getPeriod()))] = speed;
			}
		}

		// Result data.
		long time = price.getTime();
		Data data = new Data();
		data.setTime(time);
		data.setValues(values);
		return data;
	}

}
