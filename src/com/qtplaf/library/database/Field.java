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
package com.qtplaf.library.database;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFormattedTextField;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.util.Alignment;
import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.Properties;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.Time;
import com.qtplaf.library.util.Timestamp;

/**
 * A Field packs the necessary information to define a data item, mainly in the form of tabular data.
 *
 * @author Miquel Sas
 */
public class Field implements Comparable<Object> {

	/**
	 * Returns the list of all relations contained in the list of fields.
	 * 
	 * @param fields The source list of fields.
	 * @return The list of relations.
	 */
	public static List<Relation> getRelations(List<Field> fields) {
		List<Relation> relations = new ArrayList<>();
		for (Field field : fields) {
			List<Relation> fieldRelations = field.getRelations();
			for (Relation relation : fieldRelations) {
				if (!relations.contains(relation)) {
					relations.add(relation);
				}
			}
		}
		return relations;
	}

	/** Integer key value. */
	private static int index = 0;
	
	/** Field name. */
	private static final Integer KeyName = Integer.valueOf(index++);
	/** Optional field alias, if not set the name is used. */
	private static final Integer KeyAlias = Integer.valueOf(index++);
	/** Length if applicable, otherwise -1. */
	private static final Integer KeyLength = Integer.valueOf(index++);
	/** Decimals if applicable, otherwise -1. */
	private static final Integer KeyDecimals = Integer.valueOf(index++);
	/** Type. */
	private static final Integer KeyType = Integer.valueOf(index++);
	/** A flag that indicates if this field is persistent. */
	private static final Integer KeyPersistent = Integer.valueOf(index++);
	/** A flag indicating if this field can be null. */
	private static final Integer KeyNullable = Integer.valueOf(index++);
	/** A flag that indicates is this field is a primary key field. */
	private static final Integer KeyPrimaryKey = Integer.valueOf(index++);

	/** Description, normally a longer description. */
	private static final Integer KeyDescription = Integer.valueOf(index++);
	/** Label on forms. */
	private static final Integer KeyLabel = Integer.valueOf(index++);
	/** Header on grids. */
	private static final Integer KeyHeader = Integer.valueOf(index++);
	/** Title or short description. */
	private static final Integer KeyTitle = Integer.valueOf(index++);

	/** Initial value. */
	private static final Integer KeyInitialValue = Integer.valueOf(index++);
	/** Maximum value. */
	private static final Integer KeyMaximumValue = Integer.valueOf(index++);
	/** Minimum value. */
	private static final Integer KeyMinimumValue = Integer.valueOf(index++);
	/** List of possible values. */
	private static final Integer KeyPossibleValues = Integer.valueOf(index++);
	/** A flag indicating if a non empty value is required for this field. */
	private static final Integer KeyRequired = Integer.valueOf(index++);
	/** A generic value validator. */
	private static final Integer KeyValidator = Integer.valueOf(index++);
	/** A flag to indicate if the field should initialize to the current date, time or time stamp. */
	private static final Integer KeyCurrentDateTimeOrTimestamp = Integer.valueOf(index++);

	/** Horizontal alignment. */
	private static final Integer KeyHorizontalAlignment = Integer.valueOf(index++);
	/** Vertical alignment. */
	private static final Integer KeyVerticalAlignment = Integer.valueOf(index++);
	/** Adjusted display length. */
	private static final Integer KeyDisplayLength = Integer.valueOf(index++);
	/** Uppercase flag. */
	private static final Integer KeyUppercase = Integer.valueOf(index++);
	/** Minimum width, must be set for not autosize fields. */
	private static final Integer KeyMinimumWidth = Integer.valueOf(index++);
	/** Maximum width, must be set for not autosize fields. */
	private static final Integer KeyMaximumWidth = Integer.valueOf(index++);
	/** Preferred width, must be set for not autosize fields. */
	private static final Integer KeyPreferredWidth = Integer.valueOf(index++);
	/** Autosize flag. */
	private static final Integer KeyAutoSize = Integer.valueOf(index++);
	/** Width factor for string fields. */
	private static final Integer KeyWidthFactor = Integer.valueOf(index++);
	/** Fixed size flag for form and table autosize. */
	private static final Integer KeyFixedWidth = Integer.valueOf(index++);

	/** A boolean that indicates if this field is a key description, that is, the main description of a primary key. */
	private static final Integer KeyMainDescription = Integer.valueOf(index++);
	/** A boolean that indicates if this field is a lookup field. */
	private static final Integer KeyLookup = Integer.valueOf(index++);
	/** A boolean that indicates if the field becomes a min-max field in filter form. */
	private static final Integer KeyMinMaxFilter = Integer.valueOf(index++);
	/** A boolean that indicates, if the field is boolean, if it has to be edited in check or combo box. */
	private static final Integer KeyEditBooleanInCheckBox = Integer.valueOf(index++);
	/** A boolean to prevent the field from being edited. */
	private static final Integer KeyEditable = Integer.valueOf(index++);
	/** A boolean to indicate if the field is a password field. */
	private static final Integer KeyPassword = Integer.valueOf(index++);

	/** An optional formatter. */
	private static final Integer KeyFormatter = Integer.valueOf(index++);
	/** A boolean that indicates if seconds should be edited when the type is time or timestamp. */
	private static final Integer KeyEditSeconds = Integer.valueOf(index++);
	/** A supported database function if the column is virtual or calculated. */
	private static final Integer KeyFunction = Integer.valueOf(index++);
	/** Optional calculator. */
	private static final Integer KeyCalculator = Integer.valueOf(index++);

