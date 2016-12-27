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
package com.qtplaf.library.math;

import java.util.Locale;

import com.qtplaf.library.util.TextServer;

/**
 * A one dimensional matrix, a vector.
 * 
 * @author Miquel Sas
 */
public class Vector extends Matrix {

	/**
	 * Constructor setting the size.
	 * 
	 * @param size The size.
	 */
	public Vector(int size) {
		super(size, 1);
	}

	/**
	 * Constructor assigning a double array.
	 * 
	 * @param vector The data.
	 */
	public Vector(double[] vector) {
		this(vector.length);
		for (int i = 0; i < vector.length; i++) {
			set(i, vector[i]);
		}
	}

	/**
	 * Private constructor used internally.
	 * 
	 * @param matrix The internal matrix.
	 * @throws IllegalArgumentException if the argument matrix is not a vector.
	 */
	public Vector(double[][] matrix) {
		super(matrix);
		if (Calculator.columns(matrix) != 1) {
			String error = TextServer.getString("exceptionVectorDimension", Locale.UK);
			throw new IllegalArgumentException(error);
		}
	}

	/**
	 * Returns a vector that is a copy of this vector. Overwrites the matrix method to return an instance of Vector.
	 * 
	 * @return The copy.
	 */
	public Vector copy() {
		return new Vector(Calculator.copy(getMatrix()));
	}

	/**
	 * Returns the value at index.
	 * 
	 * @param index The index.
	 * @return The value at index.
	 */
	public double get(int index) {
		return get(index, 0);
	}

	/**
	 * Set the value at index.
	 * 
	 * @param index The index.
	 * @param value The value to set.
	 */
	public void set(int index, double value) {
		set(index, 0, value);
	}

	/**
	 * Returns the size of this vector.
	 * 
	 * @return The size.
	 */
	public int size() {
		return rows();
	}
}
