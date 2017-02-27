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

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;
import com.qtplaf.platform.statistics.chart.JChartNavigate;

/**
 * Navigate a chart.
 *
 * @author Miquel Sas
 */
public class ActionChartNavigate extends ActionTickerStatistics {

	/** Chart navigate frame. */
	private JChartNavigate chartNavigate;
	/** Plot data list. */
	private List<PlotData> plotDataList;
	/** Record set. */
	private RecordSet recordSet;

	/**
	 * @param statistics
	 */
	public ActionChartNavigate(TickerStatistics statistics) {
		super(statistics);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_chart));
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
	 * Return the recordset.
	 * 
	 * @return The recordset.
	 */
	public RecordSet getRecordSet() {
		return recordSet;
	}

	/**
	 * Set the recordset.
	 * 
	 * @param recordSet The recordset.
	 */
	public void setRecordSet(RecordSet recordSet) {
		this.recordSet = recordSet;
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
		if (getPlotDataList() == null || getRecordSet() == null) {
			throw new IllegalStateException();
		}
		getChartNavigate().setVisible(true);
		getChartNavigate().setRecordSet(getRecordSet());
		getChartNavigate().getChart().addPlotDataList(getPlotDataList());
	}

}
