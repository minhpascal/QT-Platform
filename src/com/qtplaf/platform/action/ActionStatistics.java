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
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.database.Lookup;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.StatisticsDefs;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.PeriodUtils;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordSetUtils;

/**
 * Statistics definitions.
 *
 * @author Miquel Sas
 */
public class ActionStatistics extends AbstractAction {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Action to close the frame.
	 */
	class ActionClose extends AbstractAction {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionClose(Session session) {
			super();
			ActionUtils.configureClose(session, this);
			ActionUtils.setDefaultCloseAction(this, true);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionFrame frame = (JOptionFrame) ActionUtils.getUserObject(this);
			frame.setVisible(false);
			frame.dispose();
			
		}
	}

	/**
	 * Action to create a new ticker.
	 */
	class ActionCreate extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionCreate(Session session) {
			super();
			ActionUtils.configureCreate(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record record = Lookup.selectTicker(session, server);
				if (record == null) {
					return;
				}
				
				Instrument instrument = InstrumentUtils.getInstrumentFromRecordTickers(session, record);
				Period period = PeriodUtils.getPeriodFromRecordTickers(record);
				
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Runnable to launch it in a thread.
	 */
	class RunStatistics implements Runnable {
		@Override
		public void run() {

			try {

				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Persistor persistor = PersistorUtils.getPersistorStatistics(session);
				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.SINGLE_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(StatisticsDefs.Fields.InstrumentId);
				tableModelRecord.addColumn(Periods.Fields.PeriodName);
				tableModelRecord.addColumn(StatisticsDefs.Fields.StatisticsId);
				tableModelRecord.addColumn(StatisticsDefs.Fields.TableName);

				tableModelRecord.setRecordSet(RecordSetUtils.getRecordSetStatistics(session, server));
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);
				frame.setTitle(
					server.getName() + " " + session.getString("qtMenuServersTickersStatistics").toLowerCase());
				frame.setComponent(panelTableRecord);

				ActionCreate actionCreate = new ActionCreate(session);
				ActionUtils.setSortIndex(actionCreate, 0);
				frame.addAction(actionCreate);

				frame.addAction(new ActionClose(session));
				frame.setSize(0.6, 0.8);
				frame.showFrame();

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public ActionStatistics() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new RunStatistics()).start();
	}

}
