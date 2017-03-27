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

/**
 * Layer of a neural network implemented with matrices (not a graph). Uses standard sigmod, weighted sum and mean
 * squared functions for output, input and error.
 *
 * @author Miquel Sas
 */
public class Layer {

	/** Number of inputs. */
	int inputSize;
	/** Number of outputs. */
	int outputSize;

	/** Neuron biases. */
	double[] biases;
	/** Biases last update. */
	double[] deltaBiases;
	/** Last calculated output source values of the sigmoid. */
	double[] outsrcs;
	/** Last calculated output. */
	double[] outputs;
	/** Output errors. */
	double[] errors;
	
	/** Input saved when processed. */
	double[] inputs;
	
	/** Weights. */
	double[][] weights;
	/** Last weight update used in back propagation to apply momentum. */
	double[][] deltaWeights;

	/**
	 * Constructor.
	 * 
	 * @param inputSize Input size.
	 * @param outputSize Output size.
	 */
	public Layer(int inputSize, int outputSize) {
		super();
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.biases = new double[outputSize];
		this.deltaBiases = new double[outputSize];
		this.outsrcs = new double[outputSize];
		this.outputs = new double[outputSize];
		this.errors = new double[outputSize];
		this.inputs = new double[inputSize];
		this.weights = new double[inputSize][outputSize];
		this.deltaWeights = new double[inputSize][outputSize];
	}

	/**
	 * Process the input vector and return the output one.
	 * 
	 * @param inputs The input vector.
	 * @return The output vector.
	 */
	public double[] processInput(double[] inputs) {

		// Check input size (should not be necessary.
		if (inputs.length != inputSize) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		// Save intput.
		this.inputs = inputs;
		
		// Weighted inputs.
		for (int out = 0; out < outputSize; out++) {
			// Add weightd sum to the bias.
			double weighted = biases[out];
			for (int in = 0; in < inputSize; in++) {
				weighted += inputs[in] * weights[in][out];
			}
			// Calculate output.
			outsrcs[out] = weighted;
			outputs[out] = Calculator.sigmoid(weighted);
		}

		return outputs;
	}
}
