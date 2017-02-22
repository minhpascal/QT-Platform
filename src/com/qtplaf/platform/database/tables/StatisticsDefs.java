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

package com.qtplaf.platform.database.tables;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.ForeignKey;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Table;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.util.FieldUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Statistics table definition.
 *
 * @author Miquel Sas
 */
public class StatisticsDefs extends Table {

	/** Field names. */
	public interface Fields {
		String ServerId = "server_id";
		String InstrumentId = "instr_id";
		String PeriodId = "period_id";
		String StatisticsId = "stats_id";
		String TableName = "table_name";
	}

	/** Table name. */
	public static final String Name = "statistics";

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public StatisticsDefs(Session session) {
		super(session);

		setName(Name);
		setSchema(Names.getSchema());

		addField(FieldUtils.getServerId(session, Fields.ServerId));
		addField(FieldUtils.getInstrumentId(session, Fields.InstrumentId));
		addField(FieldUtils.getPeriodId(session, Fields.PeriodId));
		addField(FieldUtils.getStatisticsId(session, Fields.StatisticsId));
		addField(FieldUtils.getTableName(session, Fields.TableName));
		
		getField(Fields.StatisticsId).addPossibleValues(FieldUtils.getStatisticsIdPossibleValues(session));

		getField(Fields.ServerId).setPrimaryKey(true);
		getField(Fields.InstrumentId).setPrimaryKey(true);
		getField(Fields.PeriodId).setPrimaryKey(true);
		getField(Fields.StatisticsId).setPrimaryKey(true);

		Table tablePeriods = new Periods(session);
		ForeignKey fkPeriods = new ForeignKey(false);
		fkPeriods.setLocalTable(this);
		fkPeriods.setForeignTable(tablePeriods);
		fkPeriods.add(getField(Fields.PeriodId), tablePeriods.getField(Periods.Fields.PeriodId));
		addForeignKey(fkPeriods);
		
		
		Order order = new Order();
		order.add(getField(Fields.ServerId));
		order.add(getField(Fields.InstrumentId));
		order.add(tablePeriods.getField(Periods.Fields.PeriodUnitIndex));
		order.add(tablePeriods.getField(Periods.Fields.PeriodSize));
		order.add(getField(Fields.StatisticsId));
		
		setPersistor(PersistorUtils.getPersistor(getComplexView(order)));
	}

}
