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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;
import com.qtplaf.platform.statistics.Average.Range;
import com.qtplaf.platform.statistics.Average.Speed;
import com.qtplaf.platform.statistics.Average.Spread;
import com.qtplaf.platform.util.DomainUtils;

/**
 * Averages configuration for source, ranges and normalize statistics.
 *
 * @author Miquel Sas
 */
public class Configuration {

	/** Field property: average. */
	private static final String PropertyAverage = "average";

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

	/** Map of cached fields. */
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
			field = DomainUtils.getDouble(getSession(), name, name, header, label, label);
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
		Field field = mapFields.get("close");
		if (field == null) {
			field = DomainUtils.getClose(getSession(), "close");
			mapFields.put("close", field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexGroup() {
		Field field = mapFields.get("index_group");
		if (field == null) {
			field = DomainUtils.getIndex(getSession(), "index_group");
			field.setHeader("Index group");
			field.setLabel("Index group");
			field.setTitle("Index group of correlative indexes,");
			mapFields.put("index_group", field);
		}
		return field;
	}

	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	public Field getFieldHigh() {
		Field field = mapFields.get("high");
		if (field == null) {
			field = DomainUtils.getHigh(getSession(), "high");
			mapFields.put("high", field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndex() {
		Field field = mapFields.get("index");
		if (field == null) {
			field = DomainUtils.getIndex(getSession(), "index");
			mapFields.put("index", field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexInput() {
		Field field = mapFields.get("index_in");
		if (field == null) {
			field = DomainUtils.getIndex(getSession(), "index_in");
			field.setHeader("Input index");
			field.setLabel("Input index");
			field.setTitle("Input index");
			mapFields.put("index_in", field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndexOutput() {
		Field field = mapFields.get("index_out");
		if (field == null) {
			field = DomainUtils.getIndex(getSession(), "index_out");
			field.setHeader("Output index");
			field.setLabel("Output index");
			field.setTitle("Output index");
			mapFields.put("index_out", field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKeyInput() {
		Field field = mapFields.get("key_in");
		if (field == null) {
			String header = "Input key";
			String label = "Input key";
			field = new Field();
			field.setSession(getSession());
			field.setName("key_in");
			field.setHeader(header);
			field.setLabel(label);
			field.setType(Types.String);
			field.setLength(100);
			mapFields.put("key_in", field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldKeyOutput() {
		Field field = mapFields.get("key_out");
		if (field == null) {
			String header = "Output key";
			String label = "Output key";
			field = new Field();
			field.setSession(getSession());
			field.setName("key_out");
			field.setHeader(header);
			field.setLabel(label);
			field.setType(Types.String);
			field.setLength(100);
			mapFields.put("key_out", field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getFieldMinMax() {
		Field field = mapFields.get("min_max");
		if (field == null) {
			field = DomainUtils.getMinMax(getSession(), "min_max");
			field.setHeader("Min/Max");
			mapFields.put("min_max", field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getFieldLow() {
		Field field = mapFields.get("low");
		if (field == null) {
			field = DomainUtils.getLow(getSession(), "low");
			mapFields.put("low", field);
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
		return (Average) field.getProperty(PropertyAverage);
	}

	/**
	 * Sets the average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverage(Field field, Average average) {
		field.setProperty(PropertyAverage, average);
	}
}
