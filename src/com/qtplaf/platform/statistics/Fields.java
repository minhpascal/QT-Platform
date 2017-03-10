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

import java.util.HashMap;
import java.util.Map;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.platform.database.Formatters;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.statistics.averages.Suffix;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Calculation;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.FieldUtils;

/**
 * Manage fields related to an instrument-period.
 *
 * @author Miquel Sas
 */
public class Fields {
	/**
	 * Returns the average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public static Average getPropertyAverage(Field field) {
		return (Average) field.getProperty("average");
	}

	/**
	 * Sets the average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private static void setPropertyAverage(Field field, Average average) {
		field.setProperty("average", average);
	}

	/**
	 * Returns the spread property of the field.
	 * 
	 * @param field The source field.
	 * @return The spread.
	 */
	public static Spread getPropertySpread(Field field) {
		return (Spread) field.getProperty("spread");
	}

	/**
	 * Sets the spread property to the field.
	 * 
	 * @param field The field.
	 * @param spread The spread.
	 */
	private static void setPropertySpread(Field field, Spread spread) {
		field.setProperty("spread", spread);
	}

	/**
	 * Returns the speed property of the field.
	 * 
	 * @param field The source field.
	 * @return The speed.
	 */
	public static Speed getPropertySpeed(Field field) {
		return (Speed) field.getProperty("speed");
	}

	/**
	 * Sets the speed property to the field.
	 * 
	 * @param field The field.
	 * @param speed The speed.
	 */
	private static void setPropertySpeed(Field field, Speed speed) {
		field.setProperty("speed", speed);
	}

	/**
	 * Return the source field property.
	 * 
	 * @param field The field.
	 * @return The source field.
	 */
	public static Field getPropertySourceField(Field field) {
		return (Field) field.getProperty("source-field");
	}

	/**
	 * Sets the source field property for the field.
	 * 
	 * @param field The field.
	 * @param source The source field.
	 */
	private static void setPropertySourceField(Field field, Field source) {
		field.setProperty("source-field", source);
	}

	/**
	 * Returns the calculation property of a field.
	 * 
	 * @param field The field.
	 * @return The normalizer.
	 */
	public static Calculation getPropertyCalculation(Field field) {
		return (Calculation) field.getProperty("calculation");
	}

	/**
	 * Set the calculation property of the field.
	 * 
	 * @param field The field.
	 * @param normalizer The normalizer.
	 */
	private static void setPropertyCalculation(Field field, Calculation calculation) {
		field.setProperty("calculation", calculation);
	}

	/** Working session. */
	private Session session;
	/** The instrument. */
	private Instrument instrument;
	/** The period. */
	private Period period;

	/** Map of fields. */
	private Map<String, Field> mapFields = new HashMap<>();

	/** Map of field lists. */

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param instrument Instrument.
	 * @param period Period.
	 */
	public Fields(Session session, Instrument instrument, Period period) {
		this.session = session;
		this.instrument = instrument;
		this.period = period;
	}

