/**
 * 
 */
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Value;

/**
 * An input parameter information definition.
 * 
 * @author Miquel Sas
 */
public class ParameterInfo {

	/**
	 * A field that defines the properties of this parameter.
	 */
	private Field field;
	/**
	 * The list of values assigned.
	 */
	private List<Value> values = new ArrayList<>();
	/**
	 * The maximum number of accepted values, less equal zero for a variable list.
	 */
	private int maximumValues = 1;
	/**
	 * Working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public ParameterInfo(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the field that defines the properties of this parameter.
	 * 
	 * @return The field that defines the properties of this parameter.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Sets the field that defines the properties of this parameter.
	 * 
	 * @param field The field that defines the properties of this parameter.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Returns the maximum number of values accepted.
	 * 
	 * @return The maximum number of values accepted.
	 */
	public int getMaximumValues() {
		return maximumValues;
	}

	/**
	 * Sets the maximum number of values accepted.
	 * 
	 * @param maximumValues The maximum number of values accepted.
	 */
	public void setMaximumValues(int numValues) {
		this.maximumValues = numValues;
	}

	/**
	 * Add a value to the list of values.
	 * 
	 * @param value The value.
	 */
	public void addValue(Value value) {
		if (maximumValues > 0 && values.size() == maximumValues) {
			throw new IllegalStateException("Maximum number of valuees reached: " + maximumValues);
		}
		check(value);
		values.add(value);
	}

	/**
	 * Clear the list of values.
	 */
	public void clearValues() {
		values.clear();
	}

	/**
	 * Returns the size or current number of values.
	 * 
	 * @return The size or current number of values.
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Returns the value at the given index.
	 * 
	 * @param index The index.
	 * @return The value at the given index.
	 */
	public Value getValue(int index) {
		return values.get(index);
	}

	/**
	 * Returns the first assigned value.
	 * 
	 * @return The first assigned value.
	 */
	public Value getValue() {
		if (!values.isEmpty()) {
			return values.get(0);
		}
		return null;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value The value.
	 */
	public void setValue(Value value) {
		check(value);
		if (values.isEmpty()) {
			values.add(value);
		} else {
			values.set(0, value);
		}
	}

	/**
	 * Sets the value at the given index.
	 * 
	 * @param index The index.
	 * @param value The value.
	 */
	public void setValue(int index, Value value) {
		check(value);
		values.set(index, value);
	}

	/**
	 * Check if the value is acceptable.
	 * 
	 * @param value The value to check.
	 */
	private void check(Value value) {
		if (field == null) {
			throw new IllegalArgumentException("The field must be set.");
		}
		if (!field.validate(value)) {
			throw new IllegalArgumentException(field.getValidationMessage(session, value));
		}
	}

}
