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
package com.qtplaf.library.ai.nnet.graph.learning;

import java.util.List;

import com.qtplaf.library.ai.nnet.graph.Layer;
import com.qtplaf.library.ai.nnet.graph.NeuralNetwork;
import com.qtplaf.library.ai.nnet.graph.Neuron;
import com.qtplaf.library.ai.nnet.graph.Synapse;
import com.qtplaf.library.ai.nnet.graph.function.OutputFunction;
import com.qtplaf.library.math.Vector;

/**
 * Back propagation learning process for feed forward neural networks, with learning rate and momentum, and separate
 * methods to update weights and biases. The bias–variance tradeoff (or dilemma) is the problem of simultaneously
 * minimizing two sources of error and the proposed approach adjust them separately.
 * <p>
 * The learning rate, 0.0 &lt; learning rate &lt;= 1.0, is the factor that reduces that gradient or error derivative. A
 * low learning rate slows learning, while a high learning rate produces changes that generate a greater error, mainly
 * when the error is very small. That suggests to reduce the learning rate while the error becomes small.
 * <p>
 * The momentum, 0.0 &lt;= momentum &lt;= 1.0, is the factor that weights the last change applied against the next error
 * gradient. With a momentum of 0.0, only the next error affects the changes in weights and biases.
 * 
 * @author Miquel Sas
 */
public class BackPropagationLearningProcess extends LearningProcess {

	/**
	 * The learning rate.
	 */
	private double learningRate = 0.1;
	/**
	 * The momentum.
	 */
	private double momentum = 0.1;
	/**
	 * A boolean that indicates if weights will be updated during the current error processing.
	 */
	private boolean updateWeights = true;
	/**
	 * A boolean that indicates if biases will be updated during the current error processing.
	 */
	private boolean updateBiases = true;

	/**
	 * Constructor assigning the network.
	 * 
	 * @param network The network to train.
	 */
	public BackPropagationLearningProcess(NeuralNetwork network) {
		super(network);
	}

	/**
	 * Process the input vector and return the network output vector.
	 * 
	 * @param inputVector The input vector to process.
	 * @return The output vector.
	 */
	public Vector processInput(Vector inputVector) {
		return getNetwork().feedForward(inputVector);
	}

	/**
	 * Process the error vector updating the network layers weights and biases.
	 * 
	 * @param errorVector The error vector.
	 */
	public void processError(Vector errorVector) {
		// Update the output layer errors
		updateOutputLayerErrors(errorVector);
		// Update hidden layers errors
		updateHiddenLayersErrors();
	}

	/**
	 * Updates the network output layer neuron errors and weights, given the list of output errors, by multiplying the
	 * output error by the neuron output function derivative applied to the neuron input value. Note that when the
	 * derivative is simply 1.0 the neuron error is set to the output error.
	 * <p>
	 * After calculating the neuron error the methods 'updateNeuronWeights' and 'updateNeuronBias' are called.
	 * 
	 * @param errorVector
	 */
	private void updateOutputLayerErrors(Vector errorVector) {
		List<Neuron> neurons = getNetwork().getOutputLayer().getNeurons();
		for (int i = 0; i < neurons.size(); i++) {
			Neuron neuron = neurons.get(i);
			double outputError = errorVector.get(i);
			updateOutputLayerErrors(outputError, neuron);
		}
	}

	/**
	 * Updates the output layer error to the neuron.
	 * 
	 * @param outputError The output error.
	 * @param neuron The neuron.
	 */
	private void updateOutputLayerErrors(double outputError, Neuron neuron) {

		double neuronInput = neuron.getInput();
		OutputFunction outputFunction = neuron.getOutputFunction();
		double inputDerivative = outputFunction.getDerivative(neuronInput);
		double neuronError = outputError * inputDerivative;

		neuron.setError(neuronError);

		// Update neuron weights if applicable
		if (updateWeights) {
			updateNeuronWeights(neuron);
		}

		// Update bias if applicable
		if (updateBiases) {
			updateNeuronBias(neuron);
		}
	}

