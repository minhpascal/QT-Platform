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
	private boolean colorRaised = false;
	/**
	 * The stroke that applies to lines, bars and candlesticks and histograms borders.
	 */
	private Stroke stroke = new BasicStroke();
	/**
	 * The brightness factor to apply for raised colors.
	 */
	private double brightnessFactor = 0.95;
	/**
	 * Default constructor.
	 */
	public PlotProperties() {
		super();
	}

	/**
	 * Check whether this plot properties are equal to the argument object.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlotProperties) {
			PlotProperties pp = (PlotProperties) obj;
			if (!getColorBullishEven().equals(pp.getColorBullishEven())) {
				return false;
			}
			if (!getColorBearishEven().equals(pp.getColorBearishEven())) {
				return false;
			}
			if (!getColorBullishOdd().equals(pp.getColorBullishOdd())) {
				return false;
			}
			if (!getColorBearishOdd().equals(pp.getColorBearishOdd())) {
				return false;
			}
			if (!getColorBorder().equals(pp.getColorBorder())) {
				return false;
			}
			if (isPaintBorder() != pp.isPaintBorder()) {
				return false;
			}
			if (isColorRaised() != pp.isColorRaised()) {
				return false;
			}
			if (!getStroke().equals(pp.getStroke())) {
				return false;
			}
			if (getBrightnessFactor() != pp.getBrightnessFactor()) {
				return false;
			}
			return true;
		}
		return false;
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

}
