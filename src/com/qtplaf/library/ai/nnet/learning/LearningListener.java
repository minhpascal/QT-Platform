/**
 * 
 */
package com.qtplaf.library.ai.nnet.learning;

import java.util.EventListener;

/**
 * The interface that should implement the classes interested in learning process events.
 * 
 * @author Miquel Sas
 */
public interface LearningListener extends EventListener {
	/**
	 * Called whenever a learning step in the learning process is performed.
	 * 
	 * @param e The learning event.
	 */
	void learningStepPerformed(LearningEvent e);
}
