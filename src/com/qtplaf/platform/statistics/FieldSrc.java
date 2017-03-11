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
import com.qtplaf.platform.database.Names.Fields;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.statistics.averages.Suffix;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Calculation;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;
import com.qtplaf.platform.util.FieldUtils;

/**
 * Manage fields related to an instrument-period.
 *
 * @author Miquel Sas
 */
public class FieldSrc {

	/**
	 * Properties set to fields.
	 */
	public static class Properties {

		/**
		 * Returns the average property of the field.
		 * 
		 * @param field The source field.
		 * @return The average.
		 */
		public static Average getAverage(Field field) {
			return (Average) field.getProperty("average");
		}

		/**
		 * Sets the average property to the field.
		 * 
		 * @param field The field.
		 * @param average The average.
		 */
		private static void setAverage(Field field, Average average) {
			field.setProperty("average", average);
		}

		/**
		 * Returns the spread property of the field.
		 * 
		 * @param field The source field.
		 * @return The spread.
		 */
		public static Spread getSpread(Field field) {
			return (Spread) field.getProperty("spread");
		}

		/**
		 * Sets the spread property to the field.
		 * 
		 * @param field The field.
		 * @param spread The spread.
		 */
		private static void setSpread(Field field, Spread spread) {
			field.setProperty("spread", spread);
		}

		/**
		 * Returns the speed property of the field.
		 * 
		 * @param field The source field.
		 * @return The speed.
		 */
		public static Speed getSpeed(Field field) {
			return (Speed) field.getProperty("speed");
		}

		/**
		 * Sets the speed property to the field.
		 * 
		 * @param field The field.
		 * @param speed The speed.
		 */
		private static void setSpeed(Field field, Speed speed) {
			field.setProperty("speed", speed);
		}

		/**
		 * Return the source field property.
		 * 
		 * @param field The field.
		 * @return The source field.
		 */
		public static Field getSourceField(Field field) {
			return (Field) field.getProperty("source-field");
		}

		/**
		 * Sets the source field property for the field.
		 * 
		 * @param field The field.
		 * @param source The source field.
		 */
		private static void setSourceField(Field field, Field source) {
			field.setProperty("source-field", source);
		}

		/**
		 * Returns the calculation property of a field.
		 * 
		 * @param field The field.
		 * @return The normalizer.
		 */
		public static Calculation getCalculation(Field field) {
			return (Calculation) field.getProperty("calculation");
		}

