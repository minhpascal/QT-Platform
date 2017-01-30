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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.EditMode;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.JFormRecordCustomizer;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.chart.JFrameChart;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.IndicatorUtils;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotType;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.PriceInfo;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.database.Formatters;
import com.qtplaf.platform.database.Lookup;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.Persistors;
import com.qtplaf.platform.database.RecordSets;
import com.qtplaf.platform.database.Records;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.tables.OHLCVS;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.task.TaskDownloadTicker;

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
				Value period = form.getEditField(Tickers.Fields.PeriodId).getValue();
				Record rcPeriod = Records.getRecordPeriod(session, period);
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
				if (Records.getRecordOfferSide(session, offerSide) == null) {
					MessageBox.error(session, MessageFormat.format(mustBeSet, session.getString("qtItemOfferSide")));
					return false;
				}
				// Validate data filter.
				Value dataFilter = form.getEditField(Tickers.Fields.DataFilter).getValue();
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
			String instrument = form.getEditField(Tickers.Fields.InstrumentId).getValue().toString();
			String period = form.getEditField(Tickers.Fields.PeriodId).getValue().toString();
			Value tableName = new Value(Names.getName(instrument, period));
			form.getEditField(Tickers.Fields.TableName).setValue(tableName);
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
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Instrument instrument = Lookup.selectIntrument(session, server);
				if (instrument == null) {
					return;
				}
				Record record = getTicker(session, server, instrument);
				if (record == null) {
					return;
				}
				// Create the ticker record.
				Persistor persistor = Persistors.getPersistorTickers(session);
				persistor.insert(record);
				// Create the table.
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				Table table = Tables.getTableOHLCVS(session, server, tableName);
				Persistors.getDDL().buildTable(table);
				getTableModel().insertRecord(record, persistor.getView().getOrderBy());
				getTableRecord().setSelectedRecord(record);
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Action to delete a ticker (and its data).
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
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				List<Record> records = getSelectedRecords();
				if (records.isEmpty()) {
					return;
				}

				// Ask delete.
				String question = session.getString("qtAskDeleteTickers");
				if (MessageBox.question(session, question, MessageBox.yesNo) != MessageBox.yes) {
					return;
				}

				// Delete records and tables.
				int row = getTableRecord().getSelectedRow();
				for (Record record : records) {
					Persistors.getPersistorTickers(session).delete(record);
					String tableName = record.getValue(Tickers.Fields.TableName).getString();
					Table table = Tables.getTableOHLCVS(session, server, tableName);
					Persistors.getDDL().dropTable(table);
					getTableModel().deleteRecord(record);
				}
				getTableRecord().setSelectedRow(row);

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Action to purge a ticker data.
	 */
	class ActionPurge extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionPurge(Session session) {
			super();
			ActionUtils.configurePurge(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				List<Record> records = getSelectedRecords();
				if (records.isEmpty()) {
					return;
				}

				// Ask delete.
				String question = session.getString("qtAskPurgeTickers");
				if (MessageBox.question(session, question, MessageBox.yesNo) != MessageBox.yes) {
					return;
				}

				// Delete record and table.
				for (Record record : records) {
					String tableName = record.getValue(Tickers.Fields.TableName).getString();
					Table table = Tables.getTableOHLCVS(session, server, tableName);
					Persistors.getDDL().dropTable(table);
					Persistors.getDDL().buildTable(table);
				}

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Action to download a ticker.
	 */
	class ActionDownload extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionDownload(Session session) {
			super();
			ActionUtils.configureDownload(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				List<Record> records = getSelectedRecords();
				if (records.isEmpty()) {
					return;
				}
				ProgressManager progress = new ProgressManager(session);
				progress.setSize(0.4, 0.8);
				for (Record record : records) {
					Value vSERVER_ID = record.getValue(Tickers.Fields.ServerId);
					Value vINSTR_ID = record.getValue(Tickers.Fields.InstrumentId);
					Record recordInstr = Records.getRecordInstrument(session, vSERVER_ID, vINSTR_ID);
					Instrument instrument = Records.fromRecordInstrument(recordInstr);
					Period period = Period.parseId(record.getValue(Tickers.Fields.PeriodId).getString());
					OfferSide offerSide = OfferSide.valueOf(record.getValue(Tickers.Fields.OfferSide).getString());
					Filter filter = Filter.valueOf(record.getValue(Tickers.Fields.DataFilter).getString());

					TaskDownloadTicker task =
						new TaskDownloadTicker(session, server, instrument, period, offerSide, filter);

					task.setName(instrument.getId());
					task.setDescription(period.toString());

					progress.addTask(task);
				}
				progress.showFrame();

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
	 * Action to browse the current ticker.
	 */
	class ActionBrowse extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionBrowse(Session session) {
			super();
			ActionUtils.configureBrowse(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(new RunBrowse(this)).start();
		}
	}

	/**
	 * Action to show the current ticker chart.
	 */
	class ActionChart extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionChart(Session session) {
			super();
			ActionUtils.configureChart(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(new RunChart(this)).start();
		}
	}

	/**
	 * Runnable to launch the browse action it in a thread.
	 */
	class RunBrowse implements Runnable {
		ActionBrowse action;

		RunBrowse(ActionBrowse action) {
			this.action = action;
		}

		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Record record = action.getSelectedRecord();
				if (record == null) {
					return;
				}
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				DataPersistor persistor = new DataPersistor(Persistors.getPersistorOHLCV(session, server, tableName));
				persistor.setSensitive(false);

				String serverId = record.getValue(Tickers.Fields.ServerId).getString();
				String instrId = record.getValue(Tickers.Fields.InstrumentId).getString();
				String periodId = record.getValue(Tickers.Fields.PeriodId).getString();
				Formatters.configureOHLCV(session, persistor, serverId, instrId, periodId);

				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.SINGLE_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(OHLCVS.Fields.Index);
				tableModelRecord.addColumn(OHLCVS.Fields.Time);
				tableModelRecord.addColumn(OHLCVS.Fields.TimeFmt);
				tableModelRecord.addColumn(OHLCVS.Fields.Open);
				tableModelRecord.addColumn(OHLCVS.Fields.High);
				tableModelRecord.addColumn(OHLCVS.Fields.Low);
				tableModelRecord.addColumn(OHLCVS.Fields.Close);
				tableModelRecord.addColumn(OHLCVS.Fields.Volume);

				tableModelRecord.setRecordSet(new DataRecordSet(persistor));
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);

				StringBuilder title = new StringBuilder();
				title.append(server.getName());
				title.append(", ");
				title.append(instrId);
				title.append(" ");
				title.append(Period.parseId(periodId));
				title.append(" [");
				title.append(tableName);
				title.append("]");
				frame.setTitle(title.toString());

				frame.setComponent(panelTableRecord);

				frame.addAction(new ActionClose(session));
				frame.setSize(0.6, 0.8);
				frame.showFrame();

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Runnable to launch the chart action it in a thread.
	 */
	class RunChart implements Runnable {
		ActionChart action;

		RunChart(ActionChart action) {
			this.action = action;
		}

		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Record record = action.getSelectedRecord();
				if (record == null) {
					return;
				}
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				Persistor persistor = Persistors.getPersistorOHLCV(session, server, tableName);

				String serverId = record.getValue(Tickers.Fields.ServerId).getString();
				String instrId = record.getValue(Tickers.Fields.InstrumentId).getString();
				String periodId = record.getValue(Tickers.Fields.PeriodId).getString();

				Period period = Period.parseId(periodId);
				Record recordInstr = Records.getRecordInstrument(session, serverId, instrId);
				Instrument instrument = Records.fromRecordInstrument(recordInstr);

				// Build the plot data.
				DataInfo infoPrice = new PriceInfo(session, instrument, period);
				PersistorDataList price = new PersistorDataList(session, infoPrice, persistor);
				price.setCacheSize(-1);
				price.setPlotType(PlotType.Candlestick);
				price.initializePlotProperties();
				PlotData plotData = new PlotData();
				plotData.add(price);

				// By default in this view add two SMA of 50 and 200 periods.
				IndicatorDataList sma50 =
					IndicatorUtils.getSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 50);
				IndicatorDataList sma200 =
					IndicatorUtils.getSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.BLACK, 200);

				 plotData.add(sma50);
				 plotData.add(sma200);

//				IndicatorDataList sma5 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 5, 3, 3);
//				IndicatorDataList sma21 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 21, 5, 5);
//				IndicatorDataList sma89 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 89, 13, 13);
//				IndicatorDataList sma377 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 377, 21, 21);
//				IndicatorDataList sma1597 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 1597, 34, 34);
//				IndicatorDataList sma6765 =
//					IndicatorUtils
//						.getSmoothedSimpleMovingAverage(price, OHLCV.Index.Close.getIndex(), Color.GRAY, 6765, 55, 55);

//				plotData.add(sma5);
//				plotData.add(sma21);
//				plotData.add(sma89);
//				plotData.add(sma377);
//				plotData.add(sma1597);
//				plotData.add(sma6765);

				// Chart title.
				StringBuilder title = new StringBuilder();
				title.append(server.getName());
				title.append(", ");
				title.append(instrId);
				title.append(" ");
				title.append(Period.parseId(periodId));
				title.append(" [");
				title.append(tableName);
				title.append("]");

				// The chart frame.
				JFrameChart frame = new JFrameChart(session);
				frame.setTitle(title.toString());
				frame.getChart().getPlotParameters().setChartCrossCursorWidth(-1);
				frame.getChart().getPlotParameters().setChartCrossCursorHeight(-1);
				frame.getChart().getPlotParameters().setChartCrossCursorCircleRadius(-1);
				frame.getChart().getPlotParameters().setChartCrossCursorStroke(new BasicStroke());
				frame.getChart().addPlotData(plotData);

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Runnable to launch it in a thread.
	 */
	class RunTickers implements Runnable {
		@Override
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Persistor persistor = Persistors.getPersistorTickers(session);
				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(Tickers.Fields.InstrumentId);
				tableModelRecord.addColumn(Periods.Fields.PeriodName);
				tableModelRecord.addColumn(Tickers.Fields.OfferSide);
				tableModelRecord.addColumn(Tickers.Fields.DataFilter);
				tableModelRecord.addColumn(Tickers.Fields.TableName);

				tableModelRecord.setRecordSet(RecordSets.getRecordSetTickers(session, server));
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);
				frame.setTitle(server.getName() + " " + session.getString("qtMenuServersTickers").toLowerCase());
				frame.setComponent(panelTableRecord);

				ActionCreate actionCreate = new ActionCreate(session);
				ActionUtils.setSortIndex(actionCreate, 0);
				frame.addAction(actionCreate);

				ActionDelete actionDelete = new ActionDelete(session);
				ActionUtils.setSortIndex(actionDelete, 1);
				frame.addAction(actionDelete);
				frame.addAction(actionCreate);

				ActionBrowse actionBrowse = new ActionBrowse(session);
				ActionUtils.setSortIndex(actionBrowse, 2);
				frame.addAction(actionBrowse);

				ActionChart actionChart = new ActionChart(session);
				ActionUtils.setSortIndex(actionChart, 3);
				frame.addAction(actionChart);

				frame.addAction(new ActionPurge(session));
				frame.addAction(new ActionDownload(session));

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
		new Thread(new RunTickers()).start();
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

		ActionTableName actionTableName = new ActionTableName(form);
		form.getEditField(Tickers.Fields.ServerId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.InstrumentId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.PeriodId).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.OfferSide).getEditContext().addValueAction(actionTableName);
		form.getEditField(Tickers.Fields.DataFilter).getEditContext().addValueAction(actionTableName);

		form.setCustomizer(new TickersFormCustomizer());

		if (form.edit()) {
			return form.getRecord();
		}

		return null;
	}
}
