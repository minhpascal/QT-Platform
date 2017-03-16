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

package com.qtplaf.platform.statistics.action;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.chart.JChartNavigate;
import com.qtplaf.library.trading.chart.drawings.VerticalArea;
import com.qtplaf.library.trading.chart.drawings.VerticalLine;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.averages.Averages;

/**
 * Navigate a chart.
 *
 * @author Miquel Sas
 */
public class ActionNavigateStatistics extends ActionTickerStatistics {

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
			clearDrawings();
		}
	}

	/**
	 * Move the default recordset.
	 */
	class ActionRecordSetStd extends AbstractAction {

		ActionRecordSetStd() {
			ActionUtils.setName(this, "Default data list");
			ActionUtils.setShortDescription(this, "Return to default data list.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setRecordSetStd(0);
		}
	}

	/**
	 * Filter recordset to the same state.
	 */
	class ActionFilterSameState extends AbstractAction {

		ActionFilterSameState() {
			ActionUtils.setName(this, "Filter to samer state");
			ActionUtils.setShortDescription(this, "Filter data to the state of the selected record.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			filterSameState();
		}
	}

	/**
	 * Move to index and draw a vertical line.
	 */
	class ActionMoveChartToSelectedIndex extends AbstractAction {

		ActionMoveChartToSelectedIndex() {
			ActionUtils.setName(this, "Move to index");
			ActionUtils.setShortDescription(this, "Move the chart to the selected index.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			moveChartToIndex(getSelectedIndex());
		}
	}

	/**
	 * Move the chart to the center the selection.
	 */
	class ActionMoveChartToSelectedRange extends AbstractAction {

		ActionMoveChartToSelectedRange() {
			ActionUtils.setName(this, "Move and center to selected indexes");
			ActionUtils.setShortDescription(this, "Move the chart and center it to the selected indexes.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			moveChartToSelection(getSelectedStartIndex(), getSelectedEndIndex());
		}
	}

	/**
	 * Move to the record of the selected index in the chart.
	 */
	class ActionMoveToChartIndex extends AbstractAction {

		ActionMoveToChartIndex() {
			ActionUtils.setName(this, "Move records to chart index");
			ActionUtils.setShortDescription(this, "Move the standard recordset to the chart index.");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Point p = ActionUtils.getMousePoint(this);
			setRecordSetStd(getIndexFromChart(p));
		}
	}

	/** Chart navigate frame. */
	private JChartNavigate chartNavigate;

	/**
	 * @param averages
	 */
	public ActionNavigateStatistics(Averages averages) {
		super(averages);
		ActionUtils.setName(this, "Navigate chart");
		ActionUtils.setShortDescription(this, "Navigate the chart");
		ActionUtils.setActionGroup(this, new ActionGroup("Chart", 10200));
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
	}

	/**
	 * Returns the averages statistics.
	 * 
	 * @return The averages statistics.
	 */
	private Averages getAverages() {
		return (Averages) getStatistics();
	}

	/**
	 * Returns the chart navigate to be configurated prior to perform the action.
	 * 
	 * @return
	 */
	public JChartNavigate getChartNavigate() {
		if (chartNavigate == null) {
			chartNavigate = new JChartNavigate(getSession());
		}
		return chartNavigate;
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		getChartNavigate().setVisible(true);
		getChartNavigate().setRecordSet(getRecordSetStd());

		JChart chart = getChartNavigate().getChart();
		Averages avgs = getAverages();
		PersistorDataList dataList = avgs.getStates().getDataListStates();
		dataList.setCacheSize(-1);
		dataList.setPageSize(1000);

		chart.addPlotData(avgs.getPlotDataMain(dataList), true);
		chart.addPlotData(
			avgs.getPlotData("Spreads normalized", dataList, avgs.getFieldListSpreads(Fields.Suffix.nrm)),
			false);
		chart.addPlotData(avgs.getPlotData("Slopes normalized", dataList, avgs.getFieldListSlopes(Fields.Suffix.nrm)), false);
		chart.addPlotData(avgs.getPlotData("Spreads discrete", dataList, avgs.getFieldListSpreads(Fields.Suffix.dsc)), false);
		chart.addPlotData(avgs.getPlotData("Slopes discrete", dataList, avgs.getFieldListSlopes(Fields.Suffix.dsc)), false);
		chart.addPlotData(avgs.getPlotData("Spreads raw", dataList, avgs.getFieldListSpreads(Fields.Suffix.raw)), false);
		chart.addPlotData(avgs.getPlotData("Slopes raw", dataList, avgs.getFieldListSlopes(Fields.Suffix.raw)), false);

		getChartNavigate().addActionToChart(new ActionClearDrawings());
		getChartNavigate().addActionToChart(new ActionMoveToChartIndex());

		getChartNavigate().addActionToTable(new ActionRecordSetStd());
		getChartNavigate().addActionToTable(new ActionClearDrawings());
		getChartNavigate().addActionToTable(new ActionMoveChartToSelectedIndex());
		getChartNavigate().addActionToTable(new ActionMoveChartToSelectedRange());
		getChartNavigate().addActionToTable(new ActionFilterSameState());

	}

	/**
	 * Clear drawings.
	 */
	private void clearDrawings() {
		JChart chart = getChartNavigate().getChart();
		for (PlotData plotData : chart.getPlotDataList()) {
			plotData.getDrawings().clear();
		}
		chart.repaint();
	}

	/**
	 * Set the start recordset selecting the row.
	 * 
	 * @param selectedRow The row to select.
	 */
	private void setRecordSetStd(int selectedRow) {
		getChartNavigate().setRecordSet(getRecordSetStd());
		getChartNavigate().getTableRecord().setSelectedRow(selectedRow);
	}

	/**
	 * Returns the index of the chart.
	 * 
	 * @param p The moise point.
	 * @return The index.
	 */
	private int getIndexFromChart(Point p) {
		PlotterContext context = getChartNavigate().getChart().getPlotDataList().get(0).getPlotterContext();
		return context.getDataIndex(p.x);
	}

	/**
	 * Return the selected index.
	 * 
	 * @return The selected index.
	 */
	private int getSelectedIndex() {
		return getSelectedStartIndex();
	}

	/**
	 * Return the selected start index.
	 * 
	 * @return The selected start index.
	 */
	private int getSelectedStartIndex() {
		List<Integer> indexes = getSelectedIndexes();
		return indexes.get(0);
	}

	/**
	 * Return the selected end index.
	 * 
	 * @return The selected end index.
	 */
	private int getSelectedEndIndex() {
		List<Integer> indexes = getSelectedIndexes();
		return indexes.get(indexes.size() - 1);
	}

	/**
	 * Returns the list of selected indexes.
	 * 
	 * @return The list of selected indexes.
	 */
	private List<Integer> getSelectedIndexes() {
		JTableRecord tableRecord = getChartNavigate().getTableRecord();
		List<Record> records = tableRecord.getSelectedRecords();
		List<Integer> indexes = new ArrayList<>();
		for (Record record : records) {
			indexes.add(record.getValue(Fields.Index).getInteger());
		}
		return indexes;
	}

	/**
	 * Move chart to index.
	 * 
	 * @param index The index.
	 */
	private void moveChartToIndex(int index) {
		JChart chart = getChartNavigate().getChart();
		for (PlotData plotData : chart.getPlotDataList()) {
			VerticalLine vertLine = new VerticalLine(index);
			vertLine.getParameters().setColor(Color.RED);
			plotData.addDrawing(vertLine);
		}
		PlotData plotData = chart.getChartContainer(0).getPlotData();
		plotData.move(index);
		chart.propagateFrameChanges(plotData);
	}

	/**
	 * Move chart to selection (and draw a rectangle).
	 * 
	 * @param startIndex Start index.
	 * @param endIndex End index.
	 */
	private void moveChartToSelection(int startIndex, int endIndex) {
		JChart chart = getChartNavigate().getChart();
		for (PlotData plotData : chart.getPlotDataList()) {
			VerticalArea vertBand = new VerticalArea(startIndex, endIndex);
			plotData.addDrawing(vertBand);
		}
		PlotData plotData = chart.getChartContainer(0).getPlotData();
		plotData.center(startIndex, endIndex);
		chart.propagateFrameChanges(plotData);
	}

	/**
	 * Filter records with same state.
	 */
	private void filterSameState() {
		Record record = getChartNavigate().getTableRecord().getSelectedRecord();
		if (record == null) {
			return;
		}
		Field fState = record.getField(Fields.State);
		Value vState = record.getValue(Fields.State);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fState, vState));
		RecordSet recordSet = null;
		try {
			recordSet = record.getPersistor().select(criteria);
			getChartNavigate().setRecordSet(recordSet);
			getChartNavigate().getTableRecord().setSelectedRecord(record);
		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

	/**
	 * Filter transitions from the same state.
	 */
	protected void filterTransitionsFromState() throws Exception {
		Record record = getChartNavigate().getTableRecord().getSelectedRecord();
		if (record == null) {
			return;
		}
		Value vState = record.getValue(Fields.State);
		
		// Get all transitions where state_in == state
		Persistor persistor = getAverages().getTransitions().getTable().getPersistor();
		Field fStateIn = persistor.getField(Fields.StateIn);
		
		// Select criteria.
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(fStateIn, vState));
	}

	/**
	 * Return the std recordset.
	 * 
	 * @return The recordset.
	 */
	private RecordSet getRecordSetStd() {
		DataPersistor persistor = new DataPersistor(getAverages().getStates().getTable().getPersistor());
		return new DataRecordSet(persistor);
	}
}
