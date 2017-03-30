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
package com.qtplaf.library.ai.nnet;

/**
 * A neural network supervised learning or checking pattern, with an input and an output vector.
 * 
 * @author Miquel Sas
 */
public class Pattern {

	/** Patter input. */
	private double[] patternInputs;
	/** Pattern output. */
	private double[] patternOutputs;
	/**
	 * Default constructor.
	 */
	public Pattern() {
		super();
	}

	/**
	 * Return the pattern inputs.
	 * 
	 * @return The pattern inputs.
	 */
	public double[] getPatternInputs() {
		return patternInputs;
	}

	/**
	 * Set the pattern inputs.
	 * 
	 * @param patternInputs The pattern inputs.
	 */
	public void setPatternInputs(double[] patternInputs) {
		this.patternInputs = patternInputs;
	}

	/**
	 * Return the pattern outputs.
	 * 
	 * @return The pattern outputs.
	 */
	public double[] getPatternOutputs() {
		return patternOutputs;
	}

	/**
	 * Set the pattern outputs.
	 * 
	 * @param patternOutputs The pattern outputs.
	 */
	public void setPatternOutputs(double[] patternOutputs) {
		this.patternOutputs = patternOutputs;
	}

}
