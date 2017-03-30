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
package incubator.nnet.graph.learning;

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
