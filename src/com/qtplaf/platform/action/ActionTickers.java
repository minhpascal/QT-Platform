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
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.action.DefaultActionClose;
import com.qtplaf.library.swing.action.DefaultActionCreate;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JLookupRecords;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.FieldLists;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.RecordSets;
import com.qtplaf.platform.database.Records;
import com.qtplaf.platform.database.Tables;

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
public class ActionTickers extends AbstractAction {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Action to create a new ticker.
	 */
	class ActionCreate extends DefaultActionCreate {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionCreate(Session session) {
			super(session);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = (Server) ActionUtils.getLaunchArgs(ActionTickers.this);
				Instrument instrument = selectIntrument(session, server);
				if (instrument == null) {
					return;
				}
				Record record = getTicker(session, server, instrument);
				System.out.println(record);
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Action to close the frame.
	 */
	class ActionClose extends DefaultActionClose {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionClose(Session session) {
			super(session);
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
	 * Runnable to launch it in a thread.
	 */
	class RunAction implements Runnable {
		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = (Server) ActionUtils.getLaunchArgs(ActionTickers.this);
				Persistor persistor = Tables.getTableTickers(session).getPersistor();
				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(Fields.InstrumentId);
				tableModelRecord.addColumn(Fields.PeriodName);
				tableModelRecord.addColumn(Fields.OfferSide);
				tableModelRecord.addColumn(Fields.DataFilter);
				tableModelRecord.addColumn(Fields.TableName);

				tableModelRecord.setRecordSet(RecordSets.getRecordSetTickers(session, server));
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);
				frame.setTitle(server.getName() + " " + session.getString("qtMenuServersTickers").toLowerCase());
				frame.setComponent(panelTableRecord);
				frame.addAction(new ActionCreate(session));
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
	public ActionTickers() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new RunAction()).start();
	}

	/**
	 * Select the instrument.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @return The selected instrument or null.
	 * @throws Exception
	 */
	private Instrument selectIntrument(Session session, Server server) throws Exception {
		RecordSet recordSet = RecordSets.getRecordSetAvailableInstruments(session, server);
		Record masterRecord = FieldLists.getFieldListInstruments(session).getDefaultRecord();
		JLookupRecords lookup = new JLookupRecords(session, masterRecord);
		lookup.setTitle(server.getName() + " " + session.getString("qtMenuServersAvInst").toLowerCase());
		lookup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lookup.addColumn(Fields.InstrumentId);
		lookup.addColumn(Fields.InstrumentDesc);
		lookup.addColumn(Fields.InstrumentPipValue);
		lookup.addColumn(Fields.InstrumentPipScale);
		lookup.addColumn(Fields.InstrumentTickValue);
		lookup.addColumn(Fields.InstrumentTickScale);
		lookup.addColumn(Fields.InstrumentVolumeScale);
		lookup.addColumn(Fields.InstrumentPrimaryCurrency);
		lookup.addColumn(Fields.InstrumentSecondaryCurrency);
		Record selected = lookup.lookupRecord(recordSet);
		return Records.fromRecordInstrument(selected);
	}

	/**
	 * Returns the record to create a new ticker for the given server and instrument.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @param instrument Instrument.
	 * @return The ticker record.
	 */
	private Record getTicker(Session session, Server server, Instrument instrument) {
		Persistor persistor = Tables.getTableTickers(session).getPersistor();
		Record record = persistor.getDefaultRecord();
		record.getValue(Fields.ServerId).setValue(server.getId());
		record.getValue(Fields.InstrumentId).setValue(instrument.getId());
		
		JFormRecord form = new JFormRecord(session);
		form.setRecord(record);
		form.setTitle(session.getString("qtMenuServersTickersCreate"));
		form.setEditMode(EditMode.Insert);
		form.addField(Fields.ServerId);
		form.addField(Fields.InstrumentId);
		form.addField(Fields.PeriodId);
		form.addField(Fields.PeriodName);
		form.addField(Fields.OfferSide);
		form.addField(Fields.DataFilter);
		
		form.getEditField(Fields.ServerId).setEnabled(false);
		form.getEditField(Fields.InstrumentId).setEnabled(false);

		if (form.edit()) {
			return form.getRecord();
		}
		
		return null;
	}
}
