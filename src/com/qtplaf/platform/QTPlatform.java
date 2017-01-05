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

import java.io.File;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.MetaData;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.adapters.PostgreSQLAdapter;
import com.qtplaf.library.database.rdbms.connection.ConnectionInfo;
import com.qtplaf.library.swing.JFrameMenu;
import com.qtplaf.library.swing.JPanelTreeMenu;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.TreeMenuItem;
import com.qtplaf.library.util.SystemUtils;
import com.qtplaf.library.util.TextServer;

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
	 * main entry.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		// Text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		TextServer.addBaseResource("StringsQTPlatform.xml");
		Session session = new Session(Locale.UK);

		// Frame menu.
		JFrameMenu frameMenu = new JFrameMenu(session);
		frameMenu.setTitle(session.getString("qtMenuTitle"));
		frameMenu.setLocation(20, 20);
		frameMenu.setSize(0.4, 0.8);

		// Re-direct out and err.
		System.setOut(frameMenu.getConsole().getPrintStream());
		System.setErr(frameMenu.getConsole().getPrintStream());

		// Start showing the console.
		frameMenu.showConsole();

		// Show the menu.
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
			configureDatabase(argMngr.getValue("connectionFile"));

			// Configure the menu.
			configureMenu(frameMenu.getPanelTreeMenu());
			

			// Show the menu.
			frameMenu.showTreeMenu();
			
		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

	/**
	 * Ensure the database connection and requires schemas.
	 * 
	 * @param connectionFile The connection file name.
	 * @throws Exception
	 */
	private static void configureDatabase(String connectionFile) throws Exception {

		// Connection file.
		File cnFile = SystemUtils.getFileFromClassPathEntries(connectionFile);
		// Connection info.
		ConnectionInfo cnInfo = ConnectionInfo.getConnectionInfo(cnFile);
		// DBEngine.
		DBEngineAdapter adapter = new PostgreSQLAdapter();
		DBEngine dbEngine = new DBEngine(adapter, cnInfo);
		// Meta data to chekc that the necessary schemas exists.
		MetaData metaData = new MetaData(dbEngine);
		RecordSet rsSchemas = metaData.getRecordSetSchemas();
		
		System.out.println(cnInfo);
	}

	/**
	 * Configure the menu.
	 * 
	 * @param menu The menu.
	 * @throws Exception
	 */
	private static void configureMenu(JPanelTreeMenu menu) throws Exception {

		Session session = menu.getSession();

		// Brokers.
		TreeMenuItem itemBrokers = TreeMenuItem.getMenuItem(session, session.getString("qtMenuBrokers"));
		menu.addMenuItem(itemBrokers);

		menu.refreshTree();
	}

}
