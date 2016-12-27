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
package com.qtplaf.library.database;

/**
 * An index key pointer structure. Entities build the list of key pointers for the primary key and cache it to directly
 * access key values of a elements without having to scan all the fields and getting the positions of those that are
 * primary key fields.
 *
 * @author Miquel Sas
 */
public class KeyPointer {

	/**
	 * The index of the value that maps the key segment.
	 */
	private int index = -1;
	/**
	 * The ascending flag.
	 */
	private boolean asc = true;

	/**
	 * Default constructor.
	 */
	public KeyPointer() {
		super();
	}

	/**
	 * Constructor assigning index, with default true ascending flag.
	 *
	 * @param index The value index.
	 */
	public KeyPointer(int index) {
		super();
		this.index = index;
		this.asc = true;
	}

	/**
	 * Constructor assigning index and ascending flag.
	 *
	 * @param index The value index.
	 * @param asc The ascending flag.
	 */
	public KeyPointer(int index, boolean asc) {
		super();
		this.index = index;
		this.asc = asc;
	}

	/**
	 * Copy constructor.
	 *
	 * @param keyPointer The key pointer to copy.
	 */
	public KeyPointer(KeyPointer keyPointer) {
		super();
		this.index = keyPointer.index;
		this.asc = keyPointer.asc;
	}

	/**
	 * Get the index.
	 * <p>
	 * 
	 * @return The value index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Check the ascending flag.
	 *
	 * @return A <code>boolean</code>
	 */
	public boolean isAsc() {
		return asc;
	}

	/**
	 * Set the ascending flag.
	 *
	 * @param asc The ascending flag.
	 */
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	/**
	 * Set the value index.
	 *
	 * @param index The value index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
