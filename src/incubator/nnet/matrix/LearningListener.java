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
package incubator.nnet.matrix;

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
