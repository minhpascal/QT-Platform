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
package com.qtplaf.library.trading.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Stroke;

/**
 * Encapsulates in a single class all plot parameters involved in plotting objects like lines, bars and candles, and
 * vertical and horizontal axis values.
 * 
 * @author Miquel Sas
 */
public class PlotParameters {

	/**
	 * The <i>JChart</i> parent of this parameters.
	 */
	private JChart chart;
	/**
	 * The frame or plot insets, as a top, left, bottom and right factor of the available plot area. If, for example,
	 * the available width is 1400 pixels, a left inset of 0.02 will leave 28 pixels free of any paint to the left.
	 */
	private double[] chartPlotInsets = new double[] { 0.02, 0.01, 0.01, 0.02 };
	/**
	 * Chart plotter: cross cursor width.
	 */
	private int chartCrossCursorWidth = -1;
	/**
	 * Chart plotter: cross cursor height.
	 */
	private int chartCrossCursorHeight = -1;
	/**
	 * Chart plotter: cross cursor color.
	 */
	private Color chartCrossCursorColor = Color.GRAY;
	/**
	 * Chart plotter: cross cursor stroke.
	 */
	private Stroke chartCrossCursorStroke = new BasicStroke(
		0.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 3.0f, new float[] { 3.0f }, 0.0f);
	/**
	 * ChartCross cursor circle radius.
	 */
	private int chartCrossCursorCircleRadius = 20;
	/**
	 * An integer that denotes the system cursor that applies, default is cross hair..
	 */
	private int chartCursorTypePredefined = Cursor.DEFAULT_CURSOR;
	/**
	 * The cursot type to use.
	 */
	private CursorType chartCursorType = CursorType.Custom;
	/**
	 * The bar width factor that is used to calculate the width of a bar or candle depending on the available width per
	 * bar.
	 */
	private double chartBarWidthFactor = 0.75;
	/**
	 * Chart info: the infomation separator color.
	 */
	private Color infoSeparatorColor = Color.BLACK;
	/**
	 * Chart info: the information separator string.
	 */
	private String infoSeparatorString = " - ";
	/**
	 * Chart info: the information text insets.
	 */
	private Insets infoTextInsets = new Insets(1, 2, 1, 0);
	/**
	 * Chart info: the information text font.
	 */
	private Font infoTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * The background color when the info panel gains focus.
	 */
	private Color infoBackgroundColor = new Color(208, 208, 208);
	/**
	 * The brightness factor to apply to the background color for non selected chart.
	 */
	private double infoBackgroundBrightnessFactor = 0.7;
	/**
	 * Vertical axis: the vertical text font.
	 */
	private Font verticalAxisTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * Vertical axis: the length of the small line before each value.
	 */
	private int verticalAxisLineLength = 5;
	/**
	 * Vertical axis: the line color.
	 */
	private Color verticalAxisLineColor = Color.BLACK;
	/**
	 * Vertical axis: the insets to surround the price with a rectangle.
	 */
	private Insets verticalAxisSurroundInsets = new Insets(1, 4, 1, 4);
	/**
	 * Vertical axis: insets of the text.
	 */
	private Insets verticalAxisTextInsets = new Insets(5, 8, 5, 8);
	/**
	 * Vertical axis: the stroke for the surround rectangle.
	 */
	private Stroke verticalAxisSurroundStroke = new BasicStroke();
	/**
	 * The surround cursor value fill color.
	 */
	private Color verticalAxisSurroundFillColor = new Color(224, 224, 224);
	/**
	 * Horizontal axis font.
	 */
	private Font horizontalAxisTextFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	/**
	 * Horizontal axis: insets of the text.
	 */
	private Insets horizontalAxisTextInsets = new Insets(3, 2, 3, 2);
	/**
	 * Horizontal axis: the line and text color.
	 */
	private Color horizontalAxisColor = Color.GRAY;
	/**
	 * Horizontal axis: the stroke for the surround rectangle.
	 */
	private Stroke horizontalAxisLineStroke = new BasicStroke();

