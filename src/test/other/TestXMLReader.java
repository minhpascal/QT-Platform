package test.other;

import java.io.File;

import com.qtplaf.library.ai.nnet.NeuralNetwork;
import com.qtplaf.library.ai.nnet.Persistence;
import com.qtplaf.library.util.TextServer;

public class TestXMLReader {

	public static void main(String[] args) {
		TextServer.addBaseResource("SysString.xml");
		try {
			File file = null;
			if (file == null) {
				return;
			}

			// Read the network
			NeuralNetwork network = Persistence.readNetworkFromXML(file);
			System.out.println();
			System.out.println(network.getNeurons().size());
			System.out.println(network.getSynapses().size());		
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
