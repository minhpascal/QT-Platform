/**
 * 
 */
package com.qtplaf.library.ai.nnet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.qtplaf.library.ai.nnet.persistence.NeuralNetworkParserHandler;
import com.qtplaf.library.util.xml.Parser;
import com.qtplaf.library.util.xml.XMLAttribute;
import com.qtplaf.library.util.xml.XMLWriter;

/**
 * Utility class to save an restores neural networks.
 * 
 * @author Miquel Sas
 */
public class Persistence {
	
	/**
	 * Writes (saves) the network in the argument file, in an object output format.
	 * 
	 * @param network The network to save.
	 * @param file The destination file.
	 * @throws IOException If an IO error occurs.
	 */
	public static void writeNetworkToXML(NeuralNetwork network, File file) throws IOException {
		XMLWriter xmlWriter = new XMLWriter(file);

		// Print the header
		xmlWriter.println(xmlWriter.getVersionAndEncodingHeader());

		// Open the network definition
		xmlWriter.printStartTag("neural-network", getNetworkAttributes(network));

		// Increase tab level
		xmlWriter.increaseTabLevel();

		// The network description if any
		if (network.getDescription() != null) {
			xmlWriter.println();
			xmlWriter.printDescription("description", network.getDescription());
		}

		// Start layers
		xmlWriter.println();
		xmlWriter.printStartTag("layers");

		// Increase tab level
		xmlWriter.increaseTabLevel();

		// Iterate layers
		List<Layer> layers = network.getLayers();
		for (Layer layer : layers) {
			xmlWriter.printStartTag("layer");

			// Increase tab level
			xmlWriter.increaseTabLevel();

			// Iterate neurons
			for (Neuron neuron : layer) {
				xmlWriter.printStartTag("neuron", getNeuronAttributes(neuron), true);
			}

			// Decrease level
			xmlWriter.decreaseTabLevel();

			// Close layer
			xmlWriter.printEndTag();
		}

		// Decrease level
		xmlWriter.decreaseTabLevel();

		// Close layers
		xmlWriter.println();
		xmlWriter.printEndTag();

		// Start synapses
		xmlWriter.println();
		xmlWriter.printStartTag("synapses");

		// Increase tab level
		xmlWriter.increaseTabLevel();

		// Iterate synapses
		List<Synapse> synapses = network.getSynapses();
		for (Synapse synapse : synapses) {
			xmlWriter.printStartTag("synapse", getSynapseAttributes(synapse), true);
		}

		// Decrease level
		xmlWriter.decreaseTabLevel();

		// Close layers
		xmlWriter.println();
		xmlWriter.printEndTag();

		// Decrease level
		xmlWriter.decreaseTabLevel();

		// Close the network definition.
		xmlWriter.println();
		xmlWriter.printEndTag();
		xmlWriter.close();
	}

	/**
	 * Returns the list of XML attributes of the synapse argument.
	 * 
	 * @param synapse The synapse argument.
	 * @return The list of XML attributes.
	 */
	private static List<XMLAttribute> getSynapseAttributes(Synapse synapse) {
		List<XMLAttribute> attributes = new ArrayList<>();
		// Input neuron id
		attributes.add(new XMLAttribute("input-neuron-id", Long.toString(synapse.getInputNeuron().getId())));
		// Output neuron id
		attributes.add(new XMLAttribute("output-neuron-id", Long.toString(synapse.getOutputNeuron().getId())));
		// Weight
		attributes.add(new XMLAttribute("weight", Double.toString(synapse.getWeight())));
		return attributes;
	}

	/**
	 * Returns the list of XML attributes of the neuron argument.
	 * 
	 * @param neuron The neuron argument.
	 * @return The list of XML attributes.
	 */
	private static List<XMLAttribute> getNeuronAttributes(Neuron neuron) {
		List<XMLAttribute> attributes = new ArrayList<>();
		// Id
		attributes.add(new XMLAttribute("id", Long.toString(neuron.getId())));
		// Input neuron flag
		attributes.add(new XMLAttribute("input-neuron", Boolean.toString(neuron.isInputNeuron())));
		// Output function
		if (neuron.getOutputFunction() != null) {
			attributes.add(new XMLAttribute("output-function", neuron.getOutputFunction().getClass().getName()));
		}
		// Input function
		if (neuron.getInputFunction() != null) {
			attributes.add(new XMLAttribute("input-function", neuron.getInputFunction().getClass().getName()));
		}
		// Bias
		attributes.add(new XMLAttribute("bias", Double.toString(neuron.getBias())));
		// Bias update
		attributes.add(new XMLAttribute("bias-update", Double.toString(neuron.getBiasUpdate())));
		// Output
		attributes.add(new XMLAttribute("output", Double.toString(neuron.getOutput())));
		// Input
		attributes.add(new XMLAttribute("input", Double.toString(neuron.getInput())));
		// Error
		attributes.add(new XMLAttribute("error", Double.toString(neuron.getError())));
		return attributes;
	}

	/**
	 * Returns the list of XML attributes of the network argument.
	 * 
	 * @param network The network argument.
	 * @return The list of XML attributes.
	 */
	private static List<XMLAttribute> getNetworkAttributes(NeuralNetwork network) {
		List<XMLAttribute> attributes = new ArrayList<>();
		// The id if not null
		if (network.getId() != null) {
			attributes.add(new XMLAttribute("id", network.getId()));
		}
		// The title if not null
		if (network.getTitle() != null) {
			attributes.add(new XMLAttribute("title", network.getTitle()));
		}
		return attributes;
	}
	
	/**
	 * Reads the file and returns the corresponding network.
	 * 
	 * @param file The file with the stored network.
	 * @return The network.
	 * @throws IOException If an IO error occurs.
	 * @throws SAXException If a parsing xception occurs
	 * @throws ParserConfigurationException If a parser configuration exception occurs.
	 */
	public static NeuralNetwork readNetworkFromXML(File file)
		throws ParserConfigurationException, SAXException, IOException {
		Parser parser = new Parser();
		NeuralNetworkParserHandler handler = new NeuralNetworkParserHandler();
		parser.parse(file, handler);
		return handler.getNeuralNetwork();
	}

}