		/**
		 * Set the calculation property of the field.
		 * 
		 * @param field The field.
		 * @param normalizer The normalizer.
		 */
		private static void setCalculation(Field field, Calculation calculation) {
			field.setProperty("calculation", calculation);
		}

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
	public FieldSrc(Session session, Instrument instrument, Period period) {
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
		Field field = mapFields.get(Fields.Average);
		if (field == null) {
			field = Domains.getDouble(session, "average", "Average", "Average value");
			field.setFunction("avg(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(Fields.Average, field);
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
			field = Domains.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			Properties.setAverage(field, average);
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
		Field field = mapFields.get(Fields.AvgStd1);
		if (field == null) {
			field = Domains.getDouble(session, "avgstd_1", "AvgStd_1", "Avg/1 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(session, 2));
			mapFields.put(Fields.AvgStd1, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getAvgStd2() {
		Field field = mapFields.get(Fields.AvgStd2);
		if (field == null) {
			field = Domains.getDouble(session, "avgstd_2", "AvgStd_2", "Avg/2 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(session, 2));
			mapFields.put(Fields.AvgStd2, field);
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
			field = Domains.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			Properties.setCalculation(field, calculation);
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
		Field field = mapFields.get(Fields.Close);
		if (field == null) {
			field = FieldUtils.getClose(session, Fields.Close);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(Fields.Close, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate count field.
	 * 
	 * @return The field.
	 */
	public Field getCount() {
		Field field = mapFields.get(Fields.Count);
		if (field == null) {
			field = Domains.getInteger(session, Fields.Count);
			field.setFunction("count(*)");
			mapFields.put(Fields.Count, field);
		}
		return field;
	}

	/**
	 * Returns the data filter field.
	 * 
	 * @return The field.
	 */
	public Field getDataFilter() {
		Field field = mapFields.get(Fields.DataFilter);
		if (field == null) {
			field = Domains.getString(session, Fields.DataFilter, 10);
			mapFields.put(Fields.DataFilter, field);
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
			field = Domains.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			Properties.setSourceField(field, source);
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
		Field field = mapFields.get(Fields.High);
		if (field == null) {
			field = FieldUtils.getHigh(session, Fields.High);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(Fields.High, field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getIndex() {
		Field field = mapFields.get(Fields.Index);
		if (field == null) {
			field = FieldUtils.getIndex(session, Fields.Index);
			mapFields.put(Fields.Index, field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getIndexGroup() {
		Field field = mapFields.get(Fields.IndexGroup);
		if (field == null) {
			field = FieldUtils.getIndex(session, Fields.IndexGroup);
			mapFields.put(Fields.IndexGroup, field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getIndexIn() {
		Field field = mapFields.get(Fields.IndexIn);
		if (field == null) {
			field = FieldUtils.getIndex(session, Fields.IndexIn);
			mapFields.put(Fields.IndexIn, field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getIndexOut() {
		Field field = mapFields.get(Fields.IndexOut);
		if (field == null) {
			field = FieldUtils.getIndex(session, Fields.IndexOut);
			mapFields.put(Fields.IndexOut, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getLow() {
		Field field = mapFields.get(Fields.Low);
		if (field == null) {
			field = FieldUtils.getLow(session, Fields.Low);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(Fields.Low, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate maximum field.
	 * 
	 * @return The field.
	 */
	public Field getMaximum() {
		Field field = mapFields.get(Fields.Maximum);
		if (field == null) {
			field = Domains.getDouble(session, Fields.Maximum, "Maximum", "Maximum value");
			field.setFunction("max(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(Fields.Maximum, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate minimum field.
	 * 
	 * @return The field.
	 */
	public Field getMinimum() {
		Field field = mapFields.get(Fields.Minimum);
		if (field == null) {
			field = Domains.getDouble(session, Fields.Minimum, "Minimum", "Minimum value");
			field.setFunction("min(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(Fields.Minimum, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getMinMax() {
		Field field = mapFields.get(Fields.MinMax);
		if (field == null) {
			int length = 3;
			field = Domains.getString(session, Fields.MinMax, length);
			mapFields.put(Fields.MinMax, field);
		}
		return field;
	}

	/**
	 * Returns field definition for name field, a 40 chars string used as name of fields.
	 * 
	 * @return The field definition.
	 */
	public Field getName() {
		Field field = mapFields.get(Fields.Name);
		if (field == null) {
			int length = 40;
			field = Domains.getString(session, Fields.Name, length);
			mapFields.put(Fields.Name, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	public Field getOpen() {
		Field field = mapFields.get(Fields.Open);
		if (field == null) {
			String header = "Open";
			String label = "Open price";
			field = Domains.getDouble(session, Fields.Open, header, label);
			field.setFormatter(Formatters.getTickValue(session, instrument));
			mapFields.put(Fields.Open, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	public Field getPeriod() {
		Field field = mapFields.get(Fields.Period);
		if (field == null) {
			field = Domains.getInteger(session, Fields.Period);
			mapFields.put(Fields.Period, field);
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
			field = Domains.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			Properties.setSpeed(field, speed);
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
			field = Domains.getDouble(session, name, header, label);
			field.setFormatter(Formatters.getValueFormatter(session, suffix));
			Properties.setSpread(field, spread);
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
		Field field = mapFields.get(Fields.State);
		if (field == null) {
			int length = 100;
			field = Domains.getString(session, Fields.State, length);
			field.setNullable(true);
			mapFields.put(Fields.State, field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getStateInput() {
		Field field = mapFields.get(Fields.StateIn);
		if (field == null) {
			int length = 100;
			field = Domains.getString(session, Fields.StateIn, length);
			mapFields.put(Fields.StateIn, field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getStateOutput() {
		Field field = mapFields.get(Fields.StateOut);
		if (field == null) {
			int length = 100;
			field = Domains.getString(session, Fields.StateOut, length);
			mapFields.put(Fields.StateOut, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate stddev field.
	 * 
	 * @return The field.
	 */
	public Field getStdDev() {
		Field field = mapFields.get(Fields.StdDev);
		if (field == null) {
			field = Domains.getDouble(session, Fields.StdDev, "Std Dev", "Standard deviation value");
			field.setFunction("stddev(value)");
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(Fields.StdDev, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getTime() {
		Field field = mapFields.get(Fields.Time);
		if (field == null) {
			field = FieldUtils.getTime(session, Fields.Time);
			mapFields.put(Fields.Time, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getTimeFmt() {
		Field field = mapFields.get(Fields.TimeFmt);
		if (field == null) {
			field = FieldUtils.getTimeFmt(session, Fields.TimeFmt);
			field.setFormatter(Formatters.getTimeFmtValue(period));
			field.setCalculator(Formatters.getTimeFmtValue(period));
			mapFields.put(Fields.TimeFmt, field);
		}
		return field;
	}

	/**
	 * Returns the value field.
	 * 
	 * @return The field.
	 */
	public Field getValue() {
		Field field = mapFields.get(Fields.Value);
		if (field == null) {
			String header = "Value";
			String label = "Value";
			field = Domains.getDouble(session, Fields.Value, header, label);
			field.setFormatter(new DataValue(session, 10));
			mapFields.put(Fields.Value, field);
		}
		return field;
	}
}
