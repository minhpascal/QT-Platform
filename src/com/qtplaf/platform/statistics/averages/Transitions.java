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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
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
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.chart.drawings.VerticalArea;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.database.Domains;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldIndex;
import com.qtplaf.platform.database.fields.FieldState;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionNavigateStatistics;
import com.qtplaf.platform.statistics.averages.task.TaskTransitions;
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
	 * Browse transitions.
	 */
	class ActionBrowseTransitions extends ActionBrowse {
		ActionBrowseTransitions(Transitions transitions) {
			super(transitions);
		}

		@Override
		public RecordSet getRecordSet() {
			return getRecordSetCorrelativeTransitions();
		}
	}

	/**
	 * Move the chart to the index of the selected record, adding a vertical line to it.
	 */
	class ActionMove extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JTableRecord tableRecord = ActionUtils.getTableRecordPanel(this).getTableRecord();
			Record record = tableRecord.getSelectedRecord();
			if (record == null) {
				return;
			}
			List<Record> transitions = getTransitions(record);
			int startIndex = transitions.get(0).getValue(Fields.IndexIn).getInteger();
			int endIndex =
				transitions.get(transitions.size() - 1).getValue(Fields.IndexIn).getInteger();
			VerticalArea vertBand = new VerticalArea(startIndex, endIndex);
			JChart chart = ActionUtils.getChart(this);
			PlotData plotData = chart.getChartContainer(0).getPlotData();
			plotData.addDrawing(vertBand);
			plotData.move(startIndex);
			chart.propagateFrameChanges(plotData);
		}
	}

	/** Cached table. */
	private Table table;

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

		// Calculate transitions.
		ActionCalculate actionCalculate = new ActionCalculate(this, new TaskTransitions(this));
		ActionUtils.setName(actionCalculate, "Calculate states transitions");
		ActionUtils.setShortDescription(actionCalculate, "Calculate states transitions.");
		ActionUtils.setActionGroup(actionCalculate, new ActionGroup("Calculate", 10000));
		actions.add(actionCalculate);

		// Browse correlative transitions (check resultset)
		ActionBrowseTransitions actionBrowse = new ActionBrowseTransitions(this);
		ActionUtils.setName(actionBrowse, "Browse correlative transitions");
		ActionUtils.setShortDescription(actionBrowse, "Browse correlative transitions");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10100));
		actions.add(actionBrowse);

		actions.add(new ActionNavigateStatistics(this));
		return actions;
	}

	/**
	 * Returns the transitions related to the selected record.
	 * 
	 * @param record The selected record.
	 * @return The list of transitions with the same index group.
	 */
	private List<Record> getTransitions(Record record) {
		List<Record> transitions = new ArrayList<>();

		Persistor persistor = getTable().getPersistor();

		Field fStateInput = persistor.getField(Fields.StateIn);
		Field fStateOutput = persistor.getField(Fields.StateOut);
		Field fIndexGroup = persistor.getField(Fields.IndexGroup);

		Value vStateInput = record.getValue(Fields.StateIn);
		Value vStateOutput = record.getValue(Fields.StateOut);
		Value vIndexGroup = record.getValue(Fields.IndexGroup);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fStateInput, vStateInput));
		criteria.add(Condition.fieldEQ(fStateOutput, vStateOutput));
		criteria.add(Condition.fieldEQ(fIndexGroup, vIndexGroup));

		Order order = new Order();
		order.add(persistor.getField(Fields.IndexIn));

		try {
			RecordIterator iterator = persistor.iterator(criteria, order);
			while (iterator.hasNext()) {
				transitions.add(iterator.next());
			}
			iterator.close();
		} catch (Exception exc) {
			logger.catching(exc);
		}

		return transitions;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {

		if (table == null) {

			table = new Table();

			table.setName(Tables.ticker(getInstrument(), getPeriod(), getId().toLowerCase()));
			table.setSchema(Schemas.server(getServer()));

			// Input and output states (keys)
			table.addField(new FieldState(getSession(), Fields.StateIn));
			table.addField(new FieldState(getSession(), Fields.StateOut));

			// Input and output indexes of source states.
			table.addField(new FieldIndex(getSession(), Fields.IndexIn));
			table.addField(new FieldIndex(getSession(), Fields.IndexOut));

			// Index group (groups consecutive transitions of the same state).
			table.addField(new FieldIndex(getSession(), Fields.IndexGroup));

			// Input spreads, speeds and calculations.
			table.addFields(getFieldListSpreads(Suffix.in));
			table.addFields(getFieldListSpeeds(Suffix.in));
			table.addFields(getFieldListCalculations(Suffix.in));

			// Output spreads, speeds and calculations.
			table.addFields(getFieldListSpreads(Suffix.out));
			table.addFields(getFieldListSpeeds(Suffix.out));
			table.addFields(getFieldListCalculations(Suffix.out));

			// Primary key.
			table.getField(Fields.StateIn).setPrimaryKey(true);
			table.getField(Fields.StateOut).setPrimaryKey(true);
			table.getField(Fields.IndexIn).setPrimaryKey(true);
			table.getField(Fields.IndexOut).setPrimaryKey(true);

			table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		}
		return table;
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
		view.addField(new FieldState(getSession(), Fields.StateIn));
		view.addField(new FieldState(getSession(), Fields.StateOut));
		view.addField(new FieldIndex(getSession(), Fields.IndexGroup));

		// Count(*)
		Field count = Domains.getInteger(getSession(), "count");
		count.setFunction("count(*)");
		view.addField(count);

		// Group by.
		view.addGroupBy(view.getField(Fields.StateIn));
		view.addGroupBy(view.getField(Fields.StateOut));
		view.addGroupBy(view.getField(Fields.IndexGroup));

		// Having count(*) > 2
		view.setHaving(count.getFunction() + " > 2");

		// Order by.
		view.addOrderBy(view.getField(Fields.IndexGroup));
		view.addOrderBy(view.getField(Fields.StateIn));
		view.addOrderBy(view.getField(Fields.StateOut));

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));

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
