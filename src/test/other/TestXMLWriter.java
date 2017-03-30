package test.other;

import java.io.File;

import incubator.nnet.graph.NeuralNetwork;
import incubator.nnet.graph.Persistence;
import incubator.nnet.graph.function.input.WeightedSum;
import incubator.nnet.graph.function.output.Sigmoid;

public class TestXMLWriter {

	public static void main(String[] args) {
		try {
			File file = null;
			if (file == null) {
				return;
			}

			// Configure the network
			NeuralNetwork network = new NeuralNetwork();
			network.setId("N001");
			network.setTitle("Test network");
			network.setDescription("This is a neural network aimed to test neural network saving.");

			// The hidden layer that receives the input
			network.addLayer(100, 784, new WeightedSum(), new Sigmoid());
			
			// The output layer
			network.addLayer(10, new WeightedSum(), new Sigmoid());
			network.initializeWeights();
			network.initializeBiases(2.0);
			
			// Save the network
			Persistence.writeNetworkToXML(network, file);

		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
