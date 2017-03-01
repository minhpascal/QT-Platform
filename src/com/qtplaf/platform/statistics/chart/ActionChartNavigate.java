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

package com.qtplaf.platform.statistics.chart;

import javax.swing.AbstractAction;

import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.trading.chart.JChart;

/**
 * Actions installed in the chart navigate, launched from a popup menu, so they need to be configurated with a name.
 *
 * @author Miquel Sas
 */
public abstract class ActionChartNavigate extends AbstractAction {

	/** The chart. */
	private JChart chart;
	/** The table record. */
	private JTableRecord tableRecord;

	/**
	 * Default constructor.
	 */
	public ActionChartNavigate() {
		super();
	}

	/**
	 * Return the chart.
	 * 
	 * @return The chart.
	 */
	public JChart getChart() {
		return chart;
	}

	/**
	 * Set the chart.
	 * 
	 * @param chart The chart.
	 */
	public void setChart(JChart chart) {
		this.chart = chart;
	}

	/**
	 * Return the table record.
	 * 
	 * @return The table record.
	 */
	public JTableRecord getTableRecord() {
		return tableRecord;
	}

	/**
	 * Set the table record.
	 * 
	 * @param tableRecord The table record.
	 */
	public void setTableRecord(JTableRecord tableRecord) {
		this.tableRecord = tableRecord;
	}

}
