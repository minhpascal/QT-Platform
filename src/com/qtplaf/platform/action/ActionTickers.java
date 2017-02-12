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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.chart.JFrameChart;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.IndicatorUtils;
import com.qtplaf.library.trading.data.Instrument;
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
import com.qtplaf.platform.database.tables.DataPrice;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.task.TaskDownloadTicker;
import com.qtplaf.platform.util.FormUtils;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.PeriodUtils;
import com.qtplaf.platform.util.PersistorUtils;
import com.qtplaf.platform.util.RecordSetUtils;
import com.qtplaf.platform.util.TableUtils;

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
				Record record = FormUtils.getTicker(session, server, instrument);
				if (record == null) {
					return;
				}
				// Create the ticker record.
				Persistor persistor = PersistorUtils.getPersistorTickers(session);
				persistor.insert(record);
				// Create the table.
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				Table table = TableUtils.getTableDataPrice(session, server, tableName);
				PersistorUtils.getDDL().buildTable(table);
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
					PersistorUtils.getPersistorTickers(session).delete(record);
					String tableName = record.getValue(Tickers.Fields.TableName).getString();
					Table table = TableUtils.getTableDataPrice(session, server, tableName);
					PersistorUtils.getDDL().dropTable(table);
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
					Table table = TableUtils.getTableDataPrice(session, server, tableName);
					PersistorUtils.getDDL().dropTable(table);
					PersistorUtils.getDDL().buildTable(table);
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
					Instrument instrument = InstrumentUtils.getInstrumentFromRecordTickers(session, record);
					Period period = PeriodUtils.getPeriodFromRecordTickers(record);
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
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				DataPersistor persistor = new DataPersistor(PersistorUtils.getPersistorDataPrice(session, server, tableName));
				persistor.setSensitive(false);

				String serverId = record.getValue(Tickers.Fields.ServerId).getString();
				String instrId = record.getValue(Tickers.Fields.InstrumentId).getString();
				String periodId = record.getValue(Tickers.Fields.PeriodId).getString();
				Formatters.configureDataPrice(session, persistor, serverId, instrId, periodId);

				Record masterRecord = persistor.getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.SINGLE_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				tableModelRecord.addColumn(DataPrice.Fields.Index);
				tableModelRecord.addColumn(DataPrice.Fields.Time);
				tableModelRecord.addColumn(DataPrice.Fields.TimeFmt);
				tableModelRecord.addColumn(DataPrice.Fields.Open);
				tableModelRecord.addColumn(DataPrice.Fields.High);
				tableModelRecord.addColumn(DataPrice.Fields.Low);
				tableModelRecord.addColumn(DataPrice.Fields.Close);
				tableModelRecord.addColumn(DataPrice.Fields.Volume);

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
			try {
				Session session = ActionUtils.getSession(ActionTickers.this);
				Server server = LaunchArgs.getServer(ActionTickers.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}
				String tableName = record.getValue(Tickers.Fields.TableName).getString();
				Persistor persistor = PersistorUtils.getPersistorDataPrice(session, server, tableName);

				Period period = PeriodUtils.getPeriodFromRecordTickers(record);
				Instrument instrument = InstrumentUtils.getInstrumentFromRecordTickers(session, record);

				// Build the plot data.
				DataInfo infoPrice = new PriceInfo(session, instrument, period);
				PersistorDataList price = new PersistorDataList(session, infoPrice, persistor);
				price.setCacheSize(-1);
				price.setPlotType(PlotType.Candlestick);
				PlotData plotData = new PlotData();
				plotData.add(price);

				// By default in this view add two SMA of 50 and 200 periods.
				IndicatorDataList sma50 =
					IndicatorUtils.getSimpleMovingAverage(price, Data.IndexClose, Color.GRAY, 50);
				IndicatorDataList sma200 =
					IndicatorUtils.getSimpleMovingAverage(price, Data.IndexClose, Color.BLACK, 200);

				 plotData.add(sma50);
				 plotData.add(sma200);

				// Chart title.
				StringBuilder title = new StringBuilder();
				title.append(server.getName());
				title.append(", ");
				title.append(instrument.getId());
				title.append(" ");
				title.append(period);
				title.append(" [");
				title.append(tableName);
				title.append("]");

				// The chart frame.
				JFrameChart frame = new JFrameChart(session);
				frame.setTitle(title.toString());
				frame.getChart().addPlotData(plotData);

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
		try {
			Session session = ActionUtils.getSession(ActionTickers.this);
			Server server = LaunchArgs.getServer(ActionTickers.this);
			Persistor persistor = PersistorUtils.getPersistorTickers(session);
			Record masterRecord = persistor.getDefaultRecord();

			JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
			TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
			tableModelRecord.addColumn(Tickers.Fields.InstrumentId);
			tableModelRecord.addColumn(Periods.Fields.PeriodName);
			tableModelRecord.addColumn(Tickers.Fields.OfferSide);
			tableModelRecord.addColumn(Tickers.Fields.DataFilter);
			tableModelRecord.addColumn(Tickers.Fields.TableName);

			tableModelRecord.setRecordSet(RecordSetUtils.getRecordSetTickers(session, server));
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
