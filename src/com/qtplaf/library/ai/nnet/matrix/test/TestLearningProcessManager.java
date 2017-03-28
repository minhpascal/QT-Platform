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

package com.qtplaf.library.ai.nnet.matrix.test;

import java.io.File;
import java.util.List;

import com.qtplaf.library.ai.nnet.data.mnist.matrix.NumberImageReader;
import com.qtplaf.library.ai.nnet.matrix.BackPropagation;
import com.qtplaf.library.ai.nnet.matrix.LearningEvent;
import com.qtplaf.library.ai.nnet.matrix.LearningListener;
import com.qtplaf.library.ai.nnet.matrix.LearningProcessManager;
import com.qtplaf.library.ai.nnet.matrix.ListPatternSource;
import com.qtplaf.library.ai.nnet.matrix.NeuralNetwork;
import com.qtplaf.library.ai.nnet.matrix.Pattern;
import com.qtplaf.library.ai.nnet.matrix.error.MeanSquared;
import com.qtplaf.library.ai.nnet.matrix.stop.IrreducibleErrorStop;
import com.qtplaf.library.ai.nnet.matrix.stop.MaxIterationsStop;
import com.qtplaf.library.util.SystemUtils;

/**
 * Test the learning process manager with the back propagation learning process and the MNIST database.
 *
 * @author Miquel Sas
 */
public class TestLearningProcessManager {
	
	public static class Listener implements LearningListener {
		public void learningStepPerformed(LearningEvent e) {
			if (e.getId() == LearningEvent.PatternProcessed) {
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
			if (e.getId() == LearningEvent.IterationProcessed) {
				LearningProcessManager manager = e.getLearningProcessManager();
				long seconds = manager.getIterationProcessTime() / 1000;
				System.out.println(seconds);
			}
			if (e.getId() == LearningEvent.PerformanceCalculated) {
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
		network.addLayer(784, 100);
		// Medium layer
//		network.addLayer(200);
		// The output layer
		network.addLayer(10);
		
		network.initializeWeights();
		network.initializeBiases(2.0);

		// Configure the back propagation learning process
		BackPropagation learningProcess = new BackPropagation(network);
		learningProcess.setLearningRate(0.05);
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
