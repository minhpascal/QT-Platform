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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.rlearning.function.Normalizer;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.chart.drawings.VerticalArea;
import com.qtplaf.library.trading.chart.plotter.data.BufferedLinePlotter;
import com.qtplaf.library.trading.chart.plotter.data.CandlestickPlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.TickValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.statistics.TickerStatistics;
import com.qtplaf.platform.statistics.action.ActionNavigateChart;
import com.qtplaf.platform.statistics.action.PlotDataConfigurator;
import com.qtplaf.platform.statistics.action.RecordSetProvider;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Calculation;
import com.qtplaf.platform.statistics.averages.configuration.Configuration;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.FieldUtils;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class Averages extends TickerStatistics {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Move the chart to the center the selection.
	 */
	class ActionClearDrawings extends AbstractAction {

		ActionClearDrawings() {
			ActionUtils.setName(this, "Clear drawings");
			ActionUtils.setShortDescription(this, "Clear all added drawings.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JChart chart = ActionUtils.getChart(this);
			for (int i = 0; i < chart.getChartCount(); i++) {
				chart.getChartContainer(i).getPlotData().getDrawings().clear();
			}
			chart.repaint();
		}
	}


	/**
	 * Move the chart to the center the selection.
	 */
	class ActionSelection extends AbstractAction {

		ActionSelection() {
			ActionUtils.setName(this, "Move and center to selected indexes");
			ActionUtils.setShortDescription(this, "Move the chart and center it to the selected indexes.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTableRecord tableRecord = ActionUtils.getTableRecordPanel(this).getTableRecord();
			List<Record> records = tableRecord.getSelectedRecords();
			if (records.isEmpty()) {
				return;
			}
			int startIndex = records.get(0).getValue(getFieldDefIndex().getName()).getInteger();
			int endIndex = records.get(records.size() - 1).getValue(getFieldDefIndex().getName()).getInteger();
			JChart chart = ActionUtils.getChart(this);
			for (int i = 0; i < chart.getChartCount(); i++) {
				VerticalArea vertBand = new VerticalArea(startIndex, endIndex);
				chart.getChartContainer(i).getPlotData().addDrawing(vertBand);
			}
			PlotData plotData = chart.getChartContainer(0).getPlotData();
			plotData.center(startIndex, endIndex);
			chart.propagateFrameChanges(plotData);
		}
	}

	/**
	 * Recordset provider for standard navigation initial data throw the states table.
	 */
	class StdRecordSet implements RecordSetProvider {
		@Override
		public RecordSet getRecordSet() {
			DataPersistor persistor = new DataPersistor(getStates().getTable().getPersistor());
			return new DataRecordSet(persistor);
		}
	}

	/**
	 * Std plot data list provider.
	 */
	class StdPlotDataList implements PlotDataConfigurator {
		@Override
		public void configureChart(JChart chart) {
			PersistorDataList dataList = getStates().getDataListStates();
			dataList.setCacheSize(-1);
			dataList.setPageSize(1000);

			chart.addPlotData(getPlotDataMain(dataList), true);
			chart.addPlotData(getPlotData("Spreads normalized", dataList, getFieldListSpreads(Suffix.nrm)), false);
			chart.addPlotData(getPlotData("Speeds normalized", dataList, getFieldListSpeeds(Suffix.nrm)), false);
			chart.addPlotData(getPlotData("Spreads discrete", dataList, getFieldListSpreads(Suffix.dsc)), false);
			chart.addPlotData(getPlotData("Speeds discrete", dataList, getFieldListSpeeds(Suffix.dsc)), false);
			chart.addPlotData(getPlotData("Spreads raw", dataList, getFieldListSpreads(Suffix.raw)), false);
			chart.addPlotData(getPlotData("Speeds raw", dataList, getFieldListSpeeds(Suffix.raw)), false);
		}

	}

	/** The configuration. */
	private Configuration configuration;

	/** Tick value formatter. */
	private TickValue tickFormatter;
	/** Time formatter and calculator. */
	private TimeFmtValue timeFormatter;
	/** Value formatter for raw spread and speed values. */
	private DataValue valueFormatterRaw;
	/** Value formatter for normalized continuous spread and speed values. */
	private DataValue valueFormatterNormCont;
	/** Value formatter for normalized discrete spread and speed values. */
	private DataValue valueFormatterNormDisc;

	/** Map of fields. */
	private Map<String, Field> mapFields = new HashMap<>();
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
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {
		return null;
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
	 * Returns the tick value formatter.
	 * 
	 * @return The tick value formatter.
	 */
	protected TickValue getTickFormatter() {
		if (tickFormatter == null) {
			tickFormatter = new TickValue(getSession(), getInstrument());
		}
		return tickFormatter;
	}

	/**
	 * Returns the time formatter and calculator.
	 * 
	 * @return The time formatter and calculator.
	 */
	protected TimeFmtValue getTimeFormatter() {
		if (timeFormatter == null) {
			timeFormatter = new TimeFmtValue(getPeriod().getUnit());
		}
		return timeFormatter;
	}

	/**
	 * Returns the value formatter for raw spread and speed values.
	 * 
	 * @return The value formatter for raw spread and speed values.
	 */
	protected DataValue getValueFormatterRaw() {
		if (valueFormatterRaw == null) {
			valueFormatterRaw = new DataValue(getSession(), 10);
		}
		return valueFormatterRaw;
	}

	/**
	 * Returns the value formatter for normalized continuous spread and speed values.
	 * 
	 * @return The value formatter for normalized continuous spread and speed values.
	 */
	protected DataValue getValueFormatterNormCont() {
		if (valueFormatterNormCont == null) {
			valueFormatterNormCont = new DataValue(getSession(), 4);
		}
		return valueFormatterNormCont;
	}

	/**
	 * Returns the value formatter for normalized discrete spread and speed values.
	 * 
	 * @return The value formatter for normalized discrete spread and speed values.
	 */
	protected DataValue getValueFormatterNormDisc() {
		if (valueFormatterNormDisc == null) {
			valueFormatterNormDisc = new DataValue(getSession(), 4);
		}
		return valueFormatterNormDisc;
	}

	/**
	 * Returnas the appropriate value formatter.
	 * 
	 * @param suffix The type suffix.
	 * @return The formatter.
	 */
	protected DataValue getValueFormatter(Suffix suffix) {
		if (suffix.equals(Suffix.raw)) {
			return getValueFormatterRaw();
		}
		if (suffix.equals(Suffix.nrm)) {
			return getValueFormatterNormCont();
		}
		if (suffix.equals(Suffix.dsc)) {
			return getValueFormatterNormDisc();
		}
		if (suffix.equals(Suffix.in)) {
			return getValueFormatterNormDisc();
		}
		if (suffix.equals(Suffix.out)) {
			return getValueFormatterNormDisc();
		}
		throw new IllegalArgumentException();
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
				fields.add(getFieldDefAverage(average));
			}
			mapFieldLists.put("averages", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of delta fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListDeltas(Suffix suffix) {
		String name = "deltas_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			fields.add(getFieldDefDelta(getFieldDefHigh(), suffix));
			fields.add(getFieldDefDelta(getFieldDefLow(), suffix));
			fields.add(getFieldDefDelta(getFieldDefClose(), suffix));
			mapFieldLists.put(name, fields);
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreads(Suffix suffix) {
		String name = "spreads_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Spread spread : getConfiguration().getSpreads()) {
				fields.add(getFieldDefSpread(spread, suffix));
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
	public List<Field> getFieldListCalculations(Suffix suffix) {
		String name = "calculations_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Calculation calculation : getConfiguration().getCalculations()) {
				fields.add(getFieldDefCalculation(calculation, suffix));
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
	public List<Field> getFieldListSpeeds(Suffix suffix) {
		String name = "speeds_" + suffix;
		List<Field> fields = mapFieldLists.get(name);
		if (fields == null) {
			fields = new ArrayList<>();
			for (Speed speed : getConfiguration().getSpeeds()) {
				fields.add(getFieldDefSpeed(speed, suffix));
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
			Spread spread = getPropertySpread(field);
			if (spread.isStateKey()) {
				keyFields.add(field);
			}
		}
		for (Field field : speedFields) {
			Speed speed = getPropertySpeed(field);
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
		fields.addAll(getFieldListDeltas(Suffix.raw));
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
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	public Field getFieldDefAverage(Average average) {
		String name = average.getName();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = average.getHeader();
			String label = average.getLabel();
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the close field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefClose() {
		String name = "close";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getClose(getSession(), name);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate count field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefCount() {
		String name = "count";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getInteger(getSession(), "count", "Count", "Count records for the same value");
			field.setFunction("count(*)");
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate maximum field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMaximum() {
		String name = "maximum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "maximum", "Maximum", "Maximum value");
			field.setFunction("max(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate minimum field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMinimum() {
		String name = "minimum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "minimum", "Minimum", "Minimum value");
			field.setFunction("min(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate average field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAverage() {
		String name = "average";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "average", "Average", "Average value");
			field.setFunction("avg(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAvgStd1() {
		String name = "avgstd1";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "avgstd_1", "AvgStd_1", "Avg/1 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(getSession(), 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAvgStd2() {
		String name = "avgstd2";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "avgstd_2", "AvgStd_2", "Avg/2 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(getSession(), 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStdDev() {
		String name = "stddev";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "stddev", "Std Dev", "Standard deviation value");
			field.setFunction("stddev(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefHigh() {
		String name = "high";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getHigh(getSession(), name);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndex() {
		String name = "index";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index";
			String label = "Index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexGroup() {
		String name = "index_group";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index group";
			String label = "Index group";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexInput() {
		String name = "index_in";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Input index";
			String label = "Input index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexOutput() {
		String name = "index_out";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Output index";
			String label = "Output index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStateInput() {
		String name = "state_in";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Input state key";
			String label = "Input state key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStateOutput() {
		String name = "state_out";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Output state key";
			String label = "Output state key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key state field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefState() {
		String name = "state";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "State key";
			String label = "State key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			field.setNullable(true);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefLow() {
		String name = "low";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getLow(getSession(), name);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMinMax() {
		String name = "min_max";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 3;
			String header = "Min/Max";
			String label = "Minimum/Maximum calculation";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns field definition for name field, a 40 chars string used as name of fields.
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefName() {
		String name = "name";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 40;
			String header = "Name";
			String label = "Value name";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefPeriod() {
		String name = "period";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Period";
			String label = "Period";
			field = DomainUtils.getInteger(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefOpen() {
		String name = "open";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Open";
			String label = "Open price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the speed field.
	 * 
	 * @param speed The speed definition.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldDefSpeed(Speed speed, Suffix suffix) {
		String name = speed.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = speed.getHeader() + "-" + suffix;
			String label = speed.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getValueFormatter(suffix));
			setPropertySpeed(field, speed);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field.
	 * 
	 * @param spread Spread.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldDefSpread(Spread spread, Suffix suffix) {
		String name = spread.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = spread.getHeader() + "-" + suffix;
			String label = spread.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getValueFormatter(suffix));
			setPropertySpread(field, spread);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the calculation field.
	 * 
	 * @param calculation The calculation
	 * @param suffix The suffix.
	 * @return The field.
	 */
	public Field getFieldDefCalculation(Calculation calculation, Suffix suffix) {
		String name = calculation.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = calculation.getHeader() + "-" + suffix;
			String label = calculation.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getValueFormatter(suffix));
			setPropertyCalculation(field, calculation);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the field definition for the delta high (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaHight() {
		return getFieldDefDelta(getFieldDefHigh(), Suffix.nrm);
	}

	/**
	 * Returns the field definition for the delta low (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaLow() {
		return getFieldDefDelta(getFieldDefLow(), Suffix.nrm);
	}

	/**
	 * Returns the field definition for the delta close (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaClose() {
		return getFieldDefDelta(getFieldDefClose(), Suffix.nrm);
	}

	/**
	 * Returns the field definition for a delta field.
	 * 
	 * @param source The source field.
	 * @param suffix The suffix (raw, nrm)
	 * @return The field definition.
	 */
	public Field getFieldDefDelta(Field source, Suffix suffix) {
		String name = "delta_" + source.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Delta-" + source.getName() + "-" + suffix;
			String label = "Delta " + source.getName() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			setPropertySourceField(field, source);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTime() {
		String name = "time";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTime(getSession(), name);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTimeFmt() {
		String name = "time_fmt";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTimeFmt(getSession(), name);
			field.setFormatter(getTimeFormatter());
			field.setCalculator(getTimeFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueClose() {
		String name = "value_close";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value close";
			String label = "Transition value close";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueHigh() {
		String name = "value_high";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value high";
			String label = "Transition value high";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueLow() {
		String name = "value_low";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value low";
			String label = "Transition value low";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getTickFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the value field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefValue() {
		String name = "value";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value";
			String label = "Value";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public Average getPropertyAverage(Field field) {
		return (Average) field.getProperty("average");
	}

	/**
	 * Sets the average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverage(Field field, Average average) {
		field.setProperty("average", average);
	}

	/**
	 * Returns the spread property of the field.
	 * 
	 * @param field The source field.
	 * @return The spread.
	 */
	public Spread getPropertySpread(Field field) {
		return (Spread) field.getProperty("spread");
	}

	/**
	 * Sets the spread property to the field.
	 * 
	 * @param field The field.
	 * @param spread The spread.
	 */
	private void setPropertySpread(Field field, Spread spread) {
		field.setProperty("spread", spread);
	}

	/**
	 * Returns the speed property of the field.
	 * 
	 * @param field The source field.
	 * @return The speed.
	 */
	public Speed getPropertySpeed(Field field) {
		return (Speed) field.getProperty("speed");
	}

	/**
	 * Sets the speed property to the field.
	 * 
	 * @param field The field.
	 * @param speed The speed.
	 */
	private void setPropertySpeed(Field field, Speed speed) {
		field.setProperty("speed", speed);
	}

	/**
	 * Return the source field property.
	 * 
	 * @param field The field.
	 * @return The source field.
	 */
	public Field getPropertySourceField(Field field) {
		return (Field) field.getProperty("source-field");
	}

	/**
	 * Sets the source field property for the field.
	 * 
	 * @param field The field.
	 * @param source The source field.
	 */
	private void setPropertySourceField(Field field, Field source) {
		field.setProperty("source-field", source);
	}

	/**
	 * Returns the calculation property of a field.
	 * 
	 * @param field The field.
	 * @return The normalizer.
	 */
	public Calculation getPropertyCalculation(Field field) {
		return (Calculation) field.getProperty("calculation");
	}

	/**
	 * Set the calculation property of the field.
	 * 
	 * @param field The field.
	 * @param normalizer The normalizer.
	 */
	private void setPropertyCalculation(Field field, Calculation calculation) {
		field.setProperty("calculation", calculation);
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
				String fieldName = record.getValue(ranges.getFieldDefName().getName()).getString();
				String minMax = record.getValue(ranges.getFieldDefMinMax().getName()).getString();
				double average = record.getValue(ranges.getFieldDefAverage().getName()).getDouble();
				double stddev = record.getValue(ranges.getFieldDefStdDev().getName()).getDouble();
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
	protected PlotData getPlotDataMain(PersistorDataList dataList) {

		// First data list: price and indicators.
		DataInfo info = dataList.getDataInfo();
		info.setInstrument(getInstrument());
		info.setName("OHLC");
		info.setDescription("OHLC values");
		info.setPeriod(getPeriod());

		// Candlestick on price: info
		info.addOutput("Open", "O", dataList.getDataIndex(getFieldDefOpen().getName()), "Open data value");
		info.addOutput("High", "H", dataList.getDataIndex(getFieldDefHigh().getName()), "High data value");
		info.addOutput("Low", "L", dataList.getDataIndex(getFieldDefLow().getName()), "Low data value");
		info.addOutput("Close", "C", dataList.getDataIndex(getFieldDefClose().getName()), "Close data value");

		// Candlestick on price: plotter.
		CandlestickPlotter plotterCandle = new CandlestickPlotter();
		plotterCandle.setIndexes(new int[] {
			dataList.getDataIndex(getFieldDefOpen().getName()),
			dataList.getDataIndex(getFieldDefHigh().getName()),
			dataList.getDataIndex(getFieldDefLow().getName()),
			dataList.getDataIndex(getFieldDefClose().getName()) });
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
	protected PlotData getPlotData(String dataName, PersistorDataList sourceList, List<Field> fields) {

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

	/**
	 * Returns the standard action to navigate the chart starting with a default data list.
	 * 
	 * @return The navigate action.
	 */
	public ActionNavigateChart getActionNavigateChart() {
		ActionNavigateChart actionChartNav = new ActionNavigateChart(this);
		actionChartNav.getChartNavigate().setTitle("Navigate chart on result data");
		actionChartNav.setPlotDataConfigurator(new StdPlotDataList());
		actionChartNav.setRecordSetProvider(new StdRecordSet());
		ActionUtils.setName(actionChartNav, "Navigate chart on result data");
		ActionUtils.setShortDescription(actionChartNav, "Show a standard chart with averages and normalized values");
		ActionUtils.setActionGroup(actionChartNav, new ActionGroup("Chart", 10200));

		actionChartNav.addAction(new ActionClearDrawings());
		actionChartNav.addAction(new ActionSelection());

		return actionChartNav;
	}
}
