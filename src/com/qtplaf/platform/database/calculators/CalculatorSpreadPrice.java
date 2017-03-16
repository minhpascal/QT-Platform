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

import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.configuration.Average;

/**
 * Spread between the weighted close price and an average (also already calculated, normally the fastest).
 *
 * @author Miquel Sas
 */
public class CalculatorSpreadPrice implements Calculator {

	/** Average. */
	private Average average;

	/**
	 * Constructor.
	 * 
	 * @param average The average (normally the fastest)
	 */
	public CalculatorSpreadPrice(Average average) {
		super();
		this.average = average;
	}

	/**
	 * Return the value.
	 * 
	 * @param fields The list of fields.
	 */
	@Override
	public Value getValue(Record record) {
		double high = record.getValue(Fields.High).getDouble();
		double low = record.getValue(Fields.Low).getDouble();
		double close = record.getValue(Fields.Close).getDouble();
		double avg = record.getValue(Fields.average(average)).getDouble();
		double spread = (((high + low + (2 * close)) / 4) / avg) - 1;
		return new Value(spread);
	}

}
