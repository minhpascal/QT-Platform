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

	/**
	 * Returns the name of the average.
	 * 
	 * @param avg The average.
	 * @return The name.
	 */
	public static String getAverageName(Average avg) {
		return "average_" + avg.getPeriod();
	}

	/**
	 * Returns the header of the average.
	 * 
	 * @param avg The average.
	 * @return The header.
	 */
	public static String getAverageHeader(Average avg) {
		return getAverageName(avg);
	}

	/**
	 * Returns the label of the average.
	 * 
	 * @param avg The average.
	 * @return The label.
	 */
	public static String getAverageLabel(Average avg) {
		StringBuilder b = new StringBuilder();
		b.append("Average ");
		b.append(avg.getPeriod());
		for (int smooth : avg.getSmooths()) {
			b.append(", ");
			b.append(smooth);
		}
		return b.toString();
	}

	/**
	 * Returns the spread name between a value with an id and the average.
	 * 
	 * @param id The id of the value.
	 * @param avg The average.
	 * @return The spread name.
	 */
	public static String getSpreadName(String id, Average avg) {
		return "spread_" + id + " - " + avg.getPeriod();
	}

	/**
	 * Returns the spread header between a value with an id and the average.
	 * 
	 * @param id The id of the value.
	 * @param avg The average.
	 * @return The spread header.
	 */
	public static String getSpreadHeader(String id, Average avg) {
		return getSpreadName(id, avg);
	}

	/**
	 * Returns the spread label between a value with an id and the average.
	 * 
	 * @param id The id of the value.
	 * @param avg The average.
	 * @return The spread label.
	 */
	public static String getSpreadLabel(String id, Average avg) {
		return "Spread " + id + " - " + avg.getPeriod();
	}

	/**
	 * Returns the name of the spread for two averages.
	 * 
	 * @param avgFast The fast average.
	 * @param avgSlow The slow average.
	 * @return The name for the spread.
	 */
	public static String getSpreadName(Average avgFast, Average avgSlow) {
		return "spread_" + avgFast.getPeriod() + "_" + avgSlow.getPeriod();
	}

	/**
	 * Returns the header of the spread for two averages.
	 * 
	 * @param avgFast The fast average.
	 * @param avgSlow The slow average.
	 * @return The header for the spread.
	 */
	public static String getSpreadHeader(Average avgFast, Average avgSlow) {
		return "Spread " + avgFast.getPeriod() + " - " + avgSlow.getPeriod();
	}

	/**
	 * Returns the laberl of the spread for two averages.
	 * 
	 * @param avgFast The fast average.
	 * @param avgSlow The slow average.
	 * @return The label for the spread.
	 */
	public static String getSpreadLabel(Average avgFast, Average avgSlow) {
		return getSpreadName(avgFast, avgSlow);
	}

	/**
	 * Returns the name of the average speed.
	 * 
	 * @param avg The average.
	 * @return The name.
	 */
	public static String getSpeedName(Average avg) {
		return "speed_" + avg.getPeriod();
	}

	/**
	 * Returns the header of the average speed.
	 * 
	 * @param avg The average.
	 * @return The header.
	 */
	public static String getSpeedHeader(Average avg) {
		return getSpeedName(avg);
	}

	/**
	 * Returns the label of the average speed.
	 * 
	 * @param avg The average.
	 * @return The label.
	 */
	public static String getSpeedLabel(Average avg) {
		return "Speed " + avg.getPeriod();
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
	public Average(int period, int[] smooths) {
		super();
		this.period = period;
		this.smooths = smooths;
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
}