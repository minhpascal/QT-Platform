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
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.configuration.Average;
import com.qtplaf.platform.database.fields.FieldClose;
import com.qtplaf.platform.database.fields.FieldHigh;
import com.qtplaf.platform.database.fields.FieldLow;
import com.qtplaf.platform.database.fields.FieldOpen;
import com.qtplaf.platform.statistics.averages.States;
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
		int index = 0;

		Field open = new FieldOpen(states.getSession(), states.getInstrument(), Fields.Open);
		Field high = new FieldHigh(states.getSession(), states.getInstrument(), Fields.High);
		Field low = new FieldLow(states.getSession(), states.getInstrument(), Fields.Low);
		Field close = new FieldClose(states.getSession(), states.getInstrument(), Fields.Close);
		info.addOutput(open.getName(), open.getTitle(), index++);
		info.addOutput(high.getName(), high.getTitle(), index++);
		info.addOutput(low.getName(), low.getTitle(), index++);
		info.addOutput(close.getName(), close.getTitle(), index++);

		List<Field> fields = states.getFieldListAverages();
		int lookBackward = 0;
		for (Field field : fields) {
			info.addOutput(field.getName(), field.getTitle(), index++);
			int period = ((Average) field.getProperty(Fields.Properties.Average)).getPeriod();
			lookBackward = Math.max(lookBackward, period);
		}
		info.setLookBackward(lookBackward);
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
			dataList = new IndicatorDataList(getSession(), this, sources);

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
		PersistorDataList price = (PersistorDataList) mapDataLists.get("price");
		if (price == null) {
			try {

				// Server, instrument, period.
				Server server = states.getServer();
				Instrument instrument = states.getInstrument();
				Period period = states.getPeriod();

				// The first input source is a persistor data list on the price (instrument-period).
				Record record = RecordUtils.getRecordTicker(getSession(), server, instrument, period);
				String tableName = record.getValue(Fields.TableName).getString();
				DataInfo infoPrice = new PriceInfo(getSession(), instrument, period);
				Persistor persistor = PersistorUtils.getPersistorDataPrice(getSession(), server, instrument, tableName);
				price = new PersistorDataList(getSession(), infoPrice, persistor);
				price.setCacheSize(getIndicatorInfo().getLookBackward());

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
			Average average = (Average) averageField.getProperty(Fields.Properties.Average);
			if (average.getType().equals(Average.Type.SMA)) {
				dataList = IndicatorUtils.getSmoothedSimpleMovingAverage(
					getDataListPrice(),
					Data.IndexClose,
					average.getPeriod(),
					average.getSmooths());
			}
			if (average.getType().equals(Average.Type.WMA)) {
				dataList = IndicatorUtils.getSmoothedWeightedMovingAverage(
					getDataListPrice(),
					Data.IndexClose,
					average.getPeriod(),
					average.getSmooths());
			}
			mapDataLists.put(averageField.getName(), dataList);
		}
		return (IndicatorDataList) dataList;
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
		values[info.getOutputIndex(Fields.Open)] = Data.getOpen(price);
		values[info.getOutputIndex(Fields.High)] = Data.getHigh(price);
		values[info.getOutputIndex(Fields.Low)] = Data.getLow(price);
		values[info.getOutputIndex(Fields.Close)] = Data.getClose(price);

		// Averages.
		List<Field> averageFields = states.getFieldListAverages();
		for (Field field : averageFields) {
			Data data = getDataListAverage(field).get(index);
			values[info.getOutputIndex(field.getName())] = data.getValue(0);
		}

		// Result data.
		long time = price.getTime();
		Data data = new Data();
		data.setTime(time);
		data.setValues(values);
		return data;
	}

}
