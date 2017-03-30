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

import java.util.ArrayList;
import java.util.List;

/**
 * A layer of a feed forward neural network. A layer is just a container for neurons.
 * 
 * @author Miquel Sas
 */
public class Layer {

	/**
	 * The list of neurons.
	 */
	private List<Neuron> neurons;

	/**
	 * Default constructor.
	 */
	public Layer() {
		super();
		this.neurons = new ArrayList<>();
	}

	/**
	 * Constructor assigning the list of neurons.
	 * 
	 * @param neurons The list of neurons.
	 */
	public Layer(List<Neuron> neurons) {
		super();
		this.neurons = neurons;
	}

	/**
	 * Adds a neuron to list of neurons.
	 * 
	 * @param neuron The neuron to add to the list.
	 */
	public void addNeuron(Neuron neuron) {
		neurons.add(neuron);
	}

	/**
	 * Returns the list of neurons.
	 * 
	 * @return The list of neurons
	 */
	public List<Neuron> getNeurons() {
		return neurons;
	}
}
