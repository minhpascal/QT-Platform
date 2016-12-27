/**
 * 
 */
package com.qtplaf.library.trading.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Encapsulates the properties supported to plot a line, bar, candlestick o histogram chart.
 * 
 * @author Miquel Sas
 */
public class PlotProperties {

	/**
	 * The color used for a bullish line/bar/candle in an even period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBullishEven = new Color(16, 96, 16);
	/**
	 * The color used for a bearish line bar candle is an even period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBearishEven = new Color(128, 16, 16);
	/**
	 * The color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color changes when
	 * the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBullishOdd = new Color(215, 215, 215);
	/**
	 * The color used for a bearish line bar candle is an odd period. For periods lower than day, the color changes when
	 * the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 */
	private Color colorBearishOdd = new Color(25, 25, 25);
	/**
	 * The border color applies only to candlesticks and histograms.
	 */
	private Color colorBorder = Color.BLACK;
	/**
	 * A boolean that indicates if the border with the specified border color, that applies only to candlesticks and
	 * histograms, should be painted. Explicitly set although it could be deduced if the border color is null.
	 */
	private boolean paintBorder = true;
	/**
	 * A boolean that indicates if the color in candlesticks and histograms should be raised.
	 */
	private boolean colorRaised = true;
	/**
	 * The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	private Stroke stroke = new BasicStroke();
	/**
	 * The brightness factor to apply for raised colors.
	 */
	private double brightnessFactor = 0.95;
	/**
	 * The plot type tp apply to the data item pointed by this plot properties.
	 */
	private PlotType plotType = PlotType.Line;

	/**
	 * Default constructor.
	 */
	public PlotProperties() {
		super();
	}

	/**
	 * Returns the color used for a bullish line/bar/candle in an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBullishEven The color used for a bullish line/bar/candle in an even period.
	 */
	public Color getColorBullishEven() {
		return colorBullishEven;
	}

	/**
	 * Sets the color used for a bullish line/bar/candle in an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @param colorBullishEven The color used for a bullish line/bar/candle in an even period.
	 */
	public void setColorBullishEven(Color colorBullishEven) {
		this.colorBullishEven = colorBullishEven;
	}

	/**
	 * Returns the color used for a bearish line bar candle is an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBearishEven The color used for a bearish line bar candle is an even period.
	 */
	public Color getColorBearishEven() {
		return colorBearishEven;
	}

	/**
	 * Sets the color used for a bearish line bar candle is an even period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @param colorBearishEven The color used for a bearish line bar candle is an even period.
	 */
	public void setColorBearishEven(Color colorBearishEven) {
		this.colorBearishEven = colorBearishEven;
	}

	/**
	 * Returns the color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBullishOdd The color used for a bullish line/bar/candle in an odd period.
	 */
	public Color getColorBullishOdd() {
		return colorBullishOdd;
	}

	/**
	 * Sets the color used for a bullish line/bar/candle in an odd period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 * 
	 * @param colorBullishOdd The color used for a bullish line/bar/candle in an odd period.
	 */
	public void setColorBullishOdd(Color colorBullishOdd) {
		this.colorBullishOdd = colorBullishOdd;
	}

	/**
	 * Returns the color used for a bearish line bar candle is an odd period. For periods lower than day, the color
	 * changes when the day changes, for the day period when the week changes, for the week the month and for the
	 * monththe year.
	 * 
	 * @return the colorBearishOdd The color used for a bearish line bar candle is an odd period.
	 */
	public Color getColorBearishOdd() {
		return colorBearishOdd;
	}

	/**
	 * Sets the color used for a bearish line bar candle is an odd period. For periods lower than day, the color changes
	 * when the day changes, for the day period when the week changes, for the week the month and for the monththe year.
	 * 
	 * @param colorBearishOdd The color used for a bearish line bar candle is an odd period.
	 */
	public void setColorBearishOdd(Color colorBearishOdd) {
		this.colorBearishOdd = colorBearishOdd;
	}

	/**
	 * Returns the border color that applies only to candlesticks and histograms.
	 * 
	 * @return the colorBorder The border color.
	 */
	public Color getColorBorder() {
		return colorBorder;
	}

	/**
	 * Sets the border color that applies only to candlesticks and histograms.
	 * 
	 * @param colorBorder The border color.
	 */
	public void setColorBorder(Color colorBorder) {
		this.colorBorder = colorBorder;
	}

	/**
	 * Returns a boolean indicating if the border with the specified border color, should be painted. Applies only to
	 * candlesticks and histograms and is explicitly set although it could be deduced setting the border color to null.
	 * 
	 * @return A boolean that indicates if the border color should be painted.
	 */
	public boolean isPaintBorder() {
		return paintBorder;
	}

	/**
	 * Set a boolean indicating if the border with the specified border color, should be painted. Applies only to
	 * candlesticks and histograms and is explicitly set although it could be deduced setting the border color to null.
	 * 
	 * @param paintBorder A boolean that indicates if the border color should be painted.
	 */
	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}

	/**
	 * Sets a boolean indicating if the color should be raised in candlesticks and histograms.
	 * 
	 * @return A boolean indicating if the color should be raised in candlesticks and histograms.
	 */
	public boolean isColorRaised() {
		return colorRaised;
	}

	/**
	 * Sets a boolean indicating if the color should be raised in candlesticks and histograms.
	 * 
	 * @param colorRaised A boolean indicating if the color should be raised in candlesticks and histograms.
	 */
	public void setColorRaised(boolean colorRaised) {
		this.colorRaised = colorRaised;
	}

	/**
	 * Returns the stroke that applies to lines, bars and candlesticks and histograms borders.
	 * 
	 * @return The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Sets the stroke that applies to lines, bars and candlesticks and histograms borders.
	 * 
	 * @param stroke The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	/**
	 * Returns the brightness factor.
	 * 
	 * @return The brightness factor.
	 */
	public double getBrightnessFactor() {
		return brightnessFactor;
	}

	/**
	 * Sets the brightness factor.
	 * 
	 * @param brightnessFactor The brightness factor.
	 */
	public void setBrightnessFactor(double brightnessFactor) {
		if (brightnessFactor <= 0 || brightnessFactor >= 1) {
			throw new IllegalArgumentException("Brightness factor must be > 0 and < 1");
		}
		this.brightnessFactor = brightnessFactor;
	}

	/**
	 * Returns the plot type tp apply to the data item pointed by this plot properties.
	 * 
	 * @return The plot type tp apply to the data item pointed by this plot properties.
	 */
	public PlotType getPlotType() {
		return plotType;
	}

	/**
	 * Sets the plot type tp apply to the data item pointed by this plot properties.
	 * 
	 * @param plotType The plot type tp apply to the data item pointed by this plot properties.
	 */
	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

}
