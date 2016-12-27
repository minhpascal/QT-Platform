/**
 * 
 */
package com.qtplaf.library.ai.nnet.function;

import com.qtplaf.library.math.Vector;

/**
 * Interface used to calculate the error value for an error vector and accumulate it to register the total error in a
 * learning process.
 * 
 * @author Miquel Sas
 */
public interface ErrorFunction {

	/**
	 * Returns the total network error.
	 * 
	 * @return The total network error.
	 */
	double getTotalError();

	/**
	 * Returns the error value given a list of output errors, an error vector.
	 * 
	 * @param errorVector The error values.
	 * @return The error value.
	 */
	double getError(Vector errorVector);

	/**
	 * Adds the error to the total network error.
	 * 
	 * @param error The error to accumulate.
	 */
	void addError(double error);

	/**
	 * Reset the total error setting it to zero.
	 */
	void reset();
}
