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

package com.qtplaf.library.ai.nnet.bp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.qtplaf.library.math.Calculator;

/**
 * Back propagation neural network that executes as possible in parallel.
 *
 * @author Miquel Sas
 */
public class BPNN {

	/**
	 * Recursive action to process inputs for a layer.
	 */
	class LayerInputs extends RecursiveAction {
		int layer;
		LayerInputsIndex[] tasks;

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		LayerInputsIndex[] getTasks() {
			if (tasks == null) {
				int outputSize = getOutputSize(layer);
				tasks = new LayerInputsIndex[outputSize];
				for (int out = 0; out < outputSize; out++) {
					LayerInputsIndex task = new LayerInputsIndex();
					task.layer = layer;
					task.out = out;
					tasks[out] = task;
				}
			}
			return tasks;
		}

		void reset() {
			LayerInputsIndex[] tasks = getTasks();
			for (LayerInputsIndex task : tasks) {
				task.reinitialize();
			}
			super.reinitialize();
		}
	}

	/**
	 * Recursive action to process inputs for a layer and an index.
	 */
	class LayerInputsIndex extends RecursiveAction {
		int layer;
		int out;

		@Override
		protected void compute() {
			processInputs(layer, out);
		}
	}

	/**
	 * Recursive action to update output layer errors.
	 */
	class OutputErrors extends RecursiveAction {
		OutputErrorsIndex[] tasks;

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		OutputErrorsIndex[] getTasks() {
			if (tasks == null) {
				int layer = getSize() - 1;
				int outputSize = getOutputSize(layer);
				tasks = new OutputErrorsIndex[outputSize];
				for (int out = 0; out < outputSize; out++) {
					OutputErrorsIndex task = new OutputErrorsIndex();
					task.out = out;
					tasks[out] = task;
				}
			}
			return tasks;
		}

		void reset(double[] errors) {
			OutputErrorsIndex[] tasks = getTasks();
			for (OutputErrorsIndex task : tasks) {
				task.errors = errors;
				task.reinitialize();
			}
			super.reinitialize();
		}
	}

	/**
	 * Recursive action to update output layer errors.
	 */
	class OutputErrorsIndex extends RecursiveAction {
		double[] errors;
		int out;

		@Override
		protected void compute() {
			updateOutputLayerErrors(errors, out);
		}
	}

	/**
	 * Recursive action to update hidden layer errors for an output index.
	 */
	class HiddenErrors extends RecursiveAction {
		int layerIn;
		int layerOut;
		HiddenErrorsIndex[] tasks;

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		HiddenErrorsIndex[] getTasks() {
			if (tasks == null) {
				tasks = new HiddenErrorsIndex[getInputSize(layerOut)];
				for (int in = 0; in < getInputSize(layerOut); in++) {
					HiddenErrorsIndex task = new HiddenErrorsIndex();
					task.layerIn = layerIn;
					task.layerOut = layerOut;
					task.in = in;
					tasks[in] = task;
				}
			}
			return tasks;
		}

		void reset() {
			HiddenErrorsIndex[] tasks = getTasks();
			for (HiddenErrorsIndex task : tasks) {
				task.reinitialize();
			}
			super.reinitialize();
		}
	}

	/**
	 * Recursive action to update hidden layer errors for a given output layer input index.
	 */
	class HiddenErrorsIndex extends RecursiveAction {
		int layerIn;
		int layerOut;
		int in;

		@Override
		protected void compute() {
			updateHiddenLayersErrors(layerIn, layerOut, in);
		}
	}

	/** List of layers weights. */
	private List<double[][]> weights = new ArrayList<>();
	/** List of last weights update used in back propagation. */
	private List<double[][]> deltas = new ArrayList<>();
	/** List of layers inputs/outputs. */
	private List<double[]> ios = new ArrayList<>();
	/** List of last layers signals (inputs of the sigmoid). */
	private List<double[]> signals = new ArrayList<>();
	/** List of layers errors. */
	private List<double[]> errors = new ArrayList<>();

	/** The learning rate. */
	private double learningRate = 0.1;
	/** Learning rate decrease factor, used to reduce the learning rate when the error increases. */
	private double learningRateDecreaseFactor = 0.8;

	/** Pool to execute recursive tasks. */
	private ForkJoinPool pool = new ForkJoinPool();
	/** List of recursive tasks to process inputs. */
	private List<LayerInputs> layerInputsTasks;
	/** Task to update output layer errors. */
	private OutputErrors outputErrors;
	/** List of recursive tasks to hidden layers errors. */
	private List<HiddenErrors> hiddenErrorsTasks;

	/**
	 * Constructor.
	 */
	public BPNN() {
		super();
	}

	/**
	 * Add the first layer. Can only be called once.
	 * 
	 * @param inputSize The input size.
	 * @param outputSize The output size.
	 */
	public void addLayer(int inputSize, int outputSize) {
		if (!weights.isEmpty()) {
			throw new IllegalStateException();
		}
		addLayerRaw(inputSize, outputSize);
	}

