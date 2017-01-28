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
package com.qtplaf.library.ai.rlearning;

/**
 * Descriptor of state value. Includes a description of what the value means and its scale.
 * 
 * @author Miquel Sas
 */
public abstract class StateValueDescriptor {

	/**
	 * A short id of the value.
	 */
	private String id;
	/**
	 * An optional description.
	 */
	private String description;
	/**
	 * The scale.
	 */
	private int scale;

	/**
	 * Default constructor.
	 */
	public StateValueDescriptor() {
		super();
	}

	/**
	 * Returns the id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id The id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the value description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the scale.
	 * 
	 * @return The scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Sets the scale.
	 * 
	 * @param scale The scale.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * Returns the state value given a data value.
	 * 
	 * @param value The data value.
	 * @return The state value.
	 */
	public abstract double getValue(double value);
	
	/**
	 * Check that the value descriptor has the necessary properties set.
	 */
	public void validate() {
		if (getId() == null) {
			throw new IllegalStateException();
		}
		if (getDescription() == null) {
			throw new IllegalStateException();
		}
		if (getScale() < 0) {
			throw new IllegalStateException();
		}
	}
}
