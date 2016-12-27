/**
 * 
 */
package com.qtplaf.library.ai.nnet.learning;

import com.qtplaf.library.math.Vector;

/**
 * A neural network supervised learning or checking pattern, with an input and an output vector.
 * 
 * @author Miquel Sas
 */
public abstract class Pattern {

	/**
	 * The network output given this pattern in a single pattern processing.
	 */
	private Vector networkOutputVector;
	/**
	 * The error vector.
	 */
	private Vector errorVector;
	/**
	 * The error produced when processing the pattern.
	 */
	private double error;

	/**
	 * Default constructor.
	 */
	public Pattern() {
		super();
	}

	/**
	 * Returns the input vector to be processed by the network.
	 * 
	 * @return The input vector.
	 */
	public abstract Vector getInputVector();

	/**
	 * Returns the desired output vector to be compared with the network output.
	 * 
	 * @return The desired output vector.
	 */
	public abstract Vector getOutputVector();

	/**
	 * Returns the error, generally produced when processing the pattern..
	 * 
	 * @return The error.
	 */
	public double getError() {
		return error;
	}

	/**
	 * Set the error.
	 * 
	 * @param error The error.
	 */
	public void setError(double error) {
		this.error = error;
	}

	/**
	 * Returns the error vector.
	 * 
	 * @return The error vector.
	 */
	public Vector getErrorVector() {
		return errorVector;
	}

	/**
	 * Sets the error vector.
	 * 
	 * @param errorVector The error vector.
	 */
	public void setErrorVector(Vector errorVector) {
		this.errorVector = errorVector;
	}

	/**
	 * Returns the network output vector.
	 * 
	 * @return The network output vector.
	 */
	public Vector getNetworkOutputVector() {
		return networkOutputVector;
	}

	/**
	 * Sets the network output vector.
	 * 
	 * @param networkOutputVector The network output vector.
	 */
	public void setNetworkOutputVector(Vector networkOutputVector) {
		this.networkOutputVector = networkOutputVector;
	}

}
