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
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionNavigateChart;
import com.qtplaf.platform.statistics.action.PlotDataConfigurator;
import com.qtplaf.platform.statistics.action.RecordSetProvider;
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
	 * Recordset provider.
	 */
	class RecordSetCorrelativeTransitions implements RecordSetProvider {
		@Override
		public RecordSet getRecordSet() {
			return Transitions.this.getRecordSetCorrelativeTransitions();
		}
	}

	/**
	 * Std plot data list provider.
	 */
	class StdPlotDataList implements PlotDataConfigurator {
		@Override
		public void configureChart(JChart chart) {
			List<PlotData> plotDataList = getPlotDataListStandard();
			for (PlotData plotData : plotDataList) {
				chart.addPlotData(plotData);
			}
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
			int startIndex = transitions.get(0).getValue(getFieldDefIndexInput().getName()).getInteger();
			int endIndex =
				transitions.get(transitions.size() - 1).getValue(getFieldDefIndexInput().getName()).getInteger();
			VerticalArea vertBand = new VerticalArea(startIndex, endIndex);
			JChart chart = ActionUtils.getChart(this);
			PlotData plotData = chart.getChartContainer(0).getPlotData();
			plotData.addDrawing(vertBand);
			plotData.move(startIndex);
			chart.propagateFrameChanges(plotData);
		}
	}

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
		ActionBrowse actionBrowse = new ActionBrowse(this);
		actionBrowse.setRecordSetProvider(new RecordSetCorrelativeTransitions());
		ActionUtils.setName(actionBrowse, "Browse correlative transitions");
		ActionUtils.setShortDescription(actionBrowse, "Browse correlative transitions");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10100));
		actions.add(actionBrowse);

		ActionNavigateChart actionChartNav = new ActionNavigateChart(this);
		actionChartNav.getChartNavigate().setTitle("Navigate chart on result data");
		actionChartNav.setPlotDataConfigurator(new StdPlotDataList());
		actionChartNav.setRecordSetProvider(new RecordSetCorrelativeTransitions());
		ActionUtils.setName(actionChartNav, "Navigate chart on transitions");
		ActionUtils.setShortDescription(actionChartNav, "Navigate a standard chart locating transitions");
		ActionUtils.setActionGroup(actionChartNav, new ActionGroup("Chart", 10200));

		ActionMove actionMove = new ActionMove();
		ActionUtils.setName(actionMove, "Move to selected transitions");
		ActionUtils.setShortDescription(actionMove, "Move the chart to the selected transitions group.");
		actionChartNav.addAction(actionMove);

		actions.add(actionChartNav);

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

		Field fStateInput = persistor.getField(getFieldDefStateInput().getName());
		Field fStateOutput = persistor.getField(getFieldDefStateOutput().getName());
		Field fIndexGroup = persistor.getField(getFieldDefIndexGroup().getName());

		Value vStateInput = record.getValue(getFieldDefStateInput().getName());
		Value vStateOutput = record.getValue(getFieldDefStateOutput().getName());
		Value vIndexGroup = record.getValue(getFieldDefIndexGroup().getName());

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fStateInput, vStateInput));
		criteria.add(Condition.fieldEQ(fStateOutput, vStateOutput));
		criteria.add(Condition.fieldEQ(fIndexGroup, vIndexGroup));

		Order order = new Order();
		order.add(persistor.getField(getFieldDefIndexInput().getName()));

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
	 * Returns the list of plot data for standard chart diaplay.
	 * 
	 * @return The list of plot data.
	 */
	private List<PlotData> getPlotDataListStandard() {
		List<PlotData> plotDataList = new ArrayList<>();
		PersistorDataList dataList = getDataListStates();
		dataList.setCacheSize(-1);
		dataList.setPageSize(1000);
		;
		plotDataList.add(getPlotDataMain(dataList));
		plotDataList.add(getPlotData("Spreads-nrm", dataList, getFieldListSpreads(Suffix.nrm)));
		plotDataList.add(getPlotData("Speeds-nrm", dataList, getFieldListSpeeds(Suffix.nrm)));
		return plotDataList;
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
		view.addGroupBy(getFieldDefIndexGroup());

		// Having count(*) > 2
		view.setHaving(count.getFunction() + " > 2");

		// Order by.
		view.addOrderBy(getFieldDefIndexGroup());
		view.addOrderBy(getFieldDefStateInput());
		view.addOrderBy(getFieldDefStateOutput());

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