	/** Field group. */
	private static final Integer KeyFieldGroup = Integer.valueOf(index++);
	/** An optional lookup action. */
	private static final Integer KeyActionLookup = Integer.valueOf(index++);

	/** Optional parent table. */
	private static final Integer KeyParentTable = Integer.valueOf(index++);
	/** Optional parent view */
	private static final Integer KeyParentView = Integer.valueOf(index++);

	/** The optinal working session. */
	private static final Integer KeySession = Integer.valueOf(index++);

	/** Additional properties key. */
	private static final Integer KeyProperties = Integer.valueOf(index++);

	/**
	 * The properties.
	 */
	private Properties properties = new Properties();

	/**
	 * Default constructor.
	 */
	public Field() {
		super();
	}

	/**
	 * Constructor using a reference field.
	 * 
	 * @param field The field.
	 */
	public Field(Field field) {
		super();
		put(field);
	}

	/**
	 * Fill this field properties with the argument field properties,
	 * 
	 * @param field The fill to copy.
	 */
	public void put(Field field) {
		properties.putAll(field.properties);
	}

	/**
	 * Get the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		return properties.getString(KeyName);
	}

	/**
	 * Set the name.
	 *
	 * @param name The name of the field.
	 */
	public void setName(String name) {
		properties.setString(KeyName, name);;
	}

	/**
	 * Get the field alias.
	 *
	 * @return The field alias.
	 */
	public String getAlias() {
		String alias = properties.getString(KeyAlias);
		if (alias == null) {
			return getName();
		}
		return alias;
	}

	/**
	 * Set the field alias.
	 *
	 * @param alias The field alias.
	 */
	public void setAlias(String alias) {
		properties.setString(KeyAlias, alias);
	}

	/**
	 * Get the length if applicable, otherwise -1.
	 *
	 * @return The field length if applicable, otherwise -1.
	 */
	public int getLength() {
		return properties.getInteger(KeyLength, -1);
	}

	/**
	 * Set the field length.
	 *
	 * @param length The field length.
	 */
	public void setLength(int length) {
		properties.setInteger(KeyLength, length);
	}

	/**
	 * Get the number of decimal places if applicable.
	 *
	 * @return The number of decimal places.
	 */
	public int getDecimals() {
		if (!isNumber()) {
			return 0;
		}
		if (isInteger() || isLong()) {
			return 0;
		}
		return properties.getInteger(KeyDecimals, -1);
	}

	/**
	 * Set the number of decimal places.
	 *
	 * @param decimals The number of decimal places.
	 */
	public void setDecimals(int decimals) {
		properties.setInteger(KeyDecimals, decimals);
	}

	/**
	 * Get the type.
	 *
	 * @return The type.
	 */
	public Types getType() {
		return (Types) properties.getObject(KeyType);
	}

	/**
	 * Set the type.
	 *
	 * @param type The type.
	 */
	public void setType(Types type) {
		properties.setObject(KeyType, type);
		switch (type) {
		case String:
			setHorizontalAlignment(Alignment.Left);
			break;
		case Decimal:
		case Double:
		case Long:
		case Integer:
			setHorizontalAlignment(Alignment.Right);
			break;
		case Boolean:
			setHorizontalAlignment(Alignment.Center);
			break;
		default:
			setHorizontalAlignment(Alignment.Left);
			break;
		}
	}

	/**
	 * Check if this field is persistent.
	 *
	 * @return A boolean
	 */
	public boolean isPersistent() {
		return properties.getBoolean(KeyPersistent, true);
	}

	/**
	 * Sets if this field is persistent.
	 *
	 * @param persistent A boolean.
	 */
	public void setPersistent(boolean persistent) {
		properties.setBoolean(KeyPersistent, persistent);
	}

	/**
	 * Get the maximum value.
	 *
	 * @return The maximum value.
	 */
	public Value getMaximumValue() {
		return (Value) properties.getObject(KeyMaximumValue);
	}

	/**
	 * Set the maximum value.
	 *
	 * @param maximumValue The maximum value.
	 */
	public void setMaximumValue(Value maximumValue) {
		validateValueType(maximumValue);
		properties.setObject(KeyMaximumValue, maximumValue);
	}

	/**
	 * Get the minimum value.
	 *
	 * @return The minimum value.
	 */
	public Value getMinimumValue() {
		return (Value) properties.getObject(KeyMinimumValue);
	}

	/**
	 * Set the minimum value.
	 *
	 * @param minimumValue The minimum value.
	 */
	public void setMinimumValue(Value minimumValue) {
		validateValueType(minimumValue);
		properties.setObject(KeyMinimumValue, minimumValue);
	}

	/**
	 * Adds a possible value to the list of possible values.
	 * 
	 * @param value The value to add.
	 */
	public void addPossibleValue(Value value) {
		validateValueType(value);
		if (!getPossibleValues().contains(value)) {
			getPossibleValues().add(value);
		}
	}

	/**
	 * Add a collection of possible values.
	 * 
	 * @param possibleValues The list of possible values.
	 */
	public void addPossibleValues(Collection<Value> possibleValues) {
		for (Value possibleValue : possibleValues) {
			addPossibleValue(possibleValue);
		}
	}

	/**
	 * Clear the list of possible values and set the member to null.
	 */
	public void clearPossibleValues() {
		getPossibleValues().clear();
	}

	/**
	 * Get the array of possible values.
	 *
	 * @return The array of possible values.
	 */
	public List<Value> getPossibleValues() {
		@SuppressWarnings("unchecked")
		List<Value> possibleValues = (List<Value>) properties.getObject(KeyPossibleValues);
		if (possibleValues == null) {
			possibleValues = new ArrayList<>();
			properties.setObject(KeyPossibleValues, possibleValues);
		}
		return possibleValues;
	}

