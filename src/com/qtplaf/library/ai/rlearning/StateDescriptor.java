/**
 * 
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
