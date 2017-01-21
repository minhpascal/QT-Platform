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

import com.qtplaf.library.app.Session;

/**
 * Provides field metadata as fields of the fields properties.
 * 
 * @author Miquel Sas
 */
public class FieldProperties {

	/** Property alias: INDEX. */
	public static final String Index = "INDEX";
	/** Property alias: GROUP. */
	public static final String Group = "GROUP";
	/** Property alias: GROUP_INDEX. */
	public static final String GroupIndex = "GROUP_INDEX";
	/** Property alias: NAME. */
	public static final String Name = "NAME";
	/** Property alias: ALIAS. */
	public static final String Alias = "ALIAS";
	/** Property alias: HEADER. */
	public static final String Header = "HEADER";
	/** Property alias: TITLE. */
	public static final String Title = "TITLE";
	/** Property alias: TYPE. */
	public static final String Type = "TYPE";
	/** Property alias: LENGTH. */
	public static final String Length = "LENGTH";
	/** Property alias: DECIMALS. */
	public static final String Decimals = "DECIMALS";
	/** Property alias: ASC. */
	public static final String Ascending = "ASC";

	/**
	 * The working session.
	 */
	private Session session;
	/**
	 * The properties field list.
	 */
	private FieldList fieldList;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public FieldProperties(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the properties field list.
	 * 
	 * @return The properties field list.
	 */
	public FieldList getFieldList() {
		if (fieldList == null) {
			fieldList = new FieldList();
			fieldList.addField(getFieldGroupIndex());
			fieldList.addField(getFieldIndex());
			fieldList.addField(getFieldGroup());
			fieldList.addField(getFieldName());
			fieldList.addField(getFieldAlias());
			fieldList.addField(getFieldHeader());
			fieldList.addField(getFieldTitle());
			fieldList.addField(getFieldType());
			fieldList.addField(getFieldLength());
			fieldList.addField(getFieldDecimals());
			fieldList.addField(getFieldAscending());
		}
		return fieldList;
	}

	/**
	 * Returns the properties record.
	 * 
	 * @return The properties record.
	 */
	public Record getProperties() {
		return new Record(getFieldList());
	}

	/**
	 * Returns the field corresponding to the property <i>GroupIndex</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldGroupIndex() {
		Field field = new Field();
		field.setName(GroupIndex);
		field.setAlias(GroupIndex);
		field.setTitle(session.getString("fieldGroupIndex"));
		field.setLabel(session.getString("fieldGroupIndex"));
		field.setHeader(session.getString("fieldGroupIndex"));
		field.setType(Types.Integer);
		field.setPrimaryKey(true);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Index</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldIndex() {
		Field field = new Field();
		field.setName(Index);
		field.setAlias(Index);
		field.setTitle(session.getString("fieldIndex"));
		field.setLabel(session.getString("fieldIndex"));
		field.setHeader(session.getString("fieldIndex"));
		field.setType(Types.Integer);
		field.setPrimaryKey(true);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Group</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldGroup() {
		Field field = new Field();
		field.setName(Group);
		field.setAlias(Group);
		field.setTitle(session.getString("fieldGroup"));
		field.setLabel(session.getString("fieldGroup"));
		field.setHeader(session.getString("fieldGroup"));
		field.setType(Types.String);
		field.setLength(60);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Name</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldName() {
		Field field = new Field();
		field.setName(Name);
		field.setAlias(Name);
		field.setTitle(session.getString("fieldName"));
		field.setLabel(session.getString("fieldName"));
		field.setHeader(session.getString("fieldName"));
		field.setType(Types.String);
		field.setLength(30);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Alias</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldAlias() {
		Field field = new Field();
		field.setName(Alias);
		field.setAlias(Alias);
		field.setTitle(session.getString("fieldAlias"));
		field.setLabel(session.getString("fieldAlias"));
		field.setHeader(session.getString("fieldAlias"));
		field.setType(Types.String);
		field.setLength(30);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Header</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldHeader() {
		Field field = new Field();
		field.setName(Header);
		field.setAlias(Header);
		field.setTitle(session.getString("fieldHeader"));
		field.setLabel(session.getString("fieldHeader"));
		field.setHeader(session.getString("fieldHeader"));
		field.setType(Types.String);
		field.setLength(60);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Title</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldTitle() {
		Field field = new Field();
		field.setName(Title);
		field.setAlias(Title);
		field.setTitle(session.getString("fieldTitle"));
		field.setLabel(session.getString("fieldTitle"));
		field.setHeader(session.getString("fieldTitle"));
		field.setType(Types.String);
		field.setLength(60);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Type</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldType() {
		Field field = new Field();
		field.setName(Type);
		field.setAlias(Type);
		field.setTitle(session.getString("fieldType"));
		field.setLabel(session.getString("fieldType"));
		field.setHeader(session.getString("fieldType"));
		field.setType(Types.String);
		field.setLength(20);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Length</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldLength() {
		Field field = new Field();
		field.setName(Length);
		field.setAlias(Length);
		field.setTitle(session.getString("fieldLength"));
		field.setLabel(session.getString("fieldLength"));
		field.setHeader(session.getString("fieldLength"));
		field.setType(Types.Integer);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Decimals</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldDecimals() {
		Field field = new Field();
		field.setName(Decimals);
		field.setAlias(Decimals);
		field.setTitle(session.getString("fieldDecimals"));
		field.setLabel(session.getString("fieldDecimals"));
		field.setHeader(session.getString("fieldDecimals"));
		field.setType(Types.Integer);
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the field corresponding to the property <i>Ascending/Descending</i>.
	 * 
	 * @return The field.
	 */
	public Field getFieldAscending() {
		Field field = new Field();
		field.setName(Ascending);
		field.setAlias(Ascending);
		field.setTitle(session.getString("fieldAsc"));
		field.setLabel(session.getString("fieldAsc"));
		field.setHeader(session.getString("fieldAsc"));
		field.setType(Types.String);
		field.addPossibleValue(new Value(session.getString("tokenAsc")));
		field.addPossibleValue(new Value(session.getString("tokenDesc")));
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Fill the properties record with field data.
	 * 
	 * @param field Source field.
	 * @param index The index in the list.
	 * @param ascending The ascending flag, not included by default in the field.
	 * @return properties The properties record.
	 */
	public Record getProperties(Field field, int index, boolean ascending) {
		Record properties = getProperties();
		properties.setValue(Index, index);
		properties.setValue(Group, getFieldGroupTitle(field.getFieldGroup()));
		properties.setValue(GroupIndex, getFieldGroupIndex(field.getFieldGroup()));
		properties.setValue(Name, field.getName());
		properties.setValue(Alias, field.getAlias());
		properties.setValue(Header, field.getHeader());
		properties.setValue(Title, field.getTitle());
		properties.setValue(Type, field.getType().name());
		properties.setValue(Length, field.getLength());
		properties.setValue(Decimals, field.getDecimals());
		// Special property
		String strAscending = session.getString(ascending ? "tokenAsc" : "tokenDesc");
		properties.setValue(Ascending, strAscending);
		// Set the source field that gave values to this properties.
		properties.setProperty("Source", field);

		// Nullify length and decimals if appropriate.
		if (!field.isNumber()) {
			properties.getValue(Decimals).setNull();
		}
		if (field.getLength() <= 0) {
			properties.getValue(Length).setNull();
		}

		// Store group and subgroup as objects.
		properties.setProperty(Group, field.getFieldGroup());

		return properties;
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public int getPropertyGroupIndex(Record properties) {
		return properties.getValue(GroupIndex).getInteger();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public int getPropertyIndex(Record properties) {
		return properties.getValue(Index).getInteger();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public String getPropertyGroup(Record properties) {
		return properties.getValue(Group).getString();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public String getPropertyName(Record properties) {
		return properties.getValue(Name).getString();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public String getPropertyAlias(Record properties) {
		return properties.getValue(Alias).getString();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public String getPropertyHeader(Record properties) {
		return properties.getValue(Header).getString();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public String getPropertyTitle(Record properties) {
		return properties.getValue(Title).getString();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public Types getPropertyType(Record properties) {
		return Types.parseType(properties.getValue(Type).getString());
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public int getPropertyLength(Record properties) {
		return properties.getValue(Length).getInteger();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public int getPropertyDecimals(Record properties) {
		return properties.getValue(Decimals).getInteger();
	}

	/**
	 * Returns the field property.
	 * 
	 * @param properties The properties record.
	 * @return The property value.
	 */
	public boolean getPropertyAscending(Record properties) {
		return properties.getValue(Ascending).equals(session.getString("tokenAsc"));
	}

	/**
	 * Returns the source field that gave values to the properties.
	 * 
	 * @param properties The properties.
	 * @return The source field.
	 */
	public Field getPropertiesSourceField(Record properties) {
		return (Field) properties.getProperty("Source");
	}

	/**
	 * Returns a valid string for the field group.
	 * 
	 * @param fieldGroup The field group.
	 * @return The title string.
	 */
	private String getFieldGroupTitle(FieldGroup fieldGroup) {
		if (fieldGroup == null) {
			return "";
		}
		if (fieldGroup.getName() == null) {
			throw new IllegalStateException();
		}
		if (fieldGroup.getTitle() == null) {
			return fieldGroup.getName();
		}
		return fieldGroup.getTitle();
	}

	/**
	 * Returns a valid index for the field group.
	 * 
	 * @param fieldGroup The field group.
	 * @return A valid index (Integer.MAX_VALUE if the group is null)
	 */
	private int getFieldGroupIndex(FieldGroup fieldGroup) {
		if (fieldGroup == null) {
			return FieldGroup.emptyFieldGroup.getIndex();
		}
		return fieldGroup.getIndex();
	}
}
