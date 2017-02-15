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
 * <p>
 * A number of segments can be assigned to reduce the universe of states. For instance, normalized values in the range
 * <tt>[+1.0, -1.0]</tt> with a scale of 1, the resulting number of possible values is:
 * <p>
 * <tt>[+1.0, +0.9, +0.8, +0.7, +0.6, +0.5, +0.4, +0.3, +0.2, +0.1, 0.0, -0.1, -0.2, -0.3, -0.4, -0.5, -0.6, -0.7, -0.8, -0.9, -1.0]</tt>
 * <p>
 * while setting a number of segments of 4 (4 for positives and 4 for negatives), and a scale of 2, the number of
 * possible values are:
 * <p>
 * <tt>[+1.00, +0.75, +0.50, +0.25, 0.00, -0.25, -0.50, -0.75, -1.00]</tt>
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
	 * The number of segments, less than 1 determined by the scale.
	 */
	private int segments = -1;
	/**
	 * List of positive values if segments is greater than 1.
	 */
	private double[] positives;
	/**
	 * List of negative values if segments is greater than 1.
	 */
	private double[] negatives;

	/**
	 * Consttructor.
	 */
	public NormalizedStateValueDescriptor() {
		super();
	}

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
	 * set the maximum value.
	 * 
	 * @param maximum The maximum value.
	 */
	public void setMaximum(double maximum) {
		this.maximum = maximum;
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
	 * Set the minimum value.
	 * 
	 * @param minimum The minimum value.
	 */
	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	/**
	 * Returns the number of segments.
	 * 
	 * @return The number of segments.
	 */
	public int getSegments() {
		return segments;
	}

	/**
	 * Set the number of segments.
	 * 
	 * @param segments The number of segments.
	 */
	public void setSegments(int segments) {
		this.segments = segments;
	}

	/**
	 * Returns the state value given a data value, conveniently normalised..
	 * 
	 * @param value The data value.
	 * @return The state value.
	 */
	@Override
	public double getValue(double value) {
		double normalized = Calculator.normalize(value, maximum, minimum);
		if (segments <= 1) {
			if (getScale() < 0) {
				return normalized;
			}
			return NumberUtils.round(normalized, getScale());
		}
		return getValueFromSegments(normalized);
	}

	/**
	 * Returns teh value from the list of segments.
	 * 
	 * @param value The source value.
	 * @return The result value.
	 */
	private double getValueFromSegments(double value) {
		if (maximum >= 0 && minimum >= 0) {
			return getValueFromPositives(value);
		}
		if (maximum <= 0 && minimum <= 0) {
			return getValueFromNegatives(value);
		}
		if (value >= 0) {
			return getValueFromPositives(value);
		}
		return getValueFromNegatives(value);
	}

	/**
	 * Returns the value scanning the list of positive values.
	 * 
	 * @param value The source value.
	 * @return The result value.
	 */
	private double getValueFromPositives(double value) {
		if (value < 0) {
			throw new UnsupportedOperationException();
		}
		double[] positives = getPositives();
		int size = positives.length;
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				return positives[size - 1];
			}
			double curr = positives[i];
			double next = positives[i + 1];
			double max = (curr + next) / 2.0;
			if (value < max) {
				return curr;
			}
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the value scanning the list of negative values.
	 * 
	 * @param value The source value.
	 * @return The result value.
	 */
	private double getValueFromNegatives(double value) {
		if (value >= 0) {
			throw new UnsupportedOperationException();
		}
		double[] negatives = getNegatives();
		int size = negatives.length;
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				return negatives[size - 1];
			}
			double curr = negatives[i];
			double next = negatives[i + 1];
			double min = (curr + next) / 2.0;
			if (value > min) {
				return curr;
			}
		}
		throw new UnsupportedOperationException();
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

	/**
	 * Returns the array of positive values when segments is greater than 1.
	 * 
	 * @return The array of positive values when segments is greater than 1.
	 */
	public double[] getPositives() {
		if (segments <= 1) {
			throw new UnsupportedOperationException();
		}
		if (maximum <= 0) {
			throw new UnsupportedOperationException();
		}
		if (positives == null) {
			int size = segments + 1;
			positives = new double[size];
			double step = 1.0 / Double.valueOf(segments);
			double value = 0;
			for (int i = 0; i < size; i++) {
				positives[i] = NumberUtils.round(value, getScale());
				value += step;
			}
		}
		return positives;
	}

	/**
	 * Returns the array of negative values when segments is greater than 1.
	 * 
	 * @return The array of negative values when segments is greater than 1.
	 */
	public double[] getNegatives() {
		if (segments <= 1) {
			throw new UnsupportedOperationException();
		}
		if (minimum >= 0) {
			throw new UnsupportedOperationException();
		}
		if (negatives == null) {
			int size = segments + 1;
			negatives = new double[size];
			double step = (-1.0) / Double.valueOf(segments);
			double value = 0;
			for (int i = 0; i < size; i++) {
				negatives[i] = NumberUtils.round(value, getScale());
				value += step;
			}
		}
		return negatives;
	}
}
