/**
 * 
 */
package com.qtplaf.library.ai.nnet.function.error;

import com.qtplaf.library.ai.nnet.function.ErrorFunction;
import com.qtplaf.library.math.Vector;

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
