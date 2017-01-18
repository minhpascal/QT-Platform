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
import com.qtplaf.library.database.Table;
import com.qtplaf.platform.database.FieldDef;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.Persistors;

/**
 * Servers table definition.
 * 
 * @author Miquel Sas
 */
public class Periods extends Table {
	
	public interface Fields {
		String PeriodId = "period_id";
		String PeriodName = "period_name";
		String PeriodSize = "period_size";
		String PeriodUnitIndex = "period_unit_index";
	}

	/** Table name. */
	public static final String Name = "periods";

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Periods(Session session) {
		super(session);

		setName(Name);

		addField(FieldDef.getPeriodId(session, Fields.PeriodId));
		addField(FieldDef.getPeriodName(session, Fields.PeriodName));
		addField(FieldDef.getPeriodUnitIndex(session, Fields.PeriodUnitIndex));
		addField(FieldDef.getPeriodSize(session, Fields.PeriodSize));
		
		getField(FieldDef.PeriodId).setPrimaryKey(true);
		
		setSchema(Names.getSchema());
		
		setPersistor(Persistors.getPersistor(getSimpleView()));
	}

}
