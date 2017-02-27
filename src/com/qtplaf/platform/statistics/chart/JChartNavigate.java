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

package com.qtplaf.platform.statistics.chart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.core.JFrameSession;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.swing.event.MouseHandler;
import com.qtplaf.library.swing.event.WindowHandler;
import com.qtplaf.library.trading.chart.JChart;
import com.qtplaf.library.trading.data.PlotData;

/**
 * A frame with a chart and a table record useful to navigate the chart. The table record contains information about
 * indexes or data time and can be used to move the chart there. Necessary to verify that statistic values perform as
 * expected.
 *
 * @author Miquel Sas
 */
public class JChartNavigate extends JFrameSession {

	/**
	 * Indexer interface.
	 */
	public interface Indexer {
		/**
		 * Returns the index given the record.
		 * 
		 * @param record The record.
		 * @return The indexes.
		 */
		int getIndex(Record record);
	}

	/**
	 * Mouse handler to handle double click.
	 */
	class SelectionMouseHandler extends MouseHandler {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (getIndexer() != null) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					Record record = getTableRecord().getSelectedRecord();
					int index = getIndexer().getIndex(record);
					PlotData plotData = getChart().getChartContainer(0).getPlotData();
					plotData.moveTo(index);
					plotData.calculateFrame();
					getChart().propagateFrameChanges(plotData);
				}				
			}
		}
	}

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			dispose();
		}
	}

	/**
	 * The chart panel.
	 */
	private JChart chart;
	/**
	 * The table record panel.
	 */
	private JPanelTableRecord panelTableRecord;
	/**
	 * Indexer to retrieve chart indexes from the recordset.
	 */
	private Indexer indexer;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public JChartNavigate(Session session) {
		super(session);
		getContentPane().setLayout(new GridBagLayout());
		SwingUtils.setSizeAndCenterOnSreen(this, 0.8, 0.8);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(getWidth() * 2 / 3);
		splitPane.setLeftComponent(getChart());
		splitPane.setRightComponent(getTableRecordPanel());

		GridBagConstraints constraintsSplit = new GridBagConstraints();
		constraintsSplit.anchor = GridBagConstraints.NORTH;
		constraintsSplit.fill = GridBagConstraints.BOTH;
		constraintsSplit.gridheight = 1;
		constraintsSplit.gridwidth = 1;
		constraintsSplit.weightx = 1;
		constraintsSplit.weighty = 1;
		constraintsSplit.gridx = 0;
		constraintsSplit.gridy = 1;
		constraintsSplit.insets = new Insets(1, 1, 1, 1);

		getContentPane().add(splitPane, constraintsSplit);

		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Returns the indexer to retrieve chart indexes from the recordset.
	 * 
	 * @return The indexer to retrieve chart indexes from the recordset.
	 */
	public Indexer getIndexer() {
		return indexer;
	}

	/**
	 * Set the indexer to retrieve chart indexes from the recordset.
	 * 
	 * @param indexer The indexer to retrieve chart indexes from the recordset.
	 */
	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	/**
	 * Returns the chart object.
	 * 
	 * @return The chart.
	 */
	public JChart getChart() {
		if (chart == null) {
			chart = new JChart(getSession());
		}
		return chart;
	}

	/**
	 * Returns the <tt>JPanelTableRecord</tt>.
	 * 
	 * @return The <tt>JPanelTableRecord</tt>.
	 */
	public JPanelTableRecord getTableRecordPanel() {
		if (panelTableRecord == null) {
			JTableRecord tableRecord = new JTableRecord(getSession(), ListSelectionModel.SINGLE_SELECTION);
			panelTableRecord = new JPanelTableRecord(tableRecord);
			SwingUtils.installMouseListener(panelTableRecord, new SelectionMouseHandler());
		}
		return panelTableRecord;
	}

	/**
	 * Returns the <tt>JTableRecord</tt>.
	 * 
	 * @return The <tt>JTableRecord</tt>.
	 */
	public JTableRecord getTableRecord() {
		return getTableRecordPanel().getTableRecord();
	}

	/**
	 * Set the recordset to be shown in the right pane.
	 * 
	 * @param recordSet The recordset.
	 */
	public void setRecordSet(RecordSet recordSet) {
		Record masterRecord = recordSet.getFieldList().getDefaultRecord();
		TableModelRecord tableModelRecord = new TableModelRecord(getSession(), masterRecord);
		for (int i = 0; i < recordSet.getFieldCount(); i++) {
			tableModelRecord.addColumn(recordSet.getField(i).getAlias());
		}
		tableModelRecord.setRecordSet(recordSet);
		getTableRecord().setModel(tableModelRecord);
	}
}
