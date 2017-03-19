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
import java.util.List;

import javax.swing.Action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Domains;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.tables.TablePatterns;
import com.qtplaf.platform.database.tables.TableRanges;
import com.qtplaf.platform.database.tables.TableStates;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionNavigateStatistics;
import com.qtplaf.platform.statistics.averages.task.TaskNormalizes;
import com.qtplaf.platform.statistics.averages.task.TaskPatterns;
import com.qtplaf.platform.statistics.averages.task.TaskRanges;
import com.qtplaf.platform.statistics.averages.task.TaskStates;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * States based on averages.
 *
 * @author Miquel Sas
 */
public class States extends Averages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Field calculator to view the normal distribution index.
	 */
	class NormalIndex implements Calculator {

		/** Stddev times. */
		private double stddevs;

		NormalIndex(double stddevs) {
			this.stddevs = stddevs;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Value getValue(Record record) {
			List<Double> values = (List<Double>) record.getProperty("values");
			Value normalIndex = (Value) record.getProperty(stddevs);
			if (normalIndex == null) {
				double average = record.getValue(Fields.Average).getDouble();
				double stddev = record.getValue(Fields.StdDev).getDouble();
				double min = average - (stddev * stddevs);
				double max = average + (stddev * stddevs);
				int count = 0;
				for (Double value : values) {
					if (value >= min && value <= max) {
						count++;
					}
				}
				double index = 0;
				if (!values.isEmpty()) {
					index = 100.0 * Double.valueOf(count) / Double.valueOf(values.size());
				}
				normalIndex = new Value(index);
				record.setProperty(stddevs, normalIndex);
			}
			return normalIndex;
		}

	}

	/**
	 * Browse states.
	 */
	class ActionBrowseStates extends ActionBrowse {
		ActionBrowseStates(States states) {
			super(states, getTableStates().getName());
		}

		@Override
		public RecordSet getRecordSet() {
			DataPersistor persistor = new DataPersistor(getTableStates().getPersistor());
			return new DataRecordSet(persistor);
		}
	}

	/**
	 * Browse ranges.
	 */
	class ActionBrowseRanges extends ActionBrowse {
		ActionBrowseRanges(States states) {
			super(states, getTableStates().getName());
		}

		@Override
		public RecordSet getRecordSet() {
			return getRecordSetRanges(true);
		}
	}

	/** Table states. */
	private Table tableStates;
	/** Table ranges. */
	private Table tableRanges;
	/** Table patterns. */
	private Table tablePatterns;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public States(Session session) {
		super(session);
	}

	/**
	 * Returns the persistor data list for this states statistics.
	 * 
	 * @return The persistor data list.
	 */
	public PersistorDataList getDataListStates() {

		DataPersistor persistor = new DataPersistor(getTableStates().getPersistor());

		DataInfo info = new DataInfo(getSession());
		info.setName("States");
		info.setDescription("States data info");
		info.setInstrument(getInstrument());
		info.setPeriod(getPeriod());
		DataPersistor.setDataInfoOutput(info, persistor);

		return new PersistorDataList(getSession(), info, persistor);
	}

	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {

		List<Action> actions = new ArrayList<>();

		// Calculate states.
		ActionCalculate actionCalcStates = new ActionCalculate(this, new TaskStates(this));
		ActionUtils.setName(actionCalcStates, "Calculate states");
		ActionUtils.setShortDescription(actionCalcStates, "Calculate states from scratch");
		ActionUtils.setActionGroup(actionCalcStates, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcStates);

		// Calculate ranges.
		ActionCalculate actionCalcRanges = new ActionCalculate(this, new TaskRanges(this));
		ActionUtils.setName(actionCalcRanges, "Calculate ranges");
		ActionUtils.setShortDescription(actionCalcRanges, "Calculate states ranges");
		ActionUtils.setActionGroup(actionCalcRanges, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcRanges);

		// Normalize values.
		ActionCalculate actionCalcNorm = new ActionCalculate(this, new TaskNormalizes(this));
		ActionUtils.setName(actionCalcNorm, "Normalize values");
		ActionUtils.setShortDescription(actionCalcNorm, "Calculate normalized values");
		ActionUtils.setActionGroup(actionCalcNorm, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcNorm);

		// Calculate patterns.
		ActionCalculate actionCalcPatterns = new ActionCalculate(this, new TaskPatterns(this));
		ActionUtils.setName(actionCalcPatterns, "Calculate patterns");
		ActionUtils.setShortDescription(actionCalcPatterns, "Calculate states patterns");
		ActionUtils.setActionGroup(actionCalcPatterns, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcPatterns);

		// Browse states.
		ActionBrowseStates actionBrowse = new ActionBrowseStates(this);
		ActionUtils.setName(actionBrowse, "Browse states");
		ActionUtils.setShortDescription(actionBrowse, "Browse states data");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10100));
		actions.add(actionBrowse);

		// Browse ranges.
		ActionBrowseRanges actionRanges = new ActionBrowseRanges(this);
		ActionUtils.setName(actionRanges, "Browse ranges");
		ActionUtils.setShortDescription(actionRanges, "Browse states ranges");
		ActionUtils.setActionGroup(actionRanges, new ActionGroup("Browse", 10100));
		actions.add(actionRanges);

		// Standard navigate.
		actions.add(new ActionNavigateStatistics(this));

		return actions;
	}

	/**
	 * Returns the list of tasks to calculate the results. Tasks are expected to be executed sequentially.
	 * 
	 * @return The list of tasks.
	 */
	@Override
	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(new TaskStates(this));
		tasks.add(new TaskRanges(this));
		tasks.add(new TaskNormalizes(this));
		tasks.add(new TaskPatterns(this));
		return tasks;
	}

	/**
	 * Returns the list of tables where statistic results are stored.
	 * 
	 * @return The list of result tables.
	 */
	@Override
	public List<Table> getTables() {
		List<Table> tables = new ArrayList<>();
		tables.add(getTableStates());
		tables.add(getTableRanges());
		tables.add(getTablePatterns());
		return tables;
	}

	/**
	 * Returns the states table.
	 * 
	 * @return The states table.
	 */
	public Table getTableStates() {
		if (tableStates == null) {
			tableStates = new TableStates(getSession(), this);
		}
		return tableStates;
	}

	/**
	 * Returns the ranges table.
	 * 
	 * @return The ranges table.
	 */
	public Table getTableRanges() {
		if (tableRanges == null) {
			tableRanges = new TableRanges(getSession(), this);
		}
		return tableRanges;
	}

	/**
	 * Returns the patterns table.
	 * 
	 * @return The patterns table.
	 */
	public Table getTablePatterns() {
		if (tablePatterns == null) {
			tablePatterns = new TablePatterns(getSession(), this);
		}
		return tablePatterns;
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @param includePeriod A boolean that indicates whether the period should be included.
	 * @return The recordset to browse the statistic results.
	 */
	public RecordSet getRecordSetRanges(boolean includePeriod) {

		Table table = getTableRanges();

		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Group by fields
		view.addField(table.getField(Fields.Name));
		view.addField(table.getField(Fields.MinMax));
		if (includePeriod) {
			view.addField(table.getField(Fields.Period));
		}

		// Aggregate function count.
		Field count = Domains.getInteger(getSession(), Fields.Count);
		count.setPersistent(false);
		count.setFunction("count(*)");
		view.addField(count);

		// Aggregate function minimum.
		Field minimum = Domains.getDouble(getSession(), Fields.Minimum, "Minimum", "Minimum value");
		minimum.setPersistent(false);
		minimum.setFunction("min(value)");
		minimum.setFormatter(new DataValue(getSession(), 10));
		view.addField(minimum);

		// Aggregate function maximum.
		Field maximum = Domains.getDouble(getSession(), Fields.Maximum, "Maximum", "Maximum value");
		maximum.setPersistent(false);
		maximum.setFunction("max(value)");
		maximum.setFormatter(new DataValue(getSession(), 10));
		view.addField(maximum);

		// Aggregate function average.
		Field average = Domains.getDouble(getSession(), Fields.Average);
		average.setPersistent(false);
		average.setFunction("avg(value)");
		average.setFormatter(new DataValue(getSession(), 10));
		view.addField(average);

		// Aggregate function stddev.
		Field stddev = Domains.getDouble(getSession(), Fields.StdDev, "Std Dev", "Standard deviation value");
		stddev.setPersistent(false);
		stddev.setFunction("stddev(value)");
		stddev.setFormatter(new DataValue(getSession(), 10));
		view.addField(stddev);

		// Index +- n * stddev
		Field avgStd1 = Domains.getDouble(getSession(), "avgstd_1", "AvgStd_1", "Avg/1 Stddev value");
		avgStd1.setPersistent(false);
		avgStd1.setCalculator(new NormalIndex(1));
		avgStd1.setFormatter(new DataValue(getSession(), 4));
		view.addField(avgStd1);

		Field avgStd2 = Domains.getDouble(getSession(), "avgstd_2", "AvgStd_2", "Avg/2 Stddev value");
		avgStd2.setPersistent(false);
		avgStd2.setCalculator(new NormalIndex(2));
		avgStd2.setFormatter(new DataValue(getSession(), 4));
		view.addField(avgStd2);

		// Group by.
		view.addGroupBy(table.getField(Fields.Name));
		view.addGroupBy(table.getField(Fields.MinMax));
		if (includePeriod) {
			view.addGroupBy(table.getField(Fields.Period));
		}

		// Order by.
		view.addOrderBy(table.getField(Fields.Name));
		view.addOrderBy(table.getField(Fields.MinMax));
		if (includePeriod) {
			view.addOrderBy(table.getField(Fields.Period));
		}

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));

		RecordSet recordSet = null;
		try {
			recordSet = view.getPersistor().select(null);
			Persistor persistor = PersistorUtils.getPersistor(view.getMasterTable().getSimpleView());
			for (int i = 0; i < recordSet.size(); i++) {
				setValues(persistor, recordSet.get(i), includePeriod);
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		}

		return recordSet;
	}

	/**
	 * Set the list of values of the key.
	 * 
	 * @param persistor The persistor.
	 * @param record The record.
	 * @param includePeriod A boolean that indicates whether the period should be included.
	 * @throws PersistorException
	 */
	private void setValues(Persistor persistor, Record record, boolean includePeriod) throws PersistorException {

		Field fName = persistor.getField(Fields.Name);
		Field fMinMax = persistor.getField(Fields.MinMax);
		Field fPeriod = persistor.getField(Fields.Period);
		Field fValue = persistor.getField(Fields.Value);

		Value vName = record.getValue(Fields.Name);
		Value vMinMax = record.getValue(Fields.MinMax);
		Value vPeriod = record.getValue(Fields.Period);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fName, vName));
		criteria.add(Condition.fieldEQ(fMinMax, vMinMax));
		if (includePeriod) {
			criteria.add(Condition.fieldEQ(fPeriod, vPeriod));
		}

		List<Double> values = new ArrayList<>();
		RecordIterator iter = persistor.iterator(criteria);
		while (iter.hasNext()) {
			Record rc = iter.next();
			double value = rc.getValue(fValue.getName()).getDouble();
			values.add(value);
		}
		iter.close();

		record.setProperty("values", values);
	}
}
