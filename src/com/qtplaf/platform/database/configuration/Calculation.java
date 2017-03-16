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
import com.qtplaf.library.database.Calculator;

/**
 * Generic field calculation, has a field calculator and a normalizer.
 *
 * @author Miquel Sas
 */
public class Calculation {

	/** Result field name. */
	private String name;
	/** Header. */
	private String header;
	/** Label. */
	private String label;
	/** Field calculator. */
	private Calculator calculator;
	/** Normalizer. */
	private Normalizer normalizer;
	/** A boolean that indicates if the calculation should be inclided in the state key. */
	private boolean stateKey = false;

	/**
	 * Constructor.
	 * 
	 * @param name The field name.
	 * @param header The header.
	 * @param label The label.
	 */
	public Calculation(String name, String header, String label) {
		super();
		this.name = name;
		this.header = header;
		this.label = label;
	}

	/**
	 * Returns the result field name.
	 * 
	 * @return The result field name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the header.
	 * 
	 * @return The header.
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Returns the label.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the calculator.
	 * 
	 * @return The calculator.
	 */
	public Calculator getCalculator() {
		return calculator;
	}

	/**
	 * Set the calculator.
	 * 
	 * @param calculator The calculator.
	 */
	public void setCalculator(Calculator calculator) {
		this.calculator = calculator;
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
}
