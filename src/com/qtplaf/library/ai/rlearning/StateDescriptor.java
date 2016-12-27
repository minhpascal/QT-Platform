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

import java.util.ArrayList;
import java.util.List;

/**
 * A descriptor of states that contains a value descriptor for each value in the state.
 * 
 * @author Miquel Sas
 */
public class StateDescriptor {

	/**
	 * The list of value descriptors.
	 */
	private List<StateValueDescriptor> descriptors = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public StateDescriptor() {
		super();
	}

	/**
	 * Add a value descriptor.
	 * 
	 * @param descriptor The value descriptor.
	 */
	public void addValueDescriptor(StateValueDescriptor descriptor) {
		descriptors.add(descriptor);
	}

	/**
	 * Returns the descriptor at the index position.
	 * 
	 * @param index The index.
	 * @return The value descriptor.
	 */
	public StateValueDescriptor getDescriptor(int index) {
		return descriptors.get(index);
	}

	/**
	 * Returns the size of this state descriptor.
	 * 
	 * @return The size.
	 */
	public int size() {
		return descriptors.size();
	}
}
