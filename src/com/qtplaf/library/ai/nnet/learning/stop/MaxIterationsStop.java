/**
 * 
 */
package com.qtplaf.library.ai.nnet.learning.stop;

import java.text.MessageFormat;
import java.util.Locale;

import com.qtplaf.library.ai.nnet.learning.LearningProcessManager;
import com.qtplaf.library.ai.nnet.learning.StopCondition;
import com.qtplaf.library.util.TextServer;

/**
 * A maximum iterations stop condition.
 * 
 * @author Miquel Sas
 */
public class MaxIterationsStop extends StopCondition {

	/**
	 * The maximum number of iterations.
	 */
	private int maxIterations;
	
	/**
	 * The stop message.
	 */
	private String message;

	/**
	 * Constructor assigning the learning process and the maximum number of iterations.
	 * 
	 * @param learningProcessManager
	 * @param maxIterations
	 */
	public MaxIterationsStop(LearningProcessManager learningProcessManager, int maxIterations) {
		super(learningProcessManager);
		this.maxIterations = maxIterations;
	}
	
	/**
	 * Returns a message informing about the conditions reached to suggest stop learning.
	 * 
	 * @return The stop message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns true if learning rule should stop, false otherwise
	 * 
	 * @return true if learning rule should stop, false otherwise
	 */
	public boolean shouldStopLearning() {
		int iteration = getLearningProcessManager().getIteration();
		if (iteration >= maxIterations) {
			String pattern = TextServer.getString("messageStopMaxIterations", Locale.UK);
			message = MessageFormat.format(pattern, iteration, maxIterations);
			return true;
		}
		return false;
	}

}
