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
package com.qtplaf.library.trading.chart.drawings;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.Data;

/**
 * A bar drawing.
 * 
 * @author Miquel Sas
 */
public class Bar extends DataDrawing {
	
	/** Indexes to retrieve data. */
	private int[] indexes;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param data The data.
	 * @param indexes The indexes to retrieve values.
	 */
	public Bar(int index, Data data, int[] indexes) {
		super(index, data);
		this.indexes = indexes;
		setName("Bar");
	}

	/**
	 * Returns the bar shape.
	 * 
	 * @param context The plotter context.
	 * @return The bar shape.
	 */
	public Shape getShape(PlotterContext context) {
		// The values to plot.
		Data data = getData();
		double open = data.getValue(indexes[0]);
		double high = data.getValue(indexes[1]);
		double low = data.getValue(indexes[2]);
		double close = data.getValue(indexes[3]);

		// The X coordinate to start painting.
		int x = context.getCoordinateX(getIndex());

		// And the Y coordinate for each value.
		int openY = context.getCoordinateY(open);
		int highY = context.getCoordinateY(high);
		int lowY = context.getCoordinateY(low);
		int closeY = context.getCoordinateY(close);

		// The X coordinate of the vertical line, either the candle.
		int barWidth = context.getDataItemWidth();
		int verticalLineX = context.getDrawingCenterCoordinateX(x);

		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		// The vertical bar line.
		shape.moveTo(verticalLineX, highY);
		shape.lineTo(verticalLineX, lowY);
		// Open and close horizontal lines if the bar width is greater than 1.
		if (barWidth > 1) {
			// Open horizontal line.
			shape.moveTo(x, openY);
			shape.lineTo(verticalLineX - 1, openY);
			// Close horizontal line
			shape.moveTo(verticalLineX + 1, closeY);
			shape.lineTo(x + barWidth - 1, closeY);
		}
		return shape;
	}

	/**
	 * Draw the candlestick.
	 * 
	 * @param g2 The graphics object.
	 * @param context The plotter context.
	 */
	@Override
	public void draw(Graphics2D g2, PlotterContext context) {
	}
}
