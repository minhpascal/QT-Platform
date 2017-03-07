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

import javax.swing.Action;

import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.chart.JChartNavigate;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Navigate a chart.
 *
 * @author Miquel Sas
 */
public class ActionNavigateChart extends ActionTickerStatistics {

	/** Chart navigate frame. */
	private JChartNavigate chartNavigate;
	/** Plot data list provider. */
	private PlotDataConfigurator plotDataConfigurator;
	/** Recordset provider. */
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
	public void addAction(Action action) {
		getChartNavigate().addActionToTable(action);
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
	 * Returns the plot data list provider.
	 * 
	 * @return The plot data list provider.
	 */
	public PlotDataConfigurator getPlotDataConfigurator() {
		return plotDataConfigurator;
	}

	/**
	 * Set the plot data list provider.
	 * 
	 * @param plotDataListProvider The plot data list provider.
	 */
	public void setPlotDataConfigurator(PlotDataConfigurator plotDataListProvider) {
		this.plotDataConfigurator = plotDataListProvider;
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
		if (getPlotDataConfigurator() == null || getRecordSetProvider() == null) {
			throw new IllegalStateException();
		}
		getChartNavigate().setVisible(true);
		getChartNavigate().setRecordSet(getRecordSetProvider().getRecordSet());
		getPlotDataConfigurator().configureChart(getChartNavigate().getChart());
	}

}
