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
import com.qtplaf.platform.database.Domains;

/**
 * Instrument tick value field.
 *
 * @author Miquel Sas
 */
public class FieldInstrumentTickValue extends Field {

	/**
	 * Constructor.
	 */
	public FieldInstrumentTickValue(Session session, String name) {
		super(Domains.getDouble(session, name, "Tick value", "Instrument tick value"));
	}
}
