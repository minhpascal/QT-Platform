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
 * Instruments table definition.
 * 
 * @author Miquel Sas
 */
public class Instruments extends Table {

	/** Field names. */
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
		
		addField(DomainUtils.getServerId(session, Fields.ServerId));
		addField(DomainUtils.getInstrumentId(session, Fields.InstrumentId));
		addField(DomainUtils.getInstrumentDesc(session, Fields.InstrumentDesc));
		addField(DomainUtils.getInstrumentPipValue(session, Fields.InstrumentPipValue));
		addField(DomainUtils.getInstrumentPipScale(session, Fields.InstrumentPipScale));
		addField(DomainUtils.getInstrumentTickValue(session, Fields.InstrumentTickValue));
		addField(DomainUtils.getInstrumentTickScale(session, Fields.InstrumentTickScale));
		addField(DomainUtils.getInstrumentVolumeScale(session, Fields.InstrumentVolumeScale));
		addField(DomainUtils.getInstrumentPrimaryCurrency(session, Fields.InstrumentPrimaryCurrency));
		addField(DomainUtils.getInstrumentSecondaryCurrency(session, Fields.InstrumentSecondaryCurrency));
		
		getField(Fields.ServerId).setPrimaryKey(true);
		getField(Fields.InstrumentId).setPrimaryKey(true);
		
		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
