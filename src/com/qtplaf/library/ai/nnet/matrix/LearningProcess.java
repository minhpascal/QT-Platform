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
package com.qtplaf.library.ai.nnet.matrix;

/**
 * The root interface that learning processes must implement. More in detail:
 * <p>
 * First, learning processes must process an input vector and return the output vector as a result.
 * <p>
 * Second, they must accept an error vector to update their internal parameters, e.g. weights and biases.
 * <p>
 * Third, they must provide a way to modify their internal control parameters.
 * 
 * 
 * @author Miquel Sas
 */
public abstract class LearningProcess {

	/**
	 * The network to be trained.
	 */
	private NeuralNetwork network;

	/**
	 * Constructor assigning the network.
	 * 
	 * @param network The network to train.
	 */
	public LearningProcess(NeuralNetwork network) {
		super();
		this.network = network;
	}

	/**
	 * Returns the network to train.
	 * 
	 * @return The network to train.
	 */
	public NeuralNetwork getNetwork() {
		return network;
	}

	/**
	 * Process the input vector and return the network output vector.
	 * 
	 * @param inputs The input vector to process.
	 * @return The output vector.
	 */
	public abstract double[] processInputs(double[] inputs);

	/**
	 * Process the error vector updating the network layers weights and biases.
	 * 
	 * @param errors The error vector.
	 */
	public abstract void processErrors(double[] errors);

}
