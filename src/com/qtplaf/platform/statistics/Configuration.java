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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.platform.util.DomainUtils;

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

	/** Working session. */
	private Session session;

	/** Map of fields. */
	private Map<String, Field> mapFields = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param id The id.
	 * @param title The title or short description.
	 */
	public Configuration(Session session, String id, String title) {
		super();
		this.session = session;
		this.id = id;
		this.title = title;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return session;
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
	 * Add a range for min-max calculations.
	 * 
	 * @param range The range.
	 */
	public void addRange(Range range) {
		ranges.add(range);
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
	 * Add a spread.
	 * 
	 * @param spread The spread.
	 */
	public void addSpread(Spread spread) {
		spreads.add(spread);
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

	/**
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	public Field getFieldAverage(Average average) {
		String name = average.getName();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = average.getHeader();
			String label = average.getLabel();
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the close field.
	 * 
	 * @return The field.
	 */
	public Field getFieldClose() {
		String name = "close";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Close";
			String label = "Close price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	public Field getFieldHigh() {
		String name = "high";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "High";
			String label = "High price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndex() {
		String name = "index";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index";
			String label = "Index";
			field = DomainUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexGroup() {
		String name = "index_group";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index group";
			String label = "Index group";
			field = DomainUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexInput() {
		String name = "index_in";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Input index";
			String label = "Input index";
			field = DomainUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexOutput() {
		String name = "index_out";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Output index";
			String label = "Output index";
			field = DomainUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKeyInput() {
		String name = "key_in";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Input key";
			String label = "Input key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKeyOutput() {
		String name = "key_out";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Output key";
			String label = "Output key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key state field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKeyState() {
		String name = "key_state";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "State key";
			String label = "State key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getFieldLow() {
		String name = "low";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Low";
			String label = "Low price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getFieldMinMax() {
		String name = "min_max";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 3;
			String header = "Min/Max";
			String label = "Minimum/Maximum calculation";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns field definition for name field, a 40 chars string used as name of fields.
	 * 
	 * @return The field definition.
	 */
	public Field getFieldName() {
		String name = "name";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 40;
			String header = "Name";
			String label = "Value name";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	public Field getFieldPeriod() {
		String name = "period";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Period";
			String label = "Period";
			field = DomainUtils.getInteger(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	public Field getFieldOpen() {
		String name = "open";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Open";
			String label = "Open price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
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
	public Field getFieldSpeed(Speed speed, String suffix) {
		Average average = speed.getAverage();
		String name = "speed_" + average.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Speed-" + average.getPeriod() + "-" + suffix;
			String label = "Speed " + average.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertyAverage(field, average);
			setPropertyNormalizer(field, speed.getNormalizer());
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
	public Field getFieldSpread(Spread spread, String suffix) {
		Average averageFast = spread.getFastAverage();
		Average averageSlow = spread.getSlowAverage();
		String name = "spread_" + averageFast.getPeriod() + "_" + averageSlow.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + averageFast.getPeriod() + "-" + averageSlow.getPeriod() + "-" + suffix;
			String label = "Spread " + averageFast.getPeriod() + " - " + averageSlow.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertyNormalizer(field, spread.getNormalizer());
			setPropertyAverageFast(field, averageFast);
			setPropertyAverageSlow(field, averageSlow);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field between a source field (high, low, close) and an average (normally the fast one).
	 * 
	 * @param source Source field.
	 * @param average The average.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldSpread(Field source, Average average, String suffix) {
		String name = "spread_" + source.getName() + "_" + average.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + source.getName() + "-" + average.getPeriod() + "-" + suffix;
			String label = "Spread " + source.getName() + " - " + average.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertySourceField(field, source);
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldTime() {
		String name = "time";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Time";
			String label = "Time in millis";
			field = DomainUtils.getTime(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldTimeFmt() {
		String name = "time_fmt";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Time fmt";
			String label = "Time formatted";
			field = DomainUtils.getTimeFmt(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public Average getPropertyAverage(Field field) {
		return (Average) field.getProperty("average");
	}

	/**
	 * Sets the average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverage(Field field, Average average) {
		field.setProperty("average", average);
	}

	/**
	 * Returns the fast average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public Average getPropertyAverageFast(Field field) {
		return (Average) field.getProperty("average-fast");
	}

	/**
	 * Sets the fast average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverageFast(Field field, Average average) {
		field.setProperty("average-fast", average);
	}

	/**
	 * Returns the slow average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public Average getPropertyAverageSlow(Field field) {
		return (Average) field.getProperty("average-slow");
	}

	/**
	 * Sets the slow average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverageSlow(Field field, Average average) {
		field.setProperty("average-slow", average);
	}

	/**
	 * Returns the normmalizer property of the field.
	 * 
	 * @param field The source field.
	 * @return The normalizer.
	 */
	public NormalizedStateValueDescriptor getPropertyNormalizer(Field field) {
		return (NormalizedStateValueDescriptor) field.getProperty("normalizer");
	}

	/**
	 * Sets the normalizer property for the field.
	 * 
	 * @param field The field.
	 * @param normalizer The normalizer.
	 */
	private void setPropertyNormalizer(Field field, NormalizedStateValueDescriptor normalizer) {
		field.setProperty("normalizer", normalizer);
	}

	/**
	 * Return the source field property.
	 * 
	 * @param field The field.
	 * @return The source field.
	 */
	public Field getPropertySourceField(Field field) {
		return (Field) field.getProperty("source-field");
	}

	/**
	 * Sets the source field property for the field.
	 * 
	 * @param field The field.
	 * @param source The source field.
	 */
	private void setPropertySourceField(Field field, Field source) {
		field.setProperty("source-field", source);
	}
}
