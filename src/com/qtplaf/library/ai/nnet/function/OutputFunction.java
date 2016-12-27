/**
 * 
 */
package com.qtplaf.library.ai.nnet.function;

import java.io.Serializable;


/**
 * Interface that should implement all neuron output or activation functions.
 * 
 * @author Miquel Sas
 */
public interface OutputFunction extends Serializable {
	
	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	double getOutput(double input);

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	double getDerivative(double input);
}