	/**
	 * Constructor.
	 * 
	 * @param chart The parent chart.
	 */
	public PlotParameters(JChart chart) {
		super();
		this.chart = chart;
	}

	/**
	 * Returns the cross cursor width.
	 * 
	 * @return The cross cursor width.
	 */
	public int getChartCrossCursorWidth() {
		return chartCrossCursorWidth;
	}

	/**
	 * Sets the cross cursor width.
	 * 
	 * @param chartCrossCursorWidth The cross cursor width.
	 */
	public void setChartCrossCursorWidth(int chartCrossCursorWidth) {
		this.chartCrossCursorWidth = chartCrossCursorWidth;
	}

	/**
	 * Returns the cross cursor height.
	 * 
	 * @return The cross cursor height.
	 */
	public int getChartCrossCursorHeight() {
		return chartCrossCursorHeight;
	}

	/**
	 * Sets the cross cursor height.
	 * 
	 * @param chartCrossCursorHeight The cross cursor height.
	 */
	public void setChartCrossCursorHeight(int chartCrossCursorHeight) {
		this.chartCrossCursorHeight = chartCrossCursorHeight;
	}

	/**
	 * Returns the cross cursor color.
	 * 
	 * @return The cross cursor color.
	 */
	public Color getChartCrossCursorColor() {
		return chartCrossCursorColor;
	}

	/**
	 * Sets the cross cursor color.
	 * 
	 * @param chartCrossCursorColor The cross cursor color.
	 */
	public void setChartCrossCursorColor(Color crossCursorColor) {
		this.chartCrossCursorColor = crossCursorColor;
		chart.repaint();
	}

	/**
	 * Returns the cross cursor stroke.
	 * 
	 * @return The cross cursor stroke.
	 */
	public Stroke getChartCrossCursorStroke() {
		return chartCrossCursorStroke;
	}

	/**
	 * Sets the cross cursor stroke.
	 * 
	 * @param chartCrossCursorStroke The cross cursor stroke.
	 */
	public void setChartCrossCursorStroke(Stroke crossCursorStroke) {
		this.chartCrossCursorStroke = crossCursorStroke;
		chart.repaint();
	}

	/**
	 * Returns the cross cursor circle radius.
	 * 
	 * @return The cross cursor circle radius.
	 */
	public int getChartCrossCursorCircleRadius() {
		return chartCrossCursorCircleRadius;
	}

	/**
	 * Sets the cross cursor circle radius, less or equal zero paints no circle.
	 * 
	 * @param chartCrossCursorCircleRadius The cross cursor circle radius, less or equal zero paints no circle.
	 */
	public void setChartCrossCursorCircleRadius(int chartCrossCursorCircleRadius) {
		this.chartCrossCursorCircleRadius = chartCrossCursorCircleRadius;
	}

	/**
	 * Returns the chart cursor type predefined.
	 * 
	 * @return The chart cursor type predefined.
	 */
	public int getChartCursorTypePredefined() {
		return chartCursorTypePredefined;
	}

	/**
	 * Sets the chart cursor type.
	 * 
	 * @param chartCursorTypePredefined The chart cursor type.
	 */
	public void setChartCursorTypePredefined(int chartCursorTypePredefined) {
		if (chartCursorTypePredefined < -1 || chartCursorTypePredefined > Cursor.MOVE_CURSOR) {
			throw new IllegalArgumentException("Illegal cursor type");
		}
		this.chartCursorTypePredefined = chartCursorTypePredefined;
	}

	/**
	 * Returns the default chart cursor type to use.
	 * 
	 * @return The default chart cursor type to use.
	 */
	public CursorType getChartCursorType() {
		return chartCursorType;
	}

	/**
	 * Sets the default cursor type to use.
	 * 
	 * @param chartCursorType The default cursor type to use.
	 */
	public void setChartCursorType(CursorType chartCursorType) {
		this.chartCursorType = chartCursorType;
	}

