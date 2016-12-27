/**
 * 
 */
package com.qtplaf.library.ai.nnet.function.output;

import com.qtplaf.library.ai.nnet.function.OutputFunction;

/**
 * A Gaussian activation function.
 * 
 * @author Miquel Sas
 */
public class Gaussian implements OutputFunction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 4049318519841720073L;

	/**
	 * The sigma parameter.
	 */
	private double sigma = 0.5;

	/**
	 * Default constructor.
	 */
	public Gaussian() {
	}

	/**
	 * Constructor assigning the sigma parameter.
	 * 
	 * @param sigma The sigma parameter.
	 */
	public Gaussian(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	public double getOutput(double input) {
		return Math.exp(-Math.pow(input, 2) / (2 * Math.pow(sigma, 2)));
	}

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	public double getDerivative(double input) {
		return getOutput(input) * (-input / (sigma * sigma));
	}

}
