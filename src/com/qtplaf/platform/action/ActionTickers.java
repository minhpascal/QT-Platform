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
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JFormRecordCustomizer;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Lookup;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.Persistors;
import com.qtplaf.platform.database.RecordSets;
import com.qtplaf.platform.database.Records;

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
	 * Customizer to validate the tickers form.
	 */
	class TickersFormCustomizer extends JFormRecordCustomizer {
		/**
		 * Validate the form.
		 */
		@Override
		public boolean validateForm(JFormRecord form) {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				String mustBeSet = session.getString("qtItemMustBeSet");
				// Validate the period.
				Value period = form.getEditField(Fields.PeriodId).getValue();
				if (Records.getRecordPeriod(session, period) == null) {
					MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemPeriod")));
					return false;
				}
				// Validate offer side.
				Value offerSide = form.getEditField(Fields.OfferSide).getValue();
				if (Records.getRecordOfferSide(session, offerSide) == null) {
					MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemOfferSide")));
					return false;
				}
				// Validate data filter.
				Value dataFilter = form.getEditField(Fields.DataFilter).getValue();
				if (Records.getRecordDataFilter(session, dataFilter) == null) {
					MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemDataFilter")));
					return false;
				}
				// Check that the record does not exists.
				Record record = form.getRecord();
				Persistor persistor = record.getPersistor();
				if (persistor.exists(record)) {
					MessageBox.error(session, "Record already exists");
					return false;
				}
			} catch (PersistorException exc) {
				logger.catching(exc);
			}
			return true;
		}
		
	}

	/**
	 * Value action to build the table name as values are set.
	 */
	class ActionTableName extends AbstractAction {

		/** List of form edit fields. */
		private JFormRecord form;

		/**
		 * Constructor.
		 * 
		 * @param editFields List of edit fields in the form.
		 */
		public ActionTableName(JFormRecord form) {
			super();
			this.form = form;
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String instrument = form.getEditField(Fields.InstrumentId).getValue().toString();
			String period = form.getEditField(Fields.PeriodId).getValue().toString();
			String filter = form.getEditField(Fields.DataFilter).getValue().toString();
			String offerSide = form.getEditField(Fields.OfferSide).getValue().toString();
			Value tableName = new Value(Names.getName(instrument, period, filter, offerSide));
			form.getEditField(Fields.TableName).setValue(tableName);
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
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = (Server) ActionUtils.getLaunchArgs(ActionTickers.this);
				Instrument instrument = Lookup.selectIntrument(session, server);
				if (instrument == null) {
					return;
				}
				Record record = getTicker(session, server, instrument);
				if (record == null) {
					return;
				}
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Action to create a new ticker.
	 */
	class ActionDelete extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionDelete(Session session) {
			super();
			ActionUtils.configureDelete(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
//				Session session = ActionUtils.getSession(ActionTickers.this);
//				Server server = (Server) ActionUtils.getLaunchArgs(ActionTickers.this);
				Record record = getSelectedRecord();
				System.out.println(record);
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

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
	 * Runnable to launch it in a thread.
	 */
	class RunAction implements Runnable {
		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = (Server) ActionUtils.getLaunchArgs(ActionTickers.this);
				Persistor persistor = Persistors.getPersistorTickers(session);
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
				frame.addAction(new ActionDelete(session));
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
	 * Returns the record to create a new ticker for the given server and instrument.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @param instrument Instrument.
	 * @return The ticker record.
	 */
	private Record getTicker(Session session, Server server, Instrument instrument) {
		Persistor persistor = Persistors.getPersistorTickers(session);
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
		form.addField(Fields.TableName);

		form.getEditField(Fields.ServerId).setEnabled(false);
		form.getEditField(Fields.InstrumentId).setEnabled(false);
		form.getEditField(Fields.TableName).setEnabled(false);
		
		ActionTableName actionTableName = new ActionTableName(form);
		form.getEditField(Fields.ServerId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.InstrumentId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.PeriodId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.OfferSide).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.DataFilter).getEditContext().addValueAction(actionTableName);
		
		form.setCustomizer(new TickersFormCustomizer());

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}
}
