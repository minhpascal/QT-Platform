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

package com.qtplaf.library.ai.rlearning;

import com.qtplaf.library.math.Calculator;

/**
 * A normalized state value descriptor.
 * 
 * @author Miquel Sas
 */
public class NormalizedStateValueDescriptor extends StateValueDescriptor {

	/**
	 * The maximum value from the series.
	 */
	private double maximum;
	/**
	 * The minimum value from the series.
	 */
	private double minimum;

	/**
	 * 
	 * @param maximum The maximum.
	 * @param minimum The minimum.
	 */
	public NormalizedStateValueDescriptor(double maximum, double minimum) {
		super();
		this.maximum = maximum;
		this.minimum = minimum;
	}

	/**
	 * Returns the maximum.
	 * 
	 * @return The maximum.
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Returns the minimum.
	 * 
	 * @return The minimum.
	 */
	public double getMinimum() {
		return minimum;
	}

	/**
	 * Returns the state value given a data value, conveniently normalised..
	 * 
	 * @param value The data value.
	 * @return The state value.
	 */
	@Override
	public double getValue(double value) {
		return Calculator.normalize(value, maximum, minimum);
	}

}
