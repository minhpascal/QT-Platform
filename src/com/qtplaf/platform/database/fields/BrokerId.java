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

package com.qtplaf.platform.database.fields;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Types;

/**
 * Broker id field definition.
 * 
 * @author Miquel Sas
 */
public class BrokerId extends Field {

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public BrokerId(Session session) {
		super();
		setSession(session);
		setType(Types.String);
		setLength(15);
		setHeader(session.getString("fieldBrokerIdHeader"));
		setLabel(session.getString("fieldBrokerIdTitle"));
		setTitle(session.getString("fieldBrokerIdTitle"));
	}
}
