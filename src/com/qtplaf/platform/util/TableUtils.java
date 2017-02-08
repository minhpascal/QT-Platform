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
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.DataFilters;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.OHLCVS;
import com.qtplaf.platform.database.tables.OfferSides;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Servers;
import com.qtplaf.platform.database.tables.StatisticsDefs;
import com.qtplaf.platform.database.tables.Tickers;

/**
 * Centralizes table operations.
 *
 * @author Miquel Sas
 */
public class TableUtils {

	/**
	 * Returns the table definition for standard data filters.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableDataFilters(Session session) {
		return new DataFilters(session);
	}

	/**
	 * Returns the table definition for available server instruments.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableInstruments(Session session) {
		return new Instruments(session);
	}

	/**
	 * Returns the table definition for standard offer sides.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableOfferSides(Session session) {
		return new OfferSides(session);
	}

	/**
	 * Returns the table definition for standard and user defined periods.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTablePeriods(Session session) {
		return new Periods(session);
	}

	/**
	 * Returns the table definition for the list of supported servers, located in the system schema.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableServers(Session session) {
		return new Servers(session);
	}

	/**
	 * Returns the table definition for the list of statistics, located in the system schema.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableStatistics(Session session) {
		return new StatisticsDefs(session);
	}

	/**
	 * Returns the table definition of downloaded/synchronized tickers. Those tickers by broker downloaded and
	 * synchronized, used in calculations and chart displays. Downloaded tickers are stored in a separated schema by
	 * broker.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableTickers(Session session) {
		return new Tickers(session);
	}

	/**
	 * Returns the OHLCV table for the given server with the given name.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param name The name of the table.
	 * @return The table definition.
	 */
	public static Table getTableOHLCVS(Session session, Server server, String name) {
		return new OHLCVS(session, server, name);
	}

}
