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
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldIndex;
import com.qtplaf.platform.database.fields.FieldPatternFamily;
import com.qtplaf.platform.database.fields.FieldPatternId;
import com.qtplaf.platform.database.fields.FieldTime;
import com.qtplaf.platform.database.fields.FieldTimeFmt;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Patterns table, related to the states averages.
 *
 * @author Miquel Sas
 */
public class TablePatterns extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param patterns The states statistics.
	 */
	public TablePatterns(Session session, States patterns) {
		super(session);

		Server server = patterns.getServer();
		Instrument instrument = patterns.getInstrument();
		Period period = patterns.getPeriod();
		String id = patterns.getId().toLowerCase() + "_pt";

		setName(Tables.ticker(instrument, period, id));
		setSchema(Schemas.server(server));

		// Index and time.
		addField(new FieldIndex(getSession(), Fields.Index));
		addField(new FieldTime(getSession(), Fields.Time));

		// Time formatted.
		addField(new FieldTimeFmt(getSession(), Fields.TimeFmt));
		
		// Pattern family and id.
		addField(new FieldPatternFamily(getSession(), Fields.PatternFamily));
		addField(new FieldPatternId(getSession(), Fields.PatternId));

		// Primary key on Time, PatternFamily, PatternId.
		getField(Fields.Time).setPrimaryKey(true);
		getField(Fields.PatternFamily).setPrimaryKey(true);
		getField(Fields.PatternId).setPrimaryKey(true);

		// Unique index on Index, PatternFamily, PatternId.
		Index index = new Index();
		index.add(getField(Fields.Index));
		index.add(getField(Fields.PatternFamily));
		index.add(getField(Fields.PatternId));
		index.setUnique(true);
		addIndex(index);

		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
