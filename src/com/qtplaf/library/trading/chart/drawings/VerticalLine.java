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

import java.awt.Color;
import java.awt.Stroke;

/**
 * A vertical line drawing.
 *
 * @author Miquel Sas
 */
public class VerticalLine extends Line {

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The index.
	 */
	public VerticalLine(int index) {
		super(index, index, Double.MAX_VALUE, Double.MIN_VALUE);
	}

	/**
	 * Constructor assigning the values.
	 * 
	 * @param index The index.
	 * @param stroke The stroke.
	 * @param The color.
	 */
	public VerticalLine(int index, Stroke stroke, Color color) {
		super(index, index, Double.MAX_VALUE, Double.MIN_VALUE, stroke, color);
	}
}
