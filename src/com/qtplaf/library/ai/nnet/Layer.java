/**
 * 
 */
package com.qtplaf.library.ai.nnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A layer of a feed forward neural network. A layer is just a container for neurons.
 * 
 * @author Miquel Sas
 */
public class Layer implements Iterable<Neuron>, Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 3641239539146378713L;

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

	/**
	 * Returns an iterator over the neurons.
	 * 
	 * @return An iterator over the neurons.
	 */
	public Iterator<Neuron> iterator() {
		return neurons.iterator();
	}

	/**
	 * Returns the neuron at the given index.
	 * 
	 * @param index The index.
	 * @return The neuron at the index position.
	 */
	public Neuron get(int index) {
		return neurons.get(index);
	}

	/**
	 * Returns this layer size, that is, the number of neurons.
	 * 
	 * @return The size or number of neurons.
	 */
	public int size() {
		return neurons.size();
	}
}