	/**
	 * Sets the frame or plot insets, as a top, left, bottom and right factor of the available plot area. If, for
	 * example, the available width is 1400 pixels, a left inset of 0.02 will leave 28 pixels free of any paint to the
	 * left.
	 * 
	 * @param top Top factor.
	 * @param left Left factor.
	 * @param bottom Bottom factor.
	 * @param right Right factor.
	 */
	public void setChartPlotInsets(double top, double left, double bottom, double right) {
		chartPlotInsets[0] = top;
		chartPlotInsets[1] = left;
		chartPlotInsets[2] = bottom;
		chartPlotInsets[3] = right;
		chart.repaint();
	}

	/**
	 * Returns the plot inset top as a factor of the plot height.
	 * 
	 * @return The plot inset top.
	 */
	public double getChartPlotInsetTopFactor() {
		return chartPlotInsets[0];
	}

	/**
	 * Returns the plot inset left as a factor of the plot width.
	 * 
	 * @return The plot inset left.
	 */
	public double getChartPlotInsetLeftFactor() {
		return chartPlotInsets[1];
	}

	/**
	 * Returns the plot inset bottom as a factor of the plot height.
	 * 
	 * @return The plot inset bottom.
	 */
	public double getChartPlotInsetBottomFactor() {
		return chartPlotInsets[2];
	}

	/**
	 * Returns the plot inset right as a factor of the plot width.
	 * 
	 * @return The plot inset right.
	 */
	public double getChartPlotInsetRightFactor() {
		return chartPlotInsets[3];
	}

	/**
	 * Returns the plot insets calculated with the plot factors.
	 * 
	 * @return The plot insets.
	 */
	public Insets getChartPlotInsets(Dimension size) {
		int paintAreaWidth = (int) size.getWidth();
		int paintAreaHeight = (int) size.getHeight();
		int insetTop = (int) (paintAreaHeight * getChartPlotInsetTopFactor());
		int insetLeft = (int) (paintAreaWidth * getChartPlotInsetLeftFactor());
		int insetBottom = (int) (paintAreaHeight * getChartPlotInsetBottomFactor());
		int insetRight = (int) (paintAreaWidth * getChartPlotInsetRightFactor());
		return new Insets(insetTop, insetLeft, insetBottom, insetRight);
	}

	/**
	 * Returns the factor to calculate the width of a bar. As a general rule, it can be 75% of the available width per
	 * bar, as an odd number, and if the result is less than 2, plot just a vertical line of 1 pixel width. Default is
	 * 0.75.
	 * 
	 * @return The factor to calculate the width of a bar.
	 */
	public double getChartBarWidthFactor() {
		return chartBarWidthFactor;
	}

	/**
	 * Sets the factor to calculate the width of a bar. As a general rule, it can be 75% of the available width per bar,
	 * as an odd number, and if the result is less than 2, plot just a vertical line of 1 pixel width. Default is 0.75.
	 * 
	 * @param chartBarWidthFactor The factor to calculate the width of a bar.
	 */
	public void setChartBarWidthFactor(double barWidthFactor) {
		if (barWidthFactor <= 0 || barWidthFactor > 1) {
			throw new IllegalArgumentException("Data width factor must be between 0 and 1.");
		}
		this.chartBarWidthFactor = barWidthFactor;
		chart.repaint();
	}

	/**
	 * Returns the chart info separator color.
	 * 
	 * @return The chart info separator color.
	 */
	public Color getInfoSeparatorColor() {
		return infoSeparatorColor;
	}

	/**
	 * Sets the chart info separator color.
	 * 
	 * @param infoSeparatorColor The color for the chart info separator string.
	 */
	public void setInfoSeparatorColor(Color infoSeparatorColor) {
		this.infoSeparatorColor = infoSeparatorColor;
		chart.repaint();
	}

	/**
	 * Returns the chart info separator string.
	 * 
	 * @return The chart info separator string.
	 */
	public String getInfoSeparatorString() {
		return infoSeparatorString;
	}

	/**
	 * Sets the chsrt info separator string.
	 * 
	 * @param infoSeparatorString The string that separates info items in the info chart.
	 */
	public void setInfoSeparatorString(String infoSeparatorString) {
		this.infoSeparatorString = infoSeparatorString;
		chart.repaint();
	}

