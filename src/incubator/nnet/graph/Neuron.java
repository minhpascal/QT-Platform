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
package incubator.nnet.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import incubator.nnet.graph.function.InputFunction;
import incubator.nnet.graph.function.OutputFunction;

/**
 * A neuron of a neural network.
 * 
 * @author Miquel Sas
 */
public class Neuron implements Serializable {

	/**
	 * Links two neurons.
	 * 
	 * @param inputNeuron The input neuron.
	 * @param outputNeuron The output neuron.
	 * @param weight The link weight.
	 */
	public static void link(Neuron inputNeuron, Neuron outputNeuron, double weight) {
		Synapse synapse = new Synapse();
		synapse.setWeight(weight);
		inputNeuron.addOutputSynapse(synapse);
		outputNeuron.addInputSynapse(synapse);
	}

	/**
	 * An unique identifier within the network.
	 */
	private long id;
	/**
	 * The list of input synapses that links input neurons to this neuron.
	 */
	private List<Synapse> inputSynapses = new ArrayList<>();
	/**
	 * The list of output synapses that links output neurons to this neuron.
	 */
	private List<Synapse> outputSynapses = new ArrayList<>();
	/**
	 * The output function used to calculate the output given the input.
	 */
	private OutputFunction outputFunction;
	/**
	 * The neuron input function used to retrieve input from input neurons.
	 */
	private InputFunction inputFunction;
	/**
	 * The neuron bias.
	 */
	private double bias = 0;
	/**
	 * This neuron output value, calculated by passing to the output function the input value.
	 */
	private double output;
	/**
	 * The input value, calculated by applying the input function to the input synapses, and adding the bias.
	 */
	private double input;
	/**
	 * Temporary bias update.
	 */
	private transient double biasUpdate;
	/**
	 * Temporary error.
	 */
	private transient double error;

	/**
	 * Default constructor.
	 * 
	 * @param id the neuron unique identifier.
	 */
	public Neuron(long id) {
		super();
		this.id = id;
	}

	/**
	 * Returns this neuron identifier.
	 * 
	 * @return The identifier.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Adds a synapse to the list of input synapses.
	 * 
	 * @param synapse The synapse to add.
	 */
	public void addInputSynapse(Synapse synapse) {
		synapse.setOutputNeuron(this);
		inputSynapses.add(synapse);
	}

	/**
	 * Adds a synapse to the list of output synapses.
	 * 
	 * @param synapse The synapse to add.
	 */
	public void addOutputSynapse(Synapse synapse) {
		synapse.setInputNeuron(this);
		outputSynapses.add(synapse);
	}

	/**
	 * Returns the list of input synapses.
	 * 
	 * @return The list of input synapses.
	 */
	public List<Synapse> getInputSynapses() {
		return inputSynapses;
	}

	/**
	 * Returns the list of output synapses.
	 * 
	 * @return The list of output synapses.
	 */
	public List<Synapse> getOutputSynapses() {
		return outputSynapses;
	}

	/**
	 * Returns this neuron bias.
	 * 
	 * @return The bias.
	 */
	public double getBias() {
		return bias;
	}

	/**
	 * Sets this neuron bias.
	 * 
	 * @param bias The bias.
	 */
	public void setBias(double bias) {
		this.bias = bias;
	}

	/**
	 * Returns the output. Outputs are normally calculated, unless the neuron is an origin input neuron, and has no
	 * input synapses.
	 * 
	 * @return The output value.
	 */
	public double getOutput() {
		return output;
	}

	/**
	 * Sets this neuron output. This operation is only allowed for origin input neurons.
	 * 
	 * @param output The output value
	 */
	public void setOutput(double output) {
		this.output = output;
	}

	/**
	 * Returns the input value, previously calculated with a call to the input function and adding the bias.
	 * 
	 * @return The input value.
	 */
	public double getInput() {
		return input;
	}

	/**
	 * Set the input.
	 * 
	 * @param input The input.
	 */
	public void setInput(double input) {
		this.input = input;
	}

	/**
	 * Clear this neuron output setting it to 0.
	 */
	public void clearOutput() {
		output = 0;
	}

	/**
	 * Returns the output function used to calculate output given the input.
	 * 
	 * @return The output function.
	 */
	public OutputFunction getOutputFunction() {
		return outputFunction;
	}

	/**
	 * Sets the output function used to calculate output given the input.
	 * 
	 * @param outputFunction The output function.
	 */
	public void setOutputFunction(OutputFunction outputFunction) {
		this.outputFunction = outputFunction;
	}

	/**
	 * Returns the input function to retrieve input from input neurons.
	 * 
	 * @return The input function.
	 */
	public InputFunction getInputFunction() {
		return inputFunction;
	}

	/**
	 * Sets the input function to retrieve input from input neurons.
	 * 
	 * @param inputFunction The input function.
	 */
	public void setInputFunction(InputFunction inputFunction) {
		this.inputFunction = inputFunction;
	}

	/**
	 * Calculates the output of this neuron and stores it in the output field. To calculate the output, this method
	 * passes the input synapses to the input function, and normally the input function would retrieve the output of the
	 * input neurons through the getter 'getOutput', therefore the output of the input neurons should have been
	 * calculated previously if those input neurons are not origin input neurons.
	 */
	public void calculateOutput() {
		calculateInput();
		output = outputFunction.getOutput(input);
	}

	/**
	 * Calculates the input of this neuron by applying the input function and adding the bias to the result. This
	 * function can only be applied to non origin input neurons.
	 */
	public void calculateInput() {
		input = inputFunction.getInput(inputSynapses) + bias;
	}

	/**
	 * Returns the bias update.
	 * 
	 * @return The bias update.
	 */
	public double getBiasUpdate() {
		return biasUpdate;
	}

	/**
	 * Increase the neuron bias and store this last bias update.
	 * 
	 * @param delta The increase.
	 */
	public void increaseBias(double delta) {
		bias += delta;
		biasUpdate = delta;
	}

	/**
	 * Return the error.
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
}
