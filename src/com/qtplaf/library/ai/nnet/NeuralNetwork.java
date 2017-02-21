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
package com.qtplaf.library.ai.nnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.qtplaf.library.ai.nnet.function.InputFunction;
import com.qtplaf.library.ai.nnet.function.OutputFunction;
import com.qtplaf.library.math.Vector;

/**
 * A feed forward neural network.
 * 
 * @author Miquel Sas
 */
public class NeuralNetwork implements Iterable<Layer>, Serializable {

	/**
	 * Unique identifier. This identifier is aimed to uniquely identify this network within a pool of networks.
	 */
	private String id;
	/**
	 * Title or short description.
	 */
	private String title;
	/**
	 * Long description that normally would indicate this network purposes.
	 */
	private String description;
	/**
	 * The list of layers. A layer packs a list of neurons.
	 */
	private List<Layer> layers = new ArrayList<>();

	/**
	 * The last used neuron identifier.
	 */
	private long lastNeuronId = 0;

	/**
	 * Default constructor.
	 */
	public NeuralNetwork() {
		super();
	}

	/**
	 * Returns this network id.
	 * 
	 * @return The network id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets this network id.
	 * 
	 * @param id The network id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns this network title or short description.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets this network title or short description.
	 * 
	 * @param title The title or short description.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns this network long description.
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets this network long description.
	 * 
	 * @param description The long description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Adds a layer that is supposed to be well configured.
	 * 
	 * @param layer The layer to add.
	 */
	public void addLayer(Layer layer) {
		layers.add(layer);
	}

	/**
	 * Adds the first layer of the network, normally a hidden layer, with the given number of neurons, inputs and the
	 * output function. This have to be called once and only once when configuring the network layers.
	 * 
	 * @param numberOfNeurons The number of neurons for this first layer.
	 * @param numberOfInputs The number of inputs for this first layer.
	 * @param inputFunction The layer input function.
	 * @param outputFunction The layer output or activation function.
	 */
	public void addLayer(int numberOfNeurons, int numberOfInputs, InputFunction inputFunction, OutputFunction outputFunction) {
		if (!layers.isEmpty()) {
			throw new UnsupportedOperationException("The list of layers must be empty to set the first layer.");
		}

		// Input neurons
		List<Neuron> inputNeurons = new ArrayList<>();
		for (int i = 0; i < numberOfInputs; i++) {
			Neuron neuron = new Neuron(++lastNeuronId, true);
			inputNeurons.add(neuron);
		}

		// Output neurons
		List<Neuron> outputNeurons = new ArrayList<>();
		for (int i = 0; i < numberOfNeurons; i++) {
			Neuron neuron = new Neuron(++lastNeuronId);
			neuron.setInputFunction(inputFunction);
			neuron.setOutputFunction(outputFunction);
			neuron.setBias(0.0);
			outputNeurons.add(neuron);
		}

		// Link the layers
		for (Neuron outputNeuron : outputNeurons) {
			for (Neuron inputNeuron : inputNeurons) {
				Neuron.link(inputNeuron, outputNeuron, 1.0);
			}
		}

		// Add layers
		layers.add(new Layer(inputNeurons));
		layers.add(new Layer(outputNeurons));
	}

	/**
	 * Adds a subsequent layer, with the given number of neurons, the activation function, and the previous layer number
	 * of neurons as number of inputs.
	 * 
	 * @param numberOfNeurons The number of neurons for this subsequent layer.
	 * @param inputFunction The layer input function.
	 * @param outputFunction The layer output or activation function.
	 */
	public void addLayer(int numberOfNeurons, InputFunction inputFunction, OutputFunction outputFunction) {
		if (layers.isEmpty()) {
			throw new UnsupportedOperationException("This method cannot be called for the first layer.");
		}
		List<Neuron> inputNeurons = layers.get(layers.size() - 1).getNeurons();

		List<Neuron> outputNeurons = new ArrayList<>();
		for (int i = 0; i < numberOfNeurons; i++) {
			Neuron neuron = new Neuron(++lastNeuronId);
			neuron.setInputFunction(inputFunction);
			neuron.setOutputFunction(outputFunction);
			neuron.setBias(0.0);
			outputNeurons.add(neuron);
		}

		// Link the layers
		for (Neuron outputNeuron : outputNeurons) {
			for (Neuron inputNeuron : inputNeurons) {
				Neuron.link(inputNeuron, outputNeuron, 1.0);
			}
		}

		// Add the output layer
		layers.add(new Layer(outputNeurons));
	}

