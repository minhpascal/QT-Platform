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
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Calulates states source (raw) values.
 *
 * @author Miquel Sas
 */
public class StatesIndicator extends Indicator {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** Underlying states source statistics. */
	private States states;
	/** Caching of data lists. */
	private Map<String, DataList> mapDataLists = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param session
	 */
	public StatesIndicator(States states) {
		super(states.getSession());
		this.states = states;

		// Fill indicator info.
		IndicatorInfo info = getIndicatorInfo();
		info.setName("STSRC");
		info.setInstrument(states.getInstrument());
		info.setPeriod(states.getPeriod());

		// Output informations.
		List<Field> fields = getOutputFields();
		for (int index = 0; index < fields.size(); index++) {
			Field field = fields.get(index);
			info.addOutput(field.getName(), field.getTitle(), index);
		}
	}

	/**
	 * Returns this indicator data list conveniently configurated.
	 * 
	 * @return The data list.
	 */
	public IndicatorDataList getDataList() {
		IndicatorDataList dataList = (IndicatorDataList) mapDataLists.get("this_indicator");
		if (dataList == null) {

			List<IndicatorSource> sources = new ArrayList<>();

			// Price (High, Low, Close).
			sources.add(new IndicatorSource(getDataListPrice(), Data.IndexHigh, Data.IndexLow, Data.IndexClose));

			// Averages.
			List<Field> averageFields = states.getFieldListAverages();
			for (Field field : averageFields) {
				sources.add(new IndicatorSource(getDataListAverage(field), 0));
			}

			// This indicator data list.
			dataList = new IndicatorDataList(getSession(), this, getIndicatorInfo(), sources);
			
			mapDataLists.put("this_indicator", dataList);
		}
		return dataList;
	}

	/**
	 * Returns the price data list.
	 * 
	 * @return The price data list.
	 */
	public PersistorDataList getDataListPrice() {
		DataList price = mapDataLists.get("price");
		if (price == null) {
			try {
				// Server, instrument, period.
				Server server = states.getServer();
				Instrument instrument = states.getInstrument();
				Period period = states.getPeriod();

				// The first input source is a persistor data list on the price (instrument-period).
				Record record = RecordUtils.getRecordTicker(getSession(), server, instrument, period);
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				DataInfo infoPrice = new PriceInfo(getSession(), instrument, period);
				Persistor persistor = PersistorUtils.getPersistorDataPrice(getSession(), server, tableName);
				price = new PersistorDataList(getSession(), infoPrice, persistor);

				mapDataLists.put("price", price);
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
	 * Returns the data list for a given average field.
	 * 
	 * @param averageField The average field.
	 * @return The data list for the average field.
	 */
	private IndicatorDataList getDataListAverage(Field averageField) {
		DataList dataList = mapDataLists.get(averageField.getName());
		if (dataList == null) {
			Average average = states.getPropertyAverage(averageField);
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
	 * Returns the data list for a given average field.
	 * 
	 * @param averageName The average name.
	 * @return The data list for the average field.
	 */
	private IndicatorDataList getDataListAverage(String averageName) {
		return (IndicatorDataList) mapDataLists.get(averageName);
	}

	/**
	 * Returns the list of output fields.
	 * 
	 * @return The list of output fields.
	 */
	public List<Field> getOutputFields() {
		List<Field> outputFields = new ArrayList<>();
		outputFields.add(states.getFieldDefOpen());
		outputFields.add(states.getFieldDefHigh());
		outputFields.add(states.getFieldDefLow());
		outputFields.add(states.getFieldDefClose());
		outputFields.addAll(states.getFieldListAverages());
		outputFields.addAll(states.getFieldListSpreadsAverageRaw());
		outputFields.addAll(states.getFieldListSpreadsRaw());
		outputFields.addAll(states.getFieldListSpeedsRaw());
		return outputFields;
	}

	/**
	 * Called before starting calculations to give the indicator the opportunity to initialize any internal resources.
	 * 
	 * @param indicatorSources The list of indicator sources.
	 */
	@Override
	public void start(List<IndicatorSource> indicatorSources) {
	}

	/**
	 * Calculates the indicator data at the given index, for the list of indicator sources.
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
		Data price = getDataListPrice().get(index);
		Field open = states.getFieldDefOpen();
		Field high = states.getFieldDefHigh();
		Field low = states.getFieldDefLow();
		Field close = states.getFieldDefClose();
		values[info.getOutputIndex(open.getName())] = Data.getOpen(price);
		values[info.getOutputIndex(high.getName())] = Data.getHigh(price);
		values[info.getOutputIndex(low.getName())] = Data.getLow(price);
		values[info.getOutputIndex(close.getName())] = Data.getClose(price);

		// Averages.
		List<Field> averageFields = states.getFieldListAverages();
		for (Field field : averageFields) {
			Data data = getDataListAverage(field).get(index);
			values[info.getOutputIndex(field.getName())] = data.getValue(0);
		}

		// Price spreads vs the fastest average.
		List<Field> spreadFieldsAvgFast = states.getFieldListSpreadsAverageRaw();
		for (Field field : spreadFieldsAvgFast) {
			Field srcField = states.getPropertySourceField(field);
			Data data = getDataListAverage(srcField).get(index);
			double avgValue = data.getValue(0);
			double srcValue = values[info.getOutputIndex(srcField.getName())];
			double spread = (srcValue / avgValue) - 1;
			values[info.getOutputIndex(field.getName())] = spread;
		}

		// Spreads between averages.
		List<Field> spreadFields = states.getFieldListSpreadsRaw();
		for (Field field : spreadFields) {
			Spread spread = states.getPropertySpread(field);
			Average averageFast = spread.getFastAverage();
			Average averageSlow = spread.getSlowAverage();
			Data dataFast = getDataListAverage(averageFast.getName()).get(index);
			Data dataSlow = getDataListAverage(averageSlow.getName()).get(index);
			double valueFast = dataFast.getValue(0);
			double valueSlow = dataSlow.getValue(0);
			double valueSpread = (valueFast / valueSlow) - 1;
			values[info.getOutputIndex(field.getName())] = valueSpread;
		}

		// Speed (tangent) percentual of averages.
		if (index > 0) {
			List<Field> speedFields = states.getFieldListSpeedsRaw();
			for (Field field : speedFields) {
				Speed speed = states.getPropertySpeed(field);
				Average average = speed.getAverage();
				Data dataCurr = getDataListAverage(average.getName()).get(index);
				Data dataPrev = getDataListAverage(average.getName()).get(index - 1);
				double valueCurr = dataCurr.getValue(0);
				double valuePrev = dataPrev.getValue(0);
				double valueSpeed = (valueCurr / valuePrev) - 1;
				values[info.getOutputIndex(field.getName())] = valueSpeed;
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
