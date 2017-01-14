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
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.ServerConnector;

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

		ServerConnector.connect(server);

		FieldList fieldList = FieldLists.getFieldListInstruments(session);

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

	/**
	 * Returns the tickers recordset for the given server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The tickers recordset.
	 * @throws Exception
	 */
	public static RecordSet getRecordSetTickers(Session session, Server server) throws Exception {
		Persistor persistor = Tables.getTableTickers(session).getPersistor();
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Fields.ServerId), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);
		return recordSet;
	}

}
