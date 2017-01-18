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

package com.qtplaf.platform.database;

import javax.swing.ListSelectionModel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.swing.core.JLookupRecords;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;

/**
 * Centralizes lookup operations.
 * 
 * @author Miquel Sas
 */
public class Lookup {
	/**
	 * Lookup/select the instrument.
	 * 
	 * @param session Working session.
	 * @param server Server.
	 * @return The selected instrument or null.
	 * @throws Exception
	 */
	public static Instrument selectIntrument(Session session, Server server) throws Exception {
		RecordSet recordSet = RecordSets.getRecordSetAvailableInstruments(session, server);
		Record masterRecord = recordSet.getFieldList().getDefaultRecord();
		JLookupRecords lookup = new JLookupRecords(session, masterRecord);
		lookup.setTitle(server.getName() + " " + session.getString("qtMenuServersAvInst").toLowerCase());
		lookup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lookup.addColumn(FieldDef.InstrumentId);
		lookup.addColumn(FieldDef.InstrumentDesc);
		lookup.addColumn(FieldDef.InstrumentPipValue);
		lookup.addColumn(FieldDef.InstrumentPipScale);
		lookup.addColumn(FieldDef.InstrumentTickValue);
		lookup.addColumn(FieldDef.InstrumentTickScale);
		lookup.addColumn(FieldDef.InstrumentVolumeScale);
		lookup.addColumn(FieldDef.InstrumentPrimaryCurrency);
		lookup.addColumn(FieldDef.InstrumentSecondaryCurrency);
		Record selected = lookup.lookupRecord(recordSet);
		return Records.fromRecordInstrument(selected);
	}

}
