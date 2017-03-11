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
import com.qtplaf.platform.database.Names.Fields;
import com.qtplaf.platform.database.Names.Schemas;
import com.qtplaf.platform.database.Names.Tables;
import com.qtplaf.platform.util.FieldUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Offer sides table definition.
 * 
 * @author Miquel Sas
 */
public class OfferSides extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public OfferSides(Session session) {
		super(session);
		
		setName(Tables.OfferSides);
		setSchema(Schemas.qtp);
		
		addField(FieldUtils.getOfferSide(session, Fields.OfferSide));
		
		getField(Fields.OfferSide).setPrimaryKey(true);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
