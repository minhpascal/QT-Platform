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

/**
 * An item is a defined statistics, identified by a code or id and a description.
 */
public class Reference implements Comparable<Reference> {

	public static final String Id = "id";
	public static final String Title = "title";
	public static final String Description = "desc";

	private String id;
	private String title;
	private String description;
	private Configuration configuration;

	/**
	 * Constructor.
	 * 
	 * @param id The id or code.
	 * @param title The title or short description.
	 */
	public Reference(String id, String title) {
		super();
		this.id = id;
		this.title = title;
		this.description = title;
	}

	/**
	 * Constructor.
	 * 
	 * @param id The id or code.
	 * @param title The title or short description.
	 * @param description The description.
	 */
	public Reference(String id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}

	/**
	 * Returns the averages configuration.
	 * 
	 * @return The averages configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Set the averages configuration.
	 * 
	 * @param configuration The averages configuration.
	 */
	public void setConfiguration(Configuration configuration) {
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

	/**
	 * Returns the statistics description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			Reference item = (Reference) obj;
			return getId().equals(item.getId());
		}
		return false;
	}

	@Override
	public int compareTo(Reference item) {
		return getId().compareTo(item.getId());
	}

	@Override
	public String toString() {
		return getId() + " - " + getTitle();
	}
}