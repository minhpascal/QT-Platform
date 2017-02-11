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
package com.qtplaf.library.trading.chart.plotter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.drawings.Line;
import com.qtplaf.library.trading.chart.plotter.parameters.PlotParameters;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.OutputInfo;

/**
 * Line plotter for data list items.
 * 
 * @author Miquel Sas
 */
public class LinePlotter extends DataPlotter {

	/**
	 * An array of lists to buffer lines of each data element value, width the same color and stroke. Even though a
	 * plotter has a reference to a full plot data, a suitable plotter is assigned to each data list, so a line plotter
	 * instance will plot only values of a given data list.
	 */
	private List<List<Line>> lineBuffers;

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param session The working session.
	 * @param plotData The plot data.
	 * @param chartSize The chart plotter size.
	 * @param plotParameters The plot parameters
	 */
	public LinePlotter(Session session, PlotData plotData, Dimension chartSize, PlotParameters plotParameters) {
		super(session, plotData, chartSize, plotParameters);
	}

	/**
	 * Initializes the line buffers.
	 * 
	 * @param dataList The data list.
	 */
	private void initializeLineBuffers(DataList dataList) {
		if (lineBuffers != null) {
			return;
		}
		if (dataList.isEmpty()) {
			return;
		}

		// The size of the array of buffers.
		int size = -1;
		DataType dataType = dataList.getDataInfo().getDataType();
		switch (dataType) {
		case Price:
			size = 1;
			break;
		case Indicator:
			size = dataList.get(0).size();
			break;
		case Volume:
			size = 1;
			break;
		default:
			size = dataList.get(0).size();
			break;
		}

		// Initialize.
		lineBuffers = new ArrayList<List<Line>>();
		for (int i = 0; i < size; i++) {
			lineBuffers.add(new ArrayList<Line>());
		}
	}

	/**
	 * Returns the line buffer at the given index.
	 * 
	 * @param index The index.
	 * @return The line Buffer.
	 */
	private List<Line> getLineBuffer(int index) {
		return lineBuffers.get(index);
	}

	/**
	 * Check if a given line can be buffered in agiven buffer, because the stroke and the color are the same.
	 * 
	 * @param index The index of the buffer.
	 * @param line The line.
	 * @return A boolean that indicates if a given line can be buffered in agiven buffer, because the stroke and the
	 *         color are the same.
	 */
	private boolean canBuffer(int index, Line line) {
		List<Line> buffer = getLineBuffer(index);
		if (buffer.isEmpty()) {
			return true;
		}
		Line check = buffer.get(0);
		if (check.getStroke().equals(line.getStroke()) && check.getColor().equals(line.getColor())) {
			return true;
		}
		return false;
	}

	/**
	 * Add a line to a buffer.
	 * 
	 * @param index The buffer index.
	 * @param line The line.
	 */
	private void bufferLine(int index, Line line) {
		getLineBuffer(index).add(line);
	}

