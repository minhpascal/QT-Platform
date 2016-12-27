/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter;

import java.awt.Dimension;
import java.awt.Graphics2D;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.PlotParameters;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;

/**
 * Base class for data plotters.
 * 
 * @author Miquel Sas
 */
public abstract class DataPlotter extends Plotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param session The working session.
	 * @param plotData The plot data.
	 * @param chartSize The chart plotter size.
	 * @param plotParameters The plot parameters
	 */
	public DataPlotter(Session session, PlotData plotData, Dimension chartSize, PlotParameters plotParameters) {
		super(session, plotData, chartSize, plotParameters);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is not -1, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	public abstract void plotDataIndex(Graphics2D g2, DataList dataList, int index);

	/**
	 * Termination method to end the plot and clear or close resources.
	 * 
	 * @param g2 The graphics context.
	 * @param dataList The data list.
	 */
	public abstract void endPlot(Graphics2D g2, DataList dataList);
}
