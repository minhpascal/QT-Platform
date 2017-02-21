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

package com.qtplaf.platform.util;

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.StatisticsDefs;
import com.qtplaf.platform.database.tables.Tickers;
import com.qtplaf.platform.ztrash.ReferenceOld;
import com.qtplaf.platform.ztrash.StatisticsManagerOld;

/**
 * Centralizes recordset operations.
 *
 * @author Miquel Sas
 */
public class RecordSetUtils {

	/**
	 * Returns a record set with the available instruments for the argument server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The record set.
	 * @throws Exception
	 */
	public static RecordSet getRecordSetAvailableInstruments(Session session, Server server) throws Exception {

		Persistor persistor = PersistorUtils.getPersistorInstruments(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Instruments.Fields.ServerId), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);

		// Track max pip and tick scale to set their values decimals.
		int maxPipScale = 0;
		int maxTickScale = 0;
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			maxPipScale = Math.max(maxPipScale, record.getValue(Instruments.Fields.InstrumentPipScale).getInteger());
			maxTickScale = Math.max(maxTickScale, record.getValue(Instruments.Fields.InstrumentTickScale).getInteger());
		}
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			record.getValue(Instruments.Fields.InstrumentPipValue).setDecimals(maxPipScale);
			record.getValue(Instruments.Fields.InstrumentTickValue).setDecimals(maxTickScale);
		}

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
		Persistor persistor = PersistorUtils.getPersistorTickers(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(Tickers.Fields.ServerId), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);
		return recordSet;
	}

	/**
	 * Returns the statistics recordset for the given server.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @return The statistics recordset.
	 * @throws Exception
	 */
	public static RecordSet getRecordSetStatistics(Session session, Server server) throws Exception {
		Persistor persistor = PersistorUtils.getPersistorStatistics(session);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(persistor.getField(StatisticsDefs.Fields.ServerId), new Value(server.getId())));
		RecordSet recordSet = persistor.select(criteria);
		return recordSet;
	}

	/**
	 * Returns the recordset of statistics references.
	 * 
	 * @param session The working session.
	 * @return The recordset of statistics references.
	 */
	public static RecordSet getRecordSetStatisticsReferences(Session session) {

		FieldList fields = new FieldList();
		Field fieldId = DomainUtils.getStatisticsId(session, ReferenceOld.Id);
		fieldId.setPrimaryKey(true);
		fields.addField(fieldId);
		fields.addField(DomainUtils.getStatisticsTitle(session, ReferenceOld.Title));
		fields.addField(DomainUtils.getStatisticsDesc(session, ReferenceOld.Description));

		RecordSet rs = new RecordSet(fields);
		List<ReferenceOld> items = StatisticsManagerOld.getReferences(session);
		for (ReferenceOld item : items) {
			Record rc = fields.getDefaultRecord();
			rc.setValue(ReferenceOld.Id, item.getId());
			rc.setValue(ReferenceOld.Title, item.getTitle());
			rc.setValue(ReferenceOld.Description, item.getTitle());
			rs.add(rc);
		}

		return rs;
	}
}
