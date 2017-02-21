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
package com.qtplaf.library.ai.nnet.function.output;

import com.qtplaf.library.ai.nnet.function.OutputFunction;

/**
 * Linear neuron activation function.
 * 
 * @author Miquel Sas
 */
public class Linear implements OutputFunction {
	
	/**
	 * The slope of this linear function, 1.0 by default.
	 */
	private double slope = 1.0;

	/**
	 * Default constructor.
	 */
	public Linear() {
	}

	/**
	 * Constructor assigning the slope.
	 * 
	 * @param slope The slope.
	 */
	public Linear(double slope) {
		this.slope = slope;
	}

	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	public double getOutput(double input) {
		return slope * input;
	}

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	public double getDerivative(double input) {
		return slope;
	}

}
