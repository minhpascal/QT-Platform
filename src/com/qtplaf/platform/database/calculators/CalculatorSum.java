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

package com.qtplaf.platform.database.calculators;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;

/**
 * Sum a list of fields (of double values)
 *
 * @author Miquel Sas
 */
public class CalculatorSum implements Calculator {

	/** Field names. */
	private List<String> names = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public CalculatorSum() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param fields The list of fields.
	 */
	public CalculatorSum(String... names) {
		super();
		add(names);
	}

	/**
	 * Add field to sum.
	 * 
	 * @param fields The list of fields.
	 */
	public void add(String... names) {
		for (String name : names) {
			this.names.add(name);
		}
	}

	/**
	 * Calculate and return the value.
	 * 
	 * @param record The record that contains the field.
	 * @return The calculated value.
	 */
	public Value getValue(Record record) {
		double value = 0;
		for (String name : names) {
			value += record.getValue(name).getDouble();
		}
		return new Value(value);
	}

}
