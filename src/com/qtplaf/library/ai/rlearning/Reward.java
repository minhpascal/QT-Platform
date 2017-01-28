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

package com.qtplaf.library.ai.rlearning;

/**
 * The reward function to apply to a transition.
 *
 * @author Miquel Sas
 */
public abstract class Reward {

	/**
	 * Constructor.
	 */
	public Reward() {
		super();
	}

	/**
	 * Returns the reward of a transition.
	 * 
	 * @param transition The transition to compute.
	 * @return The reward.
	 */
	public abstract double get(Transition transition);
}
