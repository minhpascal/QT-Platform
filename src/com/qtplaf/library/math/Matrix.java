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

/**
 * A matrix with fundamental operations of numerical linear algebra.
 * 
 * @author Miquel Sas
 */
public class Matrix {

	/**
	 * Internal array.
	 */
	private final double[][] matrix;

	/**
	 * Constructor assigning the number of rows and columns.
	 * 
	 * @param rows The number of rows.
	 * @param columns The number of columns.
	 */
	public Matrix(int rows, int columns) {
		matrix = new double[rows][columns];
	}

	/**
	 * Constructor assigning the number of rows and columns, and initializing with a scalar value.
	 * 
	 * @param rows The number of rows.
	 * @param columns The number of columns.
	 * @param value The scalar value to initialize the matrix.
	 */
	public Matrix(int rows, int columns, double value) {
		this(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				matrix[r][c] = value;
			}
		}
	}

	/**
	 * Constructor assigning the matrix.
	 * 
	 * @param matrix The matrix to assign.
	 */
	public Matrix(double[][] matrix) {
		super();
		this.matrix = matrix;
	}

	/**
	 * Returns the value at row and columns.
	 * 
	 * @param row The row.
	 * @param column The column.
	 * @return The value.
	 */
	public double get(int row, int column) {
		return matrix[row][column];
	}

	/**
	 * Sets the value at row and column.
	 * 
	 * @param row The row.
	 * @param column The column.
	 * @param value The value to set.
	 */
	public void set(int row, int column, double value) {
		matrix[row][column] = value;
	}

	/**
	 * Returns the number of rows.
	 * 
	 * @return The number of rows.
	 */
	public int rows() {
		return Calculator.rows(matrix);
	}

	/**
	 * Returns the number of columns.
	 * 
	 * @return The number of columns.
	 */
	public int columns() {
		return Calculator.columns(matrix);
	}

	/**
	 * Returns a matrix that is a copy of this matrix.
	 * 
	 * @return The copy.
	 */
	public Matrix copy() {
		return new Matrix(Calculator.copy(matrix));
	}

	/**
	 * Returns the internal double[][] (the matrix)
	 * 
	 * @return The internal double[][]
	 */
	public double[][] getMatrix() {
		return matrix;
	}

	/**
	 * Returns this matrix as a vector if it is a vector.
	 * 
	 * @return The vector.
	 */
	public Vector toVector() {
		if (columns() != 1) {
			throw new UnsupportedOperationException("Not supported for matrices with more than one column");
		}
		return new Vector(matrix);
	}

	/**
	 * Returns a matrix that is the addition of this matrix and the argument matrix.
	 * 
	 * @param a The matrix to add to this matrix.
	 * @return The addition matrix.
	 */
	public Matrix add(Matrix a) {
		return new Matrix(Calculator.add(matrix, a.matrix));
	}

	/**
	 * Add the argument matrix to this matrix assigning the result.
	 * 
	 * @param a The matrix to add to this matrix.
	 */
	public void addAssign(Matrix a) {
		Calculator.addAssign(matrix, a.matrix);
	}

	/**
	 * Returns a matrix that is the subtraction of the argument matrix from this matrix.
	 * 
	 * @param a The matrix to subtract.
	 * @return The matrix that is the subtraction of the argument matrix from this matrix.
	 */
	public Matrix subtract(Matrix a) {
		return new Matrix(Calculator.subtract(matrix, a.matrix));
	}

	/**
	 * Subtract the argument matrix from this matrix assigning the result.
	 * 
	 * @param a The matrix to subtract from this matrix.
	 */
	public void subtractAssign(Matrix a) {
		Calculator.subtractAssign(matrix, a.matrix);
	}

	/**
	 * Returns the matrix result of multiplying this matrix by the argument matrix.
	 * 
	 * @param a The matrix used to multiply this matrix.
	 * @return The product matrix.
	 */
	public Matrix multiply(Matrix a) {
		return new Matrix(Calculator.multiply(matrix, a.matrix));
	}

	/**
	 * Returns this matrix transposed.
	 * 
	 * @return The transposed matrix.
	 */
	public Matrix transpose() {
		return new Matrix(Calculator.transpose(matrix));
	}

	/**
	 * Multiply this matrix by a scalar value.
	 * 
	 * @param value The scalar value.
	 * @return The product matrix.
	 */
	public Matrix multiply(double value) {
		return new Matrix(Calculator.multiply(value, matrix));
	}

	/**
	 * Multiply this matrix by a scalar value assigning the result.
	 * 
	 * @param value The scalar value.
	 */
	public void multiplyAssign(double value) {
		Calculator.multiplyAssign(value, matrix);
	}
	
	/**
	 * Check if this matrix is equal to the argument matrix given a precision.
	 * @param a The matrix to check for equality.
	 * @param precision The precision.
	 * @return A boolean indicating if both 
	 */
	public boolean equals(Matrix a, int precision) {
		return Calculator.areEqual(matrix, a.matrix, precision);
	}

	/**
	 * Initializes this matrix values with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 */
	public void initialize() {
		Calculator.initialize(matrix);
	}

	/**
	 * Initializes this matrix with random values between lower and upper limits.
	 * 
	 * @param lowerLimit The lower limit.
	 * @param upperLimit The upper limit.
	 */
	public void initialize(double lowerLimit, double upperLimit) {
		Calculator.initialize(matrix, lowerLimit, upperLimit);
	}
 
	/**
	 * Initializes this matrix with a scalar value.
	 * 
	 * @param value The initializing value.
	 */
	public void initialize(double value) {
		Calculator.initialize(matrix, value);
	}
 
	/**
	 * Returns a string representation of this matrix.
	 */
	public String toString() {
		return Calculator.toString(matrix);
	}
}
