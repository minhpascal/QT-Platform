/**
 * 
 */
package com.qtplaf.library.ai.nnet.learning;

import java.util.EventObject;
import java.util.Locale;

import com.qtplaf.library.util.TextServer;

/**
 * A learning event that describes the type of event during the learning process.
 * 
 * @author Miquel Sas
 */
public class LearningEvent extends EventObject {

	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -5868989457257176452L;

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
			String error = TextServer.getString("exceptionLearningEventSource", Locale.UK);
			throw new IllegalArgumentException(error);
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
			String error = TextServer.getString("exceptionLearningEventSource", Locale.UK);
			throw new IllegalArgumentException(error);
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