	/**
	 * Returns the chart info text insets.
	 * 
	 * @return The chart info text insets.
	 */
	public Insets getInfoTextInsets() {
		return infoTextInsets;
	}

	/**
	 * Sets the info text insets.
	 * 
	 * @param top The top inset.
	 * @param left The left inset.
	 * @param bottom The bottom inset.
	 */
	public void setInfoTextInsets(Insets infoTextInsets) {
		this.infoTextInsets = infoTextInsets;
		chart.repaint();
	}

	/**
	 * Returns the info text font.
	 * 
	 * @return The info text font.
	 */
	public Font getInfoTextFont() {
		return infoTextFont;
	}

	/**
	 * Sets the info text font.
	 * 
	 * @param infoTextFont The font to set.
	 */
	public void setInfoTextFont(Font infoTextFont) {
		this.infoTextFont = infoTextFont;
		chart.repaint();
	}

	/**
	 * Returns the panel info background color when focus is gained, when the focus is lost the color is brigthner.
	 * 
	 * @return the infoBackgroundColor The panel info background.
	 */
	public Color getInfoBackgroundColor() {
		return infoBackgroundColor;
	}

	/**
	 * Set the panel info background
	 * 
	 * @param infoBackgroundColor The panel info background
	 */
	public void setInfoBackgroundColor(Color infoBackgroundColor) {
		this.infoBackgroundColor = infoBackgroundColor;
	}

	/**
	 * Returns the panel info background color brightness factor.
	 * 
	 * @return The panel info background color brightness factor.
	 */
	public double getInfoBackgroundBrightnessFactor() {
		return infoBackgroundBrightnessFactor;
	}

	/**
	 * Sets the panel info background color brightness factor.
	 * 
	 * @param infoBackgroundBrightnessFactor The panel info background color brightness factor.
	 */
	public void setInfoBackgroundBrightnessFactor(double infoBackgroundBrightnessFactor) {
		this.infoBackgroundBrightnessFactor = infoBackgroundBrightnessFactor;
	}

	/**
	 * Returns the vertical axis text font.
	 * 
	 * @return The vertical axis text font.
	 */
	public Font getVerticalAxisTextFont() {
		return verticalAxisTextFont;
	}

