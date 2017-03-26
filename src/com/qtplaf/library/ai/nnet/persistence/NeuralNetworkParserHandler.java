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
package com.qtplaf.library.ai.nnet.persistence;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.qtplaf.library.ai.nnet.Layer;
import com.qtplaf.library.ai.nnet.NeuralNetwork;
import com.qtplaf.library.ai.nnet.Neuron;
import com.qtplaf.library.ai.nnet.function.InputFunction;
import com.qtplaf.library.ai.nnet.function.OutputFunction;
import com.qtplaf.library.util.xml.ParserHandler;

/**
 * A parser handler to read a neural network.
 * 
 * @author Miquel Sas
 */
public class NeuralNetworkParserHandler extends ParserHandler {

	/**
	 * A map with input functions keyed by class name.
	 */
	private Map<String, InputFunction> inputFuntionsMap = new HashMap<>();
	/**
	 * A map with output functions keyed by class name.
	 */
	private Map<String, OutputFunction> outputFuntionsMap = new HashMap<>();
	/**
	 * The map with the network neurons keyed by id.
	 */
	private Map<Long, Neuron> neuronsMap = null;

	/**
	 * Default constructor.
	 */
	public NeuralNetworkParserHandler() {
		super();
	}

	/**
	 * Returns the parsed neural network.
	 * 
	 * @return The parsed neural network.
	 */
	public NeuralNetwork getNeuralNetwork() {
		return (NeuralNetwork)getDeque().getLast();
	}

	/**
	 * Returns the neurons map keyed by id. This method must be called on synapse parsing, when all the neurons have
	 * been created and added to the corresponding layers, and the layers added to the network.
	 * 
	 * @return The neurons map.
	 */
	private Map<Long, Neuron> getNeuronsMap() {
		if (neuronsMap == null) {
			neuronsMap = getNeuralNetwork().getNeuronsMap();
		}
		return neuronsMap;
	}

	/**
	 * Returns the input function given the class name.
	 * 
	 * @param className The input function class name.
	 * @return The input function.
	 * @throws NoSuchMethodException If such an exception occurs.
	 * @throws SecurityException If such an exception occurs.
	 * @throws ClassNotFoundException If such an exception occurs.
	 * @throws InstantiationException If such an exception occurs.
	 * @throws IllegalAccessException If such an exception occurs.
	 * @throws IllegalArgumentException If such an exception occurs.
	 * @throws InvocationTargetException If such an exception occurs.
	 */
	private InputFunction getInputFunction(String className)
		throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (className == null) {
			return null;
		}
		InputFunction inputFunction = inputFuntionsMap.get(className);
		if (inputFunction == null) {
			Constructor<?> constructor = Class.forName(className).getConstructor();
			inputFunction = (InputFunction) constructor.newInstance();
			inputFuntionsMap.put(className, inputFunction);
		}
		return inputFunction;
	}

	/**
	 * Returns the output function given the class name.
	 * 
	 * @param className The class name of the output function.
	 * @return The output function.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private OutputFunction getOutputFunction(String className)
		throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (className == null) {
			return null;
		}
		OutputFunction outputFunction = outputFuntionsMap.get(className);
		if (outputFunction == null) {
			Constructor<?> constructor = Class.forName(className).getConstructor();
			outputFunction = (OutputFunction) constructor.newInstance();
			outputFuntionsMap.put(className, outputFunction);
		}
		return outputFunction;
	}

	/**
	 * Called to notify the document start.
	 */
	public void documentStart() throws SAXException {
	}

	/**
	 * Called to notify the document end.
	 */
	public void documentEnd() throws SAXException {
		// All elements of the network have been configured and work is done: clear maps.
		if (neuronsMap != null) {
			neuronsMap.clear();
			neuronsMap = null;
		}
		if (outputFuntionsMap != null) {
			outputFuntionsMap.clear();
			outputFuntionsMap = null;
		}
		if (inputFuntionsMap == null) {
			inputFuntionsMap.clear();
			inputFuntionsMap = null;
		}
		Runtime.getRuntime().gc();
	}

	/**
	 * Called to notify an element start.
	 */
	public void elementStart(String namespace, String elementName, String path, Attributes attributes) throws SAXException {

		// element: neural-network
		if (path.equals("neural-network")) {
			NeuralNetwork neuralNetwork = new NeuralNetwork();
			neuralNetwork.setId(attributes.getValue("id"));
			neuralNetwork.setTitle(attributes.getValue("title"));
			getDeque().addFirst(neuralNetwork);
			return;
		}

		// element: neural-network/layers/layer
		if (path.equals("neural-network/layers/layer")) {
			Layer layer = new Layer();
			getDeque().addFirst(layer);
			return;
		}

		// element: neural-network/layers/layer/neuron
		if (path.equals("neural-network/layers/layer/neuron")) {
			long id = Long.parseLong(attributes.getValue("id"));
			String outputFunctionClassName = attributes.getValue("output-function");
			OutputFunction outputFunction = null;
			try {
				outputFunction = getOutputFunction(outputFunctionClassName);
			} catch (Exception exc) {
				throw new SAXException(exc);
			}
			String inputFunctionClassName = attributes.getValue("input-function");
			InputFunction inputFunction = null;
			try {
				inputFunction = getInputFunction(inputFunctionClassName);
			} catch (Exception exc) {
				throw new SAXException(exc);
			}
			double bias = Double.parseDouble(attributes.getValue("bias"));
			double output = Double.parseDouble(attributes.getValue("output"));
			double input = Double.parseDouble(attributes.getValue("input"));

			Neuron neuron = new Neuron(id);
			if (inputFunction != null) {
				neuron.setInputFunction(inputFunction);
			}
			neuron.setOutputFunction(outputFunction);
			neuron.setBias(bias);
			neuron.setOutput(output);
			neuron.setInput(input);

			getDeque().addFirst(neuron);
		}

		// element: neural-network/synapses/synapse
		if (path.equals("neural-network/synapses/synapse")) {
			long inputNeuronId = Long.parseLong(attributes.getValue("input-neuron-id"));
			long outputNeuronId = Long.parseLong(attributes.getValue("output-neuron-id"));
			double weight = Double.parseDouble(attributes.getValue("weight"));
			Neuron inputNeuron = getNeuronsMap().get(inputNeuronId);
			Neuron outputNeuron = getNeuronsMap().get(outputNeuronId);
			Neuron.link(inputNeuron, outputNeuron, weight);
		}
	}

	/**
	 * Called to notify an element body.
	 */
	public void elementBody(String namespace, String elementName, String path, String text) throws SAXException {

		// element: neural-network/description
		if (path.equals("neural-network/description")) {
			getNeuralNetwork().setDescription(text);
			return;
		}
	}

	/**
	 * Called to notify an element end.
	 */
	public void elementEnd(String namespace, String elementName, String path) throws SAXException {

		// element: neural-network/layers/layer/neuron
		if (path.equals("neural-network/layers/layer/neuron")) {
			Neuron neuron = (Neuron) getDeque().removeFirst();
			Layer layer = (Layer) getDeque().getFirst();
			layer.addNeuron(neuron);
		}

		// element: neural-network/layers/layer
		if (path.equals("neural-network/layers/layer")) {
			Layer layer = (Layer) getDeque().removeFirst();
			getNeuralNetwork().addLayer(layer);
			return;
		}
	}

}
