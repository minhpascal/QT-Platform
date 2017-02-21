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

package com.qtplaf.platform.util;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.platform.ztrash.ReferenceOld;

/**
 * Centralizes master field definitions (domains). These definitions do not include table attributes like primary key.
 * 
 * @author Miquel Sas
 */
public class DomainUtils {

	/**
	 * Returns the <b><i>ServerId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerId(Session session, String name) {
		return getServerId(session, name, name);
	}

	/**
	 * Returns the <b><i>ServerId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getServerId(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
		return getServerName(session, name, name);
	}

	/**
	 * Returns the <b><i>ServerName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getServerName(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
		return getServerName(session, name, name);
	}

	/**
	 * Returns the <b><i>ServerTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getServerTitle(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(120);
		field.setHeader(session.getString("fieldServerTitleHeader"));
		field.setLabel(session.getString("fieldServerTitleLabel"));
		field.setTitle(session.getString("fieldServerTitleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>StatisticsId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getStatisticsId(Session session, String name) {
		return getStatisticsId(session, name, name);
	}

	/**
	 * Returns the <b><i>StatisticsId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getStatisticsId(Session session, String name, String alias) {

		Field field = new Field();
		field.setSession(session);

		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(40);
		field.setHeader(session.getString("fieldStatisticsIdHeader"));
		field.setLabel(session.getString("fieldStatisticsIdLabel"));
		field.setTitle(session.getString("fieldStatisticsIdLabel"));

		return field;
	}

	/**
	 * Returns the list of possible values of the <b><i>StatisticsId</i></b> field.
	 * 
	 * @param session Working session.
	 * @return The list of possible values.
	 */
	public static List<Value> getStatisticsIdPossibleValues(Session session) {
		List<Value> values = new ArrayList<>();
		RecordSet rs = RecordSetUtils.getRecordSetStatisticsReferences(session);
		for (int i = 0; i < rs.size(); i++) {
			Record rc = rs.get(i);
			Value value = new Value(rc.getValue(ReferenceOld.Id).getString());
			value.setLabel(rc.getValue(ReferenceOld.Title).getString());
			values.add(value);
		}
		return values;
	}

	/**
	 * Returns the <b><i>StatisticsTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getStatisticsTitle(Session session, String name) {
		return getStatisticsTitle(session, name, name);
	}

	/**
	 * Returns the <b><i>StatisticsTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getStatisticsTitle(Session session, String name, String alias) {

		Field field = new Field();
		field.setSession(session);

		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(100);
		field.setHeader(session.getString("fieldStatisticsTitleHeader"));
		field.setLabel(session.getString("fieldStatisticsTitleLabel"));
		field.setTitle(session.getString("fieldStatisticsTitleLabel"));

		return field;
	}

	/**
	 * Returns the <b><i>StatisticsDesc</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getStatisticsDesc(Session session, String name) {
		return getStatisticsDesc(session, name, name);
	}

	/**
	 * Returns the <b><i>StatisticsDesc</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getStatisticsDesc(Session session, String name, String alias) {

		Field field = new Field();
		field.setSession(session);

		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(200);
		field.setHeader(session.getString("fieldStatisticsDescHeader"));
		field.setLabel(session.getString("fieldStatisticsDescLabel"));
		field.setTitle(session.getString("fieldStatisticsDescLabel"));

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
		field.setAlias(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentPipScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentPipScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentPipScaleLabel"));
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
		field.setAlias(name);
		field.setType(Types.Decimal);
		field.setLength(16);
		field.setDecimals(8);
		field.setHeader(session.getString("fieldInstrumentPipValueHeader"));
		field.setLabel(session.getString("fieldInstrumentPipValueLabel"));
		field.setTitle(session.getString("fieldInstrumentPipValueLabel"));
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
		return getString(
			session,
			name,
			name,
			6,
			session.getString("fieldInstrumentPrimaryCurrencyHeader"),
			session.getString("fieldInstrumentPrimaryCurrencyLabel"),
			session.getString("fieldInstrumentPrimaryCurrencyLabel"));
	}

	/**
	 * Returns the <b><i>InstrumentSecondaryCurrency</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentSecondaryCurrency(Session session, String name) {
		return getString(
			session,
			name,
			name,
			6,
			session.getString("fieldInstrumentSecondaryCurrencyHeader"),
			session.getString("fieldInstrumentSecondaryCurrencyLabel"),
			session.getString("fieldInstrumentSecondaryCurrencyLabel"));
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
		field.setAlias(name);
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
		field.setAlias(name);
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
		field.setAlias(name);
		field.setType(Types.Integer);
		field.setLength(2);
		field.setHeader(session.getString("fieldInstrumentVolumeScaleHeader"));
		field.setLabel(session.getString("fieldInstrumentVolumeScaleLabel"));
		field.setTitle(session.getString("fieldInstrumentVolumeScaleLabel"));
		return field;
	}

	/**
	 * Returns the <b><i>Low</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getLow(Session session, String name) {
		return getDouble(
			session,
			name,
			name,
			session.getString("fieldDataLowHeader"),
			session.getString("fieldDataLowLabel"),
			session.getString("fieldDataLowLabel"));
	}

	/**
	 * Returns the <b><i>OfferSide</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getOfferSide(Session session, String name) {
		return getString(
			session,
			name,
			name,
			3,
			session.getString("fieldOfferSideHeader"),
			session.getString("fieldOfferSideLabel"),
			session.getString("fieldOfferSideLabel"));
	}

	/**
	 * Returns the <b><i>PeriodId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodId(Session session, String name) {
		return getPeriodId(session, name, name);
	}

	/**
	 * Returns the <b><i>PeriodId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @param alias The field alias.
	 * @return The field definition.
	 */
	public static Field getPeriodId(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
		return getPeriodName(session, name, name);
	}

