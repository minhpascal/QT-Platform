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

package com.qtplaf.library.ai.rlearning.function;

import com.qtplaf.library.ai.rlearning.StateValueFunction;
import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.util.NumberUtils;

/**
 * A value normalizer. Values can be normalized continuous or discrete. For continuos values set a scale of -1 (less
 * than zero). VValues can be discretized eithwer by setting a scale or a number of segments. By setting a scale, the
 * number of segments between 0 and 1 is 10 for 1, 100 for 2, and so on. For a desired number of segments of 20, set the
 * number of segments to 20, without scale.
 *
 * @author Miquel Sas
 */
public class Normalizer implements StateValueFunction {

	/** The maximum value. */
	private double maximum;
	/** The minimum value. */
	private double minimum;
	/** The scale, less that 0 issues a continous value. */
	private int scale = -1;
	/** The number of segments, less than 1 determined by the scale. */
	private int segments = -1;

	/** List of positive values if segments is greater than 1. */
	private double[] positives;
	/** List of negative values if segments is greater than 1. */
	private double[] negatives;

	/**
	 * Constructor.
	 */
	public Normalizer() {
		super();
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
	 * Returns the scale.
	 * 
	 * @return The scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Sets the scale.
	 * 
	 * @param scale The scale.
	 */
	public void setScale(int scale) {
		this.scale = scale;
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
	 * Returns the calculated value given the source one.
	 * 
	 * @param value The source value.
	 * @return The calculated value.
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
	 * Returns the array of positive values when segments is greater than 1.
	 * 
	 * @return The array of positive values when segments is greater than 1.
	 */
	private double[] getPositives() {
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
				positives[i] = value;
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
	private double[] getNegatives() {
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
				negatives[i] = value;
				value += step;
			}
		}
		return negatives;
	}
}
