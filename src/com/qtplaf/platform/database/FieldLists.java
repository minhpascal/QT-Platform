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
	 * Returns the list of fields for the OHLCV table.
	 * 
	 * @param session The working session.
	 * @return The field list.
	 */
	public static FieldList getFieldListOHLCV(Session session) {
		
		FieldList fieldList = new FieldList();
		
		fieldList.addField(FieldDef.getTime(session));
		fieldList.addField(FieldDef.getOpen(session));
		fieldList.addField(FieldDef.getHigh(session));
		fieldList.addField(FieldDef.getLow(session));
		fieldList.addField(FieldDef.getClose(session));
		fieldList.addField(FieldDef.getVolume(session));
		
		fieldList.getField(FieldDef.Time).setPrimaryKey(true);
		
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
		
		fieldList.addField(FieldDef.getServerId(session, FieldDef.ServerId));
		fieldList.addField(FieldDef.getInstrumentId(session, FieldDef.InstrumentId));
		fieldList.addField(FieldDef.getPeriodId(session, FieldDef.PeriodId));
		fieldList.addField(FieldDef.getOfferSide(session));
		fieldList.addField(FieldDef.getDataFilter(session));
		fieldList.addField(FieldDef.getTableName(session));
		
		fieldList.getField(FieldDef.ServerId).setPrimaryKey(true);
		fieldList.getField(FieldDef.InstrumentId).setPrimaryKey(true);
		fieldList.getField(FieldDef.PeriodId).setPrimaryKey(true);
		
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
		
		fieldList.addField(FieldDef.getOfferSide(session));
		fieldList.getField(FieldDef.OfferSide).setPrimaryKey(true);
		
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
		
		fieldList.addField(FieldDef.getDataFilter(session));
		fieldList.getField(FieldDef.DataFilter).setPrimaryKey(true);
		
		return fieldList;
	}
}
