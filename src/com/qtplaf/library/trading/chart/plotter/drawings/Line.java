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

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.qtplaf.library.trading.chart.plotter.Plotter;

/**
 * A line drawing.
 * 
 * @author Miquel Sas
 */
public class Line extends Drawing {
	/**
	 * Index 1 (start).
	 */
	private int index1;
	/**
	 * Index 2 (end).
	 */
	private int index2;
	/**
	 * Value 1 (start).
	 */
	private double v1;
	/**
	 * Value 2 (end).
	 * */
	private double v2;
	/**
	 * The stroke.
	 */
	private Stroke stroke;
	/**
	 * The color.
	 */
	private Color color;

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index1 Index 1 (start).
	 * @param index2 Index 2 (end).
	 * @param v1 Value 1.
	 * @param v2 Value 2.
	 */
	public Line(int index1, int index2, double v1, double v2, Stroke stroke, Color color) {
		this.index1 = index1;
		this.index2 = index2;
		this.v1 = v1;
		this.v2 = v2;
		this.stroke = stroke;
		this.color = color;
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
	 * Returns the stroke.
	 * 
	 * @return The stroke
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Returns the color.
	 * 
	 * @return The color
	 */
	public Color getColor() {
		return color;
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
	 * @param plotter The plotter.
	 * @return The line shape.
	 */
	public Shape getShape(Plotter plotter) {

		// Coordinates.
		int coordinateXCurrent = plotter.getDrawingCenterCoordinateX(plotter.getCoordinateX(index2));
		int coordinateXPrevious = plotter.getDrawingCenterCoordinateX(plotter.getCoordinateX(index1));
		int coordinateYCurrent = plotter.getCoordinateY(v2);
		int coordinateYPrevious = plotter.getCoordinateY(v1);

		GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 1);
		shape.moveTo(coordinateXPrevious, coordinateYPrevious);
		shape.lineTo(coordinateXCurrent, coordinateYCurrent);

		return shape;
	}
}