	/**
	 * Plots the given buffer and clears it.
	 * 
	 * @param index The buffer index.
	 * @param g2 The graphics context.
	 */
	private void plotBuffer(int index, Graphics2D g2) {

		// If nothing to plot...
		List<Line> buffer = getLineBuffer(index);
		if (buffer.isEmpty()) {
			return;
		}

		// Build a path appending the line shapes.
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, buffer.size());
		for (Line line : buffer) {
			shape.append(line.getShape(this), true);
		}

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			buffer.clear();
			return;
		}

		// Save the color and stroke
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set color and stroke.
		g2.setColor(buffer.get(0).getColor());
		g2.setStroke(buffer.get(0).getStroke());

		// Do plot.
		g2.draw(shape);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);

		// Clear the buffer.
		buffer.clear();
	}

	/**
	 * Returns an array of lines for the given data list and index. For prices and volumes an array of one element is
	 * returned, while for indicators an array with as much elements as the data size is returned. Note that the index
	 * must be greater than 0 because the current and previous indexes are used.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return An array of lines for the given data list and index.
	 */
	public Line[] getLines(DataList dataList, int index) {

		// Current and previous indexes. The previous index must the first previous valid. If no one found, skip
		// plotting.
		int indexCurrent = index;
		int indexPrevious = index - 1;
		while (!dataList.get(indexPrevious).isValid()) {
			indexPrevious--;
			if (indexPrevious < 0) {
				break;
			}
		}
		// Index previous is less than 0, not foound.
		if (indexPrevious < 0) {
			return null;
		}

		// Odd/even period.
		boolean odd = getPlotData().isOdd(index);

		// Current and previous values to plot, depending on the data typeof the list.
		double[] dataCurrent = getValues(dataList, indexCurrent);
		double[] dataPrevious = getValues(dataList, indexPrevious);

		// The array of lines.
		int countLines = dataCurrent.length;
		Line[] lines = new Line[countLines];
		for (int i = 0; i < countLines; i++) {

			// Current and previous value.
			double valueCurrent = dataCurrent[i];
			double valuePrevious = dataPrevious[i];

			// Color bullish or bearish depending on odds.
			Color colorBullish;
			Color colorBearish;
			if (odd) {
				colorBullish = dataList.getPlotProperties(i).getColorBullishOdd();
				colorBearish = dataList.getPlotProperties(i).getColorBearishOdd();
			} else {
				colorBullish = dataList.getPlotProperties(i).getColorBullishEven();
				colorBearish = dataList.getPlotProperties(i).getColorBearishEven();
			}

			// Bullish/bearish.
			boolean bullish = (valueCurrent >= valuePrevious);
			Color color = (bullish ? colorBullish : colorBearish);

			// Stroke.
			Stroke stroke = dataList.getPlotProperties(i).getStroke();

			// The line.
			lines[i] = new Line(indexPrevious, indexCurrent, valuePrevious, valueCurrent, stroke, color);
		}

		return lines;
	}

	/**
	 * Returns the list of values to plot for the index, depending on the data type (Price, Indicator, Volume).
	 * 
	 * @param dataList The data list.
	 * @param index The index.
	 * @return The values to plot.
	 */
	private double[] getValues(DataList dataList, int index) {
		Data data = dataList.get(index);
		DataType dataType = dataList.getDataInfo().getDataType();
		double[] values;
		switch (dataType) {
		case Price:
			int plotIndex = dataList.getIndexPrice();
			switch (plotIndex) {
			case Data.IndexOpen:
				values = new double[] { Data.getOpen(data) };
				break;
			case Data.IndexHigh:
				values = new double[] { Data.getHigh(data) };
				break;
			case Data.IndexLow:
				values = new double[] { Data.getLow(data) };
				break;
			case Data.IndexClose:
				values = new double[] { Data.getClose(data) };
				break;
			case Data.IndexMedianPrice:
				values = new double[] { Data.getMedianPrice(data) };
				break;
			case Data.IndexTypicalPrice:
				values = new double[] { Data.getTypicalPrice(data) };
				break;
			case Data.IndexWeightedClosePrice:
				values = new double[] { Data.getWeightedClosePrice(data) };
				break;
			default:
				values = new double[] { Data.getClose(data) };
				break;
			}
			break;
		case Indicator:
			values = data.getData();
			break;
		case Volume:
			values = new double[] { Data.getVolume(data) };
			break;
		default:
			values = dataList.get(index).getData();
			break;
		}
		return values;
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is greater than 2, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @param itemsPerUnit The number of data item per plot unit.
	 */
	public void plotDataIndex(Graphics2D g2, DataList dataList, int index) {

		// To plot a line we need the current index and the previous, so index must be greater than or equal to 1.
		if (index < 1) {
			return;
		}
		// The same for the start index.
		int startIndex = getPlotData().getStartIndex();
		if (index < startIndex + 1) {
			return;
		}

		// Initialize line buffers if necessary.
		initializeLineBuffers(dataList);

		// The lines.
		Line[] lines = getLines(dataList, index);

		// Data info.
		DataInfo dataInfo = dataList.getDataInfo();

		// A boolean that indicates if what is plotted is an indicator.
		boolean indicator = dataInfo.getDataType().equals(DataType.Indicator);

		// Plot.
		int size = lines.length;
		for (int i = 0; i < size; i++) {

			// Check if the line has to be plotted.
			if (indicator) {
				OutputInfo outputInfo = dataInfo.getOutput(i);
				if (outputInfo != null) {
					if (!outputInfo.isPlot()) {
						continue;
					}
				}
			}

			// The line.
			Line line = lines[i];

			// Do plot or buffer.
			if (!canBuffer(i, line)) {
				plotBuffer(i, g2);
			}
			bufferLine(i, line);
		}

	}

	/**
	 * Termination method to end the plot and clear or close resources.
	 * <p>
	 * Plot the remaining lines in line buffer and clear them.
	 * 
	 * @param g2 The graphics context.
	 */
	@Override
	public void endPlot(Graphics2D g2) {
		if (lineBuffers == null) {
			return;
		}
		for (int i = 0; i < lineBuffers.size(); i++) {
			plotBuffer(i, g2);
		}
	}
}