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
 * A transition from one state to another.
 *
 * @author Miquel Sas
 */
public class Transition {

	/** Input state. */
	private State input;
	/** Output state. */
	private State output;
	/** Probability factor. */
	private double factor;

	/**
	 * Constructor.
	 */
	public Transition() {
		super();
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param input Input state.
	 * @param output Output state.
	 * @param factor Probability factor.
	 */
	public Transition(State input, State output, double factor) {
		super();
		this.input = input;
		this.output = output;
		this.factor = factor;
	}

	/**
	 * Returns the input state.
	 * 
	 * @return The input state.
	 */
	public State getInput() {
		return input;
	}

	/**
	 * Set the input state.
	 * 
	 * @param input The input state.
	 */
	public void setInput(State input) {
		this.input = input;
	}

	/**
	 * Returns the output state.
	 * 
	 * @return The output state.
	 */
	public State getOutput() {
		return output;
	}

	/**
	 * Sets the output state.
	 * 
	 * @param output The output state.
	 */
	public void setOutput(State output) {
		this.output = output;
	}

	/**
	 * Returns the probability factor.
	 * 
	 * @return The probability factor.
	 */
	public double getFactor() {
		return factor;
	}

	/**
	 * Set the probability factor.
	 * 
	 * @param factor The probability factor.
	 */
	public void setFactor(double factor) {
		this.factor = factor;
	}

}