	/**
	 * Returns the <b><i>PeriodName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @param alias The field alias.
	 * @return The field definition.
	 */
	public static Field getPeriodName(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
		return getPeriodSize(session, name, name);
	}

	/**
	 * Returns the <b><i>PeriodSize</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @param alias The field alias.
	 * @return The field definition.
	 */
	public static Field getPeriodSize(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
		return getPeriodUnitIndex(session, name, name);
	}

	/**
	 * Returns the <b><i>PeriodUnitIndex</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @param alias The field alias.
	 * @return The field definition.
	 */
	public static Field getPeriodUnitIndex(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
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
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getTableName(Session session, String name) {
		return getTableName(session, name, name);
	}

	/**
	 * Returns the <b><i>TableName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @param alias The field alias.
	 * @return The field definition.
	 */
	public static Field getTableName(Session session, String name, String alias) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(30);
		field.setHeader(session.getString("fieldTableNameHeader"));
		field.setLabel(session.getString("fieldTableNameLabel"));
		field.setTitle(session.getString("fieldTableNameLabel"));
		field.setFixedWidth(false);
		return field;
	}

	/**
	 * Returns the <b><i>Close</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getClose(Session session, String name) {
		return getDouble(
			session,
			name,
			name,
			session.getString("fieldDataCloseHeader"),
			session.getString("fieldDataCloseLabel"),
			session.getString("fieldDataCloseLabel"));
	}

	/**
	 * Returns the <b><i>DataFilter</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getDataFilter(Session session, String name) {
		return getString(
			session,
			name,
			name,
			10,
			session.getString("fieldDataFilterHeader"),
			session.getString("fieldDataFilterLabel"),
			session.getString("fieldDataFilterLabel"));
	}

	/**
	 * Returns field definition for a double value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param header The field header.
	 * @param label The field label.
	 * @return The field definition.
	 */
	public static Field getDouble(Session session, String name, String header, String label) {
		return getDouble(session, name, name, header, label, label);
	}

	/**
	 * Returns field definition for a double value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias The field alias.
	 * @param header The header.
	 * @param label The label.
	 * @param title The title.
	 * @return The field definition.
	 */
	public static Field getDouble(
		Session session,
		String name,
		String alias,
		String header,
		String label,
		String title) {

		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.Double);
		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(title);

