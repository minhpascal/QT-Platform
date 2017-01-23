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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.PlotParameters;
import com.qtplaf.library.trading.chart.plotter.drawings.Candlestick;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotProperties;
import com.qtplaf.library.util.ColorUtils;

/**
 * PlotterOld of candlesticks.
 * 
 * @author Miquel Sas
 */
public class CandlestickPlotter extends DataPlotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param session The working session.
	 * @param plotData The plot data.
	 * @param chartSize The chart plotter size.
	 * @param plotParameters The plot parameters
	 */
	public CandlestickPlotter(Session session, PlotData plotData, Dimension chartSize, PlotParameters plotParameters) {
		super(session, plotData, chartSize, plotParameters);
	}

	/**
	 * Returns the candlestick drawing for a given data list and index in the range.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return The candlestick drawing.
	 */
	private Candlestick getCandlestick(DataList dataList, int index) {
		return new Candlestick(index, new OHLCV(dataList.get(index)));
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is not -1, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	public void plotDataIndex(Graphics2D g2, DataList dataList, int index) {

		// The candlestick.
		Candlestick candlestick = getCandlestick(dataList, index);
		Shape shape = candlestick.getShape(this);

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// Bullish/bearish.
		boolean bullish = candlestick.isBullish();

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

		// Once defined the path the paint strategy will depend on whether the border if painted or not, and whether
		// the color is raised or not.
		if (plotProperties.isPaintBorder()) {
			if (plotProperties.isColorRaised()) {
				// Create a raised color.
				OHLCV ohlcv = candlestick.getOHLCV();
				double open = ohlcv.getOpen();
				double close = ohlcv.getClose();
				int candlestickWidth = getCandlestickOrBarWidth();
				int x = getCoordinateX(index);
				int yOpen = getCoordinateY(open);
				int yClose = getCoordinateY(close);
				Color colorRaised = ColorUtils.brighter(color, plotProperties.getBrightnessFactor());
				Point2D pt1;
				Point2D pt2;
				if (bullish) {
					pt1 = new Point2D.Float(x, yClose);
					pt2 = new Point2D.Float(x + candlestickWidth - 1, yClose);
				} else {
					pt1 = new Point2D.Float(x, yOpen);
					pt2 = new Point2D.Float(x + candlestickWidth - 1, yOpen);
				}
				GradientPaint raisedPaint = new GradientPaint(pt1, colorRaised, pt2, color, true);
				g2.setPaint(raisedPaint);
				g2.fill(shape);
			} else {
				// Set the fill color and do fill.
				g2.setColor(color);
				g2.fill(shape);
			}
			// Set the border color and draw the path.
			g2.setColor(plotProperties.getColorBorder());
			g2.draw(shape);
		} else {
			// Set the fill color and do fill.
			g2.setColor(color);
			g2.fill(shape);
			g2.draw(shape);
		}

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}

	/**
	 * Termination method to end the plot and clear or close resources.
	 * 
	 * @param g2 The graphics context.
	 * @param dataList The data list.
	 */
	public void endPlot(Graphics2D g2, DataList dataList) {
	}
}
