/**
 * 
 */
package com.qtplaf.library.ai.nnet.function.input;

import java.util.List;

import com.qtplaf.library.ai.nnet.Synapse;
import com.qtplaf.library.ai.nnet.function.InputFunction;

/**
 * Weighted sum of output from input neurons.
 * 
 * @author Miquel Sas
 */
public class WeightedSum implements InputFunction {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1296477402067237846L;

	/**
	 * Default constructor.
	 */
	public WeightedSum() {
	}

	/**
	 * Returns the input value used to compute the output value of a neuron.
	 * 
	 * @param inputSynapses List of input synapses.
	 * @return The input value.
	 */
	public double getInput(List<Synapse> inputSynapses) {
		double weightedInput = 0;
		for (Synapse inputSynapse : inputSynapses) {
			double input = inputSynapse.getInputNeuron().getOutput();
			double weight = inputSynapse.getWeight();
			weightedInput += (input * weight);
		}
		return weightedInput;
	}

}
