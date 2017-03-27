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
package com.qtplaf.library.ai.nnet.graph.function.input;

import java.util.List;

import com.qtplaf.library.ai.nnet.graph.Synapse;
import com.qtplaf.library.ai.nnet.graph.function.InputFunction;

/**
 * Weighted sum of output from input neurons.
 * 
 * @author Miquel Sas
 */
public class WeightedSum implements InputFunction {

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
