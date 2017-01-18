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
import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.rdbms.DBPersistor;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Servers;

/**
 * Centralizes table definitions.
 * 
 * @author Miquel Sas
 */
public class Tables {

	public static final String DataFilters = "data_filters";
	public static final String OfferSides = "offer_sides";
	public static final String Tickers = "tickers";

	/**
	 * Returns the table definition for standard data filters.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableDataFilters(Session session) {

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListDataFilters(session));
		table.setName(DataFilters);
		table.setSchema(Names.getSchema());
		table.setPersistor(new DBPersistor(Persistors.getDBEngine(), table));
		return table;
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

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListOfferSides(session));
		table.setName(OfferSides);
		table.setSchema(Names.getSchema());
		table.setPersistor(new DBPersistor(Persistors.getDBEngine(), table));

		return table;
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
	 * Returns the table definition of downloaded/synchronized tickers. Those tickers by broker downloaded and
	 * synchronized, used in calculations and chart displays. Downloaded tickers are stored in a separated schema by
	 * broker.
	 * 
	 * @param session The working session.
	 * @return The table definition.
	 */
	public static Table getTableTickers(Session session) {

		Table tableTickers = new Table(session);
		tableTickers.addFields(FieldLists.getFieldListTickers(session));
		tableTickers.setName(Tickers);
		tableTickers.setSchema(Names.getSchema());

		Table tablePeriods = getTablePeriods(session);
		ForeignKey fkPeriods = new ForeignKey(false);
		fkPeriods.setLocalTable(tableTickers);
		fkPeriods.setForeignTable(tablePeriods);
		fkPeriods.add(tableTickers.getField(FieldDef.PeriodId), tablePeriods.getField(FieldDef.PeriodId));
		tableTickers.addForeignKey(fkPeriods);

		Table tableOfferSides = getTableOfferSides(session);
		ForeignKey fkOfferSides = new ForeignKey(false);
		fkOfferSides.setLocalTable(tableTickers);
		fkOfferSides.setForeignTable(tableOfferSides);
		fkOfferSides.add(tableTickers.getField(FieldDef.OfferSide), tableOfferSides.getField(FieldDef.OfferSide));
		tableTickers.addForeignKey(fkOfferSides);

		Table tableDataFilters = getTableDataFilters(session);
		ForeignKey fkDataFilters = new ForeignKey(false);
		fkDataFilters.setLocalTable(tableTickers);
		fkDataFilters.setForeignTable(tableDataFilters);
		fkDataFilters.add(tableTickers.getField(FieldDef.DataFilter), tableDataFilters.getField(FieldDef.DataFilter));
		tableTickers.addForeignKey(fkDataFilters);

		tableTickers.setPersistor(
			new DBPersistor(Persistors.getDBEngine(), tableTickers.getComplexView(tableTickers.getPrimaryKey())));

		return tableTickers;
	}

	/**
	 * Returns the OHLCV table for the given server with the given name.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param name The name of the table.
	 * @return The table definition.
	 */
	public static Table getTableOHLCV(Session session, Server server, String name) {

		Table table = new Table(session);
		table.addFields(FieldLists.getFieldListOHLCV(session));
		table.setName(name);
		table.setSchema(Names.getSchema(server));
		table.setPersistor(new DBPersistor(Persistors.getDBEngine(), table));

		return table;

	}
}
