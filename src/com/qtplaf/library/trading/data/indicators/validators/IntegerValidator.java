package com.qtplaf.library.trading.data.indicators.validators;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Validator;
import com.qtplaf.library.database.Value;

/**
 * Validator for an integer value.
 */
public class IntegerValidator extends Validator<Value> {

	/**
	 * Message label.
	 */
	private String label;
	/**
	 * Minimum.
	 */
	private int minimum = Integer.MIN_VALUE;
	/**
	 * Maximum.
	 */
	private int maximum = Integer.MAX_VALUE;

	/**
	 * Default constructor.
	 */
	public IntegerValidator() {
		super();
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param label The label.
	 * @param minimum The minimum value.
	 * @param maximum The maximum value.
	 */
	public IntegerValidator(String label, int minimum, int maximum) {
		super();
		this.label = label;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * Returns the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Set the label.
	 * 
	 * @param label The message label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the minimum value.
	 * 
	 * @return The minimum value.
	 */
	public int getMinimum() {
		return minimum;
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param minimum The minimum value.
	 */
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	/**
	 * Returns the maximum value.
	 * 
	 * @return The maximum value.
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Set the maximum value.
	 * 
	 * @param maximum The maximum value.
	 */
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	/**
	 * Validates the convenience of the given type for the object.
	 *
	 * @param value The type to validate.
	 * @return A boolean indicating if the type is valid.
	 */
	public boolean validate(Value value) {
		if (value.getInteger() >= minimum && value.getInteger() <= maximum) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the validation message related to the type validation. Normally a null should be returned when the
	 * validate method returns true.
	 *
	 * @param session The working session.
	 * @param value The argument type.
	 * @return The validation message or null.
	 */
	public String getMessage(Session session, Value value) {
		if (value.getInteger() < minimum) {
			StringBuilder b = new StringBuilder();
			if (label != null) {
				b.append(label);
			} else {
				b.append("Value");
			}
			b.append(" must be greater than or equal to "+minimum);
			return b.toString();
		}
		if (value.getInteger() > maximum) {
			StringBuilder b = new StringBuilder();
			if (label != null) {
				b.append(label);
			} else {
				b.append("Value");
			}
			b.append(" must be less than or equal to "+maximum);
			return b.toString();
		}
		return null;
	}
}