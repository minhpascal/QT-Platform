/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.connection;

import com.qtplaf.library.util.StringUtils;

/**
 * The ConnectionInfo class packs the necessary information to connect to a database using JDBC.
 *
 * @author Miquel Sas
 */
public class ConnectionInfo {

	/**
	 * Connection id.
	 */
	private String id = null;
	/**
	 * Connection description.
	 */
	private String description = null;
	/**
	 * The compatible database engine.
	 */
	private String driver = null;
	/**
	 * The database connection string.
	 */
	private String database = null;
	/**
	 * The database schema.
	 */
	private String schema = null;
	/**
	 * The user.
	 */
	private String user = null;
	/**
	 * The password.
	 */
	private String password = null;

	/**
	 * Default constructor.
	 */
	public ConnectionInfo() {
		super();
	}

	/**
	 * Returns the connection URL.
	 *
	 * @return The connection URL.
	 */
	public String getURL() {
		return getDriver() + getDatabase();
	}

	/**
	 * Get the database connection string.
	 *
	 * @return The database connection string.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Set the database connection string.
	 *
	 * @param database The database connection string.
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * Get the driver string.
	 *
	 * @return The driver string.
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Set the driver string.
	 *
	 * @param driver The driver string.
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * Get the description.
	 *
	 * @return The description.
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
	 * Get the id.
	 *
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 * <p>
	 * 
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the password.
	 * <p>
	 * 
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 * <p>
	 * 
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the default schema.
	 * <p>
	 * 
	 * @return The schema.
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * Set the default schema.
	 * <p>
	 * 
	 * @param schema The schema to set.
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Get the user.
	 * <p>
	 * 
	 * @return The user.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the user.
	 * <p>
	 * 
	 * @param user The user to set.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Returns a string representation of this connection information.
	 * <p>
	 * 
	 * @return A string
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\"");
		b.append(StringUtils.defaultString(getId()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getDescription()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getDriver()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getDatabase()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getSchema()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getUser()));
		b.append("\", \"");
		b.append(StringUtils.defaultString(getPassword()));
		b.append("\"");
		return b.toString();
	}
}
