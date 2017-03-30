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

import java.util.EventObject;

/**
 * A learning event that describes the type of event during the learning process.
 * 
 * @author Miquel Sas
 */
public class LearningEvent extends EventObject {

	/** Learning event pattern processed. */
	public static final int PatternProcessed = 1;
	/** Learning event iteration processed. */
	public static final int IterationProcessed = 2;
	/** The learning event key for a stop condition reached. */
	public static final int StopConditionReached = 3;
	/** Learning event network networkPerformance calculated. */
	public static final int PerformanceCalculated = 4;

	/** Id. */
	private int id;
	/** Message. */
	private String message;

	/**
	 * @param source
	 */
	public LearningEvent(Object source, int id, String message) {
		super(source);
		if (!(source instanceof LearningManager)) {
			throw new IllegalArgumentException("Source must be an instance of LearningProcessManager");
		}
		this.id = id;
		this.message = message;
	}

	/**
	 * Returns the manager.
	 * 
	 * @return The manager.
	 */
	public LearningManager getLearningManager() {
		return (LearningManager) getSource();
	}

	/**
	 * Returns the optional message. Can be null.
	 * 
	 * @return The optional message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the id of the event.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

}
