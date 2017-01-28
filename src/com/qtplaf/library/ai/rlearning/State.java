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
 * Defines values in a reinforcement learning strategy.
 * 
 * @author Miquel Sas
 */
public class State {

	/**
	 * The finite list (array) of values.
	 */
	private double[] values;
	/**
	 * The descriptor.
	 */
	private StateDescriptor descriptor;

	/**
	 * Constructor.
	 * 
	 * @param descriptor The descriptor.
	 */
	public State(StateDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		this.values = new double[descriptor.size()];
	}

	/**
	 * Returns the descriptor.
	 * 
	 * @return The descriptor.
	 */
	public StateDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns the value of the element at index.
	 * 
	 * @param index The index.
	 * @return The value.
	 */
	public double get(int index) {
		return values[index];
	}

	/**
	 * Sets the value.
	 * 
	 * @param index The index.
	 * @param value The value.
	 */
	public void set(int index, double value) {
		values[index] = value;
	}

	/**
	 * Returns the size of this values.
	 * 
	 * @return The size.
	 */
	public int size() {
		return values.length;
	}

	/**
	 * Returns a copy of the list of values.
	 * 
	 * @return A copy of the list of values.
	 */
	public double[] getValues() {
		return values.clone();
	}
}