	/**
	 * Returns the possible value label or null if not applicable or not found.
	 * 
	 * @param value The target value.
	 * @return The possible value label.
	 */
	public String getPossibleValueLabel(Value value) {
		List<Value> possibleValues = getPossibleValues();
		for (Value possibleValue : possibleValues) {
			if (possibleValue.equals(value)) {
				return possibleValue.getLabel();
			}
		}
		return "";
	}

	/**
	 * Returns a boolean indicating if this field has possible values.
	 * 
	 * @return A boolean indicating if this field has possible values.
	 */
	public boolean isPossibleValues() {
		return !getPossibleValues().isEmpty();
	}

	/**
	 * Set the array of possible values.
	 *
	 * @param possibleValues The array of possible values.
	 */
	public void setPossibleValues(List<Value> possibleValues) {
		if (possibleValues != null) {
			for (Value value : possibleValues) {
				addPossibleValue(value);
			}
		}
	}

	/**
	 * Check if a non empty value is required for this field.
	 *
	 * @return A boolean.
	 */
	public boolean isRequired() {
		return properties.getBoolean(KeyRequired, false);
	}

	/**
	 * Set if a non empty value is required for this field.
	 *
	 * @param required A boolean.
	 */
	public void setRequired(boolean required) {
		properties.setBoolean(KeyRequired, required);
	}

	/**
	 * Check if this field can be null.
	 *
	 * @return A boolean
	 */
	public boolean isNullable() {
		return properties.getBoolean(KeyNullable, true);
	}

	/**
	 * Set if this field can be null.
	 *
	 * @param nullable A boolean
	 */
	public void setNullable(boolean nullable) {
		properties.setBoolean(KeyNullable, nullable);
	}

	/**
	 * Get the generic validator.
	 *
	 * @return The generic validator.
	 */
	@SuppressWarnings("unchecked")
	public Validator<Value> getValidator() {
		return (Validator<Value>) properties.getObject(KeyValidator);
	}

	/**
	 * Set the generic validator.
	 *
	 * @param validator The validator.
	 */
	public void setValidator(Validator<Value> validator) {
		properties.setObject(KeyValidator, validator);
	}

	/**
	 * Validates the convenience of the argument value.
	 *
	 * @param value The value to validate.
	 * @return A boolean indicating if the value is valid.
	 */
	public boolean validate(Value value) {

		// Strict type
		if (!value.getType().equals(getType())) {
			return false;
		}

		// Maximum value
		if (getMaximumValue() != null) {
			if (value.compareTo(getMaximumValue()) > 0) {
				return false;
			}
		}

		// Minimum value
		if (getMinimumValue() != null) {
			if (value.compareTo(getMinimumValue()) < 0) {
				return false;
			}
		}

		// Possible values
		if (!getPossibleValues().isEmpty()) {
			if (!value.in(getPossibleValues())) {
				return false;
			}
		}

		// Non empty required
		if (isRequired() && value.isEmpty()) {
			return false;
		}

		// Nullable
		if (!isNullable() && value.isNull()) {
			return false;
		}

		// Validator
		if (getValidator() != null) {
			return getValidator().validate(value);
		}

		return true;
	}

	/**
	 * Returns the validation message or null if validation is ok.
	 *
	 * @param session The working session.
	 * @param value The value to check for the validation message.
	 * @return The validation message or null if validation is ok.
	 */
	public String getValidationMessage(Session session, Value value) {

		// Strict type
		if (!value.getType().equals(getType())) {
			return MessageFormat.format("Value type {0} is not equal than field type {1}", value.getType(), getType());
		}

		// Maximum value
		if (getMaximumValue() != null) {
			if (value.compareTo(getMaximumValue()) > 0) {
				return MessageFormat.format("Value {0} is greater than {1}", value, getMaximumValue());
			}
		}

		// Minimum value
		if (getMinimumValue() != null) {
			if (value.compareTo(getMinimumValue()) < 0) {
				return MessageFormat.format("Value {0} is less than {1}", value, getMinimumValue());
			}
		}

		// Possible values
		if (!getPossibleValues().isEmpty()) {
			if (!value.in(getPossibleValues())) {
				return MessageFormat.format("Value {0} is not in the list of possible values", value);
			}
		}

		// Non empty required
		if (isRequired() && value.isEmpty()) {
			return "A non empty value is required for this field";
		}

		// Nullable
		if (!isNullable() && value.isNull()) {
			return "A not null value is required for this field";
		}

		// Validator
		if (getValidator() != null) {
			return getValidator().getMessage(session, value);
		}

		return null;
	}

	/**
	 * Returns the default value padded with characters if it is string.
	 *
	 * @return The default blank value.
	 */
	public Value getBlankValue() {
		if (isString()) {
			return new Value(StringUtils.repeat(" ", getLength()));
		}
		return getDefaultValue();
	}

	/**
	 * Returns the default value for this field.
	 *
	 * @return The default value.
	 */
	public Value getDefaultValue() {
		if (isBoolean()) {
			return new Value(false);
		}
		if (isByteArray()) {
			return new Value(new ByteArray());
		}
		if (isDate()) {
			return new Value((Date) null);
		}
		if (isDecimal()) {
			return new Value(new BigDecimal(0).setScale(getDecimals(), BigDecimal.ROUND_HALF_UP));
		}
		if (isDouble()) {
			return new Value((double) 0);
		}
		if (isInteger()) {
			return new Value((int) 0);
		}
		if (isLong()) {
			return new Value((long) 0);
		}
		if (isString()) {
			return new Value("");
		}
		if (isTime()) {
			return new Value((Time) null);
		}
		if (isTimestamp()) {
			return new Value((Timestamp) null);
		}
		return null;
	}

