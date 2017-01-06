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
import java.util.Locale;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.JLookupRecords;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.FieldLists;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.RecordSets;

/**
 * Shows the list of available instruments for the server set as launch argument.
 * 
 * @author Miquel Sas
 */
public class ActionAvailableInstruments extends AbstractAction {
	
	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Constructor.
	 */
	public ActionAvailableInstruments() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Session session = ActionUtils.getSession(this);
			Server server = (Server) ActionUtils.getLaunchArgs(this);
			Record masterRecord = FieldLists.getFieldListInstrument(session).getDefaultRecord();
			JLookupRecords lookup = new JLookupRecords(new Session(Locale.UK), masterRecord);
			lookup.addColumn(Fields.InstrumentId);
			lookup.addColumn(Fields.InstrumentDesc);
			lookup.addColumn(Fields.InstrumentPipValue);
			lookup.addColumn(Fields.InstrumentPipScale);
			lookup.lookupRecords(RecordSets.getRecordSetAvailableInstruments(session, server));
		} catch (Exception exc) {
			logger.catching(exc);
		}
	}

}
