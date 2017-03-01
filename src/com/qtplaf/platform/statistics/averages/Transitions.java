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
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.View;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.averages.task.TaskTransitions;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Transitions statistics.
 *
 * @author Miquel Sas
 */
public class Transitions extends Averages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * @param session
	 */
	public Transitions(Session session) {
		super(session);
	}

	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {

		List<Action> actions = new ArrayList<>();

		// Calculate ranges.
		ActionCalculate actionCalculate = new ActionCalculate(this, new TaskTransitions(this));
		ActionUtils.setName(actionCalculate, "Calculate states transitions");
		ActionUtils.setShortDescription(actionCalculate, "Calculate states transitions.");
		ActionUtils.setActionGroup(actionCalculate, new ActionGroup("Calculate", 10000));
		actions.add(actionCalculate);

		return actions;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		return getTableTransitions();
	}

	/**
	 * Return a recordset for correlative transitions.
	 * 
	 * @return The recordset.
	 */
	public RecordSet getRecordSetCorrelativeTransitions() {

		Table table = getTable();

		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Group by fields
		view.addField(getFieldDefStateInput());
		view.addField(getFieldDefStateOutput());
		view.addField(getFieldDefIndexGroup());

		// Count(*)
		Field count = DomainUtils.getInteger(getSession(), "count", "Count", "Count same index group");
		count.setFunction("count(*)");
		view.addField(count);

		// Sum(value_close)
		Field sumClose = DomainUtils.getDouble(getSession(), "sum_close", "Sum Close", "Sum value close");
		sumClose.setFunction("sum(" + getFieldDefTransitionValueClose().getName() + ")");
		sumClose.setFormatter(getValueFormatterRaw());
		view.addField(sumClose);

		// Min(value_close)
		Field minClose = DomainUtils.getDouble(getSession(), "min_close", "Min Close", "Min value close");
		minClose.setFunction("min(" + getFieldDefTransitionValueClose().getName() + ")");
		minClose.setFormatter(getValueFormatterRaw());
		view.addField(minClose);

		// Max(value_close)
		Field maxClose = DomainUtils.getDouble(getSession(), "max_close", "Max Close", "Max value close");
		maxClose.setFunction("max(" + getFieldDefTransitionValueClose().getName() + ")");
		maxClose.setFormatter(getValueFormatterRaw());
		view.addField(maxClose);

		// Avg(value_close)
		Field avgClose = DomainUtils.getDouble(getSession(), "avg_close", "Avg Close", "Avg value close");
		avgClose.setFunction("avg(" + getFieldDefTransitionValueClose().getName() + ")");
		avgClose.setFormatter(getValueFormatterRaw());
		view.addField(avgClose);

		// Group by.
		view.addGroupBy(getFieldDefStateInput());
		view.addGroupBy(getFieldDefStateOutput());
		view.addField(getFieldDefIndexGroup());

		// Having count(*) > 2
		view.setHaving(count.getFunction() + " > 2");

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));
		
		// Order by.
		view.addOrderBy(getFieldDefIndexGroup());
		view.addOrderBy(getFieldDefStateInput());
		view.addOrderBy(getFieldDefStateOutput());

		// RecordSet
		RecordSet recordSet = null;
		try {
			recordSet = view.getPersistor().select(null);
		} catch (PersistorException exc) {
			logger.catching(exc);
		}

		return recordSet;
	}

}
