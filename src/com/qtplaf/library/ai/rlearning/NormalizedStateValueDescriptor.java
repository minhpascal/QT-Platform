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
import com.qtplaf.library.util.NumberUtils;

/**
 * A normalized state value descriptor. Values are normalized only with the ranges [1, 0], [0, -1] or [1, -1].
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
	 * Constructor assigning maximum and minimum.
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
	 * Constructor assigning maximum and minimum.
	 * 
	 * @param maximum The maximum.
	 * @param minimum The minimum.
	 * @param scale The scale.
	 */
	public NormalizedStateValueDescriptor(double maximum, double minimum, int scale) {
		super();
		this.maximum = maximum;
		this.minimum = minimum;
		setScale(scale);
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
	 * Returns the number of value of this normalized value descriptor.
	 * 
	 * @return The number of values.
	 */
	public int size() {
		int base = getScale() * 10;
		if (getMaximum() > 0 && getMinimum() >= 0) {
			return base + 1;
		}
		if (getMaximum() <= 0 && getMinimum() < 0) {
			return base + 1;
		}
		if (getMaximum() > 0 && getMinimum() < 0) {
			return (base * 2) + 1;
		}
		return 0;
	}

	/**
	 * Returns the state value given a data value, conveniently normalised..
	 * 
	 * @param value The data value.
	 * @return The state value.
	 */
	@Override
	public double getValue(double value) {
		return NumberUtils.round(Calculator.normalize(value, maximum, minimum), getScale());
	}

	/**
	 * Check that the value descriptor has the necessary properties set.
	 */
	public void validate() {
		if (maximum == minimum) {
			throw new IllegalStateException();
		}
		super.validate();
	}
}
