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
package com.qtplaf.library.trading.chart.plotter.drawings;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.data.Data;

/**
 * A candlestick drawing.
 * 
 * @author Miquel Sas
 */
public class Candlestick extends CandlestickOrBar {

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param data The data.
	 */
	public Candlestick(int index, Data data) {
		super(index, data);
	}

	/**
	 * Returns the candlestick shape.
	 * 
	 * @param plotter The plotter.
	 * @return The candlestick shape.
	 */
	public Shape getShape(Plotter plotter) {
		// The values to plot.
		Data data = getData();
		double open = Data.getOpen(data);
		double high = Data.getHigh(data);
		double low = Data.getLow(data);
		double close = Data.getClose(data);

		// The X coordinate to start painting.
		int x = plotter.getCoordinateX(getIndex());

		// And the Y coordinate for each value.
		int openY = plotter.getCoordinateY(open);
		int highY = plotter.getCoordinateY(high);
		int lowY = plotter.getCoordinateY(low);
		int closeY = plotter.getCoordinateY(close);

		// The X coordinate of the vertical line, either the candle.
		int candlestickWidth = plotter.getCandlestickOrBarWidth();
		int verticalLineX = plotter.getDrawingCenterCoordinateX(x);

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
}
