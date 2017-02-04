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
	private DataList getDataListAverage(Average average) {
		DataList dataList = mapDataLists.get(average.getId());
		if (dataList == null) {
			dataList = IndicatorUtils.getSmoothedSimpleMovingAverage(
				getDataListPrice(),
				OHLCV.Index.Close.getIndex(),
				average.getPeriod(),
				average.getSmooths());
			mapDataLists.put(average.getId(), dataList);
		}
		return dataList;
	}

	/**
	 * Returns the price data list.
	 * 
	 * @return The price data list.
	 */
	private DataList getDataListPrice() {
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
		return price;
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
		return null;
	}

}
