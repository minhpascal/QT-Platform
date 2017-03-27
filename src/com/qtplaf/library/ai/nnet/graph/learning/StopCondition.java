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
package com.qtplaf.library.ai.nnet.graph.learning;


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
