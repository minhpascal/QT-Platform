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

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.Data;

/**
 * A candlestick drawing.
 * 
 * @author Miquel Sas
 */
public class Candlestick extends DataDrawing {
	
	/** Indexes to retrieve data. */
	private int[] indexes;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param data The data.
	 * @param indexes The indexes to retrieve values.
	 */
	public Candlestick(int index, Data data, int[] indexes) {
		super(index, data);
		this.indexes = indexes;
		setName("Candlestick");
	}

	/**
	 * Returns the candlestick shape.
	 * 
	 * @param context The plotter context.
	 * @return The candlestick shape.
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
		int candlestickWidth = context.getDataItemWidth();
		int verticalLineX = context.getDrawingCenterCoordinateX(x);

		// The bar candle is bullish/bearish.
		boolean bullish = Data.isBullish(data);

		// The candlestick shape.
		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
		// If bar width is 1...
		if (candlestickWidth == 1) {
			// The vertical line only.
			shape.moveTo(verticalLineX, highY);
			shape.lineTo(verticalLineX, lowY);
		} else {
			if (bullish) {
				// Upper shadow.
				shape.moveTo(verticalLineX, highY);
				shape.lineTo(verticalLineX, closeY - 1);
				// Body.
				shape.moveTo(x, closeY);
				shape.lineTo(x + candlestickWidth - 1, closeY);
				shape.lineTo(x + candlestickWidth - 1, openY);
				shape.lineTo(x, openY);
				shape.lineTo(x, closeY);
				// Lower shadow.
				shape.moveTo(verticalLineX, openY + 1);
				shape.lineTo(verticalLineX, lowY);
			} else {
				// Upper shadow.
				shape.moveTo(verticalLineX, highY);
				shape.lineTo(verticalLineX, openY - 1);
				// Body.
				shape.moveTo(x, openY);
				shape.lineTo(x + candlestickWidth - 1, openY);
				shape.lineTo(x + candlestickWidth - 1, closeY);
				shape.lineTo(x, closeY);
				shape.lineTo(x, openY);
				// Lower shadow.
				shape.moveTo(verticalLineX, closeY + 1);
				shape.lineTo(verticalLineX, lowY);
			}
		}

		return shape;
	}

	/**
	 * Returns the maximum value of the drawing.
	 * 
	 * @return The maximum value.
	 */
	@Override
	public double getMaximumValue() {
		return Data.getHigh(getData());
	}

	/**
	 * Returns the minimum value of the drawing.
	 * 
	 * @return The minimum value.
	 */
	@Override
	public double getMinimumValue() {
		return Data.getLow(getData());
	}

	/**
	 * Returns the maximum index of the drawing.
	 * 
	 * @return The maximum index.
	 */
	@Override
	public int getMaximumIndex() {
		return getIndex();
	}

	/**
	 * Returns the minimum index of the drawing.
	 * 
	 * @return The minimum index.
	 */
	@Override
	public int getMinimumIndex() {
		return getIndex();
	}
}
