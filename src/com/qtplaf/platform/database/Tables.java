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
import com.qtplaf.library.database.Table;

/**
 * Centralizes table definitions.
 * 
 * @author Miquel Sas
 */
public class Tables {
	
	public static final String Broker = "BROKER";
	public static final String Ticker = "TICKER";
	
	/**
	 * Returns the table definition for the list of supported brokers, located in the system schema.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableBroker(Session session) {
		
		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListBroker(session));
		table.setName(Broker);
		table.setSchema(Names.getSchema());
		
		return table;
	}

	/**
	 * Returns the table definition of downloaded/synchronized tickers. Those tickers by broker downloaded and
	 * synchronized, used in calculations and chart displays. Downloaded tickers are stored in a separated schema by
	 * broker.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableTicker(Session session) {
		
		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListTicker(session));
		
		return null;
	}
}
