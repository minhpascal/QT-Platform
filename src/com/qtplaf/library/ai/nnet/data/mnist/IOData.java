/**
 * 
 */
package com.qtplaf.library.ai.nnet.data.mnist;

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
	 * Version UID
	 */
	private static final long serialVersionUID = -8512856836750318682L;

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
