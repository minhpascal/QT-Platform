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

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.ConnectionType;
import com.qtplaf.library.trading.server.Server;

/**
 * Centralizes record sets generation.
 * 
 * @author Miquel Sas
 */
public class RecordSets {

	/**
	 * Returns a record set with the available instruments for the argument server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The record set.
	 * @throws Exception
	 */
	public static RecordSet getRecordSetAvailableInstruments(Session session, Server server) throws Exception {
		
		if (!server.getConnectionManager().isConnected()) {
			server.getConnectionManager().connect("msasc2EU", "C1a2r3l4a5", ConnectionType.Demo);
		}
		
		FieldList fieldList = FieldLists.getFieldListInstrument(session);
		
		// Track max pip and tick scale to set their values decimals.
		int maxPipScale = 0;
		int maxTickScale = 0;
		RecordSet recordSet = new RecordSet(fieldList);
		List<Instrument> instruments = server.getAvailableInstruments();
		for (Instrument instrument : instruments) {
			maxPipScale = Math.max(maxPipScale, instrument.getPipScale());
			maxTickScale = Math.max(maxTickScale, instrument.getPipScale());
			recordSet.add(Records.getRecordInstrument(new Record(fieldList), instrument));
		}
		recordSet.sort();
		
		fieldList.getField(Fields.InstrumentPipValue).setDecimals(maxPipScale);
		fieldList.getField(Fields.InstrumentTickValue).setDecimals(maxTickScale);
		
		return recordSet;
	}

}