	/**
	 * Get the initial value.
	 *
	 * @return The initial value.
	 */
	public Value getInitialValue() {
		return (Value) properties.getObject(KeyInitialValue);
	}

	/**
	 * Set the initial value.
	 *
	 * @param initialValue The initial value.
	 */
	public void setInitialValue(Value initialValue) {
		validateValueType(initialValue);
		properties.setObject(KeyInitialValue, initialValue);
	}

	/**
	 * Check if this field value should be initialized to the current date, time or timestamp.
	 *
	 * @return A boolean indicating the initialization rule.
	 */
	public boolean isCurrentDateTimeOrTimestamp() {
		return properties.getBoolean(KeyCurrentDateTimeOrTimestamp, false);
	}

	/**
	 * Check if his field should initialize to the current date.
	 * 
	 * @return A boolean indicating that it should be initialized to the current date.
	 */
	public boolean isCurrentDate() {
		return isDate() && isCurrentDateTimeOrTimestamp();
	}

	/**
	 * Check if his field should initialize to the current time.
	 * 
	 * @return A boolean indicating that it should be initialized to the current time.
	 */
	public boolean isCurrentTime() {
		return isTime() && isCurrentDateTimeOrTimestamp();
	}

	/**
	 * Check if his field should initialize to the current time stamp.
	 * 
	 * @return A boolean indicating that it should be initialized to the current time stamp.
	 */
	public boolean isCurrentTimestamp() {
		return isTimestamp() && isCurrentDateTimeOrTimestamp();
	}

	/**
	 * Set if this field value should be initialized to the current date, time or timestamp.
	 *
	 * @param currentDateTimeOrTimestamp A boolean.
	 */
	public void setCurrentDateTimeOrTimestamp(boolean currentDateTimeOrTimestamp) {
		properties.setBoolean(KeyCurrentDateTimeOrTimestamp, currentDateTimeOrTimestamp);
	}

	/**
	 * Get the long description.
	 *
	 * @return The long description.
	 */
	public String getDescription() {
		return properties.getString(KeyDescription);
	}

	/**
	 * Set the long description.
	 *
	 * @param description The long description.
	 */
	public void setDescription(String description) {
		properties.setString(KeyDescription, description);
	}

	/**
	 * Get the label used in forms.
	 *
	 * @return The label used in forms.
	 */
	public String getLabel() {
		return properties.getString(KeyLabel);
	}

	/**
	 * Set the label used in forms.
	 *
	 * @param label The label used in forms.
	 */
	public void setLabel(String label) {
		properties.setString(KeyLabel, label);
	}

	/**
	 * Get the header used in tables.
	 *
	 * @return The header used in tables.
	 */
	public String getHeader() {
		return properties.getString(KeyHeader);
	}

	/**
	 * Set the header used in tables.
	 *
	 * @param header The header used in tables.
	 */
	public void setHeader(String header) {
		properties.setString(KeyHeader, header);
	}

	/**
	 * Get the title or short description.
	 *
	 * @return The title or short description.
	 */
	public String getTitle() {
		return properties.getString(KeyTitle);
	}

	/**
	 * Set the title or short description.
	 *
	 * @param title The title or short description.
	 */
	public void setTitle(String title) {
		properties.setString(KeyTitle, title);
	}

	/**
	 * Get the horizontal alignment.
	 *
	 * @return The horizontal alignment.
	 */
	public Alignment getHorizontalAlignment() {
		return (Alignment) properties.getObject(KeyHorizontalAlignment, Alignment.Left);
	}

	/**
	 * Set the horizontal alignment.
	 *
	 * @param horizontalAlignment The horizontal alignment.
	 */
	public void setHorizontalAlignment(Alignment horizontalAlignment) {
		if (horizontalAlignment == null) {
			horizontalAlignment = Alignment.Left;
		}
		if (!horizontalAlignment.isHorizontal()) {
			throw new IllegalArgumentException(horizontalAlignment.toString());
		}
		properties.setObject(KeyHorizontalAlignment, horizontalAlignment);
	}

	/**
	 * Get the vertical alignment.
	 *
	 * @return The vertical alignment.
	 */
	public Alignment getVerticalAlignment() {
		return (Alignment) properties.getObject(KeyVerticalAlignment, Alignment.Center);
	}

	/**
	 * Set the vertical alignment.
	 *
	 * @param verticalAlignment The vertical alignment.
	 */
	public void setVerticalAlignment(Alignment verticalAlignment) {
		if (verticalAlignment == null) {
			verticalAlignment = Alignment.Center;
		}
		if (!verticalAlignment.isVertical()) {
			throw new IllegalArgumentException(verticalAlignment.toString());
		}
		properties.setObject(KeyVerticalAlignment, verticalAlignment);
	}

	/**
	 * Get the display length.
	 *
	 * @return The display length.
	 */
	public int getDisplayLength() {
		int displayLength = properties.getInteger(KeyDisplayLength, 0);
		if (displayLength > 0) {
			return displayLength;
		}
		return getLength();
	}

	/**
	 * Returns a display description of this field using the description, title, label, header or something not null.
	 * 
	 * @return A not null description.
	 */
	public String getDisplayDescription() {
		return StringUtils.getFirstNotNull(getDescription(), getTitle(), getLabel(), getHeader());
	}

