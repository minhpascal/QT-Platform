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
import com.qtplaf.library.database.FieldList;

/**
 * Centralizes field lists.
 * 
 * @author Miquel Sas
 */
public class FieldLists {

	/**
	 * Returns the list of fields for downloaded tickers per broker.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListTicker(Session session) {
		FieldList fieldList = new FieldList();
		fieldList.addField(Fields.getFieldBrokerId(session));
		fieldList.addField(Fields.getFieldInstrumentId(session));
		fieldList.addField(Fields.getFieldPeriodSize(session));
		fieldList.addField(Fields.getFieldUnitName(session));
		fieldList.getField(Fields.BrokerId).setPrimaryKey(true);
		fieldList.getField(Fields.InstrumentId).setPrimaryKey(true);
		fieldList.getField(Fields.PeriodSize).setPrimaryKey(true);
		fieldList.getField(Fields.UnitName).setPrimaryKey(true);
		return fieldList;
	}

	/**
	 * Returns the list of fields for an instrument records.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListInstrument(Session session) {
		FieldList fieldList = new FieldList();
		fieldList.addField(Fields.getFieldInstrumentId(session));
		fieldList.addField(Fields.getFieldInstrumentDesc(session));
		fieldList.addField(Fields.getFieldInstrumentPipValue(session));
		fieldList.addField(Fields.getFieldInstrumentPipScale(session));
		fieldList.addField(Fields.getFieldInstrumentTickValue(session));
		fieldList.addField(Fields.getFieldInstrumentTickScale(session));
		fieldList.addField(Fields.getFieldInstrumentVolumeScale(session));
		fieldList.addField(Fields.getFieldInstrumentPrimaryCurrency(session));
		fieldList.addField(Fields.getFieldInstrumentSecondaryCurrency(session));
		fieldList.getField(Fields.InstrumentId).setPrimaryKey(true);
		return fieldList;
	}
}