		return field;
	}

	/**
	 * Returns the <b><i>High</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getHigh(Session session, String name) {
		return getDouble(
			session,
			name,
			name,
			session.getString("fieldDataHighHeader"),
			session.getString("fieldDataHighLabel"),
			session.getString("fieldDataHighLabel"));
	}

	/**
	 * Returns the <b><i>Index</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getIndex(Session session, String name) {
		String header = session.getString("fieldDataIndexLabel");
		String label = session.getString("fieldDataIndexLabel");
		String title = session.getString("fieldDataIndexHeader");
		return getIndex(session, name, name, header, label, title);
	}

	/**
	 * Returns the <b><i>Index</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param header Field header.
	 * @param label Field label.
	 * @return The field definition.
	 */
	public static Field getIndex(Session session, String name, String header, String label) {
		return getIndex(session, name, name, header, label, label);
	}

	/**
	 * Returns the <b><i>Time</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getTime(Session session, String name) {
		return getTime(
			session, 
			name, 
			session.getString("fieldDataTimeHeader"),
			session.getString("fieldDataTimeLabel"));
	}

	/**
	 * Returns the <b><i>Time</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param header Field header.
	 * @param label Field label.
	 * @return The field definition.
	 */
	public static Field getTime(Session session, String name, String header, String label) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(name);
		field.setType(Types.Long);
		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(label);
		return field;
	}

	/**
	 * Returns the <b><i>TimeFmt</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getTimeFmt(Session session, String name) {
		return getTimeFmt(
			session, 
			name, 
			session.getString("fieldDataTimeFmtHeader"),
			session.getString("fieldDataTimeFmtLabel"));
	}

	/**
	 * Returns the <b><i>TimeFmt</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param header Field header.
	 * @param label Field label.
	 * @return The field definition.
	 */
	public static Field getTimeFmt(Session session, String name, String header, String label) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(name);
		field.setType(Types.Long);
		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(label);
		field.setPersistent(false);
		return field;
	}

	/**
	 * Returns the <b><i>Open</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getOpen(Session session, String name) {
		return getDouble(
			session,
			name,
			name,
			session.getString("fieldDataOpenHeader"),
			session.getString("fieldDataOpenLabel"),
			session.getString("fieldDataOpenLabel"));
	}

	/**
	 * Returns the <b><i>Volume</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getVolume(Session session, String name) {
		return getVolume(session, name, name);
	}

	/**
	 * Returns the <b><i>Volume</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static Field getVolume(Session session, String name, String alias) {
		return getDouble(
			session,
			name,
			alias,
			session.getString("fieldDataVolumeHeader"),
			session.getString("fieldDataVolumeLabel"),
			session.getString("fieldDataVolumeLabel"));
	}

	/**
	 * Returns the <b><i>Index</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias Field alias.
	 * @return The field definition.
	 */
	public static
		Field
		getIndex(Session session, String name, String alias, String header, String label, String title) {
		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.Long);
		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(title);
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
		return getString(
			session,
			name,
			name,
			120,
			session.getString("fieldInstrumentDescHeader"),
			session.getString("fieldInstrumentDescLabel"),
			session.getString("fieldInstrumentDescLabel"));
	}

	/**
	 * Returns the <b><i>InstrumentId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentId(Session session, String name) {
		return getString(
			session,
			name,
			name,
			20,
			session.getString("fieldInstrumentIdHeader"),
			session.getString("fieldInstrumentIdLabel"),
			session.getString("fieldInstrumentIdLabel"));
	}

	/**
	 * Returns field definition for a string value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param length Field length.
	 * @param header The field header.
	 * @param label The field label.
	 * @return The field definition.
	 */
	public static Field getString(Session session, String name, int length, String header, String label) {
		return getString(session, name, name, length, header, label, label);
	}


	/**
	 * Returns field definition for an integer value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param header The field header.
	 * @param label The field label.
	 * @return The field definition.
	 */
	public static Field getInteger(Session session, String name, String header, String label) {
		return getInteger(session, name, name, header, label, label);
	}
	
	/**
	 * Returns field definition for a string value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias The field alias.
	 * @param length The field length.
	 * @param header The header.
	 * @param label The label.
	 * @param title The title.
	 */
	public static Field getString(
		Session session,
		String name,
		String alias,
		int length,
		String header,
		String label,
		String title) {

		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.String);
		field.setLength(length);
		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(title);

		return field;
	}


	/**
	 * Returns field definition for an integer value.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @param alias The field alias.
	 * @param header The header.
	 * @param label The label.
	 * @param title The title.
	 */
	public static Field getInteger(
		Session session,
		String name,
		String alias,
		String header,
		String label,
		String title) {

		Field field = new Field();
		field.setSession(session);
		field.setName(name);
		field.setAlias(alias);
		field.setType(Types.Integer);

		field.setHeader(header);
		field.setLabel(label);
		field.setTitle(title);

		return field;
	}
}
