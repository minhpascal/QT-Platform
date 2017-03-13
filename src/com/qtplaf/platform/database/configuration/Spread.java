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
 * A pair of averages, fast and slow, to calculate the spread, and its normalizer.
 *
 * @author Miquel Sas
 */
public class Spread {
	/** Fast average. */
	private Average fastAverage;
	/** Slow average. */
	private Average slowAverage;
	/** The normalizer to use when calculating discretional values. */
	private Normalizer normalizer;
	/** A boolean that indicates if the spread should be inclided in the state key. */
	private boolean stateKey = false;

	/**
	 * Constructor.
	 */
	public Spread() {
		super();
	}

	/**
	 * Returns the fast average.
	 * 
	 * @return The fast average.
	 */
	public Average getFastAverage() {
		return fastAverage;
	}

	/**
	 * Set the fast average.
	 * 
	 * @param fastAverage The fast average.
	 */
	public void setFastAverage(Average fastAverage) {
		this.fastAverage = fastAverage;
	}

	/**
	 * Returns the slow average.
	 * 
	 * @return The slow average.
	 */
	public Average getSlowAverage() {
		return slowAverage;
	}

	/**
	 * Set the slow averate.
	 * 
	 * @param slowAverage The slow average.
	 */
	public void setSlowAverage(Average slowAverage) {
		this.slowAverage = slowAverage;
	}

	/**
	 * Returns the normalizer to use when calculating discretional values.
	 * 
	 * @return The normalizer.
	 */
	public Normalizer getNormalizer() {
		return normalizer;
	}

	/**
	 * set the normalizer.
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
	 * Returns the spread name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return "spread_" + getFastAverage().getPeriod() + "_" + getSlowAverage().getPeriod();
	}

	/**
	 * Returns the spread header.
	 * 
	 * @return The header.
	 */
	public String getHeader() {
		return "Spread-" + getFastAverage().getPeriod() + "-" + getSlowAverage().getPeriod();
	}

	/**
	 * Returns the spread label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return "Spread " + getFastAverage().getPeriod() + "-" + getSlowAverage().getPeriod();
	}
}