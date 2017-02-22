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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
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
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.util.PersistorUtils;

/**
 *
 *
 * @author Miquel Sas
 */
public class Ranges extends Averages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * @param session
	 */
	public Ranges(Session session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		return getTableRanges();
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

		Field fName = getFieldDefName();
		Field fMinMax = getFieldDefMinMax();
		Field fPeriod = getFieldDefPeriod();
		Field fValue = getFieldDefValue();

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
		view.addField(getFieldDefName());
		view.addField(getFieldDefMinMax());
		if (includePeriod) {
			view.addField(getFieldDefPeriod());
		}

		// Aggregate function count.
		view.addField(getFieldDefCount());

		// Aggregate function minimum.
		view.addField(getFieldDefMinimum());

		// Aggregate function maximum.
		view.addField(getFieldDefMaximum());

		// Aggregate function average.
		view.addField(getFieldDefAverage());

		// Aggregate function stddev.
		view.addField(getFieldDefStdDev());

		// Group by.
		view.addGroupBy(getFieldDefName());
		view.addGroupBy(getFieldDefMinMax());
		if (includePeriod) {
			view.addGroupBy(getFieldDefPeriod());
		}

		// Order by.
		view.addOrderBy(getFieldDefName());
		view.addOrderBy(getFieldDefMinMax());
		if (includePeriod) {
			view.addOrderBy(getFieldDefPeriod());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qtplaf.library.statistics.Statistics#getPlotDataList()
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		// TODO Auto-generated method stub
		return null;
	}

}
