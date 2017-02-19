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

package com.qtplaf.platform.statistics.backup;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;

/**
 * Defines a smoothed average used as a movement descriptor.
 * 
 * @author Miquel Sas
 */
public class AverageOld implements Comparable<AverageOld> {

	/**
	 * The range is used to calculate maximums and minimums of the range period, before and after.
	 */
	public static class Range {
		/** The period. */
		private int period;

		/**
		 * Constructor.
		 * 
		 * @param period The period.
		 */
		public Range(int period) {
			super();
			this.period = period;
		}

		/**
		 * Returns the period.
		 * 
		 * @return The period.
		 */
		public int getPeriod() {
			return period;
		}
	}

	/**
	 * A pair of averages, fast and slow, to calculate the spread.
	 */
	public static class Spread {
		/** Fast average. */
		private AverageOld fastAverage;
		/** Slow average. */
		private AverageOld slowAverage;
		/** The normalizer to use when calculating discretional values. */
		private NormalizedStateValueDescriptor normalizer;

		/**
		 * Constructor.
		 * 
		 * @param fastAverage The fast average.
		 * @param slowAverage The slow average.
		 * @param normalizer The normalizer to use when calculating discretional values.
		 */
		public Spread(AverageOld fastAverage, AverageOld slowAverage, NormalizedStateValueDescriptor normalizer) {
			super();
			this.fastAverage = fastAverage;
			this.slowAverage = slowAverage;
			this.normalizer = normalizer;
		}

		/**
		 * Returns the fast average.
		 * 
		 * @return The fast average.
		 */
		public AverageOld getFastAverage() {
			return fastAverage;
		}

		/**
		 * Returns the slow average.
		 * 
		 * @return The slow average.
		 */
		public AverageOld getSlowAverage() {
			return slowAverage;
		}

		/**
		 * Returns the normalizer to use when calculating discretional values.
		 * 
		 * @return The normalizer.
		 */
		public NormalizedStateValueDescriptor getNormalizer() {
			return normalizer;
		}

	}

	/**
	 * The speed class packs the average which speed has to be calculated and the normalized used to calculate
	 * discretional values.
	 */
	public static class Speed {
		/** The average. */
		private AverageOld average;
		/** The normalizer to use when calculating discretional values. */
		private NormalizedStateValueDescriptor normalizer;

		/**
		 * Constructor.
		 * 
		 * @param average The average.
		 * @param normalizer The normalizer.
		 */
		public Speed(AverageOld average, NormalizedStateValueDescriptor normalizer) {
			super();
			this.average = average;
			this.normalizer = normalizer;
		}

		/**
		 * Returns the average.
		 * 
		 * @return The average.
		 */
		public AverageOld getAverage() {
			return average;
		}

		/**
		 * Returns the normalizer.
		 * 
		 * @return The normalizer.
		 */
		public NormalizedStateValueDescriptor getNormalizer() {
			return normalizer;
		}

	}

	/** Average period. */
	private int period;
	/** Smoothing periods. */
	private int[] smooths;

	/**
	 * Constructor.
	 * 
	 * @param period Average period.
	 * @param averages Smoothing periods.
	 */
	public AverageOld(int period, int... smooths) {
		super();
		this.period = period;
		this.smooths = smooths;
	}

	/**
	 * Returns the name of the average.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return "average_" + getPeriod();
	}

	/**
	 * Returns the header of the average.
	 * 
	 * @return The header.
	 */
	public String getHeader() {
		return "Avg-" + getPeriod();
	}

	/**
	 * Returns the label of the average.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		StringBuilder b = new StringBuilder();
		b.append("Average (");
		b.append(getPeriod());
		for (int smooth : getSmooths()) {
			b.append(", ");
			b.append(smooth);
		}
		b.append(")");
		return b.toString();
	}

	/**
	 * Returns the SMA period.
	 * 
	 * @return The SMA period.
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * Returns the smoothing periods.
	 * 
	 * @return The smoothing periods.
	 */
	public int[] getSmooths() {
		return smooths;
	}

	/**
	 * Compare to sort.
	 */
	@Override
	public int compareTo(AverageOld avg) {
		return Integer.valueOf(getPeriod()).compareTo(Integer.valueOf(avg.getPeriod()));
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		return getLabel();
	}
}