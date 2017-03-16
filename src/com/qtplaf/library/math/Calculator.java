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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Generic static calculator methods.
 * 
 * @author Miquel Sas
 */
public class Calculator {

	/**
	 * Returns the maximum.
	 * 
	 * @param values The list of values.
	 * @return The maximum.
	 */
	public static double maximum(double[] values) {
		double maximum = Double.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			maximum = Math.max(maximum, values[i]);
		}
		return maximum;
	}

	/**
	 * Returns the minimum.
	 * 
	 * @param values The list of values.
	 * @return The minimum.
	 */
	public static double minimum(double[] values) {
		double minimum = Double.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			minimum = Math.min(minimum, values[i]);
		}
		return minimum;
	}

	/**
	 * Returns the average of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The average.
	 */
	public static double average(List<Double> values) {
		return mean(values);
	}

	/**
	 * Returns the mean of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The mean.
	 */
	public static double mean(List<Double> values) {
		return mean(ListUtils.toArray(values));
	}

	/**
	 * Returns the average of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The average.
	 */
	public static double average(double[] values) {
		return mean(values);
	}

	/**
	 * Returns the mean of a list of values.
	 * 
	 * @param values The array of values.
	 * @return The mean.
	 */
	public static double mean(double[] values) {
		if (size(values) == 0) {
			return 0;
		}
		double mean = 0;
		for (double value : values) {
			mean += value;
		}
		mean /= Double.valueOf(size(values)).doubleValue();
		return mean;
	}

	/**
	 * Returns the standard deviation for a list of values and its mean.
	 * 
	 * @param values The list of values.
	 * @param mean The mean of the list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(double[] values, double mean) {
		if (size(values) <= 1) {
			return 0;
		}
		double variance = 0;
		for (double value : values) {
			double difference = value - mean;
			variance += (difference * difference);
		}
		variance /= (Double.valueOf(size(values)).doubleValue() - 1);
		return Math.sqrt(variance);
	}

	/**
	 * Returns the standard deviation for a list of values and its mean.
	 * 
	 * @param values The list of values.
	 * @param mean The mean of the list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(List<Double> values, double mean) {
		return stddev(ListUtils.toArray(values), mean);
	}

	/**
	 * Returns the standard deviation for a list of values.
	 * 
	 * @param values The list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(double[] values) {
		return stddev(values, mean(values));
	}

	/**
	 * Returns the standard deviation for a list of values.
	 * 
	 * @param values The list of values.
	 * @return The standard deviation.
	 */
	public static double stddev(List<Double> values) {
		return stddev(values, mean(values));
	}

	/**
	 * Returns the total of the list of values.
	 * 
	 * @param values The array of double values.
	 * @return The total.
	 */
	public static double total(double[] values) {
		double total = 0;
		for (double value : values) {
			total += value;
		}
		return total;
	}

	/**
	 * Returns the total of the list of values.
	 * 
	 * @param values The list of Double values.
	 * @return The total.
	 */
	public static double total(List<Double> values) {
		return total(ListUtils.toArray(values));
	}

	/**
	 * Normalizes the value in a range of maximum/minimum values with a sign. If both the maximum and the minimum are
	 * positive, normalizes to the range [1.0, 0.0]. If both are negative, normalizes in the range [0.0, -1.0]. If the
	 * maximum is positive and the minimum is negative, normalizes in the range [1.0, -1.0].
	 * 
	 * @param value The value to normalize.
	 * @param maximum The maximum.
	 * @param minimum The minimum.
	 * @return The normalized value.
	 */
	public static double normalizeSign(double value, double maximum, double minimum) {

		// Ensure maximum and minimum, swap if necessary..
		if (minimum > maximum) {
			double tmp = maximum;
			maximum = minimum;
			minimum = tmp;
		}

		// Both maximum and minimu are zero.
		if (maximum == 0 && minimum == 0) {
			return 0;
		}

		// Ensure in the range.
		value = Math.min(value, maximum);
		value = Math.max(value, minimum);

		// Both maximum and minimum are positive, normalize [1.0, 0.0]
		if (maximum >= 0 && minimum >= 0) {
			return (value - minimum) / (maximum - minimum);
		}
		// Both maximum and minimum are negative, normalize [0.0, -1.0]
		if (maximum <= 0 && minimum <= 0) {
			return ((value - minimum) / (maximum - minimum)) - 1.0;
		}
		// Maximum positive and minimum negative.
		if (maximum >= 0 && minimum <= 0) {
			if (value >= 0) {
				return (value - 0.0) / (maximum - 0.0);
			} else {
				return ((value - minimum) / (0.0 - minimum)) - 1.0;
			}
		}
		// Never should come here.
		throw new IllegalStateException("Fatal error while normalizing");
	}

	/**
	 * Normalize a value within a minimum and maximum.
	 * 
	 * @param value The value.
	 * @param maximum The minimum.
	 * @param minimum The maximum.
	 * @return The normalized value.
	 */
	public static double normalize(double value, double maximum, double minimum) {

		// Maximum and minimum can not be equal.
		if (maximum == minimum) {
			throw new IllegalArgumentException();
		}

		// Ensure maximum and minimum, swap if necessary..
		if (minimum > maximum) {
			double tmp = maximum;
			maximum = minimum;
			minimum = tmp;
		}

		// Ensure in the range.
		value = Math.min(value, maximum);
		value = Math.max(value, minimum);

		return ((value - minimum) / (maximum - minimum));
	}

	/**
	 * Returns the list of normalized values, values that are >= 0 and <= 1.
	 * 
	 * @param values The list of value to normalize.
	 * @return The list of values normalized
	 */
	public static double[] normalizeSign(double[] values) {
		double maximum = Double.NEGATIVE_INFINITY;
		double minimum = Double.POSITIVE_INFINITY;
		for (double value : values) {
			if (value > maximum) {
				maximum = value;
			}
			if (value < minimum) {
				minimum = value;
			}
		}
		int size = size(values);
		double[] normalized = new double[size];
		for (int i = 0; i < size; i++) {
			normalized[i] = normalizeSign(values[i], maximum, minimum);
		}
		return normalized;
	}

	/**
	 * Returns the list of normalized values, values that are >= 0 and <= 1.
	 * 
	 * @param values The list of value to normalize.
	 * @return The list of values normalized
	 */
	public static List<Double> normalizeSign(List<Double> values) {
		return toList(normalizeSign(ListUtils.toArray(values)));
	}

	/**
	 * Normalize the list of values based on the mean and the standard deviation.
	 * 
	 * @param values The list of values.
	 * @param mean The mean.
	 * @param stddev The standard deviation.
	 * @return The list normalized.
	 */
	public static double[] normalizeSign(double[] values, double mean, double stddev) {
		int size = size(values);
		double[] normalized = new double[size];
		for (int i = 0; i < size; i++) {
			double value = values[i];
			if (stddev == 0) {
				normalized[i] = 0;
			} else {
				normalized[i] = (value - mean) / stddev;
			}
		}
		return normalized;
	}

	/**
	 * Normalize the list of values based on the mean and the standard deviation.
	 * 
	 * @param values The list of values.
	 * @param mean The mean.
	 * @param stddev The standard deviation.
	 * @return The list normalized.
	 */
	public static List<Double> normalizeSign(List<Double> values, double mean, double stddev) {
		return toList(normalizeSign(ListUtils.toArray(values), mean, stddev));
	}

	/**
	 * Returns the number of rows of a matrix.
	 * 
	 * @param matrix The argument matrix.
	 * @return The number of rows.
	 */
	public static int rows(double[][] matrix) {
		return matrix.length;
	}

	/**
	 * Returns the number of columns of a matrix.
	 * 
	 * @param matrix The argument matrix.
	 * @return The number of columns.
	 */
	public static int columns(double[][] matrix) {
		if (rows(matrix) != 0) {
			return matrix[0].length;
		}
		return 0;
	}

	/**
	 * Returns the size of a vector.
	 * 
	 * @param vector The vector.
	 * @return The size.
	 */
	public static int size(double[] vector) {
		return vector.length;
	}

	/**
	 * Returns the argument matrix transposed.
	 * 
	 * @param matrix The matrix to transpose.
	 * @return The transposed matrix.
	 */
	public static double[][] transpose(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] transposed = new double[columns][rows];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				transposed[column][row] = matrix[row][column];
			}
		}
		return transposed;
	}

	/**
	 * Divide a vector by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vactor The vector.
	 * @return The division vector.
	 */
	public static double[] divide(double value, double[] vector) {
		int size = size(vector);
		double[] product = new double[size];
		for (int i = 0; i < size; i++) {
			product[i] = vector[i] / value;
		}
		return product;
	}

	/**
	 * Divide a vector by a scalar value assinging it to the vector.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vactor The vector.
	 */
	public static void divideAssign(double value, double[] vector) {
		int size = size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] /= value;
		}
	}

	/**
	 * Divide a matrix by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 * @return The divide matrix.
	 */
	public static double[][] divide(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] product = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				product[row][column] = matrix[row][column] / value;
			}
		}
		return product;
	}

	/**
	 * Multiply a vector by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vactor The vector.
	 * @return The product vector.
	 */
	public static double[] multiply(double value, double[] vector) {
		int size = size(vector);
		double[] product = new double[size];
		for (int i = 0; i < size; i++) {
			product[i] = vector[i] * value;
		}
		return product;
	}

	/**
	 * Multiply a vector by a scalar value and assign it to the vector values.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param vector The vector.
	 */
	public static void multiplyAssign(double value, double[] vector) {
		int size = size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] *= value;
		}
	}

	/**
	 * Multiply a matrix by a scalar value.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 * @return The product matrix.
	 */
	public static double[][] multiply(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] product = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				product[row][column] = matrix[row][column] * value;
			}
		}
		return product;
	}

	/**
	 * Multiply a matrix by a scalar value and assign it.
	 * 
	 * @param value The scalar value used to multiply the matrix.
	 * @param matrix The matrix.
	 */
	public static void multiplyAssign(double value, double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] *= value;
			}
		}
	}

	/**
	 * Linear algebraic matrix multiplication.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The matrix product a * b
	 */
	public static double[][] multiply(double[][] a, double[][] b) {
		if (rows(b) != columns(a)) {
			throw new IllegalArgumentException(
				"The number of rows of the b matrix must be equal to the number of columns of the a matrix.");
		}

		int rows_a = rows(a);
		int columns_a = columns(a);
		int columns_b = columns(b);

		double[][] product = new double[rows_a][columns_b];
		for (int row_a = 0; row_a < rows_a; row_a++) {
			for (int column_b = 0; column_b < columns_b; column_b++) {
				double value = 0;
				for (int column_a = 0; column_a < columns_a; column_a++) {
					value += (a[row_a][column_a] * b[column_a][column_b]);
				}
				product[row_a][column_b] = value;
			}
		}

		return product;
	}

	/**
	 * Returns the identity matrix for the given dimensions.
	 * 
	 * @param rows Number of rows
	 * @param columns Number of columns.
	 * @return The identity matrix.
	 */
	public static double[][] identity(int rows, int columns) {
		double[][] identity = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				identity[row][column] = (row == column ? 1.0 : 0.0);
			}
		}
		return identity;
	}

	/**
	 * Creates and returns a diagonal matrix.
	 * 
	 * @param values The list of values of the diagonal.
	 * @return The diagonal matrix.
	 */
	public static double[][] diagonalMatrix(double[] values) {
		int size = size(values);
		double[][] diagonal = new double[size][size];
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (r == c) {
					diagonal[r][c] = values[r];
				} else {
					diagonal[r][c] = 0;
				}
			}
		}
		return diagonal;
	}

	/**
	 * Returns a copy of the matrix.
	 * 
	 * @param matrix The matrix to copy.
	 * @return The copy.
	 */
	public static double[][] copy(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		double[][] copy = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				copy[row][column] = matrix[row][column];
			}
		}
		return copy;
	}

	/**
	 * Returns the tranlated or added vector.
	 * 
	 * @param value The value to add.
	 * @param vector The source vector.
	 * @return The translated vector.
	 */
	public static double[] add(double value, double[] vector) {
		int size = size(vector);
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = value + vector[i];
		}
		return result;
	}

	/**
	 * Add the values of vectors a and b.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The result of adding the values.
	 */
	public static double[] add(double[] a, double[] b) {
		checkVectorsSizes(a, b);
		int size = size(a);
		double[] r = new double[size];
		for (int i = 0; i < size; i++) {
			r[i] = a[i] + b[i];
		}
		return r;
	}

	/**
	 * Subtract the values of vector b from vector a.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The result of subtracting the values.
	 */
	public static double[] subtract(double[] a, double[] b) {
		checkVectorsSizes(a, b);
		int size = size(a);
		double[] r = new double[size];
		for (int i = 0; i < size; i++) {
			r[i] = a[i] - b[i];
		}
		return r;
	}

	/**
	 * Assigns the tranlated or added vector.
	 * 
	 * @param value The value to add.
	 * @param vector The source vector.
	 */
	public static void addAssign(double value, double[] vector) {
		int size = size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] = value + vector[i];
		}
	}

	/**
	 * Returns the addition of the two matrices.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The addition matrix.
	 */
	public static double[][] add(double[][] a, double[][] b) {
		checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		double[][] addition = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				addition[row][column] = a[row][column] + b[row][column];
			}
		}
		return addition;
	}

	/**
	 * Check that matrices dimensions agree.
	 *
	 * @param a Matrix a
	 * @param b Matrix b
	 */
	private static void checkMatricesDimensions(double[][] a, double[][] b) {
		if (rows(a) != rows(b) || columns(a) != columns(b)) {
			throw new IllegalArgumentException("Matrices dimensions must agree");
		}
	}

	/**
	 * Check that the sizes of the vectors are the same.
	 * 
	 * @param a Vector a.
	 */
	private static void checkVectorsSizes(double[] a, double[] b) {
		if (size(a) != size(b)) {
			throw new IllegalArgumentException("Vectors sizes must agree");
		}
	}

	/**
	 * Add and assign matrix b to matrix a.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b
	 */
	public static void addAssign(double[][] a, double[][] b) {
		checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				a[row][column] += b[row][column];
			}
		}
	}

	/**
	 * Subtract and assign a value from matrix vector.
	 * 
	 * @param value The value to subtract.
	 * @param vector The vector.
	 */
	public static void subtractAssign(double value, double[] vector) {
		int size = size(vector);
		for (int i = 0; i < size; i++) {
			vector[i] -= value;
		}
	}

	/**
	 * Returns the subtraction of the value from the vector.
	 * 
	 * @param value The value to subtract.
	 * @param vector The vector.
	 * @return The subtraction vector.
	 */
	public static double[] subtract(double value, double[] vector) {
		int size = size(vector);
		double[] subtraction = new double[size];
		for (int i = 0; i < size; i++) {
			subtraction[i] = vector[i] - value;
		}
		return subtraction;
	}

	/**
	 * Subtract and assign matrix b from matrix a.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b
	 */
	public static void subtractAssign(double[][] a, double[][] b) {
		checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				a[row][column] -= b[row][column];
			}
		}
	}

	/**
	 * Returns the subtraction (a-b) of the two matrices.
	 * 
	 * @param a Matrix a.
	 * @param b Matrix b.
	 * @return The subtraction matrix.
	 */
	public static double[][] subtract(double[][] a, double[][] b) {
		checkMatricesDimensions(a, b);
		int rows = rows(a);
		int columns = columns(a);
		double[][] subtraction = new double[rows][columns];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				subtraction[row][column] = a[row][column] - b[row][column];
			}
		}
		return subtraction;
	}

	/**
	 * Returns the list given the array of values.
	 * 
	 * @param values The array of values.
	 * @return The list of values.
	 */
	public static List<Double> toList(double[] values) {
		List<Double> list = new ArrayList<>();
		for (double value : values) {
			list.add(value);
		}
		return list;
	}

	/**
	 * Returns a string representation of a matrix (for debug purposes)
	 * 
	 * @param matrix The argument matrix.
	 * @return The string representation.
	 */
	public static String toString(double[][] matrix) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		StringBuilder b = new StringBuilder();
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				b.append(matrix[row][column]);
				b.append(" ");
			}
			b.append("\n");
		}
		return b.toString();
	}

	/**
	 * Initializes the matrix values with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 * 
	 * @param matrix The matrix to initialize.
	 * @param noZeros A boolean that indicates if zeros are allowed.
	 */
	public static void initialize(double[][] matrix) {
		Random random = new Random();
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				double value = random.nextGaussian();
				// while (value == 0) {
				// value = random.nextGaussian();
				// }
				matrix[row][column] = value;
			}
		}
	}

	/**
	 * Initialize the matrix with a scalar value.
	 * 
	 * @param matrix The matrix to initialize.
	 * @param value The value to assign.
	 */
	public static void initialize(double[][] matrix, double value) {
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = value;
			}
		}
	}

	/**
	 * Initializes the matrix with random values between lower and upper limits.
	 * 
	 * @param matrix The matrix to initialize.
	 * @param lowerLimit The lower limit.
	 * @param upperLimit The upper limit.
	 */
	public static void initialize(double[][] matrix, double lowerLimit, double upperLimit) {
		Random random = new Random();
		int rows = rows(matrix);
		int columns = columns(matrix);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = lowerLimit + ((upperLimit - lowerLimit) * random.nextDouble());
			}
		}
	}

	/**
	 * Check if two matrices are equal rounding the values at the argument precision.
	 * 
	 * @param a Matrix a
	 * @param b Matrix b
	 * @param precision Rounding precision.
	 * @return
	 */
	public static boolean areEqual(double[][] a, double[][] b, int precision) {

		// Check dimensions
		if (rows(a) != rows(b) || columns(a) != columns(b)) {
			return false;
		}

		// Check every item
		int rows = rows(a);
		int columns = columns(a);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				double value_a = NumberUtils.round(a[row][column], precision);
				double value_b = NumberUtils.round(b[row][column], precision);
				if (value_a != value_b) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns the squared Euclidean norm of a vector or list of values.
	 * 
	 * @param v The vector.
	 * @return The squared Euclidean norm.
	 */
	public static double squaredEuclideanNorm(double[] v) {
		int length = size(v);
		double norm = 0;
		for (int i = 0; i < length; i++) {
			norm += Math.pow(v[i], 2);
		}
		return norm;
	}

	/**
	 * Returns the Euclidean norm of a vector or list of values.
	 * 
	 * @param v The vector.
	 * @return The Euclidean norm.
	 */
	public static double euclideanNorm(double[] v) {
		return Math.sqrt(squaredEuclideanNorm(v));
	}

	/**
	 * Returns the squared Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The squared Euclidean distance.
	 */
	public static double squaredEuclideanDistance(double[] a, double[] b) {
		if (size(a) != size(b)) {
			throw new IllegalArgumentException("Vector lengths must be the same.");
		}
		int size = size(a);
		double distance = 0;
		for (int i = 0; i < size; i++) {
			distance += Math.pow(a[i] - b[i], 2);
		}
		return distance;
	}

	/**
	 * Returns the Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Euclidean distance.
	 */
	public static double euclideanDistance(double[] a, double[] b) {
		return Math.sqrt(squaredEuclideanDistance(a, b));
	}

	/**
	 * Returns the Gaussian radial basis function.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @param sigma Sigma parameter.
	 * @return
	 */
	public static double radialBasis(double[] a, double[] b, double sigma) {
		double sd = squaredEuclideanDistance(a, b);
		double rb = Math.exp(-sd / (2 * Math.pow(sigma, 2)));
		return rb;
	}

	/**
	 * Returns the Euclidean distance between two vectors of the same size.
	 * 
	 * @param a Vector a.
	 * @param b Vector b.
	 * @return The Euclidean distance.
	 */
	public static double meanSquared(double[] a, double[] b) {
		double size = size(a);
		return euclideanDistance(a, b) * 2 / size;
	}

	/**
	 * Returns the output vector translated to minimize the mean squared error.
	 * 
	 * @param output Initial output vector.
	 * @param input Input vector, or reference.
	 * @param learningFactor Learnin factor.
	 * @param maximumError The minimum error to break the loop.
	 * @param maximumIterations The maximum number of iterations.
	 * @return The output vector translated to minimize the mean squared error.
	 */
	public static double[] meanSquaredMinimum(
		double[] output,
		double[] input,
		double learningFactor,
		double maximumError,
		int maximumIterations) {

		// Size and result.
		int size = size(output);
		double[] result = new double[size];
		for (int i = 0; i < size; i++) {
			result[i] = output[i];
		}

		// Current iteration.
		int iteration = 0;

		// Previous mean squared.
		double meanSquaredPrevious = meanSquared(result, input);

		// Error.
		double error = Double.MAX_VALUE;

		// The sign.
		double sign = 1d;

		// Main loop.
		while (true) {

			// Break maximum iterations.
			if (iteration >= maximumIterations) {
				break;
			}

			// Apply the translation.
			double translation = meanSquaredPrevious * learningFactor * sign;
			addAssign(translation, result);

			// Current mean squared.
			double meanSquaredCurrent = meanSquared(result, input);

			// Error.
			error = Math.abs(meanSquaredCurrent - meanSquaredPrevious);

			// Break if error is less than minimum error.
			if (error < maximumError) {
				break;
			}

			// If there is an increase, change the sign and reduce the learning factor.
			if (meanSquaredCurrent > meanSquaredPrevious) {
				sign *= -1d;
				learningFactor *= 0.5;
			}

			// Move mean square.
			meanSquaredPrevious = meanSquaredCurrent;

			// Increase the iteration counter.
			iteration++;
		}

		return result;
	}

	/**
	 * Returns the sigmoid of a value.
	 * 
	 * @param value The value.
	 * @return The sigmoid.
	 */
	public static double sigmoid(double value) {
		return 1 / (1 + Math.exp(-(value)));
	}

	/**
	 * Returns the sigmoid derivative of a value.
	 * 
	 * @param value The value.
	 * @return The sigmoid derivative.
	 */
	public static double sigmoidDerivative(double value) {
		double output = sigmoid(value);
		return output * (1 - output);
	}
}
