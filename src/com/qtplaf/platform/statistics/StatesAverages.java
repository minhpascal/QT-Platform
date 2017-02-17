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

package com.qtplaf.platform.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.statistics.Output;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.PipValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.statistics.Average.Speed;
import com.qtplaf.platform.statistics.Average.Spread;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class StatesAverages extends Statistics {

	/**
	 * Field names for the series of averages statistics.
	 */
	public static class Fields {
		/** Data index. */
		public static final String Index = "index";
		/** Data time. */
		public static final String Time = "time";
		/** Data time formatted. */
		public static final String TimeFmt = "time_fmt";
		/** Data open value. */
		public static final String Open = "open";
		/** Data high value. */
		public static final String High = "high";
		/** Data low value. */
		public static final String Low = "low";
		/** Data close value. */
		public static final String Close = "close";

		public static final String Name = "name";
		public static final String MinMax = "min_max";
		public static final String Period = "period";
		public static final String Value = "value";
		public static final String Count = "count";
		public static final String Minimum = "minimum";
		public static final String Maximum = "maximum";
		public static final String Average = "average";
		public static final String StdDev = "stddev";
		public static final String AvgStd_1 = "avgstd_1";
		public static final String AvgStd_2 = "avgstd_2";

		public static final String Key = "state_key";
	}

	public static Average getAverage(Field field) {
		return (Average) field.getProperty("average");
	}
	private static void setAverage(Field field, Average average) {
		field.setProperty("average", average);
	}

	public static Average getAverageFast(Field field) {
		return (Average) field.getProperty("fast");
	}
	private static void setAverageFast(Field field, Average average) {
		field.setProperty("fast", average);
	}

	public static Average getAverageSlow(Field field) {
		return (Average) field.getProperty("slow");
	}
	private static void setAverageSlow(Field field, Average average) {
		field.setProperty("slow", average);
	}

	public static Field getSourceField(Field field) {
		return (Field) field.getProperty("source-field");
	}
	private static void setSourceField(Field field, Field source) {
		field.setProperty("source-field", field);
	}
	
	public static NormalizedStateValueDescriptor getNormalizer(Field field) {
		return (NormalizedStateValueDescriptor) field.getProperty("normalizer");
	}
	private static void setNormalizer(Field field, NormalizedStateValueDescriptor normalizer) {
		field.setProperty("normalizer", normalizer);
	}

	/**
	 * Returns the descriptors map to normalize the values..
	 * 
	 * @param ranges The states ranges statistics.
	 * @return The normalized descriptors map.
	 * @throws Exception
	 */
	public static Map<String, NormalizedStateValueDescriptor> getDescriptorsMap(StatesRanges ranges) throws Exception {
		Map<String, NormalizedStateValueDescriptor> descriptorsMap = new HashMap<>();
		double stddevs = 2;
		RecordSet recordSet = ranges.getRecordSet(false);
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			String fieldName = record.getValue(Fields.Name).getString();
			String minMax = record.getValue(Fields.MinMax).getString();
			double average = record.getValue(Fields.Average).getDouble();
			double stddev = record.getValue(Fields.StdDev).getDouble();
			NormalizedStateValueDescriptor descriptor = descriptorsMap.get(fieldName);
			if (descriptor == null) {
				descriptor = new NormalizedStateValueDescriptor();
				descriptor.setMaximum(0);
				descriptor.setMinimum(0);
				descriptorsMap.put(fieldName, descriptor);
			}
			if (minMax.equals("min")) {
				descriptor.setMinimum(average - (stddev * stddevs));
			} else {
				descriptor.setMaximum(average + (stddev * stddevs));
			}
		}
		return descriptorsMap;
	}

	/**
	 * Map of fields.
	 */
	private Map<String, Field> mapFields = new HashMap<>();
	/**
	 * Working session.
	 */
	private Session session;
	/**
	 * The server.
	 */
	private Server server;
	/**
	 * The instrument.
	 */
	private Instrument instrument;
	/**
	 * The period.
	 */
	private Period period;
	/**
	 * The configuration.
	 */
	private Configuration configuration;

	/**
	 * Average fields.
	 */
	private List<Field> averageFields;

	/**
	 * Spread fields between averages.
	 */
	private List<Field> spreadFields;

	/**
	 * Speedd fields of averages.
	 */
	private List<Field> speedFields;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesAverages(Session session, Server server, Instrument instrument, Period period) {
		super();
		this.session = session;
		this.server = server;
		this.instrument = instrument;
		this.period = period;
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
	 * Set the configuration.
	 * 
	 * @param configuration The conficuration.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Setup after adding the averages.
	 */
	protected abstract void setup();

	/**
	 * Returns the list of averages that defines this statistics.
	 * 
	 * @return The list of smoothed averages.
	 */
	public List<Average> getAverages() {
		return configuration.getAverages();
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldIndex() {
		Field field = mapFields.get(Fields.Index);
		if (field == null) {
			field = DomainUtils.getIndex(getSession(), Fields.Index);
			mapFields.put(Fields.Index, field);
		}
		return field;
	}

	/**
	 * Returns the name of the average.
	 * 
	 * @param avg The average.
	 * @return The name.
	 */
	public String getAverageName(Average avg) {
		return "average_" + avg.getPeriod();
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldTime() {
		Field field = mapFields.get(Fields.Time);
		if (field == null) {
			field = DomainUtils.getTime(getSession(), Fields.Time);
			mapFields.put(Fields.Time, field);
		}
		return field;
	}

	/**
	 * Returns the time_fmt field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldTimeFmt() {
		Field field = mapFields.get(Fields.TimeFmt);
		if (field == null) {
			field = DomainUtils.getTimeFmt(getSession(), Fields.TimeFmt);
			mapFields.put(Fields.TimeFmt, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldOpen() {
		Field field = mapFields.get(Fields.Open);
		if (field == null) {
			field = DomainUtils.getOpen(getSession(), Fields.Open);
			mapFields.put(Fields.Open, field);
		}
		return field;
	}

	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldHigh() {
		Field field = mapFields.get(Fields.High);
		if (field == null) {
			field = DomainUtils.getOpen(getSession(), Fields.High);
			mapFields.put(Fields.High, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldLow() {
		Field field = mapFields.get(Fields.Low);
		if (field == null) {
			field = DomainUtils.getOpen(getSession(), Fields.Low);
			mapFields.put(Fields.Low, field);
		}
		return field;
	}

	/**
	 * Returns the close field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldClose() {
		Field field = mapFields.get(Fields.Close);
		if (field == null) {
			field = DomainUtils.getOpen(getSession(), Fields.Close);
			mapFields.put(Fields.Close, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldMinMax() {
		Field field = mapFields.get(Fields.MinMax);
		if (field == null) {
			field = DomainUtils.getMinMax(getSession(), Fields.MinMax);
			field.setHeader("Min/Max");
			mapFields.put(Fields.MinMax, field);
		}
		return field;
	}

	/**
	 * Returns the name field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldName() {
		Field field = mapFields.get(Fields.Name);
		if (field == null) {
			field = DomainUtils.getName(getSession(), Fields.Name);
			field.setHeader("Field name");
			mapFields.put(Fields.Name, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldPeriod() {
		Field field = mapFields.get(Fields.Period);
		if (field == null) {
			field = DomainUtils.getPeriod(getSession(), Fields.Period);
			field.setHeader("Period");
			mapFields.put(Fields.Period, field);
		}
		return field;
	}

	/**
	 * Returns the value field.
	 * 
	 * @return The field.
	 */
	protected Field getFieldValue() {
		Field field = mapFields.get(Fields.Value);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), Fields.Value);
			field.setHeader("Value");
			mapFields.put(Fields.Value, field);
		}
		return field;
	}

	/**
	 * Returns the spread field between two averages.
	 * 
	 * @param averageFast Fast average.
	 * @param averageSlow Slow average.
	 * @return The field.
	 */
	protected Field getFieldSpread(Average averageFast, Average averageSlow) {
		String name = "spread_" + averageFast.getPeriod() + "_" + averageSlow.getPeriod();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + averageFast.getPeriod() + "-" + averageSlow.getPeriod();
			String label = "Spread " + averageFast.getPeriod() + " - " + averageSlow.getPeriod();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setAverageFast(field, averageFast);
			setAverageSlow(field, averageSlow);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field between two averages.
	 * 
	 * @param spread Spread.
	 * @param averageSlow Slow average.
	 * @return The field.
	 */
	protected Field getFieldSpread(Spread spread) {
		Average averageFast = spread.getFastAverage();
		Average averageSlow = spread.getSlowAverage();
		String name = "spread_" + averageFast.getPeriod() + "_" + averageSlow.getPeriod();
		Field field = mapFields.get(name);
		if (field == null) {
			NormalizedStateValueDescriptor normalizer = spread.getNormalizer();
			String header = "Spread-" + averageFast.getPeriod() + "-" + averageSlow.getPeriod();
			String label = "Spread " + averageFast.getPeriod() + " - " + averageSlow.getPeriod();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setAverageFast(field, averageFast);
			setAverageSlow(field, averageSlow);
			setNormalizer(field, normalizer);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the speed field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	protected Field getFieldSpeed(Average average) {
		String name = "speed_" + average.getPeriod();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Speed-" + average.getPeriod();
			String label = "Speed " + average.getPeriod();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}
	/**
	 * Returns the speed field for the average.
	 * 
	 * @param speed The speed definition.
	 * @return The field.
	 */
	protected Field getFieldSpeed(Speed speed) {
		Average average = speed.getAverage();
		String name = "speed_" + average.getPeriod();
		Field field = mapFields.get(name);
		if (field == null) {
			NormalizedStateValueDescriptor normalizer = speed.getNormalizer();
			String header = "Speed-" + average.getPeriod();
			String label = "Speed " + average.getPeriod();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setAverage(field, average);
			setNormalizer(field, normalizer);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKey() {
		String name = Fields.Key;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Key";
			String label = "Key";
			field = new Field();
			field.setSession(getSession());
			field.setName(name);
			field.setHeader(header);
			field.setLabel(label);
			field.setType(Types.String);
			field.setLength(100);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the server.
	 * 
	 * @return The server.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Returns the list of average fields.
	 * 
	 * @return The list of average fields.
	 */
	public List<Field> getAverageFields() {
		if (averageFields == null) {
			averageFields = new ArrayList<>();
			List<Average> averages = getConfiguration().getAverages();
			for (Average average : averages) {
				averageFields.add(getFieldAverage(average));
			}
		}
		return averageFields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @return The list of spread fields.
	 */
	public List<Field> getSpreadFields() {
		if (spreadFields == null) {
			spreadFields = new ArrayList<>();
			List<Spread> spreads = getConfiguration().getSpreads();
			for (Spread spread : spreads) {
				spreadFields.add(getFieldSpread(spread));
			}
		}
		return spreadFields;
	}

	/**
	 * Returns the list of speed fields.
	 * 
	 * @return The list of speed fields.
	 */
	public List<Field> getSpeedFields() {
		if (speedFields == null) {
			speedFields = new ArrayList<>();
			List<Speed> speeds = getConfiguration().getSpeeds();
			for (Speed speed : speeds) {
				speedFields.add(getFieldSpeed(speed));
			}
		}
		return speedFields;
	}

	/**
	 * Returns the spread fields for high, low and close to the fast average.
	 * 
	 * @return The fields.
	 */
	public List<Field> getSpreadFieldsFastAverage() {
		Average average = getAverages().get(0);
		List<Field> fields = new ArrayList<>();
		fields.add(getSpreadField(getFieldHigh(), average));
		fields.add(getSpreadField(getFieldLow(), average));
		fields.add(getSpreadField(getFieldClose(), average));
		return fields;
	}

	/**
	 * Returns the spread field for a field (high, low, close) and an average.
	 * 
	 * @param sourceField The field.
	 * @param average The average.
	 * @return The field.
	 */
	private Field getSpreadField(Field sourceField, Average average) {
		String name = "spread_" + sourceField.getName() + "_" + average.getPeriod();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + sourceField.getName() + "-" + average.getPeriod();
			String label = "Spread " + sourceField.getName() + " - " + average.getPeriod();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setSourceField(field, sourceField);
			setAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	private Field getFieldAverage(Average average) {
		String name = average.getName();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = average.getHeader();
			String label = average.getLabel();
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
			setAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the list of field to calculate maximum-minimum values and normalized values.
	 * 
	 * @return The list of fieldd names.
	 */
	public List<Field> getFieldsToCalculateRanges() {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getSpreadFieldsFastAverage());
		fields.addAll(getSpreadFields());
		fields.addAll(getSpeedFields());
		return fields;
	}

	/**
	 * Configure the persistor.
	 * 
	 * @param persistor The persistor.
	 * @param raw A boolean that indicates that calculated (spread and speed) values are raw (not normalized).
	 */
	protected void configure(Persistor persistor, boolean raw) {
		configurePip(persistor, Fields.Open);
		configurePip(persistor, Fields.High);
		configurePip(persistor, Fields.Low);
		configurePip(persistor, Fields.Close);
		configureTimeFmt(persistor);
		configureAverageFields(persistor);

		int scale = (raw ? 10 : 4);
		configureSpreadFieldsFastAvg(persistor, scale);
		configureSpreadFields(persistor, scale);
		configureSpeedFields(persistor, scale);
	}

	/**
	 * Configure the field as pip field, if the field exists.
	 * 
	 * @param persistor The persistor.
	 * @param name The field name.
	 */
	private void configurePip(Persistor persistor, String name) {
		if (persistor.getField(name) != null) {
			persistor.getField(name).setFormatter(new PipValue(getSession(), getInstrument()));
		}
	}

	/**
	 * Configure the average fields.
	 * 
	 * @param persistor The persistor.
	 */
	private void configureAverageFields(Persistor persistor) {
		List<Field> fields = getAverageFields();
		for (Field field : fields) {
			configurePip(persistor, field.getName());
		}
	}

	/**
	 * Configure the spread fields high, low, close with the scale.
	 * 
	 * @param persistor The persistor.
	 * @param scale The scale.
	 */
	private void configureSpreadFieldsFastAvg(Persistor persistor, int scale) {
		List<Field> fields = getSpreadFieldsFastAverage();
		for (Field field : fields) {
			configureValue(persistor, field, scale);
		}
	}

	/**
	 * Configure the spread fields with the scale.
	 * 
	 * @param persistor The persistor.
	 * @param scale The scale.
	 */
	private void configureSpreadFields(Persistor persistor, int scale) {
		List<Field> fields = getSpreadFields();
		for (Field field : fields) {
			configureValue(persistor, field, scale);
		}
	}

	/**
	 * Configure the speed fields with the scale.
	 * 
	 * @param persistor The persistor.
	 * @param scale The scale.
	 */
	private void configureSpeedFields(Persistor persistor, int scale) {
		List<Field> fields = getSpeedFields();
		for (Field field : fields) {
			configureValue(persistor, field, scale);
		}
	}

	/**
	 * Configure a field double value with a display scale.
	 * 
	 * @param persistor The persistor.
	 * @param field The field.
	 * @param scale The scale.
	 */
	private void configureValue(Persistor persistor, Field field, int scale) {
		String name = field.getName();
		if (persistor.getField(name) != null) {
			persistor.getField(name).setFormatter(new DataValue(getSession(), scale));
		}
	}

	/**
	 * Configure the time formatted field.
	 * 
	 * @param persistor The persistor.
	 */
	private void configureTimeFmt(Persistor persistor) {
		if (persistor.getField(Fields.TimeFmt) != null) {
			TimeFmtValue timeFmt = new TimeFmtValue(getPeriod().getUnit());
			persistor.getField(Fields.TimeFmt).setFormatter(timeFmt);
			persistor.getField(Fields.TimeFmt).setCalculator(timeFmt);
		}
	}

	/**
	 * Returns the main plot data for the price and averages.
	 * 
	 * @param dataList The source list.
	 * @return The plot data.
	 */
	protected PlotData getPlotDataMain(PersistorDataList dataList) {

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
		List<Field> averageFields = getAverageFields();
		for (Field field : averageFields) {
			String name = field.getName();
			String label = field.getLabel();
			String header = field.getHeader();
			int index = dataList.getDataIndex(name);

			// Output info.
			info.addOutput(name, header, index, label);

			// Plotter.
			LinePlotter plotterAvg = new LinePlotter();
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
	 * @param sourceList The source list.
	 * @param fields The list of fields.
	 * @return The plot data.
	 */
	protected PlotData getPlotData(PersistorDataList sourceList, List<Field> fields) {

		// Data info.
		DataInfo info = new DataInfo(getSession());
		info.setInstrument(getInstrument());
		info.setName(getInstrument().getId());
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
			LinePlotter plotter = new LinePlotter();
			plotter.setIndex(index);
			dataList.addDataPlotter(plotter);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);

		return plotData;
	}

	/**
	 * Returns the table name.
	 * 
	 * @return The table name.
	 */
	protected String getTableName() {
		return Names.getName(getInstrument(), getPeriod(), getId().toLowerCase());
	}

	/**
	 * Returns the definition of source and normalized continuous and discrete statistics.
	 * 
	 * @return The results table.
	 */
	protected Table getTableForSourceAndNormalizedStatistics() {

		Table table = new Table();

		table.setName(getTableName());
		table.setSchema(Names.getSchema(getServer()));

		// Index, time and price fields.
		table.addField(getFieldIndex());
		table.addField(getFieldTime());
		table.addField(getFieldTimeFmt());
		table.addField(getFieldOpen());
		table.addField(getFieldHigh());
		table.addField(getFieldLow());
		table.addField(getFieldClose());

		// Averages fields.
		table.addFields(getAverageFields());

		// Price spreads over the first (fastest) average.
		table.addFields(getSpreadFieldsFastAverage());

		// Spreads between averages.
		table.addFields(getSpreadFields());

		// Speed (tangent) of averages.
		table.addFields(getSpeedFields());

		// Primary key on Time.
		getFieldTime().setPrimaryKey(true);

		// Unique index on Index.
		Index index = new Index();
		index.add(getFieldIndex());
		index.setUnique(true);
		table.addIndex(index);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Setup from the table definition.
	 */
	protected void setupFromTable() {
		if (getAverages().isEmpty()) {
			throw new IllegalStateException();
		}
		clear();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			Field field = table.getField(i);
			String name = field.getName();
			String description = field.getDisplayDescription();
			add(new Output(name, description, Types.Double));
		}
	}
}
