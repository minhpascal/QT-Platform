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

package com.qtplaf.platform.statistics.averages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.rlearning.function.Normalizer;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.trading.chart.plotter.data.BufferedLinePlotter;
import com.qtplaf.library.trading.chart.plotter.data.CandlestickPlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.configuration.Average;
import com.qtplaf.platform.database.configuration.Calculation;
import com.qtplaf.platform.database.configuration.Configuration;
import com.qtplaf.platform.database.configuration.Speed;
import com.qtplaf.platform.database.configuration.Spread;
import com.qtplaf.platform.database.fields.FieldAverage;
import com.qtplaf.platform.database.fields.FieldCalculation;
import com.qtplaf.platform.database.fields.FieldSpeed;
import com.qtplaf.platform.database.fields.FieldSpread;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class Averages extends TickerStatistics {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();


	/** The configuration. */
	private Configuration configuration;

	/** Map of field lists. */
	private Map<String, List<Field>> mapFieldLists = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Averages(Session session) {
		super(session);
	}
	
	/**
	 * Returns the configuration.
	 * 
	 * @return The configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the configuration.
	 * 
	 * @param configuration The configuration.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the list of average fields.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListAverages() {
		List<Field> fields = mapFieldLists.get("averages");
		if (fields == null) {
			fields = new ArrayList<>();
			for (Average average : getConfiguration().getAverages()) {
				fields.add(new FieldAverage(getSession(), getInstrument(), average, Fields.average(average)));
			}
			mapFieldLists.put("averages", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreads(String suffix) {
		String name = "spreads_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Spread spread : getConfiguration().getSpreads()) {
				fields.add(new FieldSpread(getSession(), spread, Fields.spread(spread, suffix)));
			}
			mapFieldLists.put(name, fields);
		}
		return fields;
	}

	/**
	 * Returns the list of calculations fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListCalculations(String suffix) {
		String name = "calculations_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Calculation calculation : getConfiguration().getCalculations()) {
				fields.add(new FieldCalculation(getSession(), calculation, Fields.calculation(calculation, suffix)));
			}
			mapFieldLists.put(name, fields);
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, raw values
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeeds(String suffix) {
		String name = "speeds_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Speed speed : getConfiguration().getSpeeds()) {
				fields.add(new FieldSpeed(getSession(), speed, Fields.speed(speed, suffix)));
			}
			mapFieldLists.put(name, fields);
		}
		return fields;
	}

	/**
	 * Returns the list of fields to calculate the state key.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListStateKey() {
		List<Field> spreadFields = new ArrayList<>();
		spreadFields.addAll(getFieldListSpreads(Suffix.dsc));
		List<Field> speedFields = new ArrayList<>();
		speedFields.addAll(getFieldListSpeeds(Suffix.dsc));
		List<Field> keyFields = new ArrayList<>();
		for (Field field : spreadFields) {
			Spread spread = (Spread) field.getProperty(Fields.Properties.Spread);
			if (spread.isStateKey()) {
				keyFields.add(field);
			}
		}
		for (Field field : speedFields) {
			Speed speed = (Speed) field.getProperty(Fields.Properties.Speed);
			if (speed.isStateKey()) {
				keyFields.add(field);
			}
		}
		return keyFields;
	}

	/**
	 * Returns the list of fields to calculate ranges raw.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListToCalculateRanges() {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getFieldListSpreads(Suffix.raw));
		fields.addAll(getFieldListSpeeds(Suffix.raw));
		fields.addAll(getFieldListCalculations(Suffix.raw));
		return fields;
	}

	/**
	 * Returns a states.
	 * 
	 * @return A states.
	 */
	public States getStates() {
		Manager manager = new Manager(getSession());
		return manager.getStates(getServer(), getInstrument(), getPeriod(), getConfiguration());
	}

	/**
	 * Returns a ranges.
	 * 
	 * @return A ranges.
	 */
	public Ranges getRanges() {
		Manager manager = new Manager(getSession());
		return manager.getRanges(getServer(), getInstrument(), getPeriod(), getConfiguration());
	}

	/**
	 * Returns a transitions.
	 * 
	 * @return A transitions.
	 */
	public Transitions getTransitions() {
		Manager manager = new Manager(getSession());
		return manager.getTransitions(getServer(), getInstrument(), getPeriod(), getConfiguration());
	}

	/**
	 * Returns the map of normalizers for continuous normalization.
	 * 
	 * @return The map of normalizers.
	 */
	public Map<String, Normalizer> getMapNormalizers() {
		try {

			// Ranges statistics.
			Manager manager = new Manager(getSession());
			Ranges ranges = manager.getRanges(getServer(), getInstrument(), getPeriod(), getConfiguration());

			// The map to fill.
			Map<String, Normalizer> map = new HashMap<>();

			double stddevs = 2;
			RecordSet recordSet = ranges.getRecordSet(false);
			for (int i = 0; i < recordSet.size(); i++) {
				Record record = recordSet.get(i);
				String fieldName = record.getValue(Fields.Name).getString();
				String minMax = record.getValue(Fields.MinMax).getString();
				double average = record.getValue(Fields.Average).getDouble();
				double stddev = record.getValue(Fields.StdDev).getDouble();
				Normalizer normalizer = map.get(fieldName);
				if (normalizer == null) {
					normalizer = new Normalizer();
					normalizer.setMaximum(0);
					normalizer.setMinimum(0);
					map.put(fieldName, normalizer);
				}
				if (minMax.equals("min")) {
					normalizer.setMinimum(average - (stddev * stddevs));
				} else {
					normalizer.setMaximum(average + (stddev * stddevs));
				}
			}

			return map;
		} catch (Exception exc) {
			logger.catching(exc);
		}
		return null;
	}

	/**
	 * Returns the main plot data for the price and averages.
	 * 
	 * @param dataList The source list.
	 * @return The plot data.
	 */
	public PlotData getPlotDataMain(PersistorDataList dataList) {

		// First data list: price and indicators.
		DataInfo info = dataList.getDataInfo();
		info.setInstrument(getInstrument());
		info.setName("OHLC");
		info.setDescription("OHLC values");
		info.setPeriod(getPeriod());

		// Candlestick on price: info
		info.addOutput("Open", "O", dataList.getDataIndex(Fields.Open), "Open data value");
		info.addOutput("High", "H", dataList.getDataIndex(Fields.High), "High data value");
		info.addOutput("Low", "L", dataList.getDataIndex(Fields.Low), "Low data value");
		info.addOutput("Close", "C", dataList.getDataIndex(Fields.Close), "Close data value");

		// Candlestick on price: plotter.
		CandlestickPlotter plotterCandle = new CandlestickPlotter();
		plotterCandle.setIndexes(new int[] {
			dataList.getDataIndex(Fields.Open),
			dataList.getDataIndex(Fields.High),
			dataList.getDataIndex(Fields.Low),
			dataList.getDataIndex(Fields.Close) });
		dataList.addDataPlotter(plotterCandle);

		// Line plotter for each average.
		List<Field> averageFields = getFieldListAverages();
		for (Field field : averageFields) {
			String name = field.getName();
			String label = field.getLabel();
			String header = field.getHeader();
			int index = dataList.getDataIndex(name);

			// Output info.
			info.addOutput(name, header, index, label);

			// Plotter.
			BufferedLinePlotter plotterAvg = new BufferedLinePlotter();
			plotterAvg.setIndex(index);
			dataList.addDataPlotter(plotterAvg);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);
		return plotData;
	}

	/**
	 * Returns the plot data for the list of fields.
	 * 
	 * @param dataName The name of the data list.
	 * @param sourceList The source list.
	 * @param fields The list of fields.
	 * @return The plot data.
	 */
	public PlotData getPlotData(String dataName, PersistorDataList sourceList, List<Field> fields) {

		// Data info.
		DataInfo info = new DataInfo(getSession());
		info.setInstrument(getInstrument());
		if (dataName == null) {
			dataName = getInstrument().getId();
		}
		info.setName(dataName);
		info.setDescription(getInstrument().getDescription());
		info.setPeriod(getPeriod());

		// Data list.
		DataList dataList = new DelegateDataList(getSession(), info, sourceList);

		for (Field field : fields) {
			String name = field.getName();
			String header = field.getHeader();
			String label = field.getLabel();
			int index = sourceList.getDataIndex(name);

			// Output info.
			info.addOutput(name, header, index, label);

			// Plotter.
			BufferedLinePlotter plotter = new BufferedLinePlotter();
			plotter.setIndex(index);
			dataList.addDataPlotter(plotter);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);

		return plotData;
	}
}
