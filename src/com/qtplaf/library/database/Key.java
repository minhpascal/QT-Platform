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

import java.util.ArrayList;

import com.qtplaf.library.util.list.ListUtils;

/**
 * A value key contains the list of values that conform the key. Useful for mapping multi-segment keys.
 *
 * @author Miquel Sas
 */
public class Key extends ArrayList<Value> implements Comparable<Object> {

	/**
	 * Default constructor.
	 */
	public Key() {
		super();
	}

	/**
	 * Compares this key with the argument object. Returns 0 if they are equal, -1 if this value is less than the
	 * argument, and 1 if it is greater.
	 *
	 * @return An integer.
	 * @param o The Object to compare with.
	 */
	@Override
	public int compareTo(Object o) {
		return ListUtils.compareTo(this, o);
	}
}
