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
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBPersistorDDL;
import com.qtplaf.library.trading.server.Server;

/**
 * Centralizes persistors access.
 * 
 * @author Miquel Sas
 */
public class Persistors {

	/** The database engine used to set the persistor to tables. */
	private static DBEngine dbEngine;

	/**
	 * Sets the database engine to assign the proper persistor to tables.
	 * 
	 * @param dbEngine The database engine.
	 */
	public static void setDBEngine(DBEngine dbEngine) {
		Persistors.dbEngine = dbEngine;
	}

	/**
	 * Returns the database engine in use.
	 * 
	 * @return The database engine in use.
	 */
	public static DBEngine getDBEngine() {
		return dbEngine;
	}

	
	/**
	 * Returns a suitable DDL.
	 * @return The DDL.
	 */
	public static PersistorDDL getDDL() {
		return new DBPersistorDDL(getDBEngine());
	}

	/**
	 * Returns the OHLCV persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorOHLCV(Session session, Server server, String name) {
		return Tables.getTableOHLCV(session, server, name).getPersistor();
	}

	/**
	 * Returns the instruments persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorInstruments(Session session) {
		return Tables.getTableInstruments(session).getPersistor();
	}

	/**
	 * Returns the periods persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorPeriods(Session session) {
		return Tables.getTablePeriods(session).getPersistor();
	}

	/**
	 * Returns the servers persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorServers(Session session) {
		return Tables.getTableServers(session).getPersistor();
	}

	/**
	 * Returns the tickers persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorTickers(Session session) {
		return Tables.getTableTickers(session).getPersistor();
	}

	/**
	 * Returns the offer side persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorOfferSides(Session session) {
		return Tables.getTableOfferSides(session).getPersistor();
	}

	/**
	 * Returns the data filter side persistor.
	 * 
	 * @param session Working session.
	 * @return The persistor.
	 */
	public static Persistor getPersistorDataFilters(Session session) {
		return Tables.getTableDataFilters(session).getPersistor();
	}
}
