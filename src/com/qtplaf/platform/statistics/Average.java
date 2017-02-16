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

package com.qtplaf.platform.statistics;

/**
 * Defines a smoothed average used as a movement descriptor.
 * 
 * @author Miquel Sas
 */
public class Average implements Comparable<Average> {

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
	public Average(int period, int... smooths) {
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
	public int compareTo(Average avg) {
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