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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.parameters.LinePlotParameters;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;

/**
 * A line drawing.
 * 
 * @author Miquel Sas
 */
public class Line extends Drawing {

	/** Index 1 (start). */
	private int index1;
	/** Index 2 (end). */
	private int index2;
	/** Value 1 (start). */
	private double v1;
	/** Value 2 (end). */
	private double v2;
	/** Plot parameters. */
	private LinePlotParameters parameters = new LinePlotParameters();

	/**
	 * Constructor assigning the values, with a default stroke and color.
	 * 
	 * @param index1 Index 1 (start).
	 * @param index2 Index 2 (end).
	 * @param v1 Value 1.
	 * @param v2 Value 2.
	 */
	public Line(int index1, int index2, double v1, double v2) {
		this(index1, index2, v1, v2, new BasicStroke(), Color.BLACK);
	}

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index1 Index 1 (start).
	 * @param index2 Index 2 (end).
	 * @param v1 Value 1.
	 * @param v2 Value 2.
	 * @param stroke The stroke.
	 * @param The color.
	 */
	public Line(int index1, int index2, double v1, double v2, Stroke stroke, Color color) {
		this.index1 = index1;
		this.index2 = index2;
		this.v1 = v1;
		this.v2 = v2;
		getParameters().setStroke(stroke);
		getParameters().setColor(color);
		setName("Line");
	}

	/**
	 * Returns the index 1 (start).
	 * 
	 * @return The index 1 (start).
	 */
	public int getIndex1() {
		return index1;
	}

	/**
	 * Returns the index 2 (end).
	 * 
	 * @return The index 2 (end).
	 */
	public int getIndex2() {
		return index2;
	}

	/**
	 * Returns the vaue 1.
	 * 
	 * @return The value 1.
	 */
	public double getV1() {
		return v1;
	}

	/**
	 * Returns the vaue 2.
	 * 
	 * @return The value 2.
	 */
	public double getV2() {
		return v2;
	}

	/**
	 * Returns the plot parameters.
	 * 
	 * @return The plot parameters.
	 */
	public LinePlotParameters getParameters() {
		return parameters;
	}

	/**
	 * Set the plot parameters.
	 * 
	 * @param parameters The plot parameters.
	 */
	public void setParameters(LinePlotParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Check if this shape is bullish.
	 * 
	 * @return A boolean indicating if this shape is bullish.
	 */
	public boolean isBullish() {
		return v2 > v1;
	}

	/**
	 * Check if this shape is bearish.
	 * 
	 * @return A boolean indicating if this shape is bearish.
	 */
	public boolean isBearish() {
		return v2 < v1;
	}

	/**
	 * Returns the line shape.
	 * 
	 * @param context The plotter context.
	 * @return The line shape.
	 */
	public Shape getShape(PlotterContext context) {

		int index1 = this.index1;
		int index2 = this.index2;
		double v1 = this.v1;
		double v2 = this.v2;

		// Vertical line.
		if (index1 == index2) {
			if (v1 == Double.MAX_VALUE) {
				v1 = context.getPlotData().getMaximumValue();
			}
			if (v1 == Double.MIN_VALUE) {
				v1 = context.getPlotData().getMinimumValue();
			}
			if (v2 == Double.MAX_VALUE) {
				v2 = context.getPlotData().getMaximumValue();
			}
			if (v2 == Double.MIN_VALUE) {
				v2 = context.getPlotData().getMinimumValue();
			}
		}

		// Horizontal line.
		if (v1 == v2) {
			if (index1 == Integer.MAX_VALUE) {
				index1 = context.getPlotData().getEndIndex();
			}
			if (index1 == Integer.MIN_VALUE) {
				index1 = context.getPlotData().getStartIndex();
			}
			if (index2 == Integer.MAX_VALUE) {
				index2 = context.getPlotData().getEndIndex();
			}
			if (index2 == Integer.MIN_VALUE) {
				index2 = context.getPlotData().getStartIndex();
			}
		}

		// Coordinates.
		int x2 = context.getDrawingCenterCoordinateX(context.getCoordinateX(index2));
		int x1 = context.getDrawingCenterCoordinateX(context.getCoordinateX(index1));
		int y2 = context.getCoordinateY(v2);
		int y1 = context.getCoordinateY(v1);

		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 1);
		shape.moveTo(x1, y1);
		shape.lineTo(x2, y2);

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

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set the stroke and color.
		g2.setStroke(getParameters().getStroke());
		g2.setColor(getParameters().getColor());

		// Draw
		g2.draw(getShape(context));

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}
}