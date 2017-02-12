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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import com.qtplaf.library.trading.chart.plotter.drawings.Candlestick;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotProperties;
import com.qtplaf.library.util.ColorUtils;

/**
 * PlotterOld of candlesticks.
 * 
 * @author Miquel Sas
 */
public class CandlestickPlotter extends DataPlotter {

	/**
	 * Default candlestick border stroke.
	 */
	private BasicStroke borderStroke = new BasicStroke();

	/**
	 * Constructor.
	 */
	public CandlestickPlotter() {
		super();
	}

	/**
	 * Returns the border stroke
	 * 
	 * @return The border stroke.
	 */
	public BasicStroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * @param borderStroke the borderStroke to set
	 */
	public void setBorderStroke(BasicStroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	/**
	 * Returns the candlestick drawing for a given data list and index in the range.
	 * 
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 * @return The candlestick drawing.
	 */
	private Candlestick getCandlestick(DataList dataList, int index) {
		Data data = dataList.get(index);
		return new Candlestick(index, data, getIndexes(data));
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
		Shape shape = candlestick.getShape(getContext());

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// Bullish/bearish.
		boolean bullish = candlestick.isBullish();

		// Odd/even period.
		boolean odd = dataList.isOdd(index);

		// Plot properties.
		PlotProperties plotProperties = dataList.getPlotProperties(0);

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set the stroke.
		g2.setStroke(getBorderStroke());

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
				Data data = candlestick.getData();
				double open = Data.getOpen(data);
				double close = Data.getClose(data);
				int candlestickWidth = getContext().getDataItemWidth();
				int x = getContext().getCoordinateX(index);
				int yOpen = getContext().getCoordinateY(open);
				int yClose = getContext().getCoordinateY(close);
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
}
