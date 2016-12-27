/**
 * 
 */
package com.qtplaf.library.ai.nnet.learning;


/**
 * Base class for conditions to stop the learning process.
 * 
 * @author Miquel Sas
 */
public abstract class StopCondition {

	/**
	 * The learning process that eventually should be stopped.
	 */
	private LearningProcessManager learningProcessManager;
	/**
	 * The stop message generated when the stopcondition is reached.
	 */
	private String message;

	/**
	 * Constructor assigning the learning process manager.
	 * 
	 * @param learningProcessManager The learning process manager.
	 */
	public StopCondition(LearningProcessManager learningProcessManager) {
		super();
		this.learningProcessManager = learningProcessManager;
	}

	/**
	 * Returns the learning process manager that eventually should be stopped.
	 * 
	 * @return The learning process manager.
	 */
	public LearningProcessManager getLearningProcessManager() {
		return learningProcessManager;
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
	 * Set the stop message.
	 * 
	 * @param message The stop message.
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns a boolean indicating if the learning process should stop.
	 * 
	 * @return A boolean.
	 */
	public abstract boolean shouldStopLearning();
}