	/**
	 * Returns a display header of this field using the header, label, title, descriptin or something not null.
	 * 
	 * @return A not null header.
	 */
	public String getDisplayHeader() {
		return StringUtils.getFirstNotNull(getHeader(), getLabel(), getTitle(), getDescription());
	}

	/**
	 * Returns a display label of this field using the label, header, title, descriptin or something not null.
	 * 
	 * @return A not null header.
	 */
	public String getDisplayLabel() {
		return StringUtils.getFirstNotNull(getLabel(), getHeader(), getTitle(), getDescription());
	}

	/**
	 * Gets the tooltip text for this field.
	 * 
	 * @param session The user session.
	 * @return a String.
	 */
	public String getToolTip() {
		StringBuilder b = new StringBuilder();
		b.append(getDisplayDescription());
		if (isRequired())
			b.append(" *");
		return b.toString();
	}

	/**
	 * Set the display length.
	 *
	 * @param displayLength The display length.
	 */
	public void setDisplayLength(int displayLength) {
		properties.setInteger(KeyDisplayLength, displayLength);
	}

	/**
	 * Check if edition is uppercase.
	 *
	 * @return A boolean
	 */
	public boolean isUppercase() {
		return properties.getBoolean(KeyUppercase, false);
	}

	/**
	 * Set if edition is uppercase.
	 *
	 * @param uppercase A boolean indicating that the value is uppercase.
	 */
	public void setUppercase(boolean uppercase) {
		properties.setBoolean(KeyUppercase, uppercase);
	}

	/**
	 * Get the minimum width.
	 *
	 * @return The minimum width.
	 */
	public int getMinimumWidth() {
		return properties.getInteger(KeyMinimumWidth, -1);
	}

	/**
	 * Set the minimum width.
	 *
	 * @param minimumWidth The minimum width.
	 */
	public void setMinimumWidth(int minimumWidth) {
		properties.setInteger(KeyMinimumWidth, minimumWidth);
	}

	/**
	 * Get the maximum width.
	 *
	 * @return The maximum width.
	 */
	public int getMaximumWidth() {
		return properties.getInteger(KeyMaximumWidth, -1);
	}

	/**
	 * Set the maximum width.
	 *
	 * @param maximumWidth The maximum width.
	 */
	public void setMaximumWidth(int maximumWidth) {
		properties.setInteger(KeyMaximumWidth, maximumWidth);
	}

	/**
	 * Get the preferred width.
	 *
	 * @return The preferred width.
	 */
	public int getPreferredWidth() {
		return properties.getInteger(KeyPreferredWidth, -1);
	}

	/**
	 * Set the preferred width.
	 *
	 * @param preferredWidth The preferred width.
	 */
	public void setPreferredWidth(int preferredWidth) {
		properties.setInteger(KeyPreferredWidth, preferredWidth);
	}

	/**
	 * Check if the field is auto-size. Auto-size fields automatically calculate the necessary display with.
	 *
	 * @return A boolean.
	 */
	public boolean isAutoSize() {
		return properties.getBoolean(KeyAutoSize, true);
	}

	/**
	 * Segt the auto-size flag.
	 *
	 * @param autoSize A boolean
	 */
	public void setAutoSize(boolean autoSize) {
		properties.setBoolean(KeyAutoSize, autoSize);
	}

	/**
	 * Get the width factor to increase the calculated average width necessary to display the field in a form.
	 *
	 * @return The width factor.
	 */
	public double getWidthFactor() {
		return properties.getDouble(KeyWidthFactor, 0);
	}

	/**
	 * Set the width factor to increase the calculated average width necessary to display the field in a form.
	 *
	 * @param widthFactor The width factor.
	 */
	public void setWidthFactor(double widthFactor) {
		properties.setDouble(KeyWidthFactor, widthFactor);
	}

	/**
	 * Set the optional <i>JFormattedTextField.AbstractFormatter</i>. If no formatted is set, one will be created
	 * according to the field propertiesMap when required.
	 * 
	 * @param formatter The optional formatter.
	 */
	public void setFormatter(JFormattedTextField.AbstractFormatter formatter) {
		properties.setObject(KeyFormatter, formatter);
	}

	/**
	 * Returns a boolean indicating if secnds should be edited when the type is time or timestamp.
	 * 
	 * @return A boolean indicating if secnds should be edited when the type is time or timestamp.
	 */
	public boolean isEditSeconds() {
		return properties.getBoolean(KeyEditSeconds, true);
	}

	/**
	 * Sets a boolean indicating if secnds should be edited when the type is time or timestamp.
	 * 
	 * @param editSeconds A boolean indicating if secnds should be edited when the type is time or timestamp.
	 */
	public void setEditSeconds(boolean editSeconds) {
		properties.setBoolean(KeyEditSeconds, editSeconds);
	}

	/**
	 * Check if the field is fixed with. Non fixed width fields expand to the width of the form.
	 *
	 * @return A boolean indicating if the field is fixed-width.
	 */
	public boolean isFixedWidth() {
		return properties.getBoolean(KeyFixedWidth, true);
	}

	/**
	 * Set if the field is fixed witdh.
	 *
	 * @param fixedWidth A boolean
	 */
	public void setFixedWidth(boolean fixedWidth) {
		properties.setBoolean(KeyFixedWidth, fixedWidth);
	}

	/**
	 * Returns the optional action lookupor null.
	 * 
	 * @return The optional action lookupor null.
	 */
	public Action getActionLookup() {
		return (Action) properties.getObject(KeyActionLookup);
	}

