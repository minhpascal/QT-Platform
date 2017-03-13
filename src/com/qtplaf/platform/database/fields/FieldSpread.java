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
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.platform.database.Domains;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Formatters;
import com.qtplaf.platform.database.configuration.Spread;

/**
 * Spread field on an instrument. Generated by a spread configuration.
 *
 * @author Miquel Sas
 */
public class FieldSpread extends Field {

	/**
	 * Constructor.
	 */
	public FieldSpread(Session session, Instrument instrument, Spread spread, String name) {
		super(Domains.getDouble(session, name));
		String suffix = getSuffix(name);
		setHeader(spread.getHeader() + " " + StringUtils.capitalize(suffix));
		setLabel(spread.getLabel() + " " + StringUtils.capitalize(suffix));
		setFormatter(Formatters.getValueFormatter(session, suffix));
		setProperty(Fields.Properties.Spread, spread);
	}
	
	private String getSuffix(String name) {
		int index = name.lastIndexOf("_");
		return name.substring(index+1);
	}
}