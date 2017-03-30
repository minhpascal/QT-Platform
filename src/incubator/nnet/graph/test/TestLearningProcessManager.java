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

package incubator.nnet.graph.test;

import java.io.File;
import java.util.List;

import com.qtplaf.library.util.SystemUtils;

import incubator.nnet.graph.NeuralNetwork;
import incubator.nnet.graph.function.error.MeanSquared;
import incubator.nnet.graph.function.input.WeightedSum;
import incubator.nnet.graph.function.output.Sigmoid;
import incubator.nnet.graph.learning.BackPropagationLearningProcess;
import incubator.nnet.graph.learning.LearningEvent;
import incubator.nnet.graph.learning.LearningListener;
import incubator.nnet.graph.learning.LearningProcessManager;
import incubator.nnet.graph.learning.ListPatternSource;
import incubator.nnet.graph.learning.Pattern;
import incubator.nnet.graph.learning.stop.IrreducibleErrorStop;
import incubator.nnet.graph.learning.stop.MaxIterationsStop;
import incubator.nnet.graph.mnist.NumberImageReader;

/**
 * Test the learning process manager with the back propagation learning process and the MNIST database.
 *
 * @author Miquel Sas
 */
public class TestLearningProcessManager {
	
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
				b.append(") ");
				b.append(manager.getNetworkPerformance());
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
					System.out.println("Now should save results");
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
		// Medium layer
//		network.addLayer(400, new WeightedSum(), new Sigmoid());
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
		manager.setLearningData(new ListPatternSource(getLearningPatterns()));
		manager.setCheckData(new ListPatternSource(getCheckPatterns()));
		manager.addStopCondition(new MaxIterationsStop(manager, 400));
		manager.addStopCondition(new IrreducibleErrorStop(manager, 0.000000005));
		manager.addListener(new Listener());
		manager.setErrorFunction(new MeanSquared());

		// Execute
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
