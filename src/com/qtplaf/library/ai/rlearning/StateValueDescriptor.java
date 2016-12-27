/**
 * 
 */
package com.qtplaf.library.ai.rlearning;

/**
 * Descriptor of state value. Includes a description of what the value means and its scale.
 * 
 * @author Miquel Sas
 */
public abstract class StateValueDescriptor {

	/**
	 * The scale.
	 */
	private int scale;
	/**
	 * An optional description.
	 */
	private String description;

	/**
	 * Default constructor.
	 */
	public StateValueDescriptor() {
		super();
	}

	/**
	 * Returns the scale.
	 * 
	 * @return The scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Sets the scale.
	 * 
	 * @param scale The scale.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * Returns the value description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the state value given a data value.
	 * 
	 * @param value The data value.
	 * @return The state value.
	 */
	public abstract double getValue(double value);
}
