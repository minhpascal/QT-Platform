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

package com.qtplaf.platform.ztrash;

/**
 * An item is a defined statistics, identified by a code or id and a description.
 */
public class ReferenceOld implements Comparable<ReferenceOld> {

	public static final String Id = "id";
	public static final String Title = "title";
	public static final String Description = "desc";

	private String id;
	private String title;
	private ConfigurationOld configuration;

	/**
	 * Constructor.
	 * 
	 * @param id The id or code.
	 * @param title The title or short description.
	 */
	public ReferenceOld(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	/**
	 * Returns the averages configuration.
	 * 
	 * @return The averages configuration.
	 */
	public ConfigurationOld getConfiguration() {
		return configuration;
	}

	/**
	 * Set the averages configuration.
	 * 
	 * @param configuration The averages configuration.
	 */
	public void setConfiguration(ConfigurationOld configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the statistics id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the statistics title.
	 * 
	 * @return The title or short description.
	 */
	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReferenceOld) {
			ReferenceOld item = (ReferenceOld) obj;
			return getId().equals(item.getId());
		}
		return false;
	}

	@Override
	public int compareTo(ReferenceOld item) {
		return getId().compareTo(item.getId());
	}

	@Override
	public String toString() {
		return getId() + " - " + getTitle();
	}
}