	/**
	 * Returns the aggregate average field.
	 * 
	 * @return The field.
	 */
	public Field getAverage() {
		String name = "average";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "average", "Average", "Average value");
			field.setFunction("avg(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	public Field getAverage(Average average) {
		String name = average.getName();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = average.getHeader();
			String label = average.getLabel();
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getAvgStd1() {
		String name = "avgstd1";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "avgstd_1", "AvgStd_1", "Avg/1 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(session, 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getAvgStd2() {
		String name = "avgstd2";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "avgstd_2", "AvgStd_2", "Avg/2 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(session, 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the calculation field.
	 * 
	 * @param calculation The calculation
	 * @param suffix The suffix.
	 * @return The field.
	 */
	public Field getCalculation(Calculation calculation, String suffix) {
		String name = calculation.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = calculation.getHeader() + "-" + suffix;
			String label = calculation.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			setPropertyCalculation(field, calculation);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the close field.
	 * 
	 * @return The field.
	 */
	public Field getClose() {
		String name = "close";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getClose(session, name);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate count field.
	 * 
	 * @return The field.
	 */
	public Field getCount() {
		String name = "count";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getInteger(session, "count", "Count", "Count records for the same value");
			field.setFunction("count(*)");
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the field definition for a delta field.
	 * 
	 * @param source The source field.
	 * @param suffix The suffix (raw, nrm)
	 * @return The field definition.
	 */
	public Field getDelta(Field source, String suffix) {
		String name = "delta_" + source.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Delta-" + source.getName() + "-" + suffix;
			String label = "Delta " + source.getName() + " - " + suffix;
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			setPropertySourceField(field, source);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the field definition for the delta close (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getDeltaClose() {
		return getDelta(getClose(), Suffix.nrm);
	}

	/**
	 * Returns the field definition for the delta high (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getDeltaHight() {
		return getDelta(getHigh(), Suffix.nrm);
	}

	/**
	 * Returns the field definition for the delta low (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getDeltaLow() {
		return getDelta(getLow(), Suffix.nrm);
	}
	
	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	public Field getHigh() {
		String name = "high";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getHigh(session, name);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getIndex() {
		String name = "index";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index";
			String label = "Index";
			field = FieldUtils.getIndex(session, name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getIndexGroup() {
		String name = "index_group";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index group";
			String label = "Index group";
			field = FieldUtils.getIndex(session, name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getIndexInput() {
		String name = "index_in";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Input index";
			String label = "Input index";
			field = FieldUtils.getIndex(session, name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getIndexOutput() {
		String name = "index_out";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Output index";
			String label = "Output index";
			field = FieldUtils.getIndex(session, name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getLow() {
		String name = "low";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getLow(session, name);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate maximum field.
	 * 
	 * @return The field.
	 */
	public Field getMaximum() {
		String name = "maximum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "maximum", "Maximum", "Maximum value");
			field.setFunction("max(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate minimum field.
	 * 
	 * @return The field.
	 */
	public Field getMinimum() {
		String name = "minimum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "minimum", "Minimum", "Minimum value");
			field.setFunction("min(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getMinMax() {
		String name = "min_max";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 3;
			String header = "Min/Max";
			String label = "Minimum/Maximum calculation";
			field = DomainUtils.getString(session, name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns field definition for name field, a 40 chars string used as name of fields.
	 * 
	 * @return The field definition.
	 */
	public Field getName() {
		String name = "name";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 40;
			String header = "Name";
			String label = "Value name";
			field = DomainUtils.getString(session, name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	public Field getOpen() {
		String name = "open";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Open";
			String label = "Open price";
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	public Field getPeriod() {
		String name = "period";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Period";
			String label = "Period";
			field = DomainUtils.getInteger(session, name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the speed field.
	 * 
	 * @param speed The speed definition.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getSpeed(Speed speed, String suffix) {
		String name = speed.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = speed.getHeader() + "-" + suffix;
			String label = speed.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			setPropertySpeed(field, speed);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field.
	 * 
	 * @param spread Spread.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getSpread(Spread spread, String suffix) {
		String name = spread.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = spread.getHeader() + "-" + suffix;
			String label = spread.getLabel() + " - " + suffix;
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			setPropertySpread(field, spread);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key state field.
	 * 
	 * @return The field.
	 */
	public Field getState() {
		String name = "state";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "State key";
			String label = "State key";
			field = DomainUtils.getString(session, name, length, header, label);
			field.setNullable(true);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getStateInput() {
		String name = "state_in";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Input state key";
			String label = "Input state key";
			field = DomainUtils.getString(session, name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getStateOutput() {
		String name = "state_out";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Output state key";
			String label = "Output state key";
			field = DomainUtils.getString(session, name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate stddev field.
	 * 
	 * @return The field.
	 */
	public Field getStdDev() {
		String name = "stddev";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(session, "stddev", "Std Dev", "Standard deviation value");
			field.setFunction("stddev(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getTime() {
		String name = "time";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTime(session, name);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getTimeFmt() {
		String name = "time_fmt";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTimeFmt(session, name);
			field.setFormatter(Formatters.getTimeFmtValue(period));
			field.setCalculator(Formatters.getTimeFmtValue(period));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getTransitionValueClose() {
		String name = "value_close";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value close";
			String label = "Transition value close";
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getTransitionValueHigh() {
		String name = "value_high";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value high";
			String label = "Transition value high";
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.
	 * 
	 * @return The field.
	 */
	public Field getTransitionValueLow() {
		String name = "value_low";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value low";
			String label = "Transition value low";
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the value field.
	 * 
	 * @return The field.
	 */
	public Field getValue() {
		String name = "value";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value";
			String label = "Value";
			field = DomainUtils.getDouble(session, name, header, label);
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(name, field);
		}
		return field;
	}
}
