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

package com.qtplaf.platform.database.configuration;

import com.qtplaf.library.ai.rlearning.function.Normalizer;

/**
 * The speed class packs the average which speed has to be calculated and the normalizer used to calculate discretional
 * values.
 *
 * @author Miquel Sas
 */
public class Speed {
	/** The average. */
	private Average average;
	/** The normalizer to use when calculating discretional values. */
	private Normalizer normalizer;
	/** A boolean that indicates if the spread should be inclided in the state key. */
	private boolean stateKey = false;

	/**
	 * Constructor.
	 */
	public Speed() {
		super();
	}

	/**
	 * Returns the average.
	 * 
	 * @return The average.
	 */
	public Average getAverage() {
		return average;
	}

	/**
	 * set the average.
	 * 
	 * @param average The average.
	 */
	public void setAverage(Average average) {
		this.average = average;
	}

	/**
	 * Returns the normalizer.
	 * 
	 * @return The normalizer.
	 */
	public Normalizer getNormalizer() {
		return normalizer;
	}

	/**
	 * Set the normalizer.
	 * 
	 * @param normalizer The normalizer.
	 */
	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}

	/**
	 * Returns the state key flag.
	 * 
	 * @return The state key flag.
	 */
	public boolean isStateKey() {
		return stateKey;
	}

	/**
	 * Set the state key flag.
	 * 
	 * @param stateKey The state key flag.
	 */
	public void setStateKey(boolean stateKey) {
		this.stateKey = stateKey;
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return "speed_" + getAverage().getPeriod();
	}

	/**
	 * Returns the header.
	 * 
	 * @return The header.
	 */
	public String getHeader() {
		return "Speed-" + getAverage().getPeriod();
	}

	/**
	 * Returns the label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return "Speed " + getAverage().getPeriod();
	}
}