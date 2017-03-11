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
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.Names.Fields;
import com.qtplaf.platform.util.FieldUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Price table definition.
 * 
 * @author Miquel Sas
 */
public class DataPrice extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param name The table name.
	 */
	public DataPrice(Session session, Server server, String name) {
		super(session);

		setName(name);
		setSchema(Names.Schemas.server(server));

		addField(FieldUtils.getIndex(session, Fields.Index));
		addField(FieldUtils.getTime(session, Fields.Time));
		addField(FieldUtils.getOpen(session, Fields.Open));
		addField(FieldUtils.getHigh(session, Fields.High));
		addField(FieldUtils.getLow(session, Fields.Low));
		addField(FieldUtils.getClose(session, Fields.Close));
		addField(FieldUtils.getVolume(session, Fields.Volume));
		addField(FieldUtils.getTimeFmt(session, Fields.TimeFmt));

		getField(Fields.Time).setPrimaryKey(true);
		
		Index index = new Index();
		index.add(getField(Fields.Index));
		index.setUnique(true);
		addIndex(index);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
