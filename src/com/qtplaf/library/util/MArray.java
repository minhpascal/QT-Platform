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

package com.qtplaf.library.util;

import java.lang.reflect.Array;

/**
 * Multidimentional array.
 *
 * @author Miquel Sas
 */
public class MArray<T> {

	/**
	 * Data.
	 */
	private Object data;
	/**
	 * Dimensions.
	 */
	private int[] dimensions;

	/**
	 * Constructor.
	 * 
	 * @param dataClass The type of the array data.
	 * @param dimensions The dimensions.
	 */
	public MArray(Class<T> dataClass, int... dimensions) {
		this.dimensions = dimensions;
		this.data = Array.newInstance(dataClass, dimensions);
	}

	@SuppressWarnings("unchecked")
	public T get(int... indexes) {
		checkIndexes(indexes);
		Object[] items = new Object[indexes.length];
		items[0] = data;
		for (int i = 0; i < indexes.length - 1; i++) {
			int index = indexes[i];
			if (index < 0 || index >= dimensions[i]) {
				throw new ArrayIndexOutOfBoundsException();
			}
			if (i < indexes.length - 1) {
				items[i + 1] = Array.get(items[i], index);
			}
		}
		int last = indexes.length - 1;
		return (T) Array.get(items[last], indexes[last]);
	}

	/**
	 * Set the value.
	 * 
	 * @param value The value.
	 * @param indexes The indexes (position).
	 */
	public void set(T value, int... indexes) {
		checkIndexes(indexes);
		Object[] items = new Object[indexes.length];
		items[0] = data;
		for (int i = 0; i < indexes.length; i++) {
			int index = indexes[i];
			if (index < 0 || index >= dimensions[i]) {
				throw new ArrayIndexOutOfBoundsException();
			}
			if (i < indexes.length - 1) {
				items[i + 1] = Array.get(items[i], index);
			} else {
				Array.set(items[i], index, value);
			}
		}
	}

	/**
	 * Initialize the rray with the value.
	 * 
	 * @param value The initial value.
	 */
	public void init(T value) {
		int[] indexes = new int[dimensions.length];
		for (int i = 0; i < dimensions.length; i++) {
			for (int j = 0; j < dimensions[i]; j++) {
				indexes[i] = j;
				for (int m = i + 1; m < dimensions.length; m++) {
					for (int n = 0; n < dimensions[m]; n++) {
						indexes[m] = n;
						set(value, indexes);
					}
				}
			}
		}
	}

	/**
	 * Check that indexes are in the proper ranges.
	 * 
	 * @param indexes The list of indexes.
	 */
	private void checkIndexes(int... indexes) {
		if (indexes.length != dimensions.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
}
