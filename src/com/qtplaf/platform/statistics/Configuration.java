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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.platform.statistics.Average.Range;
import com.qtplaf.platform.statistics.Average.Speed;
import com.qtplaf.platform.statistics.Average.Spread;

/**
 * Averages configuration for source, ranges and normalize statistics.
 *
 * @author Miquel Sas
 */
public class Configuration {

	/** An id that identifies the configuration root. */
	private String id;
	/** A sort description. */
	private String title;

	/** List of averages for source and normalize calculations. */
	private List<Average> averages = new ArrayList<>();
	/** List of spread to calculate over averages. */
	private List<Spread> spreads = new ArrayList<>();
	/** List of speeds to calculate over averages. */
	private List<Speed> speeds = new ArrayList<>();
	/** List of ranges for min-max calculations. */
	private List<Range> ranges = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param id The id.
	 * @param title The title or short description.
	 */
	public Configuration(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	/**
	 * Returns the id that identifies the configuration.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the short description.
	 * 
	 * @return The short description.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Add an average
	 * 
	 * @param average The average.
	 */
	public void addAverage(Average average) {
		averages.add(average);
	}

	/**
	 * Add a spread.
	 * 
	 * @param spread The spread.
	 */
	public void addSpread(Spread spread) {
		spreads.add(spread);
	}

	/**
	 * Add a speed.
	 * 
	 * @param speed The speed.
	 */
	public void addSpeed(Speed speed) {
		speeds.add(speed);
	}

	/**
	 * Add a range for min-max calculations.
	 * 
	 * @param range The range.
	 */
	public void addRange(Range range) {
		ranges.add(range);
	}

	/**
	 * Returns the list of averages.
	 * 
	 * @return The list of averages.
	 */
	public List<Average> getAverages() {
		return averages;
	}

	/**
	 * Returns the list of (periods) for ranges.
	 * 
	 * @return The list of (periods) for ranges.
	 */
	public List<Range> getRanges() {
		return ranges;
	}

	/**
	 * Returns the spreads.
	 * 
	 * @return The spreads.
	 */
	public List<Spread> getSpreads() {
		return spreads;
	}

	/**
	 * Returns the speeds.
	 * 
	 * @return The speeds.
	 */
	public List<Speed> getSpeeds() {
		return speeds;
	}

	/**
	 * Returns the string that describes the averages.
	 * 
	 * @return The string.
	 */
	public String toStringAverages() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < averages.size(); i++) {
			if (i > 0) {
				b.append("-");
			}
			b.append(averages.get(i).getPeriod());
		}
		return b.toString();
	}

	/**
	 * Returns the string that describes the ranges.
	 * 
	 * @return The string.
	 */
	public String toStringRanges() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < ranges.size(); i++) {
			if (i > 0) {
				b.append("-");
			}
			b.append(ranges.get(i).getPeriod());
		}
		return b.toString();
	}
}
