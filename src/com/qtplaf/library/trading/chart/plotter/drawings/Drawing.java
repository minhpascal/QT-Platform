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

import com.qtplaf.library.trading.chart.plotter.Plotter;

/**
 * Base class of all drawings. As a general rule a drawing should store the variables that define it, like indexes and
 * values, and return the shapes using a <i>Plotter</i> the retrieve their points coordinates.
 * 
 * @author Miquel Sas
 */
public abstract class Drawing {

	/**
	 * Default constructor.
	 */
	public Drawing() {
	}

	/**
	 * Returns this drawing shape.
	 * 
	 * @param plotter The plotter.
	 * @return The shape.
	 */
	public abstract Shape getShape(Plotter plotter);
}
