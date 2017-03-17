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
 * Weighted sum a list of fields (of double values)
 *
 * @author Miquel Sas
 */
public class CalculatorWeightedSum implements Calculator {

	/**
	 * Item.
	 */
	class Item {
		String name;
		double weight;

		Item(String name, double weight) {
			this.name = name;
			this.weight = weight;
		}
	}

	/** Items. */
	private List<Item> items = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public CalculatorWeightedSum() {
		super();
	}

	/**
	 * Add a field name with its weight.
	 * 
	 * @param name The field name.
	 * @param weight The weight.
	 */
	public void add(String name, double weight) {
		items.add(new Item(name, weight));
	}

	/**
	 * Calculate and return the value.
	 * 
	 * @param record The record that contains the field.
	 * @return The calculated value.
	 */
	public Value getValue(Record record) {
		double value = 0;
		double totalWeight = 0;
		for (Item item : items) {
			String name = item.name;
			double weight = item.weight;
			value += (record.getValue(name).getDouble() * weight);
			totalWeight += weight;
		}
		value = value / totalWeight;
		return new Value(value);
	}

}
