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

package com.qtplaf.library.ai.nnet.matrix;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.util.NumberUtils;

/**
 * Learning process manager.
 *
 * @author Miquel Sas
 */
public class LearningProcessManager {

	/** The learning process to manage execution. */
	private LearningProcess learningProcess;
	/** The learning data. */
	private PatternSource learningData;
	/** The optional check data, not seen during the learning process. */
	private PatternSource checkData;
	/** Iteration counter over all the learning data. */
	private int iteration;
	/** The size of the data in the current iteration. */
	private int iterationSize;
	/** A counter for the the pattern when processing an epochIndex. */
	private int patternIndex;
	/** The list of listeners interested in this learning process events. */
	private List<LearningListener> listeners = new ArrayList<>();
	/** The list of stop conditions. */
	private List<StopCondition> stopConditions = new ArrayList<>();
	/** The error function used to calculate the global network error. */
	private ErrorFunction errorFunction;
	/** The network networkPerformance as a percentage of matched output values against the check data. */
	private double networkPerformance;
	/** The learning performance precision as a percentage of matched output values against the learning data. */
	private double learningPerformance;
	/**
	 * The performance precision. The precision is necessary because normally output check data will 0/1 values, while
	 * network output data will be a double &gt;= 0 and &lt;= 1.
	 */
	private int performanceComparisonPrecision = 0;
	/** The learning process total error. */
	private double totalError = Double.MAX_VALUE;

	/** The milliseconds elapsed processing a pattern. */
	private long patternProcessTime;
	/** The milliseconds elapsed processing an iteration. */
	private long iterationProcessTime;

	/** The depth of the registered history of performances and errors. */
	private int historyDepth = 10;
	/** A list with the last history of network performances. */
	private List<Double> networkPerformanceHistory = new ArrayList<>();
	/** A list with the last history of learning performances. */
	private List<Double> learningPerformanceHistory = new ArrayList<>();
	/** A list with the last history of network total errors. */
	private List<Double> totalErrorHistory = new ArrayList<>();
	/** Decimals to calculate performance. */
	private int performanceDecimals = 4;

	/**
	 * Constructor assigning the learning process.
	 * 
	 * @param learningProcess The learning process.
	 */
	public LearningProcessManager(LearningProcess learningProcess) {
		super();
		this.learningProcess = learningProcess;
	}

	/**
	 * Adds a learning listener to the list of listeners.
	 * 
	 * @param listener The listener to add.
	 */
	public void addListener(LearningListener listener) {
		listeners.add(listener);
	}

	/**
	 * Adds a stop condition to the list of stop conditions.
	 * 
	 * @param stopCondition The stop condition.
	 */
	public void addStopCondition(StopCondition stopCondition) {
		stopConditions.add(stopCondition);
	}

	/**
	 * Calculates the network performance using the check data and applying the 'areEqual' method to compare outputs
	 * from the check data and the network.
	 */
	protected void calculateNetworkPerformance() {
		double matches = 0;
		if (checkData != null) {
			checkData.rewind();
			while (checkData.hasNext()) {
				Pattern pattern = checkData.next();
				double[] checkInputs = pattern.getPatternInputs();
				double[] checkOutputs = pattern.getPatternOutputs();
				double[] networkOutputs = learningProcess.processInputs(checkInputs);
				if (Calculator.areEqual(checkOutputs, networkOutputs, performanceComparisonPrecision)) {
					matches++;
				}
			}
		}
		double checkSize = checkData.size();
		networkPerformance = NumberUtils.round(100 * matches / checkSize, performanceDecimals);
		registerHistory(networkPerformance, networkPerformanceHistory);
	}

	/**
	 * Calculates the learning performance for a list of patterns processed.
	 * 
	 * @param patterns The source of patterns.
	 */
	protected void calculateLearningPerformance(PatternSource patterns) {
		double matches = 0;
		patterns.rewind();
		while (patterns.hasNext()) {
			Pattern pattern = patterns.next();
			double[] patternInputs = pattern.getPatternInputs();
			double[] patternOutputs = pattern.getPatternOutputs();
			double[] networkOutputs = learningProcess.processInputs(patternInputs);
			if (Calculator.areEqual(patternOutputs, networkOutputs, performanceComparisonPrecision)) {
				matches++;
			}
		}
		double size = patterns.size();
		learningPerformance = NumberUtils.round(100 * matches / size, performanceDecimals);
		registerHistory(learningPerformance, learningPerformanceHistory);
	}

	/**
	 * Returns the iteration counter.
	 * 
	 * @return The iteration counter.
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Returns the size of the data in the current iteration.
	 * 
	 * @return The size of the data in the current iteration.
	 */
	public int getIterationSize() {
		return iterationSize;
	}

	/**
	 * Returns the total network learning error.
	 * 
	 * @return The total error.
	 */
	public double getTotalError() {
		return totalError;
	}

