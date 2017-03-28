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
package com.qtplaf.library.ai.nnet.data.mnist.graph;

import java.io.Serializable;
import java.util.List;

/**
 * Structure to pack the input/output data. Note that in an unsupervised learning process the IO data will only have
 * input data, not output data.
 * 
 * @author Miquel Sas
 */
public class IOData implements Serializable {

	/**
	 * Input data.
	 */
	private List<Double> inputs;
	/**
	 * Output data.
	 */
	private List<Double> outputs;

	/**
	 * Default constructor.
	 */
	public IOData() {
		super();
	}

	/**
	 * Constructor assigning data.
	 * 
	 * @param inputs Input data
	 * @param outputs Output data
	 */
	public IOData(List<Double> inputs, List<Double> outputs) {
		super();
		this.inputs = inputs;
		this.outputs = outputs;
	}

	/**
	 * Returns the inputs data.
	 * 
	 * @return The inputs
	 */
	public List<Double> getInputs() {
		return inputs;
	}

	/**
	 * Set the inputs data.
	 * 
	 * @param inputs The inputs
	 */
	public void setInputs(List<Double> inputs) {
		this.inputs = inputs;
	}

	/**
	 * Returns the outputs data.
	 * 
	 * @return the outputs
	 */
	public List<Double> getOutputs() {
		return outputs;
	}

	/**
	 * Set the outputs data.
	 * 
	 * @param outputs the outputs
	 */
	public void setOutputs(List<Double> outputs) {
		this.outputs = outputs;
	}

}
