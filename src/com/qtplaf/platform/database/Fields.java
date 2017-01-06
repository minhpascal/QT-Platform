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

package com.qtplaf.platform.database;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;

/**
 * Centralizes master field definitions (domains). These definitions do not include table attributes like primary key.
 * 
 * @author Miquel Sas
 */
public class Fields {

	public static final String BrokerId = "BROKER_ID";
	public static final String BrokerName = "BROKER_NAME";
	public static final String BrokerTitle = "BROKER_TITLE";
	public static final String InstrumentId = "INSTR_ID";
	public static final String InstrumentDesc = "INSTR_DESC";
	public static final String InstrumentPipValue = "INSTR_PIPV";
	public static final String InstrumentPipScale = "INSTR_PIPS";
	public static final String InstrumentTickValue = "INSTR_TICKV";
	public static final String InstrumentTickScale = "INSTR_TICKS";
	public static final String InstrumentVolumeScale = "INSTR_VOLS";
	public static final String InstrumentPrimaryCurrency = "INSTR_CURRP";
	public static final String InstrumentSecondaryCurrency = "INSTR_CURRS";

	/**
	 * Returns <b><i>BrokerId</i></b> <tt>BROKER_ID</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldBrokerId(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(BrokerId);
		field.setType(Types.String);
		field.setLength(20);
		field.setHeader(session.getString("fieldBrokerIdHeader"));
		field.setLabel(session.getString("fieldBrokerIdLabel"));
		field.setTitle(session.getString("fieldBrokerIdLabel"));
		return field;
	}

	/**
	 * Returns <b><i>BrokerName</i></b> <tt>BROKER_NAME</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldBrokerName(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(BrokerName);
		field.setType(Types.String);
		field.setLength(60);
		field.setHeader(session.getString("fieldBrokerNameHeader"));
		field.setLabel(session.getString("fieldBrokerNameLabel"));
		field.setTitle(session.getString("fieldBrokerNameLabel"));
		return field;
	}

	/**
	 * Returns <b><i>BrokerTitle</i></b> <tt>BROKER_TITLE</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldBrokerTitle(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(BrokerTitle);
		field.setType(Types.String);
		field.setLength(120);
		field.setHeader(session.getString("fieldBrokerTitleHeader"));
		field.setLabel(session.getString("fieldBrokerTitleLabel"));
		field.setTitle(session.getString("fieldBrokerTitleLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentId</i></b> <tt>INSTR_ID</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentId(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentId);
		field.setType(Types.String);
		field.setLength(20);
		field.setHeader(session.getString("fieldInstrumentIdHeader"));
		field.setLabel(session.getString("fieldInstrumentIdLabel"));
		field.setTitle(session.getString("fieldInstrumentIdLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentDesc</i></b> <tt>INSTR_DESC</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentDesc(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentDesc);
		field.setType(Types.String);
		field.setLength(120);
		field.setHeader(session.getString("fieldInstrumentDescHeader"));
		field.setLabel(session.getString("fieldInstrumentDescLabel"));
		field.setTitle(session.getString("fieldInstrumentDescLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentPipValue</i></b> <tt>INSTR_PIPV</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentPipValue(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentPipValue);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldInstrumentPipValueHeader"));
		field.setLabel(session.getString("fieldInstrumentPipValueLabel"));
		field.setTitle(session.getString("fieldInstrumentPipValueLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentPipScale</i></b> <tt>INSTR_PIPS</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentPipScale(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentPipScale);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentPipScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentPipScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentPipScaleLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentTickValue</i></b> <tt>INSTR_TICKV</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentTickValue(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentTickValue);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldInstrumentTickValueHeader"));
		field.setLabel(session.getString("fieldInstrumentTickValueLabel"));
		field.setTitle(session.getString("fieldInstrumentTickValueLabel"));
		return field;
	}

	/**
	 * Returns <b><i>InstrumentTickScale</i></b> <tt>INSTR_TICKS</tt> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getFieldInstrumentTickScale(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(InstrumentTickScale);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentTickScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentTickScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentTickScaleLabel"));
		return field;
	}

}
