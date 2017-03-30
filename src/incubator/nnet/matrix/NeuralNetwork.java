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

package incubator.nnet.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.qtplaf.library.util.list.ListUtils;

/**
 * A feed forward neural network implemented with layer matrices and vectors.
 * 
 * @author Miquel Sas
 */
public class NeuralNetwork {

	/** List of layers. */
	List<Layer> layers = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public NeuralNetwork() {
		super();
	}

	/**
	 * Add the first layer. Can only be called once.
	 * 
	 * @param inputSize The input size.
	 * @param outputSize The output size.
	 */
	public void addLayer(int inputSize, int outputSize) {
		if (!layers.isEmpty()) {
			throw new IllegalStateException();
		}
		layers.add(new Layer(inputSize, outputSize));
	}

	/**
	 * Add subsequent layers. The input size of the layer is the output size of the previous one.
	 * 
	 * @param outputSize The output size.
	 */
	public void addLayer(int outputSize) {
		if (layers.isEmpty()) {
			throw new IllegalStateException();
		}
		int inputSize = ListUtils.getLast(layers).outputSize;
		layers.add(new Layer(inputSize, outputSize));
	}

	/**
	 * Initializes the weights with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 */
	public void initializeWeights() {
		Random random = new Random();
		for (Layer layer : layers) {
			for (int in = 0; in < layer.inputSize; in++) {
				for (int out = 0; out < layer.outputSize; out++) {
					layer.weights[in][out] = random.nextGaussian();
				}
			}
		}
	}
	
	public void initializeWeights(double[] weights) {
		int size = 0;
		for (Layer layer : layers) {
			for (int in = 0; in < layer.inputSize; in++) {
				for (int out = 0; out < layer.outputSize; out++) {
					size++;
				}
			}
		}
		if (weights.length != size) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int index = 0;
		for (Layer layer : layers) {
			for (int in = 0; in < layer.inputSize; in++) {
				for (int out = 0; out < layer.outputSize; out++) {
					layer.weights[in][out] = weights[index++];
				}
			}
		}
	}
	
	public double[] getWeights() {
		int size = 0;
		for (Layer layer : layers) {
			for (int in = 0; in < layer.inputSize; in++) {
				for (int out = 0; out < layer.outputSize; out++) {
					size++;
				}
			}
		}
		double[] weights = new double[size];
		int index = 0;
		for (Layer layer : layers) {
			for (int in = 0; in < layer.inputSize; in++) {
				for (int out = 0; out < layer.outputSize; out++) {
					weights[index++] = layer.weights[in][out];
				}
			}
		}
		return weights;
	}
}
