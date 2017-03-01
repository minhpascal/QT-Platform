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

import java.awt.event.ActionEvent;
import java.util.List;

import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;
import com.qtplaf.platform.statistics.chart.ActionChartNavigate;
import com.qtplaf.platform.statistics.chart.JChartNavigate;

/**
 * Navigate a chart.
 *
 * @author Miquel Sas
 */
public class ActionNavigateChart extends ActionTickerStatistics {

	/** Chart navigate frame. */
	private JChartNavigate chartNavigate;
	/** Plot data list. */
	private List<PlotData> plotDataList;
	/** Recordset privider. */
	private RecordSetProvider recordSetProvider;

	/**
	 * @param statistics
	 */
	public ActionNavigateChart(TickerStatistics statistics) {
		super(statistics);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
	}

	/**
	 * Add an action.
	 * 
	 * @param action The action.
	 */
	public void addAction(ActionChartNavigate action) {
		getChartNavigate().addAction(action);
	}

	/**
	 * Returns the recordset provider.
	 * 
	 * @return The recordset provider.
	 */
	public RecordSetProvider getRecordSetProvider() {
		return recordSetProvider;
	}

	/**
	 * Set the recordset provider.
	 * 
	 * @param recordSetProvider The recordset provider.
	 */
	public void setRecordSetProvider(RecordSetProvider recordSetProvider) {
		this.recordSetProvider = recordSetProvider;
	}

	/**
	 * Return the plot data list.
	 * 
	 * @return The plot data list.
	 */
	public List<PlotData> getPlotDataList() {
		return plotDataList;
	}

	/**
	 * Set the plot data list.
	 * 
	 * @param plotDataList The plot data list.
	 */
	public void setPlotDataList(List<PlotData> plotDataList) {
		this.plotDataList = plotDataList;
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
		if (getPlotDataList() == null || getRecordSetProvider() == null) {
			throw new IllegalStateException();
		}
		getChartNavigate().setVisible(true);
		getChartNavigate().setRecordSet(getRecordSetProvider().getRecordSet());
		getChartNavigate().getChart().addPlotDataList(getPlotDataList());
	}

}