	/**
	 * Add subsequent layers. The input size of the layer is the output size of the previous one.
	 * 
	 * @param outputSize The output size.
	 */
	public void addLayer(int outputSize) {
		if (weights.isEmpty()) {
			throw new IllegalStateException();
		}
		int inputSize = getInputSize(getSize() - 1);
		addLayerRaw(inputSize, outputSize);
	}

	/**
	 * Add a layer without validation.
	 * 
	 * @param inputSize The input size.
	 * @param outputSize The output size.
	 */
	private void addLayerRaw(int inputSize, int outputSize) {
		if (ios.isEmpty()) {
			ios.add(new double[inputSize]);
		}
		ios.add(new double[outputSize]);
		signals.add(new double[outputSize]);
		errors.add(new double[outputSize]);
		weights.add(new double[inputSize][outputSize]);
		deltas.add(new double[inputSize][outputSize]);
	}

	/**
	 * Returns the layer inputs.
	 * 
	 * @param layer The layer.
	 * @return The inputs.
	 */
	public double[] getInputs(int layer) {
		return ios.get(layer);
	}

	/**
	 * Returns the layer outputs.
	 * 
	 * @param layer The layer.
	 * @return The outputs.
	 */
	public double[] getOutputs(int layer) {
		return ios.get(layer + 1);
	}

	/**
	 * Returns the layer errors.
	 * 
	 * @param layer The layer.
	 * @return The errors.
	 */
	public double[] getErrors(int layer) {
		return errors.get(layer);
	}

	/**
	 * Returns the layer signals.
	 * 
	 * @param layer The layer.
	 * @return The signals.
	 */
	public double[] getSignals(int layer) {
		return signals.get(layer);
	}

	/**
	 * Returns the layer weights.
	 * 
	 * @param layer The layer.
	 * @return The weights.
	 */
	public double[][] getWeights(int layer) {
		return weights.get(layer);
	}

