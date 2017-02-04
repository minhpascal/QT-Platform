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
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JFormRecordCustomizer;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.trading.server.ServerFactory;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.StatisticsDefs;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.statistics.StatisticsManager;

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
					Value period = form.getEditField(StatisticsDefs.Fields.PeriodId).getValue();
					Record rcPeriod = RecordUtils.getRecordPeriod(session, period);
					form.getRecord().setValue(
						Periods.Fields.PeriodUnitIndex,
						rcPeriod.getValue(Periods.Fields.PeriodUnitIndex));
					form.getRecord().setValue(
						Periods.Fields.PeriodSize,
						rcPeriod.getValue(Periods.Fields.PeriodSize));
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
		public static class ActionTableNameStatistics extends AbstractAction {

			/** List of form edit fields. */
			private JFormRecord form;

			/**
			 * Constructor.
			 * 
			 * @param editFields List of edit fields in the form.
			 */
			public ActionTableNameStatistics(JFormRecord form) {
				super();
				this.form = form;
			}

			/**
			 * Perform the action.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String periodId = form.getEditField(StatisticsDefs.Fields.PeriodId).getValue().toString();
					if (periodId.isEmpty()) {
						return;
					}
					
					String serverId = form.getEditField(StatisticsDefs.Fields.ServerId).getValue().toString();
					String instrId = form.getEditField(StatisticsDefs.Fields.InstrumentId).getValue().toString();
					String statsId = form.getEditField(StatisticsDefs.Fields.StatisticsId).getValue().toString();
 
					Session session = form.getSession();
					Server server = ServerFactory.getServer(serverId);
					Instrument instrument = InstrumentUtils.getInstrument(session, serverId, instrId);
					Period period = Period.parseId(periodId);

					Statistics statistics =
						StatisticsManager.getStatistics(session, server, instrument, period, statsId);
					Value tableName = new Value(statistics.getTable().getName());
					
					form.getRecord().setValue(StatisticsDefs.Fields.TableName, tableName);
					form.getEditField(StatisticsDefs.Fields.TableName).setValue(tableName);
				} catch (Exception exc) {
					logger.catching(exc);
				}
			}
		}

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

		ValueActions.ActionTableNameTickers actionTableName = new ValueActions.ActionTableNameTickers(form);
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
		Record record = persistor.getDefaultRecord();
		record.getValue(StatisticsDefs.Fields.ServerId).setValue(server.getId());
		record.getValue(StatisticsDefs.Fields.InstrumentId).setValue(instrument.getId());
		record.getValue(StatisticsDefs.Fields.PeriodId).setValue(period.getId());
		record.getValue(Periods.Fields.PeriodName).setValue(period.toString());

		JFormRecord form = new JFormRecord(session);
		form.setRecord(record);
		form.setTitle(session.getString("qtActionCreateStatistics"));
		form.setEditMode(EditMode.Insert);
		form.addField(StatisticsDefs.Fields.ServerId);
		form.addField(StatisticsDefs.Fields.InstrumentId);
		form.addField(StatisticsDefs.Fields.PeriodId);
		form.addField(Periods.Fields.PeriodName);
		form.addField(StatisticsDefs.Fields.StatisticsId);
		form.addField(StatisticsDefs.Fields.TableName);

		form.getEditField(StatisticsDefs.Fields.ServerId).setEnabled(false);
		form.getEditField(StatisticsDefs.Fields.InstrumentId).setEnabled(false);
		form.getEditField(StatisticsDefs.Fields.PeriodId).setEnabled(false);
		form.getEditField(StatisticsDefs.Fields.TableName).setEnabled(false);

		ValueActions.ActionTableNameStatistics actionTableName = new ValueActions.ActionTableNameStatistics(form);
		form.getEditField(StatisticsDefs.Fields.InstrumentId).getEditContext().addValueAction(actionTableName);
		form.getEditField(StatisticsDefs.Fields.PeriodId).getEditContext().addValueAction(actionTableName);
		form.getEditField(StatisticsDefs.Fields.StatisticsId).getEditContext().addValueAction(actionTableName);

		form.setCustomizer(new Customizers.StatisticsFormCustomizer(session));

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}

}