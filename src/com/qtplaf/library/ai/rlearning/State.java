/**
 * 
 */
package com.qtplaf.library.ai.rlearning;

/**
 * Defines a values in a reinforcement learning strategy.
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
