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

package com.qtplaf.platform.database;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;

/**
 * Centralizes record generation.
 * 
 * @author Miquel Sas
 */
public class Records {

	/**
	 * Returns the filled record for the server.
	 * 
	 * @param record The blank server record.
	 * @param server The server.
	 * @return The record.
	 */
	public static Record getRecordServer(Record record, Server server) {
		record.setValue(Fields.ServerId, server.getId());
		record.setValue(Fields.ServerName, server.getName());
		record.setValue(Fields.ServerTitle, server.getTitle());
		return record;
	}

	/**
	 * Returns the filled record for the instrument.
	 * 
	 * @param record The blank instrument record.
	 * @param instrument The instrument.
	 * @return The record.
	 */
	public static Record getRecordInstrument(Record record, Instrument instrument) {
		record.setValue(Fields.InstrumentId, instrument.getId());
		record.setValue(Fields.InstrumentDesc, instrument.getDescription());
		record.setValue(Fields.InstrumentPipValue, instrument.getPipValue());
		record.setValue(Fields.InstrumentPipScale, instrument.getPipScale());
		record.setValue(Fields.InstrumentTickValue, instrument.getTickValue());
		record.setValue(Fields.InstrumentTickScale, instrument.getTickScale());
		record.setValue(Fields.InstrumentVolumeScale, instrument.getVolumeScale());
		record.setValue(Fields.InstrumentPrimaryCurrency, instrument.getPrimaryCurrency().toString());
		record.setValue(Fields.InstrumentSecondaryCurrency, instrument.getSecondaryCurrency().toString());
		return record;
	}

	/**
	 * Returns the filled record for the period.
	 * 
	 * @param record The blank period record.
	 * @param period The period.
	 * @return The record.
	 */
	public static Record getRecordPeriod(Record record, Period period) {
		record.setValue(Fields.PeriodId, period.getId());
		record.setValue(Fields.PeriodName, period.toString());
		return record;
	}

}
