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
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.DataSourceInfo;
import com.qtplaf.library.database.rdbms.adapters.PostgreSQLAdapter;
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
import com.qtplaf.platform.action.ActionStatistics;
import com.qtplaf.platform.action.ActionSynchronizeServerInstruments;
import com.qtplaf.platform.action.ActionTickers;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.tables.DataFilters;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.OfferSides;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Servers;
import com.qtplaf.platform.database.tables.Statistics;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.database.util.PersistorUtils;
import com.qtplaf.platform.database.util.RecordUtils;
import com.qtplaf.platform.database.util.TableUtils;

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
		// System.setOut(frameMenu.getConsole().getPrintStream());
		// System.setErr(frameMenu.getConsole().getPrintStream());

		// RunTickers the menu.
		frameMenu.setVisible(true);

		// Command line argument: database connection (xml file name).
		Argument argConnection = new Argument("dataSourceFile", "Database connection file", true, true, false);
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
			configureDatabase(session, argMngr.getValue("dataSourceFile"));
			logger.info("Database checked");

			// Configure the menu.
			logger.info("Configuring menu...");
			configureMenu(frameMenu.getPanelTreeMenu());

			// RunTickers the menu.
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

		// Data source info and db engine.
		DataSourceInfo info = DataSourceInfo.getDataSourceInfo(cnFile);
		DBEngineAdapter adapter = new PostgreSQLAdapter();
		DBEngine dbEngine = new DBEngine(adapter, info);
		PersistorUtils.setDBEngine(dbEngine);

		// Persistor DDL.
		PersistorDDL ddl = PersistorUtils.getDDL();

		// Check for the system schema.
		if (!ddl.existsSchema(Names.getSchema())) {
			ddl.createSchema(Names.getSchema());
		}

		// Check for supported servers schemas.
		List<Server> servers = ServerFactory.getSupportedServers();
		for (Server server : servers) {
			String schema = Names.getSchema(server);
			if (!ddl.existsSchema(schema)) {
				ddl.createSchema(schema);
			}
		}

		// Check for the necessary table KeyServer in the system schema.
		if (!ddl.existsTable(TableUtils.getTableServers(session))) {
			ddl.buildTable(TableUtils.getTableServers(session));
		}
		synchronizeSupportedServer(session);

		// Check for the necessary table Periods in the system schema.
		if (!ddl.existsTable(Names.getSchema(), Periods.Name)) {
			ddl.buildTable(TableUtils.getTablePeriods(session));
		}
		synchronizeStandardPeriods(session);

		// Check for the necessary table OfferSides in the system schema.
		if (!ddl.existsTable(Names.getSchema(), OfferSides.Name)) {
			ddl.buildTable(TableUtils.getTable_OfferSides(session));
		}
		synchronizeStandardOfferSides(session, dbEngine);

		// Check for the necessary table DataFilters in the system schema.
		if (!ddl.existsTable(Names.getSchema(), DataFilters.Name)) {
			ddl.buildTable(TableUtils.getTableDataFilters(session));
		}
		synchronizeStandardDataFilters(session);

		// Check for the necessary table Instruments in the system schema.
		if (!ddl.existsTable(Names.getSchema(), Instruments.Name)) {
			ddl.buildTable(TableUtils.getTableInstruments(session));
		}

		// Check for the necessary table Tickers in the system schema.
		if (!ddl.existsTable(Names.getSchema(), Tickers.Name)) {
			ddl.buildTable(TableUtils.getTableTickers(session));
		}

		// Check for the necessary table Statistics in the system schema.
		if (!ddl.existsTable(Names.getSchema(), Statistics.Name)) {
			ddl.buildTable(TableUtils.getTableStatistics(session));
		}
	}

	/**
	 * Synchronize standard periods.
	 * 
	 * @param session The working session.
	 * @throws Exception
	 */
	private static void synchronizeStandardPeriods(Session session) throws Exception {
		List<Period> periods = Period.getStandardPeriods();
		Persistor persistor = PersistorUtils.getPersistorPeriods(session);
		for (Period period : periods) {
			Record record = RecordUtils.getRecordPeriod(persistor.getDefaultRecord(), period);
			if (!persistor.exists(record)) {
				persistor.insert(record);
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
		Table table = TableUtils.getTable_OfferSides(session);
		for (OfferSide offerSide : offerSides) {
			Record record = RecordUtils.getRecordOfferSide(table.getDefaultRecord(), offerSide);
			if (!dbEngine.existsRecord(table, record)) {
				dbEngine.executeInsert(table, record);
			}
		}
	}

	/**
	 * Synchronize standard data filters.
	 * 
	 * @param session The working session.
	 * @throws Exception
	 */
	private static void synchronizeStandardDataFilters(Session session) throws Exception {
		Filter[] dataFilters = Filter.values();
		Persistor persistor = PersistorUtils.getPersistorDataFilters(session);
		for (Filter dataFilter : dataFilters) {
			Record record = RecordUtils.getRecordDataFilter(persistor.getDefaultRecord(), dataFilter);
			if (!persistor.exists(record)) {
				persistor.insert(record);
			}
		}
	}

	/**
	 * Synchronize supported servers.
	 * 
	 * @param session The working session.
	 * @throws Exception
	 */
	private static void synchronizeSupportedServer(Session session) throws Exception {
		List<Server> servers = ServerFactory.getSupportedServers();
		Persistor persistor = PersistorUtils.getPersistorServers(session);
		RecordSet recordSet = persistor.select(null);

		// Remove not supported servers.
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			boolean remove = true;
			for (Server server : servers) {
				if (server
					.getId()
					.toLowerCase()
					.equals(record.getValue(Servers.Fields.ServerId).toString().toLowerCase())) {
					remove = false;
					break;
				}
			}
			if (remove) {
				persistor.delete(record);
			}
		}

		// Add add non-existing supported servers.
		for (Server server : servers) {
			String id = server.getId().toLowerCase();
			boolean included = false;
			for (int i = 0; i < recordSet.size(); i++) {
				Record record = recordSet.get(i);
				if (record.getValue(Servers.Fields.ServerId).toString().toLowerCase().equals(id)) {
					included = true;
					break;
				}
			}
			if (!included) {
				persistor.insert(RecordUtils.getRecordServer(persistor.getDefaultRecord(), server));
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

			// KeyServer options.
			TreeMenuItem itemServer = TreeMenuItem.getMenuItem(session, name, title, id);
			itemServer.setLaunchArg(LaunchArgs.KeyServer, server);
			menu.addMenuItem(itemServers, itemServer);

			// Synchronize available instruments
			TreeMenuItem itemSrvSyncInst =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersSynchronizeInstruments"));
			itemSrvSyncInst.setActionClass(ActionSynchronizeServerInstruments.class);
			itemSrvSyncInst.setLaunchArg(LaunchArgs.KeyServer, server);
			menu.addMenuItem(itemServer, itemSrvSyncInst);

			// Available instruments
			TreeMenuItem itemSrvAvInst = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersAvInst"));
			itemSrvAvInst.setActionClass(ActionAvailableInstruments.class);
			itemSrvAvInst.setLaunchArg(LaunchArgs.KeyServer, server);
			menu.addMenuItem(itemServer, itemSrvAvInst);

			// Tickers
			TreeMenuItem itemSrvTickers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickers"));
			menu.addMenuItem(itemServer, itemSrvTickers);

			// Tickers define and download.
			TreeMenuItem itemSrvTickersDef =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickersDefine"));
			itemSrvTickersDef.setActionClass(ActionTickers.class);
			itemSrvTickersDef.setLaunchArg(LaunchArgs.KeyServer, server);
			menu.addMenuItem(itemSrvTickers, itemSrvTickersDef);

			// Tickers statistics.
			TreeMenuItem itemSrvTickersStats =
				TreeMenuItem.getMenuItem(session, session.getString("qtMenuServersTickersStatistics"));
			itemSrvTickersStats.setLaunchArg(LaunchArgs.KeyServer, server);
			itemSrvTickersStats.setActionClass(ActionStatistics.class);
			menu.addMenuItem(itemSrvTickers, itemSrvTickersStats);

		}

		menu.refreshTree();
	}

}
