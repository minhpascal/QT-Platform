/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter.drawings;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.data.OHLCV;

/**
 * A bar drawing.
 * 
 * @author Miquel Sas
 */
public class Bar extends CandlestickOrBar {

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The data index.
	 * @param ohlcv The OHLCV.
	 */
	public Bar(int index, OHLCV ohlcv) {
		super(index, ohlcv);
	}

	/**
	 * Returns the bar shape.
	 * 
	 * @param plotter The plotter.
	 * @return The bar shape.
	 */
	public Shape getShape(Plotter plotter) {
		// The values to plot.
		OHLCV ohlcv = getOHLCV();
		double open = ohlcv.getOpen();
		double high = ohlcv.getHigh();
		double low = ohlcv.getLow();
		double close = ohlcv.getClose();

		// The X coordinate to start painting.
		int x = plotter.getCoordinateX(getIndex());

		// And the Y coordinate for each value.
		int openY = plotter.getCoordinateY(open);
		int highY = plotter.getCoordinateY(high);
		int lowY = plotter.getCoordinateY(low);
		int closeY = plotter.getCoordinateY(close);

		// The X coordinate of the vertical line, either the candle.
		int barWidth = plotter.getCandlestickOrBarWidth();
		int verticalLineX = plotter.getDrawingCenterCoordinateX(x);

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
}
