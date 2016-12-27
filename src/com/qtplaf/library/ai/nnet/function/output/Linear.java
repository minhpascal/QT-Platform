/**
 * 
 */
package com.qtplaf.library.ai.nnet.function.output;

import com.qtplaf.library.ai.nnet.function.OutputFunction;

/**
 * Linear neuron activation function.
 * 
 * @author Miquel Sas
 */
public class Linear implements OutputFunction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 479406385189704891L;
	
	/**
	 * The slope of this linear function, 1.0 by default.
	 */
	private double slope = 1.0;

	/**
	 * Default constructor.
	 */
	public Linear() {
	}

	/**
	 * Constructor assigning the slope.
	 * 
	 * @param slope The slope.
	 */
	public Linear(double slope) {
		this.slope = slope;
	}

	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	public double getOutput(double input) {
		return slope * input;
	}

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	public double getDerivative(double input) {
		return slope;
	}

}