	/**
	 * Returns the layer weights deltas.
	 * 
	 * @param layer The layer.
	 * @return The weights deltas.
	 */
	public double[][] getDeltas(int layer) {
		return deltas.get(layer);
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
	 * Set the learning rate decrease factor.
	 * 
	 * @param learningRateDecreaseFactor The learning rate decrease factor.
	 */
	public void setLearningRateDecreaseFactor(double learningRateDecreaseFactor) {
		this.learningRateDecreaseFactor = learningRateDecreaseFactor;
	}

	/**
	 * Decrease the learning rate.
	 */
	public void decreaseLearningRate() {
		learningRate *= learningRateDecreaseFactor;
	}

	/**
	 * Returns the size as the number of layers.
	 * 
	 * @return The size.
	 */
	public int getSize() {
		return weights.size();
	}

	/**
	 * Returns the network outputs.
	 * 
	 * @return The outputs.
	 */
	public double[] getOutputs() {
		return getOutputs(getSize() - 1);
	}

	/**
	 * Returns the learning rate.
	 * 
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * Returns the input size of the layer.
	 * 
	 * @param layer The layer index.
	 * @return The input size.
	 */
	public int getInputSize(int layer) {
		if (layer < 0 || layer >= getSize()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return getInputs(layer).length;
	}

	/**
	 * Returns the output size of the layer.
	 * 
	 * @param layer The layer index.
	 * @return The output size.
	 */
	public int getOutputSize(int layer) {
		if (layer < 0 || layer >= getSize()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return getOutputs(layer).length;
	}

	/**
	 * Initializes the weights with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 */
	public void initializeWeights() {
		Random random = new Random();
		for (int layer = 0; layer < getSize(); layer++) {
			int inputSize = getInputSize(layer);
			int outputSize = getOutputSize(layer);
			double[][] weights = getWeights(layer);
			for (int in = 0; in < inputSize; in++) {
				for (int out = 0; out < outputSize; out++) {
					weights[in][out] = random.nextGaussian();
				}
			}
		}
	}

	/**
	 * Initialize weights with a raw vector of weights.
	 * 
	 * @param weightsRaw The raw vector of weights.
	 */
	public void initializeWeights(double[] weightsRaw) {
		int size = getWeightsRawSize();
		if (weightsRaw.length != size) {
			throw new ArrayIndexOutOfBoundsException();
		}
		int index = 0;
		for (int layer = 0; layer < getSize(); layer++) {
			int inputSize = getInputSize(layer);
			int outputSize = getOutputSize(layer);
			double[][] weights = getWeights(layer);
			for (int in = 0; in < inputSize; in++) {
				for (int out = 0; out < outputSize; out++) {
					weights[in][out] = weightsRaw[index++];
				}
			}
		}
	}

	/**
	 * Returns the weights as a raw vector.
	 * 
	 * @return The weights as a raw vector.
	 */
	public double[] getWeightsRaw() {
		int size = getWeightsRawSize();
		double[] weightsRaw = new double[size];
		int index = 0;
		for (int layer = 0; layer < getSize(); layer++) {
			int inputSize = getInputSize(layer);
			int outputSize = getOutputSize(layer);
			double[][] weights = getWeights(layer);
			for (int in = 0; in < inputSize; in++) {
				for (int out = 0; out < outputSize; out++) {
					weightsRaw[index++] = weights[in][out];
				}
			}
		}
		return weightsRaw;
	}

	/**
	 * Returns the weights size of a raw vector.
	 * 
	 * @return The weights raw size.
	 */
	private int getWeightsRawSize() {
		int size = 0;
		for (int layer = 0; layer < getSize(); layer++) {
			int inputSize = getInputSize(layer);
			int outputSize = getOutputSize(layer);
			for (int in = 0; in < inputSize; in++) {
				for (int out = 0; out < outputSize; out++) {
					size++;
				}
			}
		}
		return size;
	}

	/**
	 * Process the inputs and store the outputs.
	 * 
	 * @param inputs The inputs to process.
	 */
	public void processInputs(double[] inputs) {

		// Build tasks if not done.
		if (layerInputsTasks == null) {
			layerInputsTasks = new ArrayList<>();
			int size = getSize();
			for (int layer = 0; layer < size; layer++) {
				LayerInputs layerInputs = new LayerInputs();
				layerInputs.layer = layer;
				layerInputsTasks.add(layerInputs);
			}
		}

		// Set initial inputs.
		ios.set(0, inputs);

		// Process layers.
		for (int layer = 0; layer < getSize(); layer++) {
			LayerInputs layerInputs = layerInputsTasks.get(layer);
			layerInputs.reset();
			pool.invoke(layerInputs);
		}
	}

	/**
	 * Process the inputs for the output index of the given layer.
	 * 
	 * @param layer Layer.
	 * @param out Output index.
	 */
	private void processInputs(int layer, int out) {
		// Inputs.
		double[] inputs = getInputs(layer);
		// Input size of the layer.
		int inputSize = getInputSize(layer);
		// Retrieve the weights of the layer.
		double[][] weights = getWeights(layer);
		// Weighted input.
		double weighted = 0;
		for (int in = 0; in < inputSize; in++) {
			weighted += inputs[in] * weights[in][out];
		}
		// Signals and outputs of the layer.
		double[] signals = getSignals(layer);
		double[] outputs = getOutputs(layer);
		// Calculate output.
		signals[out] = weighted;
		outputs[out] = Calculator.sigmoid(weighted);
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
		if (hiddenErrorsTasks == null) {
			hiddenErrorsTasks = new ArrayList<>();
			for (int i = getSize() - 2; i >= 0; i--) {
				int layerIn = i;
				int layerOut = i + 1;
				HiddenErrors task = new HiddenErrors();
				task.layerIn = layerIn;
				task.layerOut = layerOut;
				hiddenErrorsTasks.add(task);
			}
		}
		for (HiddenErrors task : hiddenErrorsTasks) {
			task.reset();
			pool.invoke(task);
		}
	}

	/**
	 * Update the output layer errors.
	 * 
	 * @param errors Output errors.
	 */
	private void updateOutputLayerErrors(double[] errors) {
		if (outputErrors == null) {
			outputErrors = new OutputErrors();
		}
		outputErrors.reset(errors);
		pool.invoke(outputErrors);
	}

	/**
	 * Update errors for the hidden layer (in and out).
	 * 
	 * @param layerIn The input layer.
	 * @param layerOut The output layer.
	 * @param in The in index in the out layer inputs.
	 */
	private void updateHiddenLayersErrors(int layerIn, int layerOut, int in) {
		double weightedError = 0;
		for (int out = 0; out < getOutputSize(layerOut); out++) {
			double error = getErrors(layerOut)[out];
			double weight = getWeights(layerOut)[in][out];
			weightedError += (error * weight);
		}

		int out = in; // Out index in input layer
		double signal = getSignals(layerIn)[out];
		double derivative = Calculator.sigmoidDerivative(signal);
		double error = weightedError * derivative;
		getErrors(layerIn)[out] = error;

		updateWeights(layerIn, out);
	}

	/**
	 * Update the output layer errors.
	 * 
	 * @param errors The errors.
	 * @param out The output index.
	 */
	private void updateOutputLayerErrors(double[] errors, int out) {
		int layer = getSize() - 1;
		double signal = getSignals(layer)[out];
		double derivative = Calculator.sigmoidDerivative(signal);
		double error = errors[out] * derivative;
		getErrors(layer)[out] = error;
		updateWeights(layer, out);
	}

	/**
	 * Update weights for the argument layer and output index.
	 * 
	 * @param layer The layer.
	 * @param out The output index.
	 */
	private void updateWeights(int layer, int out) {
		double error = getErrors(layer)[out];
		for (int in = 0; in < getInputSize(layer); in++) {
			// Input from current synapse
			double input = getInputs(layer)[in];
			// Calculate weight change using momentum
			double weightChange = learningRate * error * input;
			// Update the weight and save change.
			getWeights(layer)[in][out] += weightChange;
			getDeltas(layer)[in][out] = weightChange;
		}
	}
}
