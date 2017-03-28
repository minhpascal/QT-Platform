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
package com.qtplaf.library.ai.nnet.matrix.stop;

import java.text.MessageFormat;
import java.util.Locale;

import com.qtplaf.library.ai.nnet.matrix.LearningProcessManager;
import com.qtplaf.library.ai.nnet.matrix.StopCondition;
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
