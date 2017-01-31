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

package com.qtplaf.platform.database.util;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JFormRecordCustomizer;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Tickers;

/**
 * Centralizes form operations.
 *
 * @author Miquel Sas
 */
public class FormUtils {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Form customizers.
	 */
	public static class Customizers {

		/**
		 * Customizer to validate the tickers form.
		 */
		public static class TickersFormCustomizer extends JFormRecordCustomizer {

			private Session session;

			/**
			 * Constructor.
			 * 
			 * @param session Working session.
			 */
			public TickersFormCustomizer(Session session) {
				super();
				this.session = session;
			}

			/**
			 * Validate the form.
			 */
			@Override
			public boolean validateForm(JFormRecord form) {
				try {
					String mustBeSet = session.getString("qtItemMustBeSet");
					// Validate the period.
					Value period = form.getEditField(Tickers.Fields.PeriodId).getValue();
					Record rcPeriod = RecordUtils.getRecordPeriod(session, period);
					if (rcPeriod == null) {
						MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemPeriod")));
						return false;
					}
					form.getRecord().setValue(
						Periods.Fields.PeriodUnitIndex,
						rcPeriod.getValue(Periods.Fields.PeriodUnitIndex));
					form.getRecord().setValue(
						Periods.Fields.PeriodSize,
						rcPeriod.getValue(Periods.Fields.PeriodSize));
					// Validate offer side.
					Value offerSide = form.getEditField(Tickers.Fields.OfferSide).getValue();
					if (RecordUtils.getRecordOfferSide(session, offerSide) == null) {
						MessageBox
							.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemOfferSide")));
						return false;
					}
					// Validate data filter.
					Value dataFilter = form.getEditField(Tickers.Fields.DataFilter).getValue();
					if (RecordUtils.getRecordDataFilter(session, dataFilter) == null) {
						MessageBox
							.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemDataFilter")));
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

	}
	
	/**
	 * Value actions.
	 */
	public static class ValueActions {

		/**
		 * Value action to build the table name as values are set.
		 */
		public static class ActionTableName extends AbstractAction {

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
				String instrument = form.getEditField(Tickers.Fields.InstrumentId).getValue().toString();
				String period = form.getEditField(Tickers.Fields.PeriodId).getValue().toString();
				Value tableName = new Value(Names.getName(instrument, period));
				form.getEditField(Tickers.Fields.TableName).setValue(tableName);
			}
		}
	}

	/**
	 * Returns the record to create a new ticker for the given server and instrument.
	 * 
	 * @param session Working session.
	 * @param server KeyServer.
	 * @param instrument Instrument.
	 * @return The ticker record.
	 */
	public static Record getTicker(Session session, Server server, Instrument instrument) {
		Persistor persistor = PersistorUtils.getPersistorTickers(session);
		Record record = persistor.getDefaultRecord();
		record.getValue(Tickers.Fields.ServerId).setValue(server.getId());
		record.getValue(Tickers.Fields.InstrumentId).setValue(instrument.getId());

		JFormRecord form = new JFormRecord(session);
		form.setRecord(record);
		form.setTitle(session.getString("qtActionCreateTicker"));
		form.setEditMode(EditMode.Insert);
		form.addField(Tickers.Fields.ServerId);
		form.addField(Tickers.Fields.InstrumentId);
		form.addField(Tickers.Fields.PeriodId);
		form.addField(Periods.Fields.PeriodName);
		form.addField(Tickers.Fields.OfferSide);
		form.addField(Tickers.Fields.DataFilter);
		form.addField(Tickers.Fields.TableName);

		form.getEditField(Tickers.Fields.ServerId).setEnabled(false);
		form.getEditField(Tickers.Fields.InstrumentId).setEnabled(false);
		form.getEditField(Tickers.Fields.TableName).setEnabled(false);

		ValueActions.ActionTableName actionTableName = new ValueActions.ActionTableName(form);
		form.getEditField(Tickers.Fields.ServerId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.InstrumentId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.PeriodId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.OfferSide).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.DataFilter).getEditContext().addValueAction(actionTableName);

		form.setCustomizer(new Customizers.TickersFormCustomizer(session));

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}
	
	/**
	 * Returns
	 * @param session
	 * @param server
	 * @param instrument
	 * @param period
	 * @return
	 */
	public static Record getStatistic(Session session, Server server, Instrument instrument, Period period) {
		
		return null;
	}

}
