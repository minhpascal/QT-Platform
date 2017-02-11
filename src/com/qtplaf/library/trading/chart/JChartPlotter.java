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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.BarPlotter;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.CrossCursorPlotter;
import com.qtplaf.library.trading.chart.plotter.DataPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.chart.plotter.drawings.CrossCursor;
import com.qtplaf.library.trading.chart.plotter.parameters.CursorPlotParameters;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;

/**
 * The chart panel that effectively plots charts. The types of charts are <i>line</i>, <i>bar</i>, <i>candlestick</i>
 * and <i>histogram</i>.
 * 
 * @author Miquel Sas
 */
public class JChartPlotter extends JPanel {

	/**
	 * The parent chart container.
	 */
	private JChartContainer chartContainer;
	/**
	 * Current mouse point.
	 */
	private Point currentMousePoint;
	/**
	 * Previous mouse point.
	 */
	private Point previousMousePoint;
	/**
	 * The cursor to use.
	 */
	private CursorType cursorType = CursorType.Custom;

	/**
	 * Constructor assigning the parent chart container.
	 * 
	 * @param cartContainer The parent chart container.
	 */
	public JChartPlotter(JChartContainer chartContainer) {
		super();
		this.chartContainer = chartContainer;

		// All panels above the container must be transparent.
		setOpaque(false);

		// Setup mouse listeners.
		JChartPlotterListener listener = new JChartPlotterListener(this);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);

