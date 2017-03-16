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

package com.qtplaf.library.trading.pattern;

import com.qtplaf.library.trading.data.DataList;

/**
 * Generic data pattern. Receives a data list and an index and it is requested to identify a categorized a pattern.
 *
 * @author Miquel Sas
 */
public abstract class Pattern {

	/** Id. */
	private String id;
	/** Description. */
	private String description;

	/**
	 * Returns the unique identifier of the pattern.
	 * 
	 * @return The identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 * 
	 * @param id The id.set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns a description of the patter.
	 * 
	 * @return A description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Check if the pattern can be identified at the current data and index.
	 * 
	 * @param dataList The data list.
	 * @param index The current index.
	 * @return A boolean indicating that the pattern has been identified.
	 */
	public abstract boolean isPattern(DataList dataList, int index);
}
