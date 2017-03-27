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
package com.qtplaf.library.ai.nnet.graph.learning.stop;

import java.text.MessageFormat;
import java.util.Locale;

import com.qtplaf.library.ai.nnet.graph.learning.LearningProcessManager;
import com.qtplaf.library.ai.nnet.graph.learning.StopCondition;
import com.qtplaf.library.util.TextServer;

/**
 * Stop condition reached when the error is less than or equal to the irreducible error,
 * 
 * @author Miquel Sas
 */
public class IrreducibleErrorStop extends StopCondition {

	/**
	 * The irreducible error.
	 */
	private double irreducibleError;

	/**
	 * The stop message.
	 */
	private String message;

	/**
	 * Constructor assigning the learning process.
	 * 
	 * @param learningProcess The learning process.
	 */
	public IrreducibleErrorStop(LearningProcessManager learningProcessManager, double irreducibleError) {
		super(learningProcessManager);
		this.irreducibleError = irreducibleError;
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
	 * Returns a boolean indicating if the learning process should stop.
	 * 
	 * @return A boolean.
	 */
	public boolean shouldStopLearning() {
		double totalError = getLearningProcessManager().getTotalError();
		if (totalError <= irreducibleError) {
			String pattern = TextServer.getString("messageStopIrreducibleError", Locale.UK);
			message = MessageFormat.format(pattern, totalError, irreducibleError);
			return true;
		}
		return false;
	}

}