	/**
	 * Returns the learning process.
	 * 
	 * @return The learning process.
	 */
	public LearningProcess getLearningProcess() {
		return learningProcess;
	}

	/**
	 * Returns the last calculated network performance.
	 * 
	 * @return The last calculated network performance.
	 */
	public double getNetworkPerformance() {
		return networkPerformance;
	}

	/**
	 * Returns the last calculated learning performance.
	 * 
	 * @return The learning performance.
	 */
	public double getLearningPerformance() {
		return learningPerformance;
	}

	/**
	 * Returns the index of the current pattern processed.
	 * 
	 * @return The index of the pattern.
	 */
	public int getPatternIndex() {
		return patternIndex;
	}

	/**
	 * Returns the history depth.
	 * 
	 * @return The history depth.
	 */
	public int getHistoryDepth() {
		return historyDepth;
	}

	/**
	 * Returns a copy of the network performances history.
	 * 
	 * @return A copy of the network performances history.
	 */
	public List<Double> getNetworkPerformanceHistory() {
		return new ArrayList<Double>(networkPerformanceHistory);
	}

	/**
	 * Returns a copy of the learning performances history.
	 * 
	 * @return A copy of the learning performances history.
	 */
	public List<Double> getLearningPerformanceHistory() {
		return new ArrayList<Double>(learningPerformanceHistory);
	}

	/**
	 * Returns a copy of the total errors history.
	 * 
	 * @return A copy of the total errors history.
	 */
	public List<Double> getTotalErrorHistory() {
		return new ArrayList<Double>(totalErrorHistory);
	}

	/**
	 * Returns the pattern process time.
	 * 
	 * @return The pattern process time.
	 */
	public long getPatternProcessTime() {
		return patternProcessTime;
	}

	/**
	 * Returns the iteration process time.
	 * 
	 * @return The iteration process time.
	 */
	public long getIterationProcessTime() {
		return iterationProcessTime;
	}

	/**
	 * Sets the desired history depth.
	 * 
	 * @param historyDepth The history depth.
	 */
	public void setHistoryDepth(int historyDepth) {
		this.historyDepth = historyDepth;
	}

	/**
	 * Fires a learning event to the list of listeners.
	 * 
	 * @param e The learning event.
	 */
	private void fireLearningEvent(LearningEvent e) {
		for (LearningListener listener : listeners) {
			listener.learningStepPerformed(e);
		}
	}

	/**
	 * Fires a learning event with the argument key.
	 * 
	 * @param id The event id.
	 */
	protected void fireLearningEvent(int id) {
		fireLearningEvent(new LearningEvent(this, id, null));
	}

	/**
	 * Fires a learning event with the argument key and message.
	 * 
	 * @param id The event id.
	 * @param message The optional explanation message.
	 */
	protected void fireLearningEvent(int id, String message) {
		fireLearningEvent(new LearningEvent(this, id, message));
	}

	/**
	 * Set the learning data.
	 * 
	 * @param learningData The learning data.
	 */
	public void setLearningData(PatternSource learningData) {
		this.learningData = learningData;
	}

	/**
	 * Set the check data.
	 * 
	 * @param checkData The check data.
	 */
	public void setCheckData(PatternSource checkData) {
		this.checkData = checkData;
	}

	/**
	 * Set the error function used to calculate the global network error.
	 * 
	 * @param errorFunction The error function.
	 */
	public void setErrorFunction(ErrorFunction errorFunction) {
		this.errorFunction = errorFunction;
	}

	/**
	 * Set the networkPerformance precision.
	 * 
	 * @param performanceComparisonPrecision The performance comparison precision.
	 */
	public void setPerformanceComparisonPrecision(int performanceComparisonPrecision) {
		this.performanceComparisonPrecision = performanceComparisonPrecision;
	}

	/**
	 * Set the performance decimals.
	 * @param performanceDecimals The performance decimals.
	 */
	public void setPerformanceDecimals(int performanceDecimals) {
		this.performanceDecimals = performanceDecimals;
	}

	/**
	 * Check if the process should stop learning by asking every stop condition.
	 * 
	 * @return A boolean indicating if the process should stop learning.
	 */
	protected boolean shouldStopLearning() {
		for (StopCondition stopCondition : stopConditions) {
			if (stopCondition.shouldStopLearning()) {
				fireLearningEvent(LearningEvent.StopConditionReached, stopCondition.getMessage());
				return true;
			}
		}
		return false;
	}

	/**
	 * Ask if network networkPerformance should be checked, formerly when there is check data.
	 * 
	 * @return A boolean indicating if network networkPerformance should be checked.
	 */
	public boolean isCalculateNetworkPerformance() {
		return (checkData != null && !checkData.isEmpty());
	}

