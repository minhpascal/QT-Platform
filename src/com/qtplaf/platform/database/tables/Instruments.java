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
 * Instruments table definition.
 * 
 * @author Miquel Sas
 */
public class Instruments extends Table {
	
	public interface Fields {
		String ServerId = "server_id";
		String InstrumentId = "instr_id";
		String InstrumentDesc = "instr_desc";
		String InstrumentPipValue = "instr_pipv";
		String InstrumentPipScale = "instr_pips";
		String InstrumentTickValue = "instr_tickv";
		String InstrumentTickScale = "instr_ticks";
		String InstrumentVolumeScale = "instr_vols";
		String InstrumentPrimaryCurrency = "instr_currp";
		String InstrumentSecondaryCurrency = "instr_currs";
	}

	/** Table name. */
	public static final String Name = "instruments";

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Instruments(Session session) {
		super(session);

		setName(Name);

		addField(FieldDef.getServerId(session, Fields.ServerId));
		addField(FieldDef.getInstrumentId(session, Fields.InstrumentId));
		addField(FieldDef.getInstrumentDesc(session, Fields.InstrumentDesc));
		addField(FieldDef.getInstrumentPipValue(session, Fields.InstrumentPipValue));
		addField(FieldDef.getInstrumentPipScale(session, Fields.InstrumentPipScale));
		addField(FieldDef.getInstrumentTickValue(session, Fields.InstrumentTickValue));
		addField(FieldDef.getInstrumentTickScale(session, Fields.InstrumentTickScale));
		addField(FieldDef.getInstrumentVolumeScale(session, Fields.InstrumentVolumeScale));
		addField(FieldDef.getInstrumentPrimaryCurrency(session, Fields.InstrumentPrimaryCurrency));
		addField(FieldDef.getInstrumentSecondaryCurrency(session, Fields.InstrumentSecondaryCurrency));
		
		getField(FieldDef.ServerId).setPrimaryKey(true);
		getField(FieldDef.InstrumentId).setPrimaryKey(true);
		
		setSchema(Names.getSchema());
		
		setPersistor(Persistors.getPersistor(getSimpleView()));
	}

}
