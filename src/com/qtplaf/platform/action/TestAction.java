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

package com.qtplaf.platform.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.FieldDef;
import com.qtplaf.platform.database.Persistors;

/**
 * Edit the list of server tickers.
 * <ul>
 * <li>Create</li>
 * <li>Remove</li>
 * <li>Download</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class TestAction extends AbstractAction {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Constructor.
	 */
	public TestAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Session session = ActionUtils.getSession(this);
			Server server = (Server) ActionUtils.getLaunchArgs(this);
			String tableName = "eurusd_mn001_aa";
			Persistor persistor = Persistors.getPersistorOHLCV(session, server, tableName);
			Field fTIME = persistor.getField(FieldDef.Time);
			Order order = new Order();
			order.add(fTIME, false);
			Record record = null;
			RecordIterator iter = persistor.iterator(null, order);
			if (iter.hasNext()) {
				record = iter.next();
			}
			iter.close();
			System.out.println(record);
		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

}
