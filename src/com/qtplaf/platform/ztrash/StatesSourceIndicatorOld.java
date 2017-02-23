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

package com.qtplaf.platform.ztrash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.library.trading.data.info.PriceInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordUtils;
import com.qtplaf.platform.ztrash.StatesAveragesOld.Fields;

/**
 * Indicator to calculate the states source values.
 *
 * @author Miquel Sas
 */
public class StatesSourceIndicatorOld extends Indicator {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** Data list price key. */
	private static final String KeyPrice = "price";

	/** Underlying states source statistics. */
	private StatesSourceOld statesSource;
	/** Caching of data lists. */
	private Map<String, DataList> mapDataLists = new HashMap<>();
	/** This indicator data list. */
	private IndicatorDataList indicatorDataList;

	/**
	 * Constructor.
	 * 
	 * @param statesSource The underlying states source statistics.
	 */
	public StatesSourceIndicatorOld(StatesSourceOld statesSource) {
		super(statesSource.getSession());
		this.statesSource = statesSource;

		// Fill indicator info.
		IndicatorInfo info = getIndicatorInfo();
		info.setName("STSRC");
		info.setInstrument(statesSource.getInstrument());
		info.setPeriod(statesSource.getPeriod());

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
	public StatesSourceOld getStatesSource() {
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
			sources.add(new IndicatorSource(getDataListPrice(), Data.IndexHigh, Data.IndexLow, Data.IndexClose));

			// Averages.
			List<Field> averageFields = statesSource.getAverageFields();
			for (Field field : averageFields) {
				sources.add(new IndicatorSource(getDataListAverage(field), 0));
			}

			// This indicator data list.
			indicatorDataList = new IndicatorDataList(getSession(), this, sources);
		}
		return indicatorDataList;
	}

	/**
	 * Returns the data list for a given average field.
	 * 
	 * @param averageField The average field.
	 * @return The data list for the average field.
	 */
	private IndicatorDataList getDataListAverage(Field averageField) {
		DataList dataList = mapDataLists.get(averageField.getName());
		if (dataList == null) {
			AverageOld average = StatesAveragesOld.getAverage(averageField);
			dataList = IndicatorUtils.getSmoothedSimpleMovingAverage(
				getDataListPrice(),
				Data.IndexClose,
				average.getPeriod(),
				average.getSmooths());
			mapDataLists.put(averageField.getName(), dataList);
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
				Persistor persistor = PersistorUtils.getPersistorDataPrice(getSession(), server, tableName);
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
	private Data getInputDataAverage(int index, AverageOld average) {
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
		double open = Data.getOpen(price);
		double high = Data.getHigh(price);
		double low = Data.getLow(price);
		double close = Data.getClose(price);
		values[info.getOutputIndex(Fields.Open)] = open;
		values[info.getOutputIndex(Fields.High)] = high;
		values[info.getOutputIndex(Fields.Low)] = low;
		values[info.getOutputIndex(Fields.Close)] = close;

		// Averages.
		List<Field> averageFields = statesSource.getAverageFields();
		for (Field field : averageFields) {
			AverageOld average = StatesAveragesOld.getAverage(field);
			Data data = getInputDataAverage(index, average);
			values[info.getOutputIndex(field.getName())] = data.getValue(0);
		}

		// Price spreads vs the fastest average.
		List<Field> spreadFast = getStatesSource().getSpreadFieldsFastAverage();
		for (Field field : spreadFast) {
			Field srcField = StatesSourceOld.getSourceField(field);
			AverageOld avg = StatesAveragesOld.getAverage(field);
			Data data = getInputDataAverage(index, avg);
			double avgValue = data.getValue(0);
			double srcValue = values[info.getOutputIndex(srcField.getName())];
			double spread = (srcValue / avgValue) - 1;
			values[info.getOutputIndex(field.getName())] = spread;
		}

		// Spreads between averages.
		List<Field> spreadFields = getStatesSource().getSpreadFields();
		for (Field field : spreadFields) {
			AverageOld averageFast = StatesSourceOld.getAverageFast(field);
			AverageOld averageSlow = StatesSourceOld.getAverageSlow(field);
			Data dataFast = getInputDataAverage(index, averageFast);
			double valueFast = dataFast.getValue(0);
			Data dataSlow = getInputDataAverage(index, averageSlow);
			double valueSlow = dataSlow.getValue(0);
			double spread = (valueFast / valueSlow) - 1;
			values[info.getOutputIndex(field.getName())] = spread;
		}

		// Speed (tangent) percentual of averages.
		if (index > 0) {
			List<Field> speedFields = getStatesSource().getSpeedFields();
			for (Field field : speedFields) {
				AverageOld average = StatesAveragesOld.getAverage(field);
				Data dataCurr = getInputDataAverage(index, average);
				double valueCurr = dataCurr.getValue(0);
				Data dataPrev = getInputDataAverage(index - 1, average);
				double valuePrev = dataPrev.getValue(0);
				double speed = (valueCurr / valuePrev) - 1;
				values[info.getOutputIndex(field.getName())] = speed;
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
