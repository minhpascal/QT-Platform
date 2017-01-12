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
	 * Returns the list of fields for the servers table.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListServers(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(Fields.getFieldServerId(session));
		fieldList.addField(Fields.getFieldServerName(session));
		fieldList.addField(Fields.getFieldServerTitle(session));
		
		fieldList.getField(Fields.ServerId).setPrimaryKey(true);
		
		return fieldList;
	}

	/**
	 * Returns the list of fields for downloaded tickers per broker.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListTickers(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(Fields.getFieldServerId(session));
		fieldList.addField(Fields.getFieldInstrumentId(session));
		fieldList.addField(Fields.getFieldPeriodId(session));
		fieldList.addField(Fields.getFieldOfferSide(session));
		fieldList.addField(Fields.getFieldDataFilter(session));
		fieldList.addField(Fields.getFieldTableName(session));
		
		fieldList.getField(Fields.ServerId).setPrimaryKey(true);
		fieldList.getField(Fields.InstrumentId).setPrimaryKey(true);
		fieldList.getField(Fields.PeriodId).setPrimaryKey(true);
		
		return fieldList;
	}

	/**
	 * Returns the list of fields for an instrument records.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListInstruments(Session session) {
		
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
	
	/**
	 * Returns the list of fields for the period records.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListPeriods(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(Fields.getFieldPeriodId(session));
		fieldList.addField(Fields.getFieldPeriodName(session));
		fieldList.addField(Fields.getFieldPeriodUnitIndex(session));
		fieldList.addField(Fields.getFieldPeriodSize(session));
		fieldList.getField(Fields.PeriodId).setPrimaryKey(true);
		
		return fieldList;
	}
	
	/**
	 * Returns the list of fields for the offer sides records.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListOfferSides(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(Fields.getFieldOfferSide(session));
		fieldList.getField(Fields.OfferSide).setPrimaryKey(true);
		
		return fieldList;
	}
	
	/**
	 * Returns the list of fields for the data filters records.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListDataFilters(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(Fields.getFieldDataFilter(session));
		fieldList.getField(Fields.DataFilter).setPrimaryKey(true);
		
		return fieldList;
	}
}
