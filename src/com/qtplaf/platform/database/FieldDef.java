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
public class FieldDef {

	public static final String ServerId = "server_id";
	public static final String ServerName = "server_name";
	public static final String ServerTitle = "server_title";
	public static final String InstrumentId = "instr_id";
	public static final String InstrumentDesc = "instr_desc";
	public static final String InstrumentPipValue = "instr_pipv";
	public static final String InstrumentPipScale = "instr_pips";
	public static final String InstrumentTickValue = "instr_tickv";
	public static final String InstrumentTickScale = "instr_ticks";
	public static final String InstrumentVolumeScale = "instr_vols";
	public static final String InstrumentPrimaryCurrency = "instr_currp";
	public static final String InstrumentSecondaryCurrency = "instr_currs";
	public static final String UnitId = "unit_id";
	public static final String UnitName = "unit_name";
	public static final String PeriodId = "period_id";
	public static final String PeriodName = "period_name";
	public static final String PeriodSize = "period_size";
	public static final String PeriodUnitIndex = "period_unit_index";
	public static final String TableName = "table_name";
	public static final String DataFilter = "data_filter";
	public static final String OfferSide = "offer_side";

	public static final String Time = "time";
	public static final String Open = "open";
	public static final String High = "high";
	public static final String Low = "low";
	public static final String Close = "close";
	public static final String Volume = "volume";

