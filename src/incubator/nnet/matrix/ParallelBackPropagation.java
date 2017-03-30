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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Back propagation learning process.
 *
 * @author Miquel Sas
 */
public class ParallelBackPropagation extends LearningProcess {

	/**
	 * Recursive action to process inputs for a layer.
	 */
	class LayerInputs extends RecursiveAction {
		Layer layer;
		LayerInputsIndex[] tasks;

		LayerInputs(Layer layer) {
			super();
			this.layer = layer;
		}

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		LayerInputsIndex[] getTasks() {
			if (tasks == null) {
				int size = layer.outputSize;
				tasks = new LayerInputsIndex[size];
				for (int i = 0; i < size; i++) {
					tasks[i] = new LayerInputsIndex(layer, i);
				}
			}
			return tasks;
		}
		
		void reset(double[] inputs) {
			layer.inputs = inputs;
			LayerInputsIndex[] tasks = getTasks();
			for (LayerInputsIndex task : tasks) {
				task.inputs = inputs;
				task.reinitialize();
			}
			super.reinitialize();
		}
	}

	/**
	 * Recursive action to process inputs for a layer and an index.
	 */
	class LayerInputsIndex extends RecursiveAction {
		Layer layer;
		double[] inputs;
		int out;

		LayerInputsIndex(Layer layer, int out) {
			super();
			this.layer = layer;
			this.out = out;
		}

		@Override
		protected void compute() {
			layer.processInputs(inputs, out);
		}
	}

	/**
	 * Recursive action to update output layer errors.
	 */
	class OutputErrors extends RecursiveAction {
		Layer layer;
		OutputErrorsIndex[] tasks;

