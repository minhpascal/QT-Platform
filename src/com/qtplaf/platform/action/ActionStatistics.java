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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.ProgressManager;
import com.qtplaf.library.swing.action.ActionTableOption;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.trading.chart.JFrameChart;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.database.Lookup;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.StatisticsDefs;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.util.FormUtils;
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
				Record rcTicker = Lookup.selectTicker(session, server);
				if (rcTicker == null) {
					return;
				}

				Instrument instrument = InstrumentUtils.getInstrumentFromRecordTickers(session, rcTicker);
				Period period = PeriodUtils.getPeriodFromRecordTickers(rcTicker);
				Record rcStats = FormUtils.getStatistics(session, server, instrument, period);
				if (rcStats == null) {
					return;
				}
				String statsId = rcStats.getValue(StatisticsDefs.Fields.StatisticsId).getString();
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
				
				// Create the statistics record.
				Persistor persistor = PersistorUtils.getPersistorStatistics(session);
				persistor.insert(rcStats);
				PersistorUtils.getDDL().buildTable(statistics.getTable());
				getTableModel().insertRecord(rcStats, persistor.getView().getOrderBy());
				getTableRecord().setSelectedRecord(rcStats);

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
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				List<Record> records = getSelectedRecords();
				if (records.isEmpty()) {
					return;
				}

				// Ask delete.
				String question = session.getString("qtAskDeleteStatistic");
				if (MessageBox.question(session, question, MessageBox.yesNo) != MessageBox.yes) {
					return;
				}

				// Delete records and tables.
				String serverId = server.getId();
				int row = getTableRecord().getSelectedRow();
				for (Record record : records) {
					PersistorUtils.getPersistorStatistics(session).delete(record);
					String instrId = record.getValue(StatisticsDefs.Fields.InstrumentId).getString();
					String periodId = record.getValue(StatisticsDefs.Fields.PeriodId).getString();
					String statsId = record.getValue(StatisticsDefs.Fields.StatisticsId).getString();
					Instrument instrument = InstrumentUtils.getInstrument(session, serverId, instrId);
					Period period = Period.parseId(periodId);
					Manager manager = new Manager(session);
					Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
					Table table = statistics.getTable();
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
	 * Action to calculate a statistics.
	 */
	class ActionCalculate extends ActionTableOption {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionCalculate(Session session) {
			super();
			ActionUtils.configureCalculate(session, this);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}
				String instrId = record.getValue(StatisticsDefs.Fields.InstrumentId).getString();
				String periodId = record.getValue(StatisticsDefs.Fields.PeriodId).getString();
				Instrument instrument = InstrumentUtils.getInstrument(session, server.getId(), instrId);
				Period period = Period.parseId(periodId);
				String statsId = record.getValue(StatisticsDefs.Fields.StatisticsId).getString();
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
				if (statistics.getTask() == null) {
					MessageBox.warning(session, "No calculation task defined.");
					return;
				}
				ProgressManager progress = new ProgressManager(session);
				progress.setSize(0.4, 0.4);
				progress.addTask(statistics.getTask());
				progress.showFrame();

			} catch (Exception exc) {
				logger.catching(exc);
			}
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
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}
				String instrId = record.getValue(StatisticsDefs.Fields.InstrumentId).getString();
				String periodId = record.getValue(StatisticsDefs.Fields.PeriodId).getString();
				Instrument instrument = InstrumentUtils.getInstrument(session, server.getId(), instrId);
				Period period = Period.parseId(periodId);
				String statsId = record.getValue(StatisticsDefs.Fields.StatisticsId).getString();
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
				RecordSet recordSet = statistics.getRecordSet();
				if (recordSet == null) {
					MessageBox.warning(session, "No recordset configurated");
					return;
				}

				Record masterRecord = recordSet.getFieldList().getDefaultRecord();

				JTableRecord tableRecord = new JTableRecord(session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
				TableModelRecord tableModelRecord = new TableModelRecord(session, masterRecord);
				for (int i = 0; i < recordSet.getFieldCount(); i++) {
					tableModelRecord.addColumn(recordSet.getField(i).getAlias());
				}

				tableModelRecord.setRecordSet(recordSet);
				tableRecord.setModel(tableModelRecord);

				JOptionFrame frame = new JOptionFrame(session);

				StringBuilder title = new StringBuilder();
				title.append(server.getName());
				title.append(", ");
				title.append(instrId);
				title.append(" ");
				title.append(Period.parseId(periodId));
				title.append(" [");
				title.append(statistics.getTable().getName());
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
				Session session = ActionUtils.getSession(ActionStatistics.this);
				Server server = LaunchArgs.getServer(ActionStatistics.this);
				Record record = getSelectedRecord();
				if (record == null) {
					return;
				}
				String instrId = record.getValue(StatisticsDefs.Fields.InstrumentId).getString();
				String periodId = record.getValue(StatisticsDefs.Fields.PeriodId).getString();
				Instrument instrument = InstrumentUtils.getInstrument(session, server.getId(), instrId);
				Period period = Period.parseId(periodId);
				String statsId = record.getValue(StatisticsDefs.Fields.StatisticsId).getString();
				Manager manager = new Manager(session);
				Statistics statistics = manager.getStatistics(server, instrument, period, statsId);
				List<PlotData> plotDataList = statistics.getPlotDataList();
				if (plotDataList == null || plotDataList.isEmpty()) {
					MessageBox.warning(session, "No plot data defined for the current statistics");
					return;
				}
				
				// Chart title.
				StringBuilder title = new StringBuilder();
				title.append(server.getName());
				title.append(", ");
				title.append(instrument.getId());
				title.append(" ");
				title.append(period);
				title.append(" [");
				title.append(statistics.getTable().getName());
				title.append("]");

				// The chart frame.
				JFrameChart frame = new JFrameChart(session);
				frame.setTitle(title.toString());
				for (PlotData plotData : plotDataList) {
					frame.getChart().addPlotData(plotData);
				}
			
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

			ActionDelete actionDelete = new ActionDelete(session);
			ActionUtils.setSortIndex(actionDelete, 1);
			frame.addAction(actionDelete);

			ActionBrowse actionBrowse = new ActionBrowse(session);
			ActionUtils.setSortIndex(actionBrowse, 2);
			frame.addAction(actionBrowse);
			
			ActionChart actionChart = new ActionChart(session);
			ActionUtils.setSortIndex(actionChart, 3);
			frame.addAction(actionChart);

			ActionCalculate actionCalculate = new ActionCalculate(session);
			ActionUtils.setSortIndex(actionCalculate, 4);
			frame.addAction(actionCalculate);

			frame.addAction(new ActionClose(session));
			frame.setSize(0.6, 0.8);
			frame.showFrame();

		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

}
