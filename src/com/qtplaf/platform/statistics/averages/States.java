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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.chart.drawings.Line;
import com.qtplaf.library.trading.chart.drawings.VerticalLine;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionChart;
import com.qtplaf.platform.statistics.action.ActionNavigateChart;
import com.qtplaf.platform.statistics.action.RecordSetProvider;
import com.qtplaf.platform.statistics.averages.task.TaskNormalizes;
import com.qtplaf.platform.statistics.averages.task.TaskStates;
import com.qtplaf.platform.statistics.chart.ActionChartNavigate;

/**
 * States based on averages.
 *
 * @author Miquel Sas
 */
public class States extends Averages {

	/**
	 * Move the chart to the index of the selected record, adding a vertical line to it.
	 */
	class ActionMove extends ActionChartNavigate {
		@Override
		public void actionPerformed(ActionEvent e) {
			Record record = getTableRecord().getSelectedRecord();
			if (record == null) {
				return;
			}
			int index = record.getValue(getFieldDefIndex().getName()).getInteger();
			for (int i = 0; i < getChart().getChartCount(); i++) {
				Line line = new VerticalLine(index);
				line.getParameters().setColor(Color.RED);
				getChart().getChartContainer(i).getPlotData().addDrawing(line);
			}
			PlotData plotData = getChart().getChartContainer(0).getPlotData();
			plotData.moveTo(index);
			getChart().propagateFrameChanges(plotData);
		}
	}
	
	/**
	 * Recordset provider.
	 */
	class StdRecordSet implements RecordSetProvider {
		@Override
		public RecordSet getRecordSet() {
			return States.this.getRecordSet();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public States(Session session) {
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

		// Calculate states.
		ActionCalculate actionCalcStates = new ActionCalculate(this, new TaskStates(this));
		ActionUtils.setName(actionCalcStates, "Calculate states");
		ActionUtils.setShortDescription(actionCalcStates, "Calculate states from scratch");
		ActionUtils.setActionGroup(actionCalcStates, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcStates);

		// Normalize values.
		ActionCalculate actionCalcNorm = new ActionCalculate(this, new TaskNormalizes(this));
		ActionUtils.setName(actionCalcNorm, "Normalize values");
		ActionUtils.setShortDescription(actionCalcNorm, "Calculate normalized values");
		ActionUtils.setActionGroup(actionCalcNorm, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcNorm);

		// Standard browse of data.
		ActionBrowse actionBrowse = new ActionBrowse(this);
		actionBrowse.setRecordSetProvider(new StdRecordSet());
		ActionUtils.setName(actionBrowse, "Browse data");
		ActionUtils.setShortDescription(actionBrowse, "Browse calculated data");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10100));
		actions.add(actionBrowse);

		// Chart standard
		ActionChart actionChartStd = new ActionChart(this, getListPlotDataStandard());
		ActionUtils.setName(actionChartStd, "Standard chart");
		ActionUtils.setShortDescription(actionChartStd, "Show a standard chart with averages and normalized values");
		ActionUtils.setActionGroup(actionChartStd, new ActionGroup("Chart", 10200));
		actions.add(actionChartStd);

		// Chart continuous and discrete
		ActionChart actionChartAll = new ActionChart(this, getListPlotDataContinuousAndDiscrete());
		ActionUtils.setName(actionChartAll, "Chart with continuous and discrete spreads and speeds.");
		ActionUtils.setShortDescription(
			actionChartAll, "Show a chart with continuous and discrete spreads and speeds.");
		ActionUtils.setActionGroup(actionChartAll, new ActionGroup("Chart", 10200));
		actions.add(actionChartAll);

		// Chart navigate.
		ActionNavigateChart actionChartNav = new ActionNavigateChart(this);
		actionChartNav.getChartNavigate().setTitle("Navigate chart on result data");
		actionChartNav.setPlotDataList(getListPlotDataStandard());
		actionChartNav.setRecordSetProvider(new StdRecordSet());
		ActionUtils.setName(actionChartNav, "Navigate chart on result data");
		ActionUtils.setShortDescription(actionChartNav, "Show a standard chart with averages and normalized values");
		ActionUtils.setActionGroup(actionChartNav, new ActionGroup("Chart", 10200));
		
		ActionMove actionMove = new ActionMove();
		ActionUtils.setName(actionMove, "Move to selected index");
		ActionUtils.setShortDescription(actionMove, "Move the chart to the selected index.");
		actionChartNav.addAction(actionMove);
		
		actions.add(actionChartNav);

		return actions;
	}

	/**
	 * Returns the list of plot data for standard chart diaplay.
	 * 
	 * @return The list of plot data.
	 */
	private List<PlotData> getListPlotDataStandard() {
		List<PlotData> plotDataList = new ArrayList<>();
		PersistorDataList dataList = getDataList();
		dataList.setCacheSize(-1);
		plotDataList.add(getPlotDataMain(dataList));
		plotDataList.add(getPlotData(dataList, getFieldListSpreadsNormalizedContinuous()));
		plotDataList.add(getPlotData(dataList, getFieldListSpeedsNormalizedContinuous()));
		return plotDataList;
	}

	/**
	 * Returns the list of plot data for standard chart diaplay.
	 * 
	 * @return The list of plot data.
	 */
	private List<PlotData> getListPlotDataContinuousAndDiscrete() {
		List<PlotData> plotDataList = new ArrayList<>();
		PersistorDataList dataList = getDataList();
		dataList.setCacheSize(-1);
		plotDataList.add(getPlotDataMain(dataList));
		plotDataList.add(getPlotData(dataList, getFieldListSpreadsNormalizedContinuous()));
		plotDataList.add(getPlotData(dataList, getFieldListSpreadsNormalizedDiscrete()));
		plotDataList.add(getPlotData(dataList, getFieldListSpeedsNormalizedContinuous()));
		plotDataList.add(getPlotData(dataList, getFieldListSpeedsNormalizedDiscrete()));
		return plotDataList;
	}

	/**
	 * Returns the persistor data list for this states statistics.
	 * 
	 * @return The persistor data list.
	 */
	public PersistorDataList getDataList() {

		DataPersistor persistor = new DataPersistor(getTable().getPersistor());

		DataInfo info = new DataInfo(getSession());
		info.setName("States");
		info.setDescription("States data info");
		info.setInstrument(getInstrument());
		info.setPeriod(getPeriod());
		DataPersistor.setDataInfoOutput(info, persistor);

		return new PersistorDataList(getSession(), info, persistor);
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		return getTableStates();
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	private RecordSet getRecordSet() {
		DataPersistor persistor = new DataPersistor(getTable().getPersistor());
		return new DataRecordSet(persistor);
	}
}
