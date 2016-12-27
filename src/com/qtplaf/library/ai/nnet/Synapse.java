/**
 * 
 */
package com.qtplaf.library.ai.nnet;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a synapse, the link between neurons in a neural network.
 * 
 * @author Miquel Sas
 */
public class Synapse implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5690239369134501230L;

	/**
	 * The input neuron.
	 */
	private Neuron inputNeuron;
	/**
	 * The output neuron.
	 */
	private Neuron outputNeuron;
	/**
	 * The weight of the input neuron.
	 */
	private double weight;
	/**
	 * The last weight update.
	 */
	private double weightUpdate;

	/**
	 * Default constructor.
	 */
	public Synapse() {
		super();
	}

	/**
	 * Returns the weight.
	 * 
	 * @return The weight.
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Set the weight.
	 * 
	 * @param weight The weight.
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Returns the input neuron.
	 * 
	 * @return The input neuron
	 */
	public Neuron getInputNeuron() {
		return inputNeuron;
	}

	/**
	 * Sets the input neuron.
	 * 
	 * @param inputNeuron The input neuron.
	 */
	public void setInputNeuron(Neuron inputNeuron) {
		this.inputNeuron = inputNeuron;
	}

	/**
	 * Returns the output neuron of this synapse.
	 * 
	 * @return The output neuron.
	 */
	public Neuron getOutputNeuron() {
		return outputNeuron;
	}

	/**
	 * Sets the output neuron of this synapse.
	 * 
	 * @param outputNeuron The output neuron.
	 */
	public void setOutputNeuron(Neuron outputNeuron) {
		this.outputNeuron = outputNeuron;
	}

	/**
	 * Check if this synapse object is equal to the argument object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Synapse) {
			Synapse s = (Synapse) o;
			boolean inputEquals = false;
			if (inputNeuron == null && s.inputNeuron == null) {
				inputEquals = true;
			} else {
				if (Objects.equals(inputNeuron, s.inputNeuron)) {
					inputEquals = true;
				}
			}
			boolean outputEquals = false;
			if (outputNeuron == null && s.outputNeuron == null) {
				outputEquals = true;
			} else {
				if (Objects.equals(outputNeuron, s.outputNeuron)) {
					outputEquals = true;
				}
			}
			if (inputEquals && outputEquals) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Increase the weight by the delta amount and register the last weight update.
	 * 
	 * @param delta The delta amount used to increase the weight.
	 */
	public void increaseWeight(double delta) {
		weight += delta;
		weightUpdate = delta;
	}

	/**
	 * Returns the last weight update.
	 * 
	 * @return The last weight update.
	 */
	public double getWeightUpdate() {
		return weightUpdate;
	}
}
