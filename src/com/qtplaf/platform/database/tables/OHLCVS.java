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
import com.qtplaf.platform.database.util.DomainUtils;
import com.qtplaf.platform.database.util.PersistorUtils;

/**
 * OHLCVS table definition.
 * 
 * @author Miquel Sas
 */
public class OHLCVS extends Table {

	public interface Fields {
		String Index = "index";
		String Time = "time";
		String TimeFmt = "time_fmt";
		String Open = "open";
		String High = "high";
		String Low = "low";
		String Close = "close";
		String Volume = "volume";
	}

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param name The table name.
	 */
	public OHLCVS(Session session, Server server, String name) {
		super(session);

		setName(name);
		setSchema(Names.getSchema(server));

		addField(DomainUtils.getIndex(session, Fields.Index));
		addField(DomainUtils.getTime(session, Fields.Time));
		addField(DomainUtils.getOpen(session, Fields.Open));
		addField(DomainUtils.getHigh(session, Fields.High));
		addField(DomainUtils.getLow(session, Fields.Low));
		addField(DomainUtils.getClose(session, Fields.Close));
		addField(DomainUtils.getVolume(session, Fields.Volume));
		addField(DomainUtils.getTimeFmt(session, Fields.TimeFmt));

		getField(Fields.Time).setPrimaryKey(true);
		
		Index index = new Index();
		index.add(getField(Fields.Index));
		index.setUnique(true);
		addIndex(index);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
