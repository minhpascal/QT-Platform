/**
 * 
 */
package test.com.msasc.library.ai.nnet;

import java.io.File;
import java.util.List;

import com.qtplaf.library.ai.nnet.NeuralNetwork;
import com.qtplaf.library.ai.nnet.data.mnist.NumberImageReader;
import com.qtplaf.library.ai.nnet.function.error.MeanSquared;
import com.qtplaf.library.ai.nnet.function.input.WeightedSum;
import com.qtplaf.library.ai.nnet.function.output.Sigmoid;
import com.qtplaf.library.ai.nnet.learning.BackPropagationLearningProcess;
import com.qtplaf.library.ai.nnet.learning.LearningEvent;
import com.qtplaf.library.ai.nnet.learning.LearningListener;
import com.qtplaf.library.ai.nnet.learning.LearningProcessManager;
import com.qtplaf.library.ai.nnet.learning.Pattern;
import com.qtplaf.library.ai.nnet.learning.stop.IrreducibleErrorStop;
import com.qtplaf.library.ai.nnet.learning.stop.MaxIterationsStop;
import com.qtplaf.library.util.SystemUtils;

/**
 * Test the default learning process manager.
 * 
 * @author Miquel Sas
 */
public class TestDefaultLearningProcessManager {

	public static class Listener implements LearningListener {
		public void learningStepPerformed(LearningEvent e) {
			if (e.getKey().equals(LearningProcessManager.LearningEventPatternProcessed)) {
				LearningProcessManager manager = e.getLearningProcessManager();
				int patternIndex = manager.getPatternIndex();
				int patternsCount = manager.getIterationSize();
				double totalError = manager.getTotalError();
				int iteration = manager.getIteration();
				long millis = manager.getPatternProcessTime();
				StringBuilder b = new StringBuilder();
				b.append("Iteration ");
				b.append(iteration);
				b.append(" pattern ");
				b.append(patternIndex);
				b.append(" of ");
				b.append(patternsCount);
				b.append(" with error ");
				b.append(totalError);
				b.append(" (");
				b.append(millis);
				b.append(")");
				System.out.println(b.toString());
			}
			if (e.getKey().equals(LearningProcessManager.LearningEventIterationProcessed)) {
				LearningProcessManager manager = e.getLearningProcessManager();
				long seconds = manager.getIterationProcessTime() / 1000;
				System.out.println(seconds);
			}
			if (e.getKey().equals(LearningProcessManager.LearningEventNetworkPerformanceCalculated)) {
				LearningProcessManager manager = e.getLearningProcessManager();
				if (manager.getNetworkPerformance() > 50) {
//					try {
//						File path = SystemUtils.getFileFromClassPathEntries("files");
//						File file = new File(path,"network.txt");
//						file.createNewFile();
//						NeuralNetwork network = manager.getLearningProcess().getNetwork();
//						NeuralNetwork.writeNetwork(network, file);
//					} catch (Exception exc) {
//						exc.printStackTrace();
//					}
				}
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Configure the network
		NeuralNetwork network = new NeuralNetwork();
		// The hidden layer that receives the input
		network.addLayer(100, 784, new WeightedSum(), new Sigmoid());
		// The output layer
		network.addLayer(10, new WeightedSum(), new Sigmoid());
		network.initializeWeights();
		network.initializeBiases(2.0);

		// Configure the back propagation learning process
		BackPropagationLearningProcess learningProcess = new BackPropagationLearningProcess(network);
		learningProcess.setLearningRate(0.1);
		learningProcess.setMomentum(0.3);
		learningProcess.setUpdateWeights(true);
		learningProcess.setUpdateBiases(true);

		// Configure the iterative learning process manager
		LearningProcessManager manager = new LearningProcessManager(learningProcess);
		manager.setLearningData(getLearningPatterns());
		manager.setCheckData(getCheckPatterns());
		manager.addStopCondition(new MaxIterationsStop(manager, 400));
		manager.addStopCondition(new IrreducibleErrorStop(manager, 0.000000005));
		manager.addListener(new Listener());
		manager.setErrorFunction(new MeanSquared());


		manager.execute();

		System.exit(0);
	}

	private static List<Pattern> getLearningPatterns() throws Exception {
		File fileImageLearning = SystemUtils.getFileFromClassPathEntries("train-images.idx3-ubyte");
		File fileLabelLearning = SystemUtils.getFileFromClassPathEntries("train-labels.idx1-ubyte");
		NumberImageReader readerLearning = new NumberImageReader(fileLabelLearning, fileImageLearning);
		readerLearning.read();
		return readerLearning.getPatterns();
	}

	private static List<Pattern> getCheckPatterns() throws Exception {
		File fileImageCheck = SystemUtils.getFileFromClassPathEntries("t10k-images.idx3-ubyte");
		File fileLabelCheck = SystemUtils.getFileFromClassPathEntries("t10k-labels.idx1-ubyte");
		NumberImageReader readerCheck = new NumberImageReader(fileLabelCheck, fileImageCheck);
		readerCheck.read();
		return readerCheck.getPatterns();
	}

}
