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
package com.qtplaf.library.ai.nnet.graph.function.output;

import com.qtplaf.library.ai.nnet.graph.function.OutputFunction;

/**
 * A Gaussian activation function.
 * 
 * @author Miquel Sas
 */
public class Gaussian implements OutputFunction {

	/**
	 * The sigma parameter.
	 */
	private double sigma = 0.5;

	/**
	 * Default constructor.
	 */
	public Gaussian() {
	}

	/**
	 * Constructor assigning the sigma parameter.
	 * 
	 * @param sigma The sigma parameter.
	 */
	public Gaussian(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * Returns the output of the function given the input value.
	 * 
	 * @param input The input value.
	 * @return The output.
	 */
	public double getOutput(double input) {
		return Math.exp(-Math.pow(input, 2) / (2 * Math.pow(sigma, 2)));
	}

	/**
	 * Returns the first derivative of the function, given the input value.
	 * 
	 * @param input The input value.
	 * @return The first derivative
	 */
	public double getDerivative(double input) {
		return getOutput(input) * (-input / (sigma * sigma));
	}

}
