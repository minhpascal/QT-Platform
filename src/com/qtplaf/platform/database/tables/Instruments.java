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
import com.qtplaf.platform.database.Domains;
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
		setSchema(Names.getSchema());
		
		addField(Domains.getServerId(session, Fields.ServerId));
		addField(Domains.getInstrumentId(session, Fields.InstrumentId));
		addField(Domains.getInstrumentDesc(session, Fields.InstrumentDesc));
		addField(Domains.getInstrumentPipValue(session, Fields.InstrumentPipValue));
		addField(Domains.getInstrumentPipScale(session, Fields.InstrumentPipScale));
		addField(Domains.getInstrumentTickValue(session, Fields.InstrumentTickValue));
		addField(Domains.getInstrumentTickScale(session, Fields.InstrumentTickScale));
		addField(Domains.getInstrumentVolumeScale(session, Fields.InstrumentVolumeScale));
		addField(Domains.getInstrumentPrimaryCurrency(session, Fields.InstrumentPrimaryCurrency));
		addField(Domains.getInstrumentSecondaryCurrency(session, Fields.InstrumentSecondaryCurrency));
		
		getField(Fields.ServerId).setPrimaryKey(true);
		getField(Fields.InstrumentId).setPrimaryKey(true);
		
		setPersistor(Persistors.getPersistor(getSimpleView()));
	}

}
