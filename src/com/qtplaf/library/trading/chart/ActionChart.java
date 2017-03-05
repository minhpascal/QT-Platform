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

package com.qtplaf.library.trading.chart;

import java.awt.Point;

import javax.swing.AbstractAction;

/**
 * Actions installed in the chart, aimed to interact with it. Actions are performed by clicking the right button of the
 * mouse, presented in a popup menu.
 *
 * @author Miquel Sas
 */
public abstract class ActionChart extends AbstractAction {

	/** The parent chart. */
	private JChart chart;
	/** The chart plotter that lauched the action. */
	private JChartPlotter chartPlotter;
	/** The mouse point. */
	private Point mousePoint;

	/**
	 * Constructor.
	 */
	public ActionChart() {
		super();
	}

	/**
	 * Returns the chart.
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
	void setChart(JChart chart) {
		this.chart = chart;
	}

	/**
	 * Returns the chart plotter that lauched the action (where the popup menu was invoked).
	 * 
	 * @return The chart plotter.
	 */
	public JChartPlotter getChartPlotter() {
		return chartPlotter;
	}

	/**
	 * Set the chart plotter.
	 * 
	 * @param chartPlotter The chart plotter.
	 */
	void setChartPlotter(JChartPlotter chartPlotter) {
		this.chartPlotter = chartPlotter;
	}

	/**
	 * Returns the mouse point relative to the chart plotter.
	 * 
	 * @return The mouse point.
	 */
	public Point getMousePoint() {
		return mousePoint;
	}

	/**
	 * Sets the mouse point.
	 * 
	 * @param mousePoint The moise point.
	 */
	void setMousePoint(Point mousePoint) {
		this.mousePoint = mousePoint;
	}

}