	/**
	 * Updates the hidden layers errors by back propagating the output layer errors.
	 */
	private void updateHiddenLayersErrors() {
		// Back iterate hidden layers
		List<Layer> layers = getNetwork().getLayers();
		for (int i = layers.size() - 2; i > 0; i--) {
			Layer layer = layers.get(i);
			// Iterate the neurons of the layer
			List<Neuron> neurons = layer.getNeurons();
			for (Neuron neuron : neurons) {
				// Do update.
				updateHiddenLayersErrors(neuron);
			}
		}
	}

	/**
	 * Updates the hidden layers errors by back propagating the output layer errors, for the argument neuron.
	 * 
	 * @param neuron The neuron.
	 */
	private void updateHiddenLayersErrors(Neuron neuron) {

		// Calculate the neuron error by weighting the errors of the output neurons of the output synapses.
		List<Synapse> outputSynapses = neuron.getOutputSynapses();
		double weightedOutputError = 0;
		for (Synapse outputSynapse : outputSynapses) {
			// Output neuron and update.
			Neuron outputNeuron = outputSynapse.getOutputNeuron();
			double error = outputNeuron.getError();
			double weight = outputSynapse.getWeight();
			weightedOutputError += (error * weight);
		}

		// Back propagate weighted output
		OutputFunction outputFunction = neuron.getOutputFunction();
		double input = neuron.getInput();
		double derivative = outputFunction.getDerivative(input);
		double neuronError = weightedOutputError * derivative;
		neuron.setError(neuronError);

		// Update neuron weights if applicable
		if (updateWeights) {
			updateNeuronWeights(neuron);
		}

		// Update bias if applicable
		if (updateBiases) {
			updateNeuronBias(neuron);
		}
	}

	/**
	 * Updates the neuron input weights once the error has been set to the neuron, with the following formula applied
	 * for each synapse:
	 * <p>
	 * weightChange = learningRate * neuronError * synapseOutput
	 * 
	 * @param neuron The neuron to update input synapses weights.
	 */
	private void updateNeuronWeights(Neuron neuron) {

		// The current neuron error
		double neuronError = neuron.getError();

		// Iterate the list of input synapses
		List<Synapse> inputSynapses = neuron.getInputSynapses();
		for (Synapse inputSynapse : inputSynapses) {
			// Input from current synapse
			double synapseOutput = inputSynapse.getInputNeuron().getOutput();
			// Next weight change
			double nextWeightChange = learningRate * neuronError * synapseOutput;
			// Last weight change
			double lastWeightChange = inputSynapse.getWeightUpdate();
			// Calculate weight change using momentum
			double weightChange = (nextWeightChange * (1 - momentum)) + (lastWeightChange * momentum);
			// Update the weight.
			inputSynapse.increaseWeight(weightChange);
		}
	}

	/**
	 * Updates the neuron bias once the error has been set to the neuron, with the formula:
	 * <p>
	 * biasChange = learningRate * neuronError * neuronInput
	 * 
	 * @param neuron The neuron to update bias.
	 */
	private void updateNeuronBias(Neuron neuron) {

		// The current neuron error
		double neuronError = neuron.getError();
		// The current neuron input
		double neuronInput = neuron.getInput();
		// Next bias change
		double nextBiasChange = learningRate * neuronError * neuronInput;
		// Last bias change
		double lastBiasChange = neuron.getBiasUpdate();
		// Calculate bias change using momentum
		double biasChange = (nextBiasChange * (1 - momentum)) + (lastBiasChange * momentum);
		// Update the bias.
		neuron.increaseBias(biasChange);
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
	 * Returns the momentum.
	 * 
	 * @return The momentum
	 */
	public double getMomentum() {
		return momentum;
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
	 * Check if weights should be updated.
	 * 
	 * @return A boolean that indicates if weights should be updated.
	 */
	public boolean isUpdateWeights() {
		return updateWeights;
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
	 * Check if biases should be updated.
	 * 
	 * @return A boolean that indicates if biases should be updated.
	 */
	public boolean isUpdateBiases() {
		return updateBiases;
	}

	/**
	 * Set if biases should be updated.
	 * 
	 * @param updateBiases A boolean that indicates if biases should be updated.
	 */
	public void setUpdateBiases(boolean updateBiases) {
		this.updateBiases = updateBiases;
	}
}
