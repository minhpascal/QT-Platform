/**
 * 
 */
package com.qtplaf.library.ai.nnet.function;

import java.io.Serializable;
import java.util.List;

import com.qtplaf.library.ai.nnet.Synapse;

/**
 * Interface that should implement all neuron input functions.
 * 
 * @author Miquel Sas
 */
public interface InputFunction extends Serializable {
	/**
	 * Returns the input value used to compute the output value of a neuron.
	 * 
	 * @param inputSynapses List of input synapses.
	 * @return The input value.
	 */
	double getInput(List<Synapse> inputSynapses);
}