	/**
	 * Sets the optional action lookup.
	 * 
	 * @param actionLookup The optional action lookup.
	 */
	public void setActionLookup(Action actionLookup) {
		properties.setObject(KeyActionLookup, actionLookup);
	}

	/**
	 * Returns a boolean that indicates if the field can be edited, default is <code>true</code>.
	 * 
	 * @return A boolean.
	 */
	public boolean isEditable() {
		return properties.getBoolean(KeyEditBooleanInCheckBox, true);
	}

	/**
	 * Sets a boolean that indicates if the field can be edited, default is <code>true</code>.
	 * 
	 * @param editable A boolean.
	 */
	public void setEditable(boolean editable) {
		properties.setBoolean(KeyEditable, editable);
	}

	/**
	 * Returns a boolean that indicates, if the field is boolean, if it has to be edited in check or combo box.
	 * 
	 * @return A boolean.
	 */
	public boolean isEditBooleanInCheckBox() {
		return properties.getBoolean(KeyEditBooleanInCheckBox, false);
	}

	/**
	 * Sets a boolean that indicates, if the field is boolean, if it has to be edited in check or combo box.
	 * 
	 * @param editBooleanInCheckBox A boolean.
	 */
	public void setEditBooleanInCheckBox(boolean editBooleanInCheckBox) {
		if (!isBoolean()) {
			throw new UnsupportedOperationException("Operation only valid for boolean types.");
		}
		properties.setBoolean(KeyEditBooleanInCheckBox, editBooleanInCheckBox);
	}

	/**
	 * Returns a negative integer, zero, or a positive integer as this value is less than, equal to, or greater than the
	 * specified value.
	 * <p>
	 * A field is considered to be equal to another field if the alias, type, length and decimals are the same.
	 *
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@Override
	public int compareTo(Object o) {
		Field field = null;
		try {
			field = (Field) o;
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
		}
		if (getAlias().equals(field.getAlias())) {
			if (getType().equals(field.getType())) {
				if (getLength() == field.getLength()) {
					if (getDecimals() == field.getDecimals()) {
						return 0;
					}
				}
			}
		}
		return getAlias().compareTo(field.getAlias());
	}

	/**
	 * Returns the hash code for this field.
	 *
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		return hash;
	}

	/**
	 * Check wheter the argument object is equal to this field.
	 *
	 * @param obj The object to compare
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Field field = (Field) obj;
		return getAlias().equals(field.getAlias()) && getName().equals(field.getName());
	}

	/**
	 * Validates that the type of the argument value is valid for this field.
	 *
	 * @param value The value to validate.
	 */
	private void validateValueType(Value value) {
		if ((isBoolean() && !value.isBoolean())
			|| (isDateTimeOrTimestamp() && !value.isDateTimeOrTimestamp())
			|| (isString() && !value.isString())
			|| (isNumber() && !value.isNumber())) {
			throw new IllegalArgumentException(
				MessageFormat.format("Invalid value type ({0}) for field type {1}", value.getType(), getType()));
		}
	}

	/**
	 * Check if this field is boolean.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() {
		return getType().isBoolean();
	}

	/**
	 * Check if this field is a string.
	 *
	 * @return A boolean.
	 */
	public boolean isString() {
		return getType().isString();
	}

	/**
	 * Check if this field is a number (decimal) with fixed precision.
	 *
	 * @return A boolean.
	 */
	public boolean isDecimal() {
		return getType().isDecimal();
	}

	/**
	 * Check if this field is a double.
	 *
	 * @return A boolean.
	 */
	public boolean isDouble() {
		return getType().isDouble();
	}

	/**
	 * Check if this field is an integer.
	 *
	 * @return A boolean.
	 */
	public boolean isInteger() {
		return getType().isInteger();
	}

	/**
	 * Check if this field is a long.
	 *
	 * @return A boolean.
	 */
	public boolean isLong() {
		return getType().isLong();
	}

	/**
	 * Check if this field is a number (decimal, double or integer).
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() {
		return getType().isNumber();
	}

	/**
	 * Check if this field is a floating point number.
	 *
	 * @return A boolean.
	 */
	public boolean isFloatingPoint() {
		return getType().isFloatingPoint();
	}

	/**
	 * Check if this field is a date.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() {
		return getType().isDate();
	}

	/**
	 * Check if this field is a time.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() {
		return getType().isTime();
	}

	/**
	 * Check if this field is a time.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() {
		return getType().isTimestamp();
	}

	/**
	 * Check if this field is a date, time or time stamp.
	 *
	 * @return A boolean.
	 */
	public boolean isDateTimeOrTimestamp() {
		return getType().isDateTimeOrTimestamp();
	}

	/**
	 * Check if this field is binary (byte[]).
	 *
	 * @return A boolean.
	 */
	public boolean isByteArray() {
		return getType().isTimestamp();
	}

	/**
	 * Returns the optional <i>JFormattedTextField.AbstractFormatter</i>. If not set, a default formatter will be
	 * created according to the field propertiesMap.
	 * 
	 * @return The optional formatter.
	 */
	public JFormattedTextField.AbstractFormatter getFormatter() {
		return (JFormattedTextField.AbstractFormatter) properties.getObject(KeyFormatter);
	}

	/**
	 * Check if this field is a key description.
	 * 
	 * @return A boolean that indicates if this field is a key description.
	 */
	public boolean isMainDescription() {
		return properties.getBoolean(KeyMainDescription, false);
	}

	/**
	 * Set if this field is a key description. By rule, only a key description field should be set by table.
	 * 
	 * @param mainDescription A boolean.
	 */
	public void setMainDescription(boolean mainDescription) {
		properties.setBoolean(KeyMainDescription, mainDescription);
	}

