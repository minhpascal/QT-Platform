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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Static methods to operate n-dimensional vectors as lists of values.
 * 
 * @author Miquel Sas
 */
public class VectorUtils {

	/**
	 * Returns a copy of theargument vector.
	 * 
	 * @param v The vector to copy
	 * @return The copy
	 */
	public static List<Double> copy(List<Double> v) {
		return new ArrayList<>(v);
	}

	/**
	 * Returns a vector that is the addition of the argument vectors.
	 * 
	 * @param v1 Vector 1
	 * @param v2 Vector 2
	 * @return The addition of v1 + v2
	 */
	public static List<Double> add(List<Double> v1, List<Double> v2) {
		validateDimensions(v1, v2);
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < v1.size(); i++) {
			result.add(v1.get(i) + v2.get(i));
		}
		return result;
	}

	/**
	 * Returns a vector that is the subtraction of the argument vectors.
	 * 
	 * @param v1 Vector 1
	 * @param v2 Vector 2
	 * @return The subtraction of v1 - v2
	 */
	public static List<Double> subtract(List<Double> v1, List<Double> v2) {
		validateDimensions(v1, v2);
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < v1.size(); i++) {
			result.add(v1.get(i) - v2.get(i));
		}
		return result;
	}

	/**
	 * Returns the dot product sum.
	 * 
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 * @return The dot product.
	 */
	public static double dotProduct(List<Double> v1, List<Double> v2) {
		validateDimensions(v1, v2);
		double product = 0;
		for (int i = 0; i < v1.size(); i++) {
			product += (v1.get(i) * v2.get(i));
		}
		return product;
	}

	/**
	 * Multiply the vector by a scalar.
	 * 
	 * @param v The vector.
	 * @param a The scalar
	 * @return The vector result.
	 */
	public static List<Double> scalarMuliply(List<Double> v, double a) {
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < v.size(); i++) {
			result.add(v.get(i) * a);
		}
		return result;
	}

	/**
	 * Returns the normalized vector.
	 * 
	 * @param v The vector to normalize.
	 * @return The normalized vector.
	 */
	public static List<Double> normalize(List<Double> v) {
		double s = getNorm(v);
		if (s == 0) {
			// Only possible if all dimensions are 0.
			return copy(v);
		}
		return scalarMuliply(v, 1d / s);
	}

	/**
	 * Returns the vector negated.
	 * 
	 * @param v The vector to negate.
	 * @return The negated vector.
	 */
	public static List<Double> negate(List<Double> v) {
		List<Double> n = new ArrayList<>();
		for (double d : v) {
			n.add(-d);
		}
		return n;
	}
	
	/**
	 * Returns the usual length of a vector, the Euclidean norm.
	 * 
	 * @param v The vector
	 * @return The length
	 */
	public static double length(List<Double> v) {
		return getNorm(v);
	}

	/**
	 * Returns the Euclidean norm for the vector.
	 * 
	 * @param v The vector
	 * @return The Euclidean norm
	 */
	public static double getNorm(List<Double> v) {
		return Math.sqrt(getNormSquare(v));
	}

	/**
	 * Returns the square of the Euclidean norm for the vector.
	 * 
	 * @param v The vector
	 * @return The square of the Euclidean norm
	 */
	public static double getNormSquare(List<Double> v) {
		double normSq = 0;
		for (double d : v) {
			normSq += (d * d);
		}
		return normSq;
	}

	/**
	 * Returns true if any dimension of this vector is NaN, otherwise false.
	 * 
	 * @param v The vector to check.
	 * @return A boolean indicating if any dimension of this vector is NaN.
	 */
	public static boolean isNaN(List<Double> v) {
		for (double d : v) {
			if (Double.isNaN(d)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Computes the distance between the argument vectors (v1 - v2)
	 * 
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 * @return The distance v1 - v2
	 */
	public static double distance(List<Double> v1, List<Double> v2) {
		return Math.sqrt(distanceSquare(v1, v2));
	}

	/**
	 * Computes the square distance between the argument vectors (v1 - v2)
	 * 
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 * @return The square distance v1 - v2
	 */
	public static double distanceSquare(List<Double> v1, List<Double> v2) {
		validateDimensions(v1, v2);
		double dSq = 0;
		for (int i = 0; i < v1.size(); i++) {
			double d1 = v1.get(i);
			double d2 = v2.get(i);
			double d = d1 - d2;
			dSq += (d * d);
		}
		return dSq;
	}

	/**
	 * Returns true if the vector is not NaN and any dimension is infinite.
	 * 
	 * @param v The vector to check.
	 * @return A boolean
	 */
	public static boolean isInfinite(List<Double> v) {
		if (isNaN(v)) {
			return false;
		}
		for (double d : v) {
			if (Double.isInfinite(d)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the product of a matrix by a vector.
	 * @param matrix The matrix
	 * @param vector The vector
	 * @return The vector product
	 */
	public static List<Double> matrixVectorProduct(List<List<Double>> matrix, List<Double> vector) {
		validateDimensions(matrix, vector);
		List<Double> product = new ArrayList<>();
		for (int i = 0; i < matrix.size(); i++) {
			List<Double> m = matrix.get(i);
			double x = vector.get(i);
			double p = 0;
			for (Double a : m) {
				p += a * x;
			}
			product.add(p);
		}
		return product;
	}

	/**
	 * Validates that the argument vectors number of dimensions.
	 * 
	 * @param v1 Vector 1.
	 * @param v2 Vector 2.
	 */
	private static void validateDimensions(List<?> v1, List<?> v2) {
		if (v1.size() != v2.size()) {
			String error = TextServer.getString("exceptionValidateVectorDimensions", Locale.UK);
			throw new IllegalArgumentException(error);
		}
	}

}
