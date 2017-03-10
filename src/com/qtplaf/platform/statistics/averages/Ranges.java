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
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldCalculator;
import com.qtplaf.library.database.Index;
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
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionNavigateStatistics;
import com.qtplaf.platform.statistics.averages.task.TaskRanges;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Ranges for min-max values statistics.
 *
 * @author Miquel Sas
 */
public class Ranges extends Averages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Browse ranges.
	 */
	class ActionBrowseRanges extends ActionBrowse {
		ActionBrowseRanges(Ranges ranges) {
			super(ranges);
		}

		@Override
		public RecordSet getRecordSet() {
			return Ranges.this.getRecordSet();
		}
	}

	/**
	 * Field calculator to view the normal distribution index.
	 */
	class NormalIndex implements FieldCalculator {

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
				double average = record.getValue(getFields().getAverage().getName()).getDouble();
				double stddev = record.getValue(getFields().getStdDev().getName()).getDouble();
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
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Ranges(Session session) {
		super(session);
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {

		Table table = new Table();

		table.setName(Names.getTable(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Names.getSchema(getServer()));

		table.addField(getFields().getName());
		table.addField(getFields().getMinMax());
		table.addField(getFields().getPeriod());
		table.addField(getFields().getValue());
		table.addField(getFields().getIndex());
		table.addField(getFields().getTime());

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(getFields().getName());
		index.add(getFields().getMinMax());
		index.add(getFields().getPeriod());
		index.setUnique(false);
		table.addIndex(index);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
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

		Field fName = getFields().getName();
		Field fMinMax = getFields().getMinMax();
		Field fPeriod = getFields().getPeriod();
		Field fValue = getFields().getValue();

		Value vName = record.getValue(fName.getName());
		Value vMinMax = record.getValue(fMinMax.getName());
		Value vPeriod = record.getValue(fPeriod.getName());

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

	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {

		List<Action> actions = new ArrayList<>();

		// Standard browse.
		ActionBrowseRanges actionBrowse = new ActionBrowseRanges(this);
		ActionUtils.setName(actionBrowse, "Browse min/max values");
		ActionUtils.setShortDescription(actionBrowse, "Browse min/max, average and standard deviation values.");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10000));
		actions.add(actionBrowse);

		// Calculate ranges.
		ActionCalculate actionCalculate = new ActionCalculate(this, new TaskRanges(this));
		ActionUtils.setName(actionCalculate, "Calculate min/max ranges");
		ActionUtils.setShortDescription(actionCalculate, "Calculate min/max ranges for state fields to normalize.");
		ActionUtils.setActionGroup(actionCalculate, new ActionGroup("Calculate", 10000));
		actions.add(actionCalculate);

		actions.add(new ActionNavigateStatistics(this));
		return actions;
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	private RecordSet getRecordSet() {
		return getRecordSet(true);
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @param includePeriod A boolean that indicates whether the period should be included.
	 * @return The recordset to browse the statistic results.
	 */
	public RecordSet getRecordSet(boolean includePeriod) {

		Table table = getTable();

		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Group by fields
		view.addField(getFields().getName());
		view.addField(getFields().getMinMax());
		if (includePeriod) {
			view.addField(getFields().getPeriod());
		}

		// Aggregate function count.
		view.addField(getFields().getCount());

		// Aggregate function minimum.
		view.addField(getFields().getMinimum());

		// Aggregate function maximum.
		view.addField(getFields().getMaximum());

		// Aggregate function average.
		view.addField(getFields().getAverage());

		// Aggregate function stddev.
		view.addField(getFields().getStdDev());

		// Index +- n * stddev
		view.addField(getFields().getAvgStd1());
		view.addField(getFields().getAvgStd2());
		getFields().getAvgStd1().setCalculator(new NormalIndex(1));
		getFields().getAvgStd2().setCalculator(new NormalIndex(2));

		// Group by.
		view.addGroupBy(getFields().getName());
		view.addGroupBy(getFields().getMinMax());
		if (includePeriod) {
			view.addGroupBy(getFields().getPeriod());
		}

		// Order by.
		view.addOrderBy(getFields().getName());
		view.addOrderBy(getFields().getMinMax());
		if (includePeriod) {
			view.addOrderBy(getFields().getPeriod());
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
}