	/**
	 * Check if this field is a lookup field. In a lookup, primary key fields are shown by default, then the key
	 * description field if it exists, and finally any field tagged as lookup.
	 * 
	 * @return A boolean.
	 */
	public boolean isLookup() {
		return properties.getBoolean(KeyLookup, false);
	}

	/**
	 * Set if this field is a lookup field. In a lookup, primary key fields are shown by default, then the key
	 * description field if it exists, and finally any field tagged as lookup.
	 * 
	 * @param lookup A boolean.
	 */
	public void setLookup(boolean lookup) {
		properties.setBoolean(KeyLookup, lookup);
	}

	/**
	 * Check if this field is a password field.
	 * 
	 * @return A boolean.
	 */
	public boolean isPassword() {
		return properties.getBoolean(KeyPassword, false);
	}

	/**
	 * Set if this field is a password field.
	 * 
	 * @param password A boolean.
	 */
	public void setPassword(boolean password) {
		properties.setBoolean(KeyPassword, password);
	}

	/**
	 * Check if this field is a primary key field.
	 *
	 * @return A boolean
	 */
	public boolean isPrimaryKey() {
		return properties.getBoolean(KeyPrimaryKey, false);
	}

	/**
	 * Check if this field is a foreign field, that is, belongs to a foreign table in the list of relations of the
	 * parent view.
	 * 
	 * @return A boolean that indicates if this field is a foreign field
	 */
	public boolean isForeign() {
		// Parent table null and parent view not null can not be. Or both null, or parent view or none.
		if (getParentTable() == null) {
			return false;
		}
		if (getParentView() == null) {
			return false;
		}
		if (getParentView().getMasterTable().equals(getParentTable())) {
			return false;
		}
		List<Relation> relations = getParentView().getRelations();
		for (Relation relation : relations) {
			if (relation.getForeignTable().equals(getParentTable())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if this field is a local field. Has no parent table or belongs to the parent table.
	 * 
	 * @return A boolean that indicates if this field is a local field.
	 */
	public boolean isLocal() {
		return !isForeign();
	}

	/**
	 * Set if this field is a primary key field.
	 *
	 * @param primaryKey A boolean that indicates if this field is a primary key field.
	 */
	public void setPrimaryKey(boolean primaryKey) {
		properties.setBoolean(KeyPrimaryKey, primaryKey);
		if (primaryKey) {
			setNullable(false);
		}
	}

	/**
	 * Gets the function or formula.
	 *
	 * @return The function.
	 */
	public String getFunction() {
		return properties.getString(KeyFunction);
	}

	/**
	 * Sets the function or formula.
	 *
	 * @param function The function.
	 */
	public void setFunction(String function) {
		if (function != null && function.trim().length() > 0) {
			properties.setString(KeyFunction, function);
		}
	}

	/**
	 * Returns the calculator.
	 * 
	 * @return The calculator.
	 */
	public FieldCalculator getCalculator() {
		return (FieldCalculator) properties.getObject(KeyCalculator);
	}

	/**
	 * Sets the calcualtor.
	 * 
	 * @param calculator The field calculator.
	 */
	public void setCalculator(FieldCalculator calculator) {
		properties.setObject(KeyCalculator, calculator);
	}

	/**
	 * Check if this column is virtual. A column is virtual is it has a function but not a name.
	 *
	 * @return A <code>boolean</code>.
	 */
	public boolean isVirtual() {
		return (getFunction() != null);
	}

	/**
	 * Check if this column has to create constraints.
	 *
	 * @return A boolean.
	 */
	public boolean isCreateConstraints() {
		if (!isPersistent()) {
			return false;
		}
		if (getParentTable() == null) {
			return false;
		}
		if (!getParentTable().isPersistentConstraints()) {
			return false;
		}
		if (getMinimumValue() != null) {
			return true;
		}
		if (getMaximumValue() != null) {
			return true;
		}
		if (!getPossibleValues().isEmpty()) {
			if (!isBoolean()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return this column parent table.
	 *
	 * @return The parent table.
	 */
	public Table getParentTable() {
		return (Table) properties.getObject(KeyParentTable);
	}

	/**
	 * Set this column parent table.
	 *
	 * @param parentTable The parent table.
	 */
	public void setParentTable(Table parentTable) {
		properties.setObject(KeyParentTable, parentTable);
	}

	/**
	 * Return this column parent view.
	 *
	 * @return The parent view
	 */
	public View getParentView() {
		return (View) properties.getObject(KeyParentView);
	}

	/**
	 * Set this column parent view if any.
	 *
	 * @param parentView The parent view
	 */
	public void setParentView(View parentView) {
		properties.setObject(KeyParentView, parentView);
	}

	/**
	 * Returns the field group if any.
	 * 
	 * @return The field group if any.
	 */
	public FieldGroup getFieldGroup() {
		return (FieldGroup) properties.getObject(KeyFieldGroup, FieldGroup.emptyFieldGroup);
	}

	/**
	 * Sets the field group.
	 * 
	 * @param fieldGroup The field group.
	 */
	public void setFieldGroup(FieldGroup fieldGroup) {
		properties.setObject(KeyFieldGroup, fieldGroup);
	}

	/**
	 * Returns the working session or null.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return (Session) properties.getObject(KeySession);
	}

	/**
	 * Set the working session.
	 * 
	 * @param session The working session.
	 */
	public void setSession(Session session) {
		properties.setObject(KeySession, session);
	}

	/**
	 * Returns a boolean indicating if tthis field is a min-max field in a filter form.
	 * 
	 * @return A boolean.
	 */
	public boolean isMinMaxFilter() {
		return properties.getBoolean(KeyMinMaxFilter, false);
	}

	/**
	 * Sets a boolean indicating if tthis field is a min-max field in a filter form.
	 * 
	 * @param minMaxFilter A boolean.
	 */
	public void setMinMaxFilter(boolean minMaxFilter) {
		properties.setBoolean(KeyMinMaxFilter, minMaxFilter);
	}

	/**
	 * Returns the name of the column in the database, qualified with the parent table or view alias if it exists.
	 *
	 * @return The name.
	 */
	public String getNameParent() {
		String name = getName();
		String parentAlias = (getParentTable() != null ? getParentTable().getAlias() : null);
		if (parentAlias != null) {
			return parentAlias + "." + name;
		}
		return name;
	}

	/**
	 * Returns the name to use in a relation of a select statement.
	 *
	 * @return The name.
	 */
	public String getNameRelate() {
		return getNameParent();
	}

	/**
	 * Returns the name that most accurate identifies the field whithin the database. For field belonging to a table in
	 * a schema, this would be <code>SCHEMA.TABLE.FIELD</code>.
	 * 
	 * @return The name that most accurate identifies the field.
	 */
	public String getNameLong() {
		String name = getName();
		String parentName = (getParentTable() != null ? getParentTable().getNameSchema() : null);
		if (parentName != null) {
			return parentName + "." + name;
		}
		return name;
	}

	/**
	 * Returns the name that will be used for access restrictions security. This is he long name.
	 * 
	 * @return The name used for access restrictions.
	 */
	public String getNameSecurity() {
		return getNameLong();
	}

	/**
	 * Returns the name to use in an <code>UPDATE</code> statement.
	 *
	 * @return The name.
	 */
	public String getNameUpdate() {
		String name = getName();
		String parentName = (getParentTable() != null ? getParentTable().getName() : null);
		if (parentName != null) {
			return parentName + "." + name;
		}
		return name;
	}

	/**
	 * Returns the name to use in an <code>DELETE</code> statement.
	 *
	 * @return The name.
	 */
	public String getNameDelete() {
		return getNameUpdate();
	}

	/**
	 * Returns the name to use in a <code>WHERE</code> clause.
	 *
	 * @return The name.
	 */
	public String getNameWhere() {
		if (isVirtual()) {
			return "(" + getFunction() + ")";
		}
		return getNameParent();
	}

	/**
	 * Returns the generic property or null.
	 * 
	 * @param key The key.
	 * @return The property or null.
	 */
	public Object getProperty(Object key) {
		return getAdditionalProperties().getObject(key);
	}

	/**
	 * Set a generic property.
	 * 
	 * @param key The key.
	 * @param property The property.
	 */
	public void setProperty(Object key, Object property) {
		getAdditionalProperties().setObject(key, property);
	}

	/**
	 * Returns the additional properties.
	 * 
	 * @return The additional properties.
	 */
	private Properties getAdditionalProperties() {
		Properties additionalProperties = (Properties) properties.getObject(KeyProperties);
		if (additionalProperties == null) {
			additionalProperties = new Properties();
			properties.setObject(KeyProperties, additionalProperties);
		}
		return additionalProperties;
	}

	/**
	 * Returns the name to use in the column list of a <code>SELECT</code> query.
	 *
	 * @return The name.
	 */
	public String getNameSelect() {
		StringBuilder name = new StringBuilder();
		if (isVirtual()) {
			name.append("(");
			name.append(getFunction());
			name.append(")");
		} else {
			name.append(getNameParent());
		}
		if (getAlias() != null) {
			name.append(" AS ");
			name.append(getAlias());
		}
		return name.toString();
	}

	/**
	 * Gets the name to use in a <code>CREATE TABLE</code> or <code>ALTER TABLE</code> statement.
	 *
	 * @return The name.
	 */
	public String getNameCreate() {
		return getName();
	}

	/**
	 * Returns the name to use in a <code>GROUP BY</code> clause of a <code>SELECT</code> query.
	 *
	 * @return The name.
	 */
	public String getNameGroupBy() {
		StringBuilder name = new StringBuilder();
		if (isVirtual()) {
			name.append("(");
			name.append(getFunction());
			name.append(")");
		} else {
			name.append(getNameParent());
		}
		return name.toString();
	}

	/**
	 * Returns the name to use in an <code>ORDER BY</code> clause of a select query.
	 *
	 * @return The name.
	 */
	public String getNameOrderBy() {
		StringBuilder name = new StringBuilder();
		if (isVirtual()) {
			name.append("(");
			name.append(getFunction());
			name.append(")");
		} else {
			name.append(getNameParent());
		}
		return name.toString();
	}

	/**
	 * Returns the list of relations associated with the parent view or table of this field.
	 * 
	 * @return
	 */
	public List<Relation> getRelations() {
		List<Relation> relations = new ArrayList<>();
		if (getParentView() != null) {
			relations.addAll(getParentView().getRelations());
		} else if (getParentTable() != null) {
			List<ForeignKey> foreignKeys = getParentTable().getForeignKeys();
			for (ForeignKey foreignKey : foreignKeys) {
				relations.add(foreignKey.getRelation());
			}
		}
		return relations;
	}

	/**
	 * Gets a string representation of the field.
	 * 
	 * @return A string representation of this field.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Field: ");
		b.append(getAlias());
		b.append(", ");
		b.append(getType());
		b.append(", ");
		b.append(getLength());
		b.append(", ");
		b.append(getDecimals());
		return b.toString();
	}
}
