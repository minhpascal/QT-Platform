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
import java.awt.Shape;
import java.awt.Stroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.PlotParameters;
import com.qtplaf.library.trading.chart.plotter.drawings.Bar;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotProperties;

/**
 * PlotterOld of bars.
 * 
 * @author Miquel Sas
 */
public class BarPlotter extends DataPlotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param session The working session.
	 * @param plotData The plot data.
	 * @param chartSize The chart plotter size.
	 * @param plotParameters The plot parameters
	 */
	public BarPlotter(Session session, PlotData plotData, Dimension chartSize, PlotParameters plotParameters) {
		super(session, plotData, chartSize, plotParameters);
	}

	/**
	 * Returns the bar drawing for a given data list and index in the range.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return The bar drawing.
	 */
	public Bar getBar(DataList dataList, int index) {
		return new Bar(index, dataList.get(index));
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

		// The bar.
		Bar bar = getBar(dataList, index);
		Shape shape = bar.getShape(this);

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// Bullish/bearish.
		boolean bullish = bar.isBullish();

		// Odd/even period.
		boolean odd = getPlotData().isOdd(index);

		// Plot properties.
		PlotProperties plotProperties = dataList.getPlotProperties(0);

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set the stroke.
		g2.setStroke(getBorderAndBarStroke());

		// The color to apply.
		Color color;
		if (odd) {
			if (bullish) {
				color = plotProperties.getColorBullishOdd();
			} else {
				color = plotProperties.getColorBearishOdd();
			}
		} else {
			if (bullish) {
				color = plotProperties.getColorBullishEven();
			} else {
				color = plotProperties.getColorBearishEven();
			}
		}

		// Set color and paint.
		g2.setColor(color);
		g2.draw(shape);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}
}
