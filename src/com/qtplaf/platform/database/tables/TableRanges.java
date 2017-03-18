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
import com.qtplaf.platform.database.fields.FieldMinMax;
import com.qtplaf.platform.database.fields.FieldName;
import com.qtplaf.platform.database.fields.FieldPeriod;
import com.qtplaf.platform.database.fields.FieldTime;
import com.qtplaf.platform.database.fields.FieldValue;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Ranges table, based on states averages.
 *
 * @author Miquel Sas
 */
public class TableRanges extends Table {
	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param states The states statistics.
	 */
	public TableRanges(Session session, States states) {
		super(session);
		
		Server server = states.getServer();
		Instrument instrument = states.getInstrument();
		Period period = states.getPeriod();
		String id = states.getId().toLowerCase() + "_rn";
		
		setName(Tables.ticker(instrument, period, id));
		setSchema(Schemas.server(server));

		addField(new FieldName(getSession(), Fields.Name));
		addField(new FieldMinMax(getSession(), Fields.MinMax));
		addField(new FieldPeriod(getSession(), Fields.Period));
		addField(new FieldValue(getSession(), Fields.Value));
		addField(new FieldIndex(getSession(), Fields.Index));
		addField(new FieldTime(getSession(), Fields.Time));

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(getField(Fields.Name));
		index.add(getField(Fields.MinMax));
		index.add(getField(Fields.Period));
		index.setUnique(false);
		addIndex(index);

		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
