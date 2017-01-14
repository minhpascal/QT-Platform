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

package com.qtplaf.platform;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.MetaData;
import com.qtplaf.library.database.OrderKey;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.adapters.PostgreSQLAdapter;
import com.qtplaf.library.database.rdbms.connection.ConnectionInfo;
import com.qtplaf.library.swing.FrameMenu;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.core.JPanelTreeMenu;
import com.qtplaf.library.swing.core.TreeMenuItem;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.trading.server.ServerFactory;
import com.qtplaf.library.util.SystemUtils;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.platform.action.ActionAvailableInstruments;
import com.qtplaf.platform.action.ActionSynchronizeServerInstruments;
import com.qtplaf.platform.action.ActionTickers;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.Records;
import com.qtplaf.platform.database.Tables;

/**
 * Main entry of the QT-Platform.
 * 
 * @author Miquel Sas
 */
public class QTPlatform {

	/** Logger configuration. */
	static {
		System.setProperty("log4j.configurationFile", "LoggerQTPlatform.xml");
	}
	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Pre-exit action, disconnect any connectred servers.
	 */
	static class PreExitAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				List<Server> servers = ServerFactory.getSupportedServers();
				for (Server server : servers) {
					if (server.getConnectionManager().isConnected()) {
						server.getConnectionManager().disconnect();
					}
				}
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}

	}

	/**
	 * main entry.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		// Text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		TextServer.addBaseResource("StringsQTPlatform.xml");
		TextServer.addBaseResource("StringsQTPlatformDB.xml");
		Session session = new Session(Locale.UK);

		// Frame menu.
		FrameMenu frameMenu = new FrameMenu(session);
		frameMenu.setTitle(session.getString("qtMenuTitle"));
		frameMenu.setLocation(20, 20);
		frameMenu.setSize(0.4, 0.8);
		frameMenu.setPreExitAction(new PreExitAction());

		// Re-direct out and err.
		System.setOut(frameMenu.getConsole().getPrintStream());
		System.setErr(frameMenu.getConsole().getPrintStream());

		// RunAction the menu.
		frameMenu.setVisible(true);

		// Command line argument: database connection (xml file name).
		Argument argConnection = new Argument("connectionFile", "Database connection file", true, true, false);
		ArgumentManager argMngr = new ArgumentManager(argConnection);
		if (!argMngr.parse(args)) {
			for (String error : argMngr.getErrors()) {
				MessageBox.error(session, error);
			}
			System.exit(1);
		}

		try {

			// Ensure database.
			logger.info("Database checking...");
			configureDatabase(session, argMngr.getValue("connectionFile"));
			logger.info("Database checked");

			// Configure the menu.
			logger.info("Configuring menu...");
			configureMenu(frameMenu.getPanelTreeMenu());

			// RunAction the menu.
			frameMenu.showTreeMenu();

		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

	/**
	 * Ensure the database connection and required schemas.
	 * <ul>
	 * <li>QT-Platform system schema <tt>QTP</tt></li>
	 * <li>One schema for each supported server, for instance <tt>QTP_DKCP</tt></li>
	 * </ul>
	 *
	 * @param session The working session.
	 * @param connectionFile The connection file name.
	 * @throws Exception
	 */
	private static void configureDatabase(Session session, String connectionFile) throws Exception {

		// Connection file.
		File cnFile = SystemUtils.getFileFromClassPathEntries(connectionFile);

		// Connection info and db engine.
		ConnectionInfo cnInfo = ConnectionInfo.getConnectionInfo(cnFile);
		DBEngineAdapter adapter = new PostgreSQLAdapter();
		DBEngine dbEngine = new DBEngine(adapter, cnInfo);
		Tables.setDBEngine(dbEngine);

		// Meta data to check that the necessary schemas exists.
		MetaData metaData = new MetaData(dbEngine);
		RecordSet rsSchemas = metaData.getRecordSetSchemas();

		// Check for the system schema.
		if (!rsSchemas.contains(new OrderKey(new Value(Names.getSchema())))) {
			dbEngine.executeCreateSchema(Names.getSchema());
		}

		// Check for supported servers schemas.
		List<Server> servers = ServerFactory.getSupportedServers();
		for (Server server : servers) {
			String schema = Names.getSchema(server);
			if (!rsSchemas.contains(new OrderKey(new Value(schema)))) {
				dbEngine.executeCreateSchema(schema);
			}
		}

		// Check for necessary system schema tables.
		RecordSet rsSysTables = metaData.getRecordSetTables(Names.getSchema());

		// Check for the necessary table Server in the system schema.
		if (!containsTable(rsSysTables, Tables.Servers)) {
			dbEngine.executeBuildTable(Tables.getTableServers(session));
		}
		synchronizeSupportedServer(session, dbEngine);

		// Check for the necessary table Periods in the system schema.
		if (!containsTable(rsSysTables, Tables.Periods)) {
			dbEngine.executeBuildTable(Tables.getTablePeriods(session));
		}
		synchronizeStandardPeriods(session, dbEngine);

		// Check for the necessary table OfferSides in the system schema.
		if (!containsTable(rsSysTables, Tables.OfferSides)) {
			dbEngine.executeBuildTable(Tables.getTableOfferSides(session));
		}
		synchronizeStandardOfferSides(session, dbEngine);

		// Check for the necessary table DataFilters in the system schema.
		if (!containsTable(rsSysTables, Tables.DataFilters)) {
			dbEngine.executeBuildTable(Tables.getTableDataFilters(session));
		}
		synchronizeStandardDataFilters(session, dbEngine);

		// Check for the necessary table Instruments in the system schema.
		if (!containsTable(rsSysTables, Tables.Instruments)) {
			dbEngine.executeBuildTable(Tables.getTableInstruments(session));
		}

		// Check for the necessary table Tickers in the system schema.
		if (!containsTable(rsSysTables, Tables.Tickers)) {
			dbEngine.executeBuildTable(Tables.getTableTickers(session));
		}
	}

	/**
	 * Check if the meta data recordset of tables contains the table name.
	 * 
	 * @param rs The meta data recordset of tables.
	 * @param tableName The table name.
	 * @return A boolean.
	 */
	private static boolean containsTable(RecordSet rs, String tableName) {
		for (int i = 0; i < rs.size(); i++) {
			Record rc = rs.get(i);
			if (rc.getValue(MetaData.TableName).toString().toLowerCase().equals(tableName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Synchronize standard periods.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @throws Exception
	 */
	private static void synchronizeStandardPeriods(Session session, DBEngine dbEngine) throws Exception {
		List<Period> periods = Period.getStandardPeriods();
		Table table = Tables.getTablePeriods(session);
		for (Period period : periods) {
			Record record = Records.getRecordPeriod(table.getDefaultRecord(), period);
			if (!dbEngine.existsRecord(table, record)) {
				dbEngine.executeInsert(table, record);
			}
		}
	}

	/**
	 * Synchronize standard offer sides.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @throws Exception
	 */
	private static void synchronizeStandardOfferSides(Session session, DBEngine dbEngine) throws Exception {
		OfferSide[] offerSides = OfferSide.values();
		Table table = Tables.getTableOfferSides(session);
		for (OfferSide offerSide : offerSides) {
			Record record = Records.getRecordOfferSide(table.getDefaultRecord(), offerSide);
			if (!dbEngine.existsRecord(table, record)) {
				dbEngine.executeInsert(table, record);
			}
		}
	}

	/**
	 * Synchronize standard data filters.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @throws Exception
	 */
	private static void synchronizeStandardDataFilters(Session session, DBEngine dbEngine) throws Exception {
		Filter[] dataFilters = Filter.values();
		Table table = Tables.getTableDataFilters(session);
		for (Filter dataFilter : dataFilters) {
			Record record = Records.getRecordDataFilter(table.getDefaultRecord(), dataFilter);
			if (!dbEngine.existsRecord(table, record)) {
				dbEngine.executeInsert(table, record);
			}
		}
	}

	/**
	 * Synchronize supported servers.
	 * 
	 * @param session The working session.
	 * @param dbEngine The database engine.
	 * @throws Exception
	 */
	private static void synchronizeSupportedServer(Session session, DBEngine dbEngine) throws Exception {
		List<Server> servers = ServerFactory.getSupportedServers();
		Table table = Tables.getTableServers(session);
		View view = table.getSimpleView(table.getPrimaryKey());
		RecordSet recordSet = dbEngine.executeSelectRecordSet(view);

		// Remove not supported servers.
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			boolean remove = true;
			for (Server server : servers) {
				if (server.getId().toLowerCase().equals(record.getValue(Fields.ServerId).toString().toLowerCase())) {
					remove = false;
					break;
				}
			}
			if (remove) {
				dbEngine.executeDelete(table, record);
			}
		}

		// Add add non-existing supported servers.
		for (Server server : servers) {
			String id = server.getId().toLowerCase();
			boolean included = false;
			for (int i = 0; i < recordSet.size(); i++) {
				Record record = recordSet.get(i);
				if (record.getValue(Fields.ServerId).toString().toLowerCase().equals(id)) {
					included = true;
					break;
				}
			}
			if (!included) {
				dbEngine.executeInsert(table, Records.getRecordServer(table.getDefaultRecord(), server));
			}
		}
	}

	/**
	 * Configure the menu.
	 * 
	 * @param menu The menu.
	 * @throws Exception
	 */
	private static void configureMenu(JPanelTreeMenu menu) throws Exception {

		Session session = menu.getSession();

		// Broker servers.
		TreeMenuItem itemServers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServers"));
		menu.addMenuItem(itemServers);

		// One menu item for each supported server.
		List<Server> servers = ServerFactory.getSupportedServers();
		for (Server server : servers) {
			String name = server.getName();
			String title = server.getTitle();
			String id = server.getId();

			// Server options.
			TreeMenuItem itemServer = TreeMenuItem.getMenuItem(session, name, title, id);
			itemServer.setLaunchArgs(server);
			menu.addMenuItem(itemServers, itemServer);

			// Synchronize available instruments
			TreeMenuItem itemSrvSyncInst =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersSynchronizeInstruments"));
			itemSrvSyncInst.setActionClass(ActionSynchronizeServerInstruments.class);
			itemSrvSyncInst.setLaunchArgs(server);
			menu.addMenuItem(itemServer, itemSrvSyncInst);

			// Available instruments
			TreeMenuItem itemSrvAvInst = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersAvInst"));
			itemSrvAvInst.setActionClass(ActionAvailableInstruments.class);
			itemSrvAvInst.setLaunchArgs(server);
			menu.addMenuItem(itemServer, itemSrvAvInst);

			// Tickers
			TreeMenuItem itemSrvTickers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickers"));
			itemSrvTickers.setActionClass(ActionTickers.class);
			itemSrvTickers.setLaunchArgs(server);
			menu.addMenuItem(itemServer, itemSrvTickers);
		}

		menu.refreshTree();
	}

}
