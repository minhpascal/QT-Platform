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

package com.qtplaf.library.ai.nnet.bp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.qtplaf.library.ai.nnet.ListPatternSource;
import com.qtplaf.library.ai.nnet.Pattern;
import com.qtplaf.library.ai.nnet.mnist.NumberImageReader;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.SystemUtils;

/**
 * Test the learning process manager with the back propagation learning process and the MNIST database.
 *
 * @author Miquel Sas
 */
public class TestBPNN {
	
	public static class Listener implements LearningListener {
		public void learningStepPerformed(LearningEvent e) {
			if (e.getId() == LearningEvent.PatternProcessed) {
				LearningManager manager = e.getLearningManager();
				int patternIndex = manager.getPatternIndex();
				int patternsCount = manager.getIterationSize();
				double totalError = manager.getTotalError();
				int iteration = manager.getIteration();
				long millis = manager.getPatternProcessTime();
				StringBuilder b = new StringBuilder();
				b.append("I: ");
				b.append(iteration);
				b.append(" P: ");
				b.append(patternIndex);
				b.append(" of ");
				b.append(patternsCount);
				b.append(" E: ");
				b.append(NumberUtils.getBigDecimal(totalError,8));
				b.append(" PT: ");
				b.append(StringUtils.leftPad(Long.toString(millis),3));
				b.append(" IT: ");
				b.append(manager.getIterationProcessTime()/1000);
				b.append(" NP: ");
				b.append(manager.getNetworkPerformance());
				b.append(" LR: ");
				b.append(manager.getBPNN().getLearningRate());
				System.out.println(b.toString());
			}
			if (e.getId() == LearningEvent.IterationProcessed) {
				LearningManager manager = e.getLearningManager();
				long seconds = manager.getIterationProcessTime() / 1000;
				System.out.println(seconds);
			}
			if (e.getId() == LearningEvent.PerformanceCalculated) {
				LearningManager manager = e.getLearningManager();
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
		
		System.out.println(Runtime.getRuntime().availableProcessors());
		
		// Configure the network
		BPNN bpnn = new BPNN();
		bpnn.setLearningRate(0.1);
		
		// The input layer
		bpnn.addLayer(784, 400);
		// Medium layer
		bpnn.addLayer(400);
		// The output layer
		bpnn.addLayer(10);
		
//		File weights = SystemUtils.getFileFromClassPathEntries("weights.dat");
		
		bpnn.initializeWeights();
//		bpnn.initializeWeights(readWeights(weights));
		
//		writeWeights(weights, bpnn.getWeights());

		// Configure the back propagation learning process

		// Configure the iterative learning process manager
		LearningManager manager = new LearningManager(bpnn);
		manager.setLearningData(new ListPatternSource(getLearningPatterns()));
		manager.setCheckData(new ListPatternSource(getCheckPatterns()));
		manager.addListener(new Listener());

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
	
	private static void writeWeights(File file, double[] weights) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(weights.length * 8);
		buffer.rewind();
		for (double weight : weights) {
			buffer.putDouble(weight);
		}
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(buffer.array());
		fo.close();
	}
	
	private static double[] readWeights(File file) throws Exception {
		int length = Long.valueOf(file.length() / 8).intValue();
		ByteBuffer buffer = ByteBuffer.allocate(length * 8);
		FileInputStream fi = new FileInputStream(file);
		fi.read(buffer.array());
		fi.close();
		double[] weights = new double[length];
		for (int i = 0; i < length; i++) {
			weights[i] = buffer.getDouble();
		}
		return weights;
	}

}
