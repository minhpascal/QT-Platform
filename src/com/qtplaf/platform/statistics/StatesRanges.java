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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.task.TaskStatesRanges;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Calculates the ranges min-max of the percentual values calculated in the correspondent <tt>StatesSource</tt>:
 * <ul>
 * <li>Range.</li>
 * <li>Price (high, low and close) spreads vs the fast average.</li>
 * <li>Averages spreads.</li>
 * <li>Averages speeds.</li>
 * </ul>
 * To consider a value a maximum (or minimum), it must be the maximum of a certain number of periods before and after.
 * For each average period, min-max values will be calculated for each target value.
 *
 * @author Miquel Sas
 */
public class StatesRanges extends StatesAverages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** Record property values. */
	private static final String Values = "values";

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
			List<Double> values = (List<Double>) record.getProperty(Values);
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
	 * The parent states source statistics.
	 */
	private StatesSource statesSource;

	/**
	 * Constructor.
	 * 
	 * @param statesSource The parent states source statistics.
	 */
	public StatesRanges(StatesSource statesSource) {
		super(
			statesSource.getSession(),
			statesSource.getServer(),
			statesSource.getInstrument(),
			statesSource.getPeriod());
		this.statesSource = statesSource;
	}

	/**
	 * Returns the parent states source statistics.
	 * 
	 * @return The parent states source statistics.
	 */
	public StatesSource getStatesSource() {
		return statesSource;
	}

	/**
	 * Setup after adding the averages.
	 */
	protected void setup() {
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		return new TaskStatesRanges(this);
	}

	/**
	 * Returns the table name.
	 * 
	 * @return The table name.
	 */
	public String getTableName() {
		return Names.getName(getInstrument(), getPeriod(), getId().toLowerCase());
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

		table.setName(getTableName());
		table.setSchema(Names.getSchema(getServer()));

		table.addField(DomainUtils.getName(getSession(), Fields.Name));
		table.addField(DomainUtils.getMinMax(getSession(), Fields.MinMax));
		table.addField(DomainUtils.getPeriod(getSession(), Fields.Period));
		table.addField(DomainUtils.getDouble(getSession(), Fields.Value));
		table.addField(DomainUtils.getIndex(getSession(), Fields.Index));
		table.addField(DomainUtils.getTime(getSession(), Fields.Time));

		table.getField(Fields.Name).setHeader("Field name");
		table.getField(Fields.MinMax).setHeader("Min/Max");
		table.getField(Fields.Period).setHeader("Period");

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(table.getField(Fields.Name));
		index.add(table.getField(Fields.MinMax));
		index.add(table.getField(Fields.Period));
		index.setUnique(false);
		table.addIndex(index);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));

		return table;
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	@Override
	public RecordSet getRecordSet() {
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
		view.addField(table.getField(Fields.Name));
		view.addField(table.getField(Fields.MinMax));
		if (includePeriod) {
			view.addField(table.getField(Fields.Period));
		}

		// Aggregate function count.
		Field count = new Field();
		count.setName(Fields.Count);
		count.setHeader("Count");
		count.setType(Types.Integer);
		count.setFunction("count(*)");
		view.addField(count);

		// Aggregate function min.
		Field minimum = new Field();
		minimum.setName(Fields.Minimum);
		minimum.setHeader("Minimum");
		minimum.setType(Types.Double);
		minimum.setFunction("min(value)");
		minimum.setFormatter(new DataValue(getSession(), 15));
		view.addField(minimum);

		// Aggregate function max.
		Field maximum = new Field();
		maximum.setName(Fields.Maximum);
		maximum.setHeader("Maximum");
		maximum.setType(Types.Double);
		maximum.setFunction("max(value)");
		maximum.setFormatter(new DataValue(getSession(), 15));
		view.addField(maximum);

		// Aggregate function avg.
		Field average = new Field();
		average.setName(Fields.Average);
		average.setHeader("Average");
		average.setType(Types.Double);
		average.setFunction("avg(value)");
		average.setFormatter(new DataValue(getSession(), 15));
		view.addField(average);

		// Aggregate function stddev.
		Field stddev = new Field();
		stddev.setName(Fields.StdDev);
		stddev.setHeader("Standard deviation");
		stddev.setType(Types.Double);
		stddev.setFunction("stddev(value)");
		stddev.setFormatter(new DataValue(getSession(), 20));
		view.addField(stddev);

		// Index +- n * stddev
		Field norm1 = new Field();
		norm1.setName(Fields.AvgStd_1);
		norm1.setHeader("AvgStd_1");
		norm1.setType(Types.Double);
		norm1.setPersistent(false);
		norm1.setFormatter(new DataValue(getSession(), 2));
		norm1.setCalculator(new NormalIndex(1));
		view.addField(norm1);

		Field norm2 = new Field();
		norm2.setName(Fields.AvgStd_2);
		norm2.setHeader("AvgStd_2");
		norm2.setType(Types.Double);
		norm2.setPersistent(false);
		norm2.setFormatter(new DataValue(getSession(), 2));
		norm2.setCalculator(new NormalIndex(2));
		view.addField(norm2);

		// Group by.
		view.addGroupBy(view.getField(Fields.Name));
		view.addGroupBy(view.getField(Fields.MinMax));
		if (includePeriod) {
			view.addGroupBy(view.getField(Fields.Period));
		}

		// Order by.
		view.addOrderBy(view.getField(Fields.Name));
		view.addOrderBy(view.getField(Fields.MinMax));
		if (includePeriod) {
			view.addOrderBy(view.getField(Fields.Period));
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
			double value = rc.getValue(Fields.Value).getDouble();
			values.add(value);
		}
		iter.close();

		record.setProperty(Values, values);
	}

	/**
	 * Returns the list of plot datas to configure a chart and show the statistics results.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		return null;
	}
}
