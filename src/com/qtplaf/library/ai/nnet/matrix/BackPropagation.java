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

import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Back propagation learning process.
 *
 * @author Miquel Sas
 */
public class BackPropagation {

	/** The network. */
	private NeuralNetwork network;
	/** The learning rate. */
	private double learningRate = 0.1;
	/** The momentum. */
	private double momentum = 0.1;
	/** A boolean that indicates if weights will be updated during the current error processing. */
	private boolean updateWeights = true;
	/** A boolean that indicates if biases will be updated during the current error processing. */
	private boolean updateBiases = true;

	/**
	 * Constructor assigning the network.
	 * 
	 * @param network The network to train.
	 */
	public BackPropagation(NeuralNetwork network) {
		super();
		this.network = network;
	}

	/**
	 * Sets the learning rate.
	 * 
	 * @param learningRate The learning rate.
	 */
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * Sets the momentum.
	 * 
	 * @param momentum The momentum.
	 */
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	/**
	 * Set if weights should be updated.
	 * 
	 * @param updateWeights A boolean that indicates if weights should be updated.
	 */
	public void setUpdateWeights(boolean updateWeights) {
		this.updateWeights = updateWeights;
	}

	/**
	 * Set if biases should be updated.
	 * 
	 * @param updateBiases A boolean that indicates if biases should be updated.
	 */
	public void setUpdateBiases(boolean updateBiases) {
		this.updateBiases = updateBiases;
	}

	/**
	 * Process the input vector and return the network output vector.
	 * 
	 * @param input The input vector to process.
	 * @return The output vector.
	 */
	public double[] processInput(double[] input) {
		return network.processInput(input);
	}

	/**
	 * Process the error vector updating the network layers weights and biases.
	 * 
	 * @param errors The error vector.
	 */
	public void processErrors(double[] errors) {
		// Update the output layer errors
		updateOutputLayerErrors(errors);
		// Update hidden layers errors
		updateHiddenLayersErrors();
	}

	/**
	 * Updates the hidden layers errors by back propagating the output layer errors.
	 */
	private void updateHiddenLayersErrors() {
		for (int i = network.layers.size() - 2; i > 0; i--) {
			Layer layerIn = network.layers.get(i);
			Layer layerOut = network.layers.get(i + 1);
		}
	}

	/**
	 * Update errors for the hidden layer (in).
	 * 
	 * @param layerIn The input layer.
	 * @param layerOut The output layer.
	 */
	private void updateHiddenLayersErrors(Layer layerIn, Layer layerOut) {

	}

	/**
	 * Update the output layer errors.
	 * 
	 * @param errors Output errors.
	 */
	private void updateOutputLayerErrors(double[] errors) {

		// Output layer.
		Layer layer = ListUtils.getLast(network.layers);

		// Check errors size.
		if (errors.length != layer.outputSize) {
			throw new IllegalStateException();
		}

		// Process layer.
		for (int out = 0; out < layer.outputSize; out++) {
			double outsrc = layer.outsrcs[out];
			double derivative = Calculator.sigmoidDerivative(outsrc);
			double error = errors[out] * derivative;
			layer.errors[out] = error;

			// Update neuron weights if applicable
			if (updateWeights) {
				updateWeights(layer, out);
			}

			// Update bias if applicable
			if (updateBiases) {
				updateBias(layer, out);
			}
		}
	}

	/**
	 * Update the output bias of the layer.
	 * 
	 * @param layer The layer.
	 * @param out The output index.
	 */
	private void updateBias(Layer layer, int out) {
		// Output error.
		double error = layer.errors[out];
		// The current output weighted source.
		double outsrc = layer.outsrcs[out];
		// Next bias change
		double nextBiasChange = learningRate * error * outsrc;
		// Last bias change
		double lastBiasChange = layer.deltaBiases[out];
		// Calculate bias change using momentum
		double biasChange = (nextBiasChange * (1 - momentum)) + (lastBiasChange * momentum);
		// Update the bias.
		layer.biases[out] += biasChange;
		layer.deltaBiases[out] = biasChange;
	}

	/**
	 * Update weights for the argument layer and oyutput index.
	 * 
	 * @param layer The layer.
	 * @param out The output index.
	 */
	private void updateWeights(Layer layer, int out) {
		double error = layer.errors[out];
		for (int in = 0; in < layer.inputSize; in++) {
			// Input from current synapse
			double input = layer.inputs[in];
			// Next weight change
			double nextWeightChange = learningRate * error * input;
			// Last weight change
			double lastWeightChange = layer.deltaWeights[out][in];
			// Calculate weight change using momentum
			double weightChange = (nextWeightChange * (1 - momentum)) + (lastWeightChange * momentum);
			// Update the weight and save change.
			layer.weights[out][in] += weightChange;
			layer.deltaWeights[out][in] = weightChange;
		}
	}
}