	/**
	 * Validates the execution start conditions. By default this method checks that the learning process, the learning
	 * data, the error function and at least a stop condition are set.
	 * <p>
	 * This method is aimed to be overwritten, generally calling the super functionality, and adding extra controls.
	 * 
	 * @throws IllegalStateException If any condition is not met.
	 */
	protected void validateExecutionStartConditions() throws IllegalStateException {

		// The learning process can not be null.
		if (getLearningProcess() == null) {
			throw new IllegalStateException("The learning process can not be null");
		}

		// The learning data must have been set.
		if (learningData == null || learningData.isEmpty()) {
			throw new IllegalStateException("y");
		}

		// The error function must be set.
		if (errorFunction == null) {
			throw new IllegalStateException("The error function can not be null");
		}

		// At least a stop condition is needed.
		if (stopConditions.isEmpty()) {
			throw new IllegalStateException("Stop conditions needed");
		}
	}

	/**
	 * Learning process execution.
	 */
	public void execute() {

		// Validate start conditions
		validateExecutionStartConditions();

		// Prepare an iteration through the overall data.
		iteration = 0;

		// Iterate while not should stop learning
		boolean stopExecution = false;
		while (!stopExecution) {

			// Retrieve the patterns that will be processed in this iteration.
			PatternSource iterationPatterns = getIterationPatterns();
			iterationPatterns.rewind();
			iterationSize = iterationPatterns.size();

			// Reset the pattern index in the epoch
			patternIndex = 0;

			// Reset the total error before processing the epoch
			errorFunction.reset();
			totalError = Double.MAX_VALUE;

			// Iteration start time
			long iterationStartTime = System.currentTimeMillis();

			// Iterate patterns
			while (!stopExecution && iterationPatterns.hasNext()) {

				// The current pattern to process
				Pattern pattern = iterationPatterns.next();

				// Pattern start time
				long patternStartTime = System.currentTimeMillis();

				// Process the pattern.
				double error = processPattern(pattern);

				// Accumulate the output errors in the error function
				errorFunction.addError(error);
				totalError = errorFunction.getTotalError();

				// Pattern process time
				patternProcessTime = System.currentTimeMillis() - patternStartTime;

				// Fire a learning event indicating that the pattern has been processed.
				fireLearningEvent(LearningEvent.PatternProcessed);

				// Increase the pattern index
				patternIndex++;

				// Check stop execution after pattern processing
				stopExecution = shouldStopLearning();
			}

			// Register the history of total error after the iteration.
			registerHistory(totalError, totalErrorHistory);

			// Calculate the learning performance after the iteration.
			calculateLearningPerformance(iterationPatterns);

			// Calculate network performance after the iteration
			if (isCalculateNetworkPerformance()) {
				calculateNetworkPerformance();
				fireLearningEvent(LearningEvent.PerformanceCalculated);
			}

			// Iteration process time.
			iterationProcessTime = System.currentTimeMillis() - iterationStartTime;

			// Fire a learning event indicating that the iteration has been processed.
			fireLearningEvent(LearningEvent.IterationProcessed);

			// Check stop execution.
			stopExecution = shouldStopLearning();

			// Increase iteration counter.
			iteration++;
		}
	}
	/**
	 * Process the pattern. By default, the method does the following:
	 * <p>
	 * The input vector of the pattern is processed.
	 * <p>
	 * The error vector and the error are calculated and assigned to the pattern.
	 * <p>
	 * The error vector is passed to the learning process <i>processError</i> method delegating to update weights and
	 * biases.
	 * 
	 * @param pattern The pattern to process.
	 */
	protected double processPattern(Pattern pattern) {

		double[] patternInputs = pattern.getPatternInputs();
		double[] patternOutputs = pattern.getPatternOutputs();
		double[] networkOutputs = learningProcess.processInputs(patternInputs);

		// Calculate the error by subtracting the network output from the target output.
		double[] outputErrors = Calculator.subtract(patternOutputs, networkOutputs);
		double error = errorFunction.getError(outputErrors);

		// Delegate error processing to the learning process
		learningProcess.processErrors(outputErrors);
		
		// Return the error.
		return error;
	}

	/**
	 * Returns the list of patterns that will be processed during the current iteration. A default iterative learning
	 * process manager would perhaps just shuffle the learning list of patterns, while a competitive manager would
	 * perhaps order the patterns by error and process those with the worst performance.
	 * <p>
	 * When the learning data is too huge, a 'BatchIterator' could be used to process a fraction of the initial data.
	 * 
	 * @return The list of patterns that will be processed during the current iteration.
	 */
	protected PatternSource getIterationPatterns() {
		return learningData;
	}

	/**
	 * Registers the value in the history list.
	 * 
	 * @param value The value to register.
	 * @param history The history list.
	 */
	private void registerHistory(double value, List<Double> history) {
		if (history.size() == historyDepth) {
			history.remove(0);
		}
		history.add(value);
	}
}
