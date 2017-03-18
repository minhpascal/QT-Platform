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
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.statistics.Manager;

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
		 * Customizer to validate the statistics form.
		 */
		public static class StatisticsFormCustomizer extends JFormRecordCustomizer {

			private Session session;

			/**
			 * Constructor.
			 * 
			 * @param session Working session.
			 */
			public StatisticsFormCustomizer(Session session) {
				super();
				this.session = session;
			}

			/**
			 * Validate the form.
			 */
			@Override
			public boolean validateForm(JFormRecord form) {
				try {
					// Validate the period.
					Value period = form.getEditField(Fields.PeriodId).getValue();
					Record rcPeriod = RecordUtils.getRecordPeriod(session, period);
					form.getRecord().setValue(
						Fields.PeriodUnitIndex,
						rcPeriod.getValue(Fields.PeriodUnitIndex));
					form.getRecord().setValue(
						Fields.PeriodSize,
						rcPeriod.getValue(Fields.PeriodSize));
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
					Value period = form.getEditField(Fields.PeriodId).getValue();
					Record rcPeriod = RecordUtils.getRecordPeriod(session, period);
					if (rcPeriod == null) {
						MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemPeriod")));
						return false;
					}
					form.getRecord().setValue(
						Fields.PeriodUnitIndex,
						rcPeriod.getValue(Fields.PeriodUnitIndex));
					form.getRecord().setValue(
						Fields.PeriodSize,
						rcPeriod.getValue(Fields.PeriodSize));
					// Validate offer side.
					Value offerSide = form.getEditField(Fields.OfferSide).getValue();
					if (RecordUtils.getRecordOfferSide(session, offerSide) == null) {
						MessageBox
							.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemOfferSide")));
						return false;
					}
					// Validate data filter.
					Value dataFilter = form.getEditField(Fields.DataFilter).getValue();
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
		public static class ActionTableNameTickers extends AbstractAction {

			/** List of form edit fields. */
			private JFormRecord form;

			/**
			 * Constructor.
			 * 
			 * @param editFields List of edit fields in the form.
			 */
			public ActionTableNameTickers(JFormRecord form) {
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
				Value tableName = new Value(Tables.ticker(instrument, period));
				form.getEditField(Fields.TableName).setValue(tableName);
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
		record.getValue(Fields.ServerId).setValue(server.getId());
		record.getValue(Fields.InstrumentId).setValue(instrument.getId());

		JFormRecord form = new JFormRecord(session);
		form.setRecord(record);
		form.setTitle(session.getString("qtActionCreateTicker"));
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

		ValueActions.ActionTableNameTickers actionTableName = new ValueActions.ActionTableNameTickers(form);
		form.getEditField(Fields.InstrumentId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.PeriodId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.OfferSide).getEditContext().addValueAction(actionTableName);
		form.getEditField(Fields.DataFilter).getEditContext().addValueAction(actionTableName);

		form.setCustomizer(new Customizers.TickersFormCustomizer(session));

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}

	/**
	 * Returns the statistics record.
	 * 
	 * @param session Working session.
	 * @param server KeyServer.
	 * @param instrument Instrument.
	 * @param period Period.
	 * @return The statistics record.
	 */
	public static Record getStatistics(Session session, Server server, Instrument instrument, Period period) {
		Persistor persistor = PersistorUtils.getPersistorStatistics(session);
		Manager manager = new Manager(session);
		persistor.getField(Fields.StatisticsId).setPossibleValues(
			manager.getStatisticsIdPossibleValues(server, instrument, period));

		Record record = persistor.getDefaultRecord();
		record.getValue(Fields.ServerId).setValue(server.getId());
		record.getValue(Fields.InstrumentId).setValue(instrument.getId());
		record.getValue(Fields.PeriodId).setValue(period.getId());
		record.getValue(Fields.PeriodName).setValue(period.toString());

		JFormRecord form = new JFormRecord(session);
		form.setRecord(record);
		form.setTitle(session.getString("qtActionCreateStatistics"));
		form.setEditMode(EditMode.Insert);
		form.addField(Fields.ServerId);
		form.addField(Fields.InstrumentId);
		form.addField(Fields.PeriodId);
		form.addField(Fields.PeriodName);
		form.addField(Fields.StatisticsId);

		form.getEditField(Fields.ServerId).setEnabled(false);
		form.getEditField(Fields.InstrumentId).setEnabled(false);
		form.getEditField(Fields.PeriodId).setEnabled(false);

		form.setCustomizer(new Customizers.StatisticsFormCustomizer(session));

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}

}
