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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.platform.statistics.Domains;

/**
 * Field definitions.
 *
 * @author Miquel Sas
 */
public class FieldUtils {

	/**
	 * Returns the <b><i>Close</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getClose(Session session, String name) {
		return Domains.getDouble(session, name,	"Close", "Close value");
	}

	/**
	 * Returns the <b><i>DataFilter</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getDataFilter(Session session, String name) {
		return Domains.getString(session, name, 10, "Filter", "Data filter");
	}

	/**
	 * Returns the <b><i>High</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getHigh(Session session, String name) {
		return Domains.getDouble(session, name, "High", "High value");
	}

	/**
	 * Returns the <b><i>Index</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getIndex(Session session, String name) {
		return Domains.getLong(session, name);
	}

	/**
	 * Returns the <b><i>InstrumentDesc</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentDesc(Session session, String name) {
		return Domains.getString(session, name, 120, "Instrument description", "Instrument description");
	}

	/**
	 * Returns the <b><i>InstrumentId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentId(Session session, String name) {
		return Domains.getString(session, name,	20,	"Instrument", "Instrument id");
	}

	/**
	 * Returns the <b><i>InstrumentPipScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPipScale(Session session, String name) {
		return Domains.getInteger(session, name, "Pip scale", "Instrument pip scale");
	}

	/**
	 * Returns the <b><i>InstrumentPipValue</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPipValue(Session session, String name) {
		return Domains.getDouble(session, name, "Pip value", "Instrument pip value");
	}

	/**
	 * Returns the <b><i>InstrumentPrimaryCurrency</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentPrimaryCurrency(Session session, String name) {
		return Domains.getString(session, name, 6, "P-Currency", "Primary currency");
	}

	/**
	 * Returns the <b><i>InstrumentSecondaryCurrency</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentSecondaryCurrency(Session session, String name) {
		return Domains.getString(session, name, 6, "S-Currency", "Secondary currency");
	}

	/**
	 * Returns the <b><i>InstrumentTickScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentTickScale(Session session, String name) {
		return Domains.getInteger(session, name, "Tick scale", "Instrument tick scale");
	}

	/**
	 * Returns the <b><i>InstrumentTickValue</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentTickValue(Session session, String name) {
		return Domains.getDouble(session, name, "Tick value", "Instrument tick value");
	}

	/**
	 * Returns the <b><i>InstrumentVolumeScale</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getInstrumentVolumeScale(Session session, String name) {
		return Domains.getInteger(session, name, "Volume scale", "Instrument volume scale");
	}

	/**
	 * Returns the <b><i>Low</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getLow(Session session, String name) {
		return Domains.getDouble(session, name, "Low", "Low value");
	}

	/**
	 * Returns the <b><i>OfferSide</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getOfferSide(Session session, String name) {
		return Domains.getString(session, name, 3, "Offer side", "Offer side");
	}

	/**
	 * Returns the <b><i>Open</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getOpen(Session session, String name) {
		return Domains.getDouble(session, name, "Open", "Open value");
	}

	/**
	 * Returns the <b><i>PeriodId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodId(Session session, String name) {
		return Domains.getString(session, name, 5, "Period id", "Period id");
	}

	/**
	 * Returns the <b><i>PeriodName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodName(Session session, String name) {
		return Domains.getString(session, name, 15, "Period name", "Period name");
	}

	/**
	 * Returns the <b><i>PeriodSize</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodSize(Session session, String name) {
		return Domains.getInteger(session, name, "Period size", "Period size");
	}

	/**
	 * Returns the <b><i>PeriodUnitIndex</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getPeriodUnitIndex(Session session, String name) {
		return Domains.getInteger(session, name, "Period unit index", "Period unit index");
	}

	/**
	 * Returns the <b><i>ServerId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerId(Session session, String name) {
		return Domains.getString(session, name, 20, "Server id", "Server id");
	}

	/**
	 * Returns the <b><i>ServerName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerName(Session session, String name) {
		return Domains.getString(session, name, 60, "Server name", "Server name");
	}

	/**
	 * Returns the <b><i>ServerTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getServerTitle(Session session, String name) {
		return Domains.getString(session, name, 120, "Server title", "Server title");
	}

	/**
	 * Returns the <b><i>StatisticsId</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getStatisticsId(Session session, String name) {
		return Domains.getString(session, name, 40, "Stats id", "Statistics id");
	}

	/**
	 * Returns the <b><i>StatisticsTitle</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getStatisticsTitle(Session session, String name) {
		return Domains.getString(session, name, 100, "Statistics title", "Statistics title");
	}

	/**
	 * Returns the <b><i>TableName</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name The field name.
	 * @return The field definition.
	 */
	public static Field getTableName(Session session, String name) {
		return Domains.getString(session, name, 30, "Table name", "Table name");
	}

	/**
	 * Returns the <b><i>Time</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getTime(Session session, String name) {
		return Domains.getLong(session, name, "Time", "Time");
	}

	/**
	 * Returns the <b><i>TimeFmt</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getTimeFmt(Session session, String name) {
		Field timeFmt = Domains.getLong(session, name, "Time fmt", "Time fmt");
		timeFmt.setPersistent(false);
		return timeFmt;
	}

	/**
	 * Returns the <b><i>Volume</i></b> field definition.
	 * 
	 * @param session Working session.
	 * @param name Field name.
	 * @return The field definition.
	 */
	public static Field getVolume(Session session, String name) {
		return Domains.getDouble(session, name, "Volume", "Volume");
	}
}