		// Set the cursor type from default plot parameters.
		setCursorType(chartContainer.getChart().getCursorPlotParameters().getChartCursorType());
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return chartContainer.getSession();
	}

	/**
	 * Returns the parent container.
	 * 
	 * @return The parent container.
	 */
	public JChartContainer getChartContainer() {
		return chartContainer;
	}

	/**
	 * Returns the cursor type.
	 * 
	 * @return The cursor type.
	 */
	public CursorType getCursorType() {
		return cursorType;
	}

	/**
	 * Sets the cursor type.
	 * 
	 * @param cursorType The cursor type.
	 */
	public void setCursorType(CursorType cursorType) {
		this.cursorType = cursorType;
	}

	/**
	 * Set the mouse point and repaint if required. If repaint is required a the clips for the cross cursor are added.
	 * 
	 * @param currentMousePoint The mouse point.
	 * @param repaint A boolean that indicates if the panel sould be repainted.
	 */
	public void setMousePoint(Point mousePoint, boolean repaint) {

		// Register the mouse points.
		previousMousePoint = currentMousePoint;
		currentMousePoint = mousePoint;

		// The need to repaint is only applicable if the cursor type is the chart cross cursor.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (repaint) {
				Rectangle currentBounds = getCrossCursor(mousePoint).getShape(getCrossCursorPlotter()).getBounds();
				currentBounds = Plotter.getIntersectionBounds(currentBounds);
				if (previousMousePoint != null) {
					Rectangle previousBounds =
						getCrossCursor(previousMousePoint).getShape(getCrossCursorPlotter()).getBounds();
					previousBounds = Plotter.getIntersectionBounds(previousBounds);
					currentBounds = currentBounds.union(previousBounds);
				}
				paintImmediately(currentBounds);
			}
		}
	}

	/**
	 * Clear the mouse point, so it does not show custom cursors.
	 */
	public void clearMousePoint(boolean repaint) {
		if (currentMousePoint == null) {
			return;
		}
		// The need to repaint is only applicable if the cursor type is the chart cross cursor.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (repaint) {
				Rectangle bounds = getCrossCursor(currentMousePoint).getShape(getCrossCursorPlotter()).getBounds();
				repaint(bounds);
			}
		}
		currentMousePoint = null;
		previousMousePoint = null;
	}

	/**
	 * Paint this chart.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// The graphics object.
		Graphics2D g2 = (Graphics2D) g;

		// Set plotters.
		setDataPlotters();

		// Plot chart data.
		plotChartData(g2, chartContainer.getPlotData());

		// Plot the cross cursor if required.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (currentMousePoint != null) {
				CrossCursorPlotter crossCursorPlotter = getCrossCursorPlotter();
				crossCursorPlotter.plot(g2, getCrossCursor(currentMousePoint));
			}
		}
	}

	/**
	 * Plot the chart data.
	 * 
	 * @param g2 The graphics object.
	 */
	private void plotChartData(Graphics2D g2, PlotData plotData) {

		// Retrieve a plotter to calculate start and end indexes depending on the clip bounds.
		Plotter plotter = plotData.get(0).getDataPlotter();
		int x1 = g2.getClipBounds().x;
		int x2 = x1 + g2.getClipBounds().width;
		int startIndexCheck = plotter.getDataIndex(x1);
		int endIndexCheck = plotter.getDataIndex(x2);

		// Start and end indexes from plot data.
		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();
		int startIndexClip = startIndex;
		int endIndexClip = endIndex;

		// Set start and end indexes applying clip bounds with a margin.
		int margin = Math.max((endIndex - startIndex + 1) / 50, 2);
		if (startIndexCheck > startIndex + margin) {
			startIndexClip = startIndexCheck - margin;
		}
		if (endIndexCheck < endIndex - margin) {
			endIndexClip = endIndexCheck + margin;
		}

		// Plot first non indicator data lists.
		List<DataList> nonIndicator = plotData.getDataListsNonIndicator();
		List<DataList> fromClip = plotData.getDataListsIndicatorToPlotClip();
		List<DataList> fromScratch = plotData.getDataListsIndicatorToPlotFromScratch();

		// Do plot.
		if (!nonIndicator.isEmpty()) {
			for (int index = startIndexClip; index < endIndexClip; index++) {
				for (DataList dataList : nonIndicator) {
					plotChartData(g2, dataList, index);
				}
			}
		}
		if (!fromClip.isEmpty()) {
			for (int index = startIndexClip; index < endIndexClip; index++) {
				for (DataList dataList : fromClip) {
					plotChartData(g2, dataList, index);
				}
			}
		}
		if (!fromScratch.isEmpty()) {
			for (DataList dataList : fromScratch) {
				for (int index = startIndex; index < endIndex; index++) {
					plotChartData(g2, dataList, index);
				}
			}
		}

		// Terminate plots.
		for (int i = 0; i < plotData.size(); i++) {
			DataList dataList = plotData.get(i);
			dataList.getDataPlotter().endPlot(g2);
		}
	}

	/**
	 * Plot an index of a data list.
	 * 
	 * @param g2 The graphics object.
	 * @param index The index to plot.
	 */
	private void plotChartData(Graphics2D g2, DataList dataList, int index) {

		// Data size.
		int size = dataList.size();

		// If the index is out of range, do nothing.
		if (index < 0 || index >= size) {
			return;
		}

		// If the current data is not valid, skip it.
		if (!dataList.get(index).isValid()) {
			return;
		}

		// Do plot.
		dataList.getDataPlotter().plotDataIndex(g2, dataList, index);
	}

	/**
	 * Sets a suitable plotter to each data list in the plot data.
	 */
	private void setDataPlotters() {
		Dimension chartSize = getSize();
		PlotData plotData = chartContainer.getPlotData();
		for (int i = 0; i < plotData.size(); i++) {
			DataList dataList = plotData.get(i);
			DataPlotter dataPlotter;
			switch (dataList.getPlotType()) {
			case Bar:
				dataPlotter = new BarPlotter(chartContainer.getChart(), plotData, chartSize);
				break;
			case Candlestick:
				dataPlotter = new CandlestickPlotter(chartContainer.getChart(), plotData, chartSize);
				break;
			case Histogram:
				// TODO: implement histogram plotter.
				dataPlotter = new LinePlotter(chartContainer.getChart(), plotData, chartSize);
				break;
			case Line:
				dataPlotter = new LinePlotter(chartContainer.getChart(), plotData, chartSize);
				break;
			default:
				dataPlotter = new LinePlotter(chartContainer.getChart(), plotData, chartSize);
				break;
			}
			dataList.setDataPlotter(dataPlotter);
		}
	}

	/**
	 * Returns the cross cursor plotter.
	 * 
	 * @return The cross cursor plotter.
	 */
	private CrossCursorPlotter getCrossCursorPlotter() {
		Dimension chartSize = getSize();
		PlotData plotData = chartContainer.getPlotData();
		return new CrossCursorPlotter(chartContainer.getChart(), plotData, chartSize);
	}

	/**
	 * Returns the cross cursor given the point.
	 * 
	 * @param point The cursor point.
	 * @return The cross cursor.
	 */
	protected CrossCursor getCrossCursor(Point point) {
		CursorPlotParameters plotParameters = chartContainer.getChart().getCursorPlotParameters();
		CrossCursor cursor = new CrossCursor(point);
		cursor.setWidth(plotParameters.getChartCrossCursorWidth());
		cursor.setHeight(plotParameters.getChartCrossCursorHeight());
		cursor.setStroke(plotParameters.getChartCrossCursorStroke());
		cursor.setColor(plotParameters.getChartCrossCursorColor());
		cursor.setRadius(plotParameters.getChartCrossCursorCircleRadius());
		return cursor;
	}

	/**
	 * Returns a transparent cursor
	 * 
	 * @return The transparent cursor.
	 */
	protected Cursor getTransparentCursor() {
		Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(0, 0);
		BufferedImage cursorImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Transparent cursor");
	}

	/**
	 * Returns the custom cross hair cursor, thinest that the Java one.
	 * 
	 * @return The custom cross hair cursor.
	 */
	protected Cursor getCustomCursor() {
		Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(0, 0);
		int width = size.width;
		int height = size.height;
		int x = width / 2;
		int y = height / 2;
		BufferedImage cursorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = cursorImage.getGraphics();
		g.setColor(Color.GRAY);
		g.drawLine(0, y, width, y);
		g.drawLine(x, 0, x, height);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Transparent cursor");
	}

	/**
	 * Sets the appropriate cursor.
	 */
	void setCursor() {
		CursorPlotParameters plotParameters = getChartContainer().getChart().getCursorPlotParameters();
		switch (cursorType) {
		case Predefined:
			setCursor(Cursor.getPredefinedCursor(plotParameters.getChartCursorTypePredefined()));
			break;
		case Custom:
			setCursor(getCustomCursor());
			break;
		case ChartCross:
			// Set the transparent cursor.
			// setCursor(getTransparentCursor());
			setCursor(Cursor.getPredefinedCursor(plotParameters.getChartCursorTypePredefined()));
			break;
		default:
			setCursor(Cursor.getPredefinedCursor(plotParameters.getChartCursorTypePredefined()));
			break;
		}
	}

}
