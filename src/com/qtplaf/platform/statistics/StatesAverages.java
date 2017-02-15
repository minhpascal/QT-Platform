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
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.PipValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.util.DomainUtils;

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

		/** Index in data list persistor. */
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
	 * The list of averages definitions.
	 */
	private List<Average> averages = new ArrayList<Average>();

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
	 * Add a smoothed simple moving average
	 * 
	 * @param period
	 * @param smooths Smoothing periods.
	 */
	public void addAverage(int period, int... smooths) {
		addAverage(new Average(period, smooths));
	}

	/**
	 * Add an average
	 * 
	 * @param average The average.
	 */
	public void addAverage(Average average) {
		averages.add(average);
		ListUtils.sort(averages);
		setup();
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
		return averages;
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
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < getAverages().size(); i++) {
			fields.add(getAverageField(getAverages().get(i)));
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @return The list of spread fields.
	 */
	public List<Field> getSpreadFields() {
		List<Field> fields = new ArrayList<>();
		for (int i = 1; i < getAverages().size(); i++) {
			Average averageFast = getAverages().get(i - 1);
			Average averageSlow = getAverages().get(i);
			fields.add(getSpreadField(averageFast, averageSlow));
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields.
	 * 
	 * @return The list of speed fields.
	 */
	public List<Field> getSpeedFields() {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < getAverages().size(); i++) {
			fields.add(getSpeedField(getAverages().get(i)));
		}
		return fields;
	}

	/**
	 * Returns the speed field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	private Field getSpeedField(Average average) {
		String name = Average.getSpeedName(average);
		String header = Average.getSpeedHeader(average);
		String label = Average.getSpeedLabel(average);
		return DomainUtils.getDouble(getSession(), name, name, header, label, label);
	}

	/**
	 * Returns the spread field between two averages.
	 * 
	 * @param averageFast Fast average.
	 * @param averageSlow Slow average.
	 * @return The field.
	 */
	private Field getSpreadField(Average averageFast, Average averageSlow) {
		String name = Average.getSpreadName(averageFast, averageSlow);
		String header = Average.getSpreadHeader(averageFast, averageSlow);
		String label = Average.getSpreadLabel(averageFast, averageSlow);
		return DomainUtils.getDouble(getSession(), name, name, header, label, label);
	}

	/**
	 * Returns the spread fields for high, low and close to the fast average.
	 * 
	 * @return The fields.
	 */
	public List<Field> getSpreadFieldsFastAverage() {
		Average average = getAverages().get(0);
		List<Field> fields = new ArrayList<>();
		fields.add(getSpreadField(Fields.High, average));
		fields.add(getSpreadField(Fields.Low, average));
		fields.add(getSpreadField(Fields.Close, average));
		return fields;
	}

	/**
	 * Returns the spread field for a field name and an average.
	 * 
	 * @param fieldName The field name.
	 * @param average The average.
	 * @return The field.
	 */
	private Field getSpreadField(String fieldName, Average average) {
		String name = Average.getSpreadName(fieldName, average);
		String header = Average.getSpreadHeader(fieldName, average);
		String label = Average.getSpreadLabel(fieldName, average);
		return DomainUtils.getDouble(getSession(), name, name, header, label, label);
	}

	/**
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	private Field getAverageField(Average average) {
		String name = Average.getAverageName(average);
		String header = Average.getAverageHeader(average);
		String label = Average.getAverageLabel(average);
		return DomainUtils.getDouble(getSession(), name, name, header, label, label);
	}

	/**
	 * Returns the list of field names to calculate maximum-minimum values.
	 * 
	 * @return The list of fieldd names.
	 */
	public List<String> getNamesToCalculateRanges() {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getSpreadFieldsFastAverage());
		fields.addAll(getSpreadFields());
		fields.addAll(getSpeedFields());
		List<String> names = new ArrayList<>();
		for (Field field : fields) {
			names.add(field.getName());
		}
		return names;
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
		info.setDataType(DataType.Indicator);
		info.setInstrument(getInstrument());
		info.setName(getInstrument().getId());
		info.setDescription(getInstrument().getDescription());
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

		// Line plotter for each average. Skip the rane percentage.
		for (int i = 0; i < getAverages().size(); i++) {
			Average average = getAverages().get(i);
			String name = Average.getAverageName(average);
			String label = Average.getAverageLabel(average);
			String header = Average.getAverageHeader(average);
			int index = dataList.getDataIndex(name);

			// Output info.
			info.addOutput(label, header, index, label);

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
		info.setDataType(DataType.Indicator);
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
			info.addOutput(label, header, index, label);

			// Plotter.
			LinePlotter plotter = new LinePlotter();
			plotter.setIndex(index);
			dataList.addDataPlotter(plotter);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);

		return plotData;
	}
}
