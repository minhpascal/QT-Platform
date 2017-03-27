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

import java.util.EventObject;

/**
 * A learning event that describes the type of event during the learning process.
 * 
 * @author Miquel Sas
 */
public class LearningEvent extends EventObject {

	/**
	 * The key of event.
	 */
	private String key;

	/**
	 * An optional message set by the source.
	 */
	private String message;

	/**
	 * Constructor assigning the source and the key of the event. The source has to be an instance of
	 * terativeLearningProcess that, if so, will be assigned to the 'learningEvent' member variable.
	 * 
	 * @param source The object on which the Event initially occurred, an instance of LearningProcess.
	 * @param key The key of the event.
	 */
	LearningEvent(Object source, String key) {
		super(source);
		if (!(source instanceof LearningProcessManager)) {
			throw new IllegalArgumentException("Source must be an instance of LearningProcessManager");
		}
		this.key = key;
	}

	/**
	 * Constructor assigning the source and the key of the event and an optional message. The source has to be an
	 * instance of LearningProcess that, if so, will be assigned to the 'learningEvent' member variable.
	 * 
	 * @param source The object on which the Event initially occurred, an instance of LearningProcess.
	 * @param key The key of the event.
	 * @param message The optional explanation message.
	 */
	LearningEvent(Object source, String key, String message) {
		super(source);
		if (!(source instanceof LearningProcessManager)) {
			throw new IllegalArgumentException("Source must be an instance of LearningProcessManager");
		}
		this.key = key;
		this.message = message;
	}

	/**
	 * Returns the learning process manager that generated this event.
	 * 
	 * @return The learning process manager.
	 */
	public LearningProcessManager getLearningProcessManager() {
		return (LearningProcessManager)getSource();
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
	 * Returns the key of the event.
	 * 
	 * @return The key.
	 */
	public String getKey() {
		return key;
	}

}