	/**
	 * Sets the vertical axis text font.
	 * 
	 * @param verticalAxisTextFonts The vertical axis text font.
	 */
	public void setVerticalAxisTextFont(Font verticalAxisTextFont) {
		this.verticalAxisTextFont = verticalAxisTextFont;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis small line length.
	 * 
	 * @return The vertical axis small line length.
	 */
	public int getVerticalAxisLineLength() {
		return verticalAxisLineLength;
	}

	/**
	 * Sets the vertical axis small line length.
	 * 
	 * @param verticalAxisLineLength The vertical axis small line length.
	 */
	public void setVerticalAxisLineLength(int verticalAxisLineLength) {
		this.verticalAxisLineLength = verticalAxisLineLength;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis line color.
	 * 
	 * @return The vertical axis line color.
	 */
	public Color getVerticalAxisLineColor() {
		return verticalAxisLineColor;
	}

	/**
	 * Set the vertical axis small line color.
	 * 
	 * @param vertiicalAxisLineColor The vertical axis small line color.
	 */
	public void setVerticalAxisLineColor(Color vertiicalAxisLineColor) {
		this.verticalAxisLineColor = vertiicalAxisLineColor;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis surround insets.
	 * 
	 * @return The vertical axis surround insets.
	 */
	public Insets getVerticalAxisSurroundInsets() {
		return verticalAxisSurroundInsets;
	}

	/**
	 * Sets the vertical axis surround insets.
	 * 
	 * @param verticalAxisSurroundInsets The vertical axis surround insets.
	 */
	public void setVerticalAxisSurroundInsets(Insets verticalAxisSurroundInsets) {
		this.verticalAxisSurroundInsets = verticalAxisSurroundInsets;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis text insets.
	 * 
	 * @return The vertical axis text insets.
	 */
	public Insets getVerticalAxisTextInsets() {
		return verticalAxisTextInsets;
	}

	/**
	 * Sets the vertical axis text insets.
	 * 
	 * @param verticalAxisTextInsets The vertical axis text insets.
	 */
	public void setVerticalAxisTextInsets(Insets verticalAxisTextInsets) {
		this.verticalAxisTextInsets = verticalAxisTextInsets;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis surround stroke.
	 * 
	 * @return The vertical axis surround stroke.
	 */
	public Stroke getVerticalAxisSurroundStroke() {
		return verticalAxisSurroundStroke;
	}

	/**
	 * Sets the vertical axis surround stroke.
	 * 
	 * @param verticalAxisSurroundStroke The vertical axis surround stroke.
	 */
	public void setVerticalAxisSurroundStroke(Stroke surroundStroke) {
		this.verticalAxisSurroundStroke = surroundStroke;
		chart.repaint();
	}

	/**
	 * Returns the vertical axis surround fill color for te cursor value. Other surround color for averages and so on
	 * will be taken from the plot color of the indicator.
	 * 
	 * @return The vertical axis surround fill color for te cursor value.
	 */
	public Color getVerticalAxisSurroundFillColor() {
		return verticalAxisSurroundFillColor;
	}

	/**
	 * Sets the vertical axis surround fill color for te cursor value. Other surround color for averages and so on will
	 * be taken from the plot color of the indicator.
	 * 
	 * @param verticalAxisSurroundFillColor The vertical axis surround fill color for te cursor value.
	 */
	public void setVerticalAxisSurroundFillColor(Color verticalAxisSurroundFillColor) {
		this.verticalAxisSurroundFillColor = verticalAxisSurroundFillColor;
		chart.repaint();
	}

	/**
	 * Returns the horizontal axis text font.
	 * 
	 * @return The horizontal axis text font.
	 */
	public Font getHorizontalAxisTextFont() {
		return horizontalAxisTextFont;
	}

	/**
	 * Sets the horizontal axis text font.
	 * 
	 * @param horizontalAxisTextFont The horizontal axis text font.
	 */
	public void setHorizontalAxisTextFont(Font horizontalAxisTextFont) {
		this.horizontalAxisTextFont = horizontalAxisTextFont;
		chart.repaint();
	}

	/**
	 * Returns the horizontal axis text insets.
	 * 
	 * @return The horizontal axis text insets.
	 */
	public Insets getHorizontalAxisTextInsets() {
		return horizontalAxisTextInsets;
	}

	/**
	 * Sets the horizontal axis text insets.
	 * 
	 * @param horizontalAxisTextInsets The horizontal axis text insets.
	 */
	public void setHorizontalAxisTextInsets(Insets horizontalAxisTextInsets) {
		this.horizontalAxisTextInsets = horizontalAxisTextInsets;
		chart.repaint();
	}

	/**
	 * Returns the horizontal axis color for text and lines.
	 * 
	 * @return The horizontal axis color for text and lines.
	 */
	public Color getHorizontalAxisColor() {
		return horizontalAxisColor;
	}

	/**
	 * Sets the horizontal axis color for text and lines.
	 * 
	 * @param horizontalAxisColor The horizontal axis color for text and lines.
	 */
	public void setHorizontalAxisColor(Color horizontalAxisColor) {
		this.horizontalAxisColor = horizontalAxisColor;
		chart.repaint();
	}

	/**
	 * Returns the horizontal axis line stroke.
	 * 
	 * @return The horizontal axis line stroke.
	 */
	public Stroke getHorizontalAxisLineStroke() {
		return horizontalAxisLineStroke;
	}

	/**
	 * Sets the horizontal axis line stroke.
	 * 
	 * @param horizontalAxisLineStroke The horizontal axis line stroke.
	 */
	public void setHorizontalAxisLineStroke(Stroke horizontalAxisLineStroke) {
		this.horizontalAxisLineStroke = horizontalAxisLineStroke;
		chart.repaint();
	}
}