	/**
	 * Calculate the network outputs given the inputs and returns them as an output vector.
	 * 
	 * @param inputVector The input vector.
	 * @return The output vector.
	 */
	public Vector feedForward(Vector inputVector) {

		// Get the input layer and set the values.
		Layer inputLayer = getInputLayer();
		if (inputVector.size() != inputLayer.size()) {
			throw new IllegalArgumentException("Size of inputs is not the same than the input layer");
		}
		for (int i = 0; i < inputLayer.size(); i++) {
			inputLayer.get(i).setOutput(inputVector.get(i));
		}

		// Iterate subsequent layers and calculate outputs.
		for (int i = 1; i < layers.size(); i++) {
			calculateOutput(layers.get(i));
		}

		// Get the output layer and build the return list
		Layer outputLayer = getOutputLayer();
		Vector outputVector = new Vector(outputLayer.size());
		for (int i = 0; i < outputLayer.size(); i++) {
			Neuron neuron = outputLayer.get(i);
			outputVector.set(i, neuron.getOutput());
		}

		return outputVector;
	}

	/**
	 * Calculates the output for the argument layer.
	 * 
	 * @param layer The layer.
	 */
	private void calculateOutput(Layer layer) {
		for (Neuron neuron : layer) {
			neuron.calculateOutput();
		}
	}

	/**
	 * Returns the input layer.
	 * 
	 * @return The input layer.
	 */
	public Layer getInputLayer() {
		return layers.get(0);
	}

	/**
	 * Returns the output layer.
	 * 
	 * @return The output layer.
	 */
	public Layer getOutputLayer() {
		return layers.get(layers.size() - 1);
	}

	/**
	 * Returns this network list of neurons.
	 * 
	 * @return A list with all this network neurons.
	 */
	public List<Neuron> getNeurons() {
		List<Neuron> neurons = new ArrayList<>();
		for (Layer layer : layers) {
			neurons.addAll(layer.getNeurons());
		}
		return neurons;
	}

	/**
	 * Returns a map with all the neurons keyed by id.
	 * 
	 * @return The neurons map.
	 */
	public Map<Long, Neuron> getNeuronsMap() {
		Map<Long, Neuron> map = new LinkedHashMap<>();
		for (Layer layer : layers) {
			for (Neuron neuron : layer) {
				map.put(neuron.getId(), neuron);
			}
		}
		return map;
	}

	/**
	 * Returns a list with all the network synapses.
	 * 
	 * @return A list with all this network synapses.
	 */
	public List<Synapse> getSynapses() {
		List<Synapse> synapses = new ArrayList<>();
		for (int i = 1; i < layers.size(); i++) {
			List<Neuron> layer = layers.get(i).getNeurons();
			for (Neuron neuron : layer) {
				synapses.addAll(neuron.getInputSynapses());
			}
		}
		return synapses;
	}

	/**
	 * Returns the list of layers of this neural network.
	 * 
	 * @return The list of layers.
	 */
	public List<Layer> getLayers() {
		return layers;
	}

	/**
	 * Returns the iterator through the layers.
	 * 
	 * @return The iterator.
	 */
	public Iterator<Layer> iterator() {
		return layers.iterator();
	}

	/**
	 * Returns the size of the network, the number of layers.
	 * 
	 * @return The number of layers.
	 */
	public int size() {
		return layers.size();
	}

	/**
	 * Returns the layer at the given index.
	 * 
	 * @param index The index of the layer.
	 * @return The layer at index.
	 */
	public Layer get(int index) {
		return layers.get(index);
	}

	/**
	 * Initializes the weights with a Gaussian distribution of mean 0.0 and standard deviation 1.0.
	 */
	public void initializeWeights() {
		Random random = new Random();
		List<Synapse> synapses = getSynapses();
		for (Synapse synapse : synapses) {
			synapse.setWeight(random.nextGaussian());
		}
	}

	/**
	 * Initializes the biases with a given value.
	 * 
	 * @param bias The bias
	 */
	public void initializeBiases(double bias) {
		List<Neuron> neurons = getNeurons();
		for (Neuron neuron : neurons) {
			neuron.setBias(bias);
		}
	}
}
