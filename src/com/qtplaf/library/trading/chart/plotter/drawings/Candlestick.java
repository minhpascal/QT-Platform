/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter.drawings;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.data.OHLCV;

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
	 * @param ohlcv The OHLCV.
	 */
	public Candlestick(int index, OHLCV ohlcv) {
		super(index, ohlcv);
	}

	/**
	 * Returns the candlestick shape.
	 * 
	 * @param plotter The plotter.
	 * @return The candlestick shape.
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
		int candlestickWidth = plotter.getCandlestickOrBarWidth();
		int verticalLineX = plotter.getDrawingCenterCoordinateX(x);

		// The bar candle is bullish/bearish.
		boolean bullish = ohlcv.isBullish();

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
