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
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBPersistor;

/**
 * Centralizes table definitions.
 * 
 * @author Miquel Sas
 */
public class Tables {

	public static final String Servers = "servers";
	public static final String Periods = "periods";
	public static final String Tickers = "tickers";

	/**
	 * Returns the table definition for the list of supported servers, located in the system schema.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @return The table definition.
	 */
	public static Table getTableServers(Session session, DBEngine dbEngine) {

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListServer(session));
		table.setName(Servers);
		table.setSchema(Names.getSchema());
		table.setPersistor(new DBPersistor(dbEngine, table));

		return table;
	}

	/**
	 * Returns the table definition of downloaded/synchronized tickers. Those tickers by broker downloaded and
	 * synchronized, used in calculations and chart displays. Downloaded tickers are stored in a separated schema by
	 * broker.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @return The table definition.
	 */
	public static Table getTableTickers(Session session, DBEngine dbEngine) {

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListTicker(session));
		table.setName(Tickers);
		table.setSchema(Names.getSchema());
		table.setPersistor(new DBPersistor(dbEngine, table));

		return table;
	}

	/**
	 * Returns the table definition for standard and user defined periods.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @return The table definition.
	 */
	public static Table getTablePeriods(Session session, DBEngine dbEngine) {

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListPeriod(session));
		table.setName(Periods);
		table.setSchema(Names.getSchema());
		table.setPersistor(new DBPersistor(dbEngine, table));

		return table;
	}
}