		OutputErrors(Layer layer) {
			super();
			this.layer = layer;
		}

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		OutputErrorsIndex[] getTasks() {
			if (tasks == null) {
				int size = layer.outputSize;
				tasks = new OutputErrorsIndex[size];
				for (int i = 0; i < size; i++) {
					tasks[i] = new OutputErrorsIndex(layer, i);
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
		Layer layer;
		double[] errors;
		int out;

		OutputErrorsIndex(Layer layer, int out) {
			super();
			this.layer = layer;
			this.out = out;
		}

		@Override
		protected void compute() {
			updateOutputLayerErrors(layer, errors, out);
		}
	}

	/**
	 * Recursive action to update hidden layer errors for an output index.
	 */
	class HiddenErrors extends RecursiveAction {
		Layer layerIn;
		Layer layerOut;
		HiddenErrorsIndex[] tasks;

		HiddenErrors(Layer layerIn, Layer layerOut) {
			super();
			this.layerIn = layerIn;
			this.layerOut = layerOut;
		}

		@Override
		protected void compute() {
			invokeAll(getTasks());
		}

		HiddenErrorsIndex[] getTasks() {
			if (tasks == null) {
				int size = layerOut.inputSize;
				tasks = new HiddenErrorsIndex[size];
				for (int i = 0; i < size; i++) {
					tasks[i] = new HiddenErrorsIndex(layerIn, layerOut, i);
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
		Layer layerIn;
		Layer layerOut;
		int in;

		HiddenErrorsIndex(Layer layerIn, Layer layerOut, int in) {
			super();
			this.layerIn = layerIn;
			this.layerOut = layerOut;
			this.in = in;
		}

		@Override
		protected void compute() {
			updateHiddenLayersErrors(layerIn, layerOut, in);
		}
	}

	/** The learning rate. */
	private double learningRate = 0.1;
	/** Learning rate decrease factor. */
	private double learningRateDecreaseFactor = 0.8;
	/** A boolean that indicates if weights will be updated during the current error processing. */
	private boolean updateWeights = true;
	/** Pool to execute recursive tasks. */
	private ForkJoinPool pool = new ForkJoinPool();
	/** List of recursive tasks to process inputs. */
	private List<LayerInputs> layerInputsTasks;
	/** List of recursive tasks to update hidden layers errors. */
	private List<HiddenErrors> hiddenErrorsTasks;
	/** Task to update output layer errors. */
	private OutputErrors outputErrors;

	/**
	 * Constructor assigning the network.
	 * 
	 * @param network The network to train.
	 */
	public ParallelBackPropagation(NeuralNetwork network) {
		super(network);
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
	 * Set if weights should be updated.
	 * 
	 * @param updateWeights A boolean that indicates if weights should be updated.
	 */
	public void setUpdateWeights(boolean updateWeights) {
		this.updateWeights = updateWeights;
	}

	/**
	 * Decrease the learning rate.
	 */
	public void decreaseLearningRate() {
		learningRate *= learningRateDecreaseFactor;
	}

	/**
	 * Process the input vector and return the network output vector.
	 * 
	 * @param inputs The input vector to process.
	 * @return The output vector.
	 */
	public double[] processInputs(double[] inputs) {
		if (layerInputsTasks == null) {
			layerInputsTasks = new ArrayList<>();
			for (Layer layer : getNetwork().layers) {
				layerInputsTasks.add(new LayerInputs(layer));
			}
		}
		double[] outputs = inputs;
		for (LayerInputs task : layerInputsTasks) {
			task.reset(outputs);
			pool.invoke(task);
			outputs = task.layer.outputs;
		}
		return outputs;
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
			for (int i = getNetwork().layers.size() - 2; i >= 0; i--) {
				Layer layerIn = getNetwork().layers.get(i);
				Layer layerOut = getNetwork().layers.get(i + 1);
				hiddenErrorsTasks.add(new HiddenErrors(layerIn, layerOut));
			}
		}
		for (HiddenErrors task : hiddenErrorsTasks) {
			task.reset();
			pool.invoke(task);
		}
	}

	/**
	 * Update errors for the hidden layer (in and out).
	 * 
	 * @param layerIn The input layer.
	 * @param layerOut The output layer.
	 * @param in The in index in the out layer inputs.
	 */
	private void updateHiddenLayersErrors(Layer layerIn, Layer layerOut, int in) {
		double weightedError = 0;
		for (int out = 0; out < layerOut.outputSize; out++) {
			double error = layerOut.errors[out];
			double weight = layerOut.weights[in][out];
			weightedError += (error * weight);
		}

		int out = in; // Out index in input layer
		double signal = layerIn.signals[out];
		double derivative = Calculator.sigmoidDerivative(signal);
		double error = weightedError * derivative;
		layerIn.errors[out] = error;

		// Update neuron weights if applicable
		if (updateWeights) {
			updateWeights(layerIn, out);
		}
	}

	/**
	 * Update the output layer errors.
	 * 
	 * @param errors Output errors.
	 */
	private void updateOutputLayerErrors(double[] errors) {
		if (outputErrors == null) {
			outputErrors = new OutputErrors(ListUtils.getLast(getNetwork().layers));
		}
		outputErrors.reset(errors);
		pool.invoke(outputErrors);
	}

	/**
	 * Update the output layer errors.
	 * 
	 * @param layer The layer.
	 * @param errors The errors.
	 * @param out The output index.
	 */
	private void updateOutputLayerErrors(Layer layer, double[] errors, int out) {
		double signal = layer.signals[out];
		double derivative = Calculator.sigmoidDerivative(signal);
		double error = errors[out] * derivative;
		layer.errors[out] = error;

		// Update neuron weights if applicable
		if (updateWeights) {
			updateWeights(layer, out);
		}
	}

	/**
	 * Update weights for the argument layer and output index.
	 * 
	 * @param layer The layer.
	 * @param out The output index.
	 */
	private void updateWeights(Layer layer, int out) {
		double error = layer.errors[out];
		for (int in = 0; in < layer.inputSize; in++) {
			// Input from current synapse
			double input = layer.inputs[in];
			// Calculate weight change using momentum
			double weightChange = learningRate * error * input;
			// Update the weight and save change.
			layer.weights[in][out] += weightChange;
			layer.deltaWeights[in][out] = weightChange;
		}
	}
}
