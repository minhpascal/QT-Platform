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

package com.qtplaf.library.swing.core;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.qtplaf.library.app.Session;

/**
 * A panel that holds a <code>JTableRecord</code>.
 * 
 * @author Miquel Sas
 */
public class JPanelTableRecord extends JPanel {

	/**
	 * The listener to handle selection events.
	 */
	class SelectionAdapter implements ListSelectionListener {
		/**
		 * The "Line of lines" message.
		 */
		private String lineOfLinesMessage;

		/**
		 * RunTickers the selected line number and number of lines in the status label.
		 */
		@Override
		public void valueChanged(ListSelectionEvent e) {

			// RunTickers the selected line number and number of lines in the status label.
			if (tableRecord != null) {
				if (lineOfLinesMessage == null) {
					lineOfLinesMessage = getSession().getString("lineOfLines");
				}
				int row = tableRecord.getSelectedRow();
				int count = tableRecord.getRowCount();
				tableRecordStatusPanel
					.getStatusLabel()
					.setText(MessageFormat.format(lineOfLinesMessage, row + 1, count));
			}
		}
	}

	/**
	 * The <code>JTableRecord</code>.
	 */
	private JTableRecord tableRecord;
	/**
	 * The <code>JTableRecordStatusPanel</code>.
	 */
	private JTableRecordStatusPanel tableRecordStatusPanel = new JTableRecordStatusPanel();
	/**
	 * The working session.
	 */
	private Session session;
	/**
	 * A boolean that indicates if the status panel should be included.
	 */
	private boolean includeStatusPanel = true;

	/**
	 * Constructor assigning the table.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 */
	public JPanelTableRecord(JTableRecord tableRecord) {
		super(new GridBagLayout());
		this.session = tableRecord.getSession();
		setTableRecord(tableRecord);
	}

	/**
	 * Constructor assigning the table.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 * @param includeStatusPanel A boolean that indicates if the status panel is to be included.
	 */
	public JPanelTableRecord(JTableRecord tableRecord, boolean includeStatusPanel) {
		super(new GridBagLayout());
		this.session = tableRecord.getSession();
		this.includeStatusPanel = includeStatusPanel;
		setTableRecord(tableRecord);
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the <code>JTableRecord</code>.
	 * 
	 * @return The <code>JTableRecord</code>.
	 */
	public JTableRecord getTableRecord() {
		return tableRecord;
	}

	/**
	 * Set the <code>JTableRecord</code>.
	 * 
	 * @param tableRecord The <code>JTableRecord</code>.
	 */
	public void setTableRecord(JTableRecord tableRecord) {
		this.tableRecord = tableRecord;

		// Set the table listeners.
		SelectionAdapter selectionHandler = new SelectionAdapter();
		this.tableRecord.getSelectionModel().addListSelectionListener(selectionHandler);

		// Install the components in the panel.
		removeAll();
		JScrollPane scrollPane =
			new JScrollPane(
				this.tableRecord,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		super.add(scrollPane, getTableRecordConstraints());
		if (includeStatusPanel) {
			super.add(tableRecordStatusPanel, getStatusPanelConstraints());
		}
	}

	/**
	 * Returns the constraints of the <code>JTableRecord</code>.
	 * 
	 * @return he constraints of the <code>JTableRecord</code>.
	 */
	private GridBagConstraints getTableRecordConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		return constraints;
	}

	/**
	 * Returns the status panel constraints.
	 * 
	 * @return The status panel constraints.
	 */
	private GridBagConstraints getStatusPanelConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(2, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 0;
		return constraints;
	}

}
