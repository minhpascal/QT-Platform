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

package com.qtplaf.platform.statistics.action;

import java.awt.event.ActionEvent;

import javax.swing.ListSelectionModel;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.action.ActionCloseFrame;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelTableRecord;
import com.qtplaf.library.swing.core.JTableRecord;
import com.qtplaf.library.swing.core.TableModelRecord;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.platform.statistics.TickerStatistics;

/**
 * Action to simply browse a recordset generated by the statistics.
 *
 * @author Miquel Sas
 */
public abstract class ActionBrowse extends ActionTickerStatistics {
	
	/** Title suffix. */
	private String titleSuffix;
	
	/**
	 * Constructor.
	 * 
	 * @param statistics The source statistics.
	 * @param titleSuffix The title suffix.
	 */
	public ActionBrowse(TickerStatistics statistics, String titleSuffix) {
		super(statistics);
		ActionUtils.setSmallIcon(this, ImageIconUtils.getImageIcon(Icons.app_16x16_browse));
		this.titleSuffix = titleSuffix;
	}

	/**
	 * Return the recordset to browse.
	 * 
	 * @return The recordset to browse.
	 */
	public abstract RecordSet getRecordSet();


	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		RecordSet recordSet = getRecordSet();
		Record masterRecord = recordSet.getFieldList().getDefaultRecord();

		JTableRecord tableRecord = new JTableRecord(getSession(), ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JPanelTableRecord panelTableRecord = new JPanelTableRecord(tableRecord);
		TableModelRecord tableModelRecord = new TableModelRecord(getSession(), masterRecord);
		for (int i = 0; i < recordSet.getFieldCount(); i++) {
			tableModelRecord.addColumn(recordSet.getField(i).getAlias());
		}
		tableModelRecord.setRecordSet(recordSet);
		tableRecord.setModel(tableModelRecord);

		JOptionFrame frame = new JOptionFrame(getSession());

		StringBuilder title = new StringBuilder();
		title.append(getServer().getName());
		title.append(", ");
		title.append(getInstrument().getId());
		title.append(" ");
		title.append(getPeriod());
		title.append(" [");
		title.append(titleSuffix);
		title.append("]");
		frame.setTitle(title.toString());

		frame.setComponent(panelTableRecord);

		frame.addAction(new ActionCloseFrame(getSession()));
		frame.setSize(0.6, 0.8);
		frame.showFrame();
	}
}
