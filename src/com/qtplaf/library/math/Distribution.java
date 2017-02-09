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

package com.qtplaf.library.math;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.list.ListUtils;

/**
 * Performs the distribution of a list of values, between their minimum and maximum, in the desired number of segments.
 *
 * @author Miquel Sas
 */
public class Distribution {

	/**
	 * The segment.
	 */
	public class Segment {
		/** Minimum. */
		private double minimum;
		/** Maximum. */
		private double maximum;
		/** List of values in the segment. */
		private List<Double> values = new ArrayList<>();

		/**
		 * Constructor.
		 * 
		 * @param minimum Minimum.
		 * @param maximum Maximum.
		 */
		public Segment(double minimum, double maximum) {
			super();
			this.minimum = minimum;
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
		 * Returns the maximum.
		 * 
		 * @return The maximum.
		 */
		public double getMaximum() {
			return maximum;
		}

		/**
		 * Returns the vaklues in the segment.
		 * 
		 * @return The number of values.
		 */
		public List<Double> getValues() {
			return values;
		}

		/**
		 * Returns the average.
		 * 
		 * @return The average.
		 */
		public double getAverage() {
			return Calculator.average(values);
		}

		/**
		 * Returns the standard deviation.
		 * 
		 * @return The standard deviation.
		 */
		public double getStdDev() {
			return Calculator.stddev(values);
		}

		/**
		 * Returns the segment size.
		 * 
		 * @return The size.
		 */
		public int getSize() {
			return values.size();
		}
	}

	/** The number of segments between minimum and maximum or size of the distribution. */
	private int size;
	/** The list of segments. */
	private List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param size The size or number of segments of the distribution.
	 */
	public Distribution(int size) {
		super();
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		this.size = size;
	}

	/**
	 * Returns the optimum segment, that with the most values.
	 * 
	 * @return The optimum segment.
	 */
	public Segment getSegmentOptimum() {
		Segment optimum = null;
		for (Segment segment : segments) {
			if (optimum == null) {
				optimum = segment;
			}
			if (segment.getSize() > optimum.getSize()) {
				optimum = segment;
			}
		}
		return optimum;
	}

	/**
	 * Distribute the list of values.
	 * 
	 * @param values The list of values to distribute.
	 */
	public void distribute(List<Double> values) {
		distribute(ListUtils.toArray(values));
	}

	/**
	 * Distribute the list of values.
	 * 
	 * @param values The list of values to distribute.
	 */
	public void distribute(double[] values) {
		// Create the segments.
		createSegments(values);
		// Distribute.
		for (double value : values) {
			Segment segment = getSegment(value);
			segment.getValues().add(value);
		}
	}

	/**
	 * Returns the segment where the value should be.
	 * 
	 * @param value The value.
	 * @return The segment where the value should be.
	 */
	private Segment getSegment(double value) {
		for (Segment segment : segments) {
			if (segment.getMinimum() <= value && segment.getMaximum() >= value) {
				return segment;
			}
		}
		throw new IllegalStateException();
	}

	/**
	 * Creates the list of segments.
	 * 
	 * @param values The list of values.
	 */
	private void createSegments(double[] values) {
		segments.clear();
		double minimum = Calculator.minimum(values);
		double maximum = Calculator.maximum(values);
		double step = (maximum - minimum) / Double.valueOf(size).doubleValue();
		while (true) {
			double segmentMin = minimum;
			double segmentMax = minimum + step;
			segments.add(new Segment(segmentMin, segmentMax));
			minimum = Math.nextUp(segmentMax);
			if (minimum > maximum) {
				break;
			}
		}
	}
}
