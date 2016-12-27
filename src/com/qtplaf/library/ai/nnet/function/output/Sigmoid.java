/**
 * 
 */
package com.qtplaf.library.ai.nnet.function.output;

import com.qtplaf.library.ai.nnet.function.OutputFunction;

/**
 * A Sigmoid activation function.
 * 
 * @author Miquel Sas
 */
public class Sigmoid implements OutputFunction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5541027766668660421L;

	/**
	 * Default constructor.
	 */
	public Sigmoid() {
	}

	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	public double getOutput(double input) {
		return 1 / (1 + Math.exp(-(input)));
	}

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	public double getDerivative(double input) {
		double output = getOutput(input);
		return output * (1 - output);
	}

}
