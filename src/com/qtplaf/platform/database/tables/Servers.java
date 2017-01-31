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
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.util.DomainUtils;
import com.qtplaf.platform.database.util.PersistorUtils;

/**
 * Servers table definition.
 * 
 * @author Miquel Sas
 */
public class Servers extends Table {

	/** Field names. */
	public interface Fields {
		String ServerId = "server_id";
		String ServerName = "server_name";
		String ServerTitle = "server_title";
	}

	/** Table name. */
	public static final String Name = "servers";

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Servers(Session session) {
		super(session);
		
		setName(Name);
		setSchema(Names.getSchema());
		
		addField(DomainUtils.getServerId(session, Fields.ServerId));
		addField(DomainUtils.getServerName(session, Fields.ServerName));
		addField(DomainUtils.getServerTitle(session, Fields.ServerTitle));
		
		getField(Fields.ServerId).setPrimaryKey(true);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