	/**
	 * Returns the <b><i>ServerId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerId(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(20);
		field.setHeader(session.getString("fieldServerIdHeader"));
		field.setLabel(session.getString("fieldServerIdLabel"));
		field.setTitle(session.getString("fieldServerIdLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>ServerName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerName(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(60);
		field.setHeader(session.getString("fieldServerNameHeader"));
		field.setLabel(session.getString("fieldServerNameLabel"));
		field.setTitle(session.getString("fieldServerNameLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>ServerTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerTitle(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(120);
		field.setHeader(session.getString("fieldServerTitleHeader"));
		field.setLabel(session.getString("fieldServerTitleLabel"));
		field.setTitle(session.getString("fieldServerTitleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentId(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(20);
		field.setHeader(session.getString("fieldInstrumentIdHeader"));
		field.setLabel(session.getString("fieldInstrumentIdLabel"));
		field.setTitle(session.getString("fieldInstrumentIdLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentDesc</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentDesc(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(120);
		field.setHeader(session.getString("fieldInstrumentDescHeader"));
		field.setLabel(session.getString("fieldInstrumentDescLabel"));
		field.setTitle(session.getString("fieldInstrumentDescLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentPipValue</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPipValue(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Decimal);
		field.setLength(16);
		field.setDecimals(8);
		field.setHeader(session.getString("fieldInstrumentPipValueHeader"));
		field.setLabel(session.getString("fieldInstrumentPipValueLabel"));
		field.setTitle(session.getString("fieldInstrumentPipValueLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentPipScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPipScale(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentPipScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentPipScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentPipScaleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentTickValue</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentTickValue(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Decimal);
		field.setLength(16);
		field.setDecimals(8);
		field.setHeader(session.getString("fieldInstrumentTickValueHeader"));
		field.setLabel(session.getString("fieldInstrumentTickValueLabel"));
		field.setTitle(session.getString("fieldInstrumentTickValueLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentTickScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentTickScale(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentTickScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentTickScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentTickScaleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentVolumeScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentVolumeScale(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentVolumeScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentVolumeScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentVolumeScaleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentPrimaryCurrency</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPrimaryCurrency(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(6);
		field.setHeader(session.getString("fieldInstrumentPrimaryCurrencyHeader"));
		field.setLabel(session.getString("fieldInstrumentPrimaryCurrencyLabel"));
		field.setTitle(session.getString("fieldInstrumentPrimaryCurrencyLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>InstrumentSecondaryCurrency</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentSecondaryCurrency(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(6);
		field.setHeader(session.getString("fieldInstrumentSecondaryCurrencyHeader"));
		field.setLabel(session.getString("fieldInstrumentSecondaryCurrencyLabel"));
		field.setTitle(session.getString("fieldInstrumentSecondaryCurrencyLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>UnitId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getUnitId(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(UnitId);
		field.setType(Types.String);
		field.setLength(2);
		field.setHeader(session.getString("fieldUnitIdHeader"));
		field.setLabel(session.getString("fieldUnitIdLabel"));
		field.setTitle(session.getString("fieldUnitIdLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>UnitName</i></b> the field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getUnitName(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(UnitName);
		field.setType(Types.String);
		field.setLength(15);
		field.setHeader(session.getString("fieldUnitNameHeader"));
		field.setLabel(session.getString("fieldUnitNameLabel"));
		field.setTitle(session.getString("fieldUnitNameLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>PeriodId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodId(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(5);
		field.setHeader(session.getString("fieldPeriodIdHeader"));
		field.setLabel(session.getString("fieldPeriodIdLabel"));
		field.setTitle(session.getString("fieldPeriodIdLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>PeriodName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodName(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.String);
		field.setLength(15);
		field.setHeader(session.getString("fieldPeriodNameHeader"));
		field.setLabel(session.getString("fieldPeriodNameLabel"));
		field.setTitle(session.getString("fieldPeriodNameLabel"));
		field.setMainDescription(true);
		return field;
	}

	/**
	 * Returns the <b><i>PeriodSize</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodSize(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Integer);
		field.setLength(3);
		field.setHeader(session.getString("fieldPeriodSizeHeader"));
		field.setLabel(session.getString("fieldPeriodSizeLabel"));
		field.setTitle(session.getString("fieldPeriodSizeLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>PeriodUnitIndex</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodUnitIndex(Session session, String name) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldPeriodUnitIndexHeader"));
		field.setLabel(session.getString("fieldPeriodUnitIndexLabel"));
		field.setTitle(session.getString("fieldPeriodUnitIndexLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>TableName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getTableName(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(TableName);
		field.setType(Types.String);
		field.setLength(30);
		field.setHeader(session.getString("fieldTableNameHeader"));
		field.setLabel(session.getString("fieldTableNameLabel"));
		field.setTitle(session.getString("fieldTableNameLabel"));
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the <b><i>DataFilter</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getDataFilter(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(DataFilter);
		field.setType(Types.String);
		field.setLength(10);
		field.setHeader(session.getString("fieldDataFilterHeader"));
		field.setLabel(session.getString("fieldDataFilterLabel"));
		field.setTitle(session.getString("fieldDataFilterLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>OfferSide</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getOfferSide(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(OfferSide);
		field.setType(Types.String);
		field.setLength(3);
		field.setHeader(session.getString("fieldOfferSideHeader"));
		field.setLabel(session.getString("fieldOfferSideLabel"));
		field.setTitle(session.getString("fieldOfferSideLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>Time</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getTime(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(Time);
		field.setType(Types.Long);
		field.setHeader(session.getString("fieldOHLCVTimeLabel"));
		field.setLabel(session.getString("fieldOHLCVTimeLabel"));
		field.setTitle(session.getString("fieldOHLCVTimeHeader"));
		return field;
	}

	/**
	 * Returns the <b><i>Open</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getOpen(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(Open);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldOHLCVOpenLabel"));
		field.setLabel(session.getString("fieldOHLCVOpenLabel"));
		field.setTitle(session.getString("fieldOHLCVOpenHeader"));
		return field;
	}

	/**
	 * Returns the <b><i>High</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getHigh(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(High);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldOHLCVHighLabel"));
		field.setLabel(session.getString("fieldOHLCVHighLabel"));
		field.setTitle(session.getString("fieldOHLCVHighHeader"));
		return field;
	}

	/**
	 * Returns the <b><i>Low</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getLow(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(Low);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldOHLCVLowLabel"));
		field.setLabel(session.getString("fieldOHLCVLowLabel"));
		field.setTitle(session.getString("fieldOHLCVLowHeader"));
		return field;
	}

	/**
	 * Returns the <b><i>Close</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getClose(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(Close);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldOHLCVCloseLabel"));
		field.setLabel(session.getString("fieldOHLCVCloseLabel"));
		field.setTitle(session.getString("fieldOHLCVCloseHeader"));
		return field;
	}

	/**
	 * Returns the <b><i>Volume</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @return The field definition.
	 */
	public static Field getVolume(Session session) {
		Field field = new Field();
		field.setSession(session);
		field.setName(Volume);
		field.setType(Types.Double);
		field.setHeader(session.getString("fieldOHLCVVolumeLabel"));
		field.setLabel(session.getString("fieldOHLCVVolumeLabel"));
		field.setTitle(session.getString("fieldOHLCVVolumeHeader"));
		return field;
	}
}
