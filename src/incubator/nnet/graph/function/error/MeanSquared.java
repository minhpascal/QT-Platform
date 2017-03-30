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
package incubator.nnet.graph.function.error;

import com.qtplaf.library.math.Vector;

import incubator.nnet.graph.function.ErrorFunction;

/**
 * Mean squared error.
 * 
 * @author Miquel Sas
 */
public class MeanSquared implements ErrorFunction {

	/**
	 * Total accumulated error.
	 */
	private double totalSquaredError = 0;
	/**
	 * The counter of patterns.
	 */
	private double patterns = 0;

	/**
	 * Default constructor.
	 */
	public MeanSquared() {
		super();
	}

	/**
	 * Returns the total network error.
	 * 
	 * @return The total network error.
	 */
	public double getTotalError() {
		if (patterns == 0) {
			return 0;
		}
		return totalSquaredError / patterns;
	}

	/**
	 * Returns the error value given a list of output errors, an error vector.
	 * 
	 * @param errorVector The error values.
	 * @return The error value.
	 */
	public double getError(Vector errorVector) {
		double squaredError = 0;
		int size = errorVector.size();
		for (int i = 0; i < size; i++) {
			double error = errorVector.get(i);
			squaredError += (error * error) * 0.5;
		}
		return squaredError;
	}
	
	/**
	 * Adds the error to the total network error.
	 * 
	 * @param error The error to accumulate.
	 */
	public void addError(double error) {
		totalSquaredError += error;
		patterns += 1;
	}

	/**
	 * Reset the total error setting it to zero.
	 */
	public void reset() {
		totalSquaredError = 0;
		patterns = 0;
	}

}
