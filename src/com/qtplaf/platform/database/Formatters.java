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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.PipValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.database.formatters.VolumeValue;
import com.qtplaf.platform.database.tables.DataPrice;
import com.qtplaf.platform.statistics.StatesSource;
import com.qtplaf.platform.util.InstrumentUtils;
import com.qtplaf.platform.util.RecordUtils;

/**
 * Centralizes formatters intallation into persistors.
 * 
 * @author Miquel Sas
 */
public class Formatters {
	/**
	 * Set formatters to the data price persistor.
	 *
	 * @param session Working session.
	 * @param persistor The persistor.
	 * @param serverId The server id.
	 * @param instrId The instrument id.
	 * @param periodId The period id.
	 * @throws PersistorException
	 */
	public static void configureDataPrice(
		Session session,
		Persistor persistor,
		String serverId,
		String instrId,
		String periodId)
		throws PersistorException {

		// Time based on period.
		Period period = Period.parseId(periodId);
		TimeFmtValue timeFmt = new TimeFmtValue(period.getUnit());
		persistor.getField(DataPrice.Fields.TimeFmt).setFormatter(timeFmt);
		persistor.getField(DataPrice.Fields.TimeFmt).setCalculator(timeFmt);

		Record recordInstr = RecordUtils.getRecordInstrument(session, serverId, instrId);
		Instrument instrument = InstrumentUtils.getInstrumentFromRecordInstruments(recordInstr);
		persistor.getField(DataPrice.Fields.Open).setFormatter(new PipValue(session, instrument));
		persistor.getField(DataPrice.Fields.High).setFormatter(new PipValue(session, instrument));
		persistor.getField(DataPrice.Fields.Low).setFormatter(new PipValue(session, instrument));
		persistor.getField(DataPrice.Fields.Close).setFormatter(new PipValue(session, instrument));
		persistor.getField(DataPrice.Fields.Volume).setFormatter(new VolumeValue(session, instrument));
	}
	/**
	 * Set formatters to the states source persistor.
	 *
	 * @param session Working session.
	 * @param persistor The persistor.
	 * @param serverId The server id.
	 * @param instrId The instrument id.
	 * @param periodId The period id.
	 * @throws PersistorException
	 */
	public static void configureStatesSource(
		Session session,
		Persistor persistor,
		Server server,
		Instrument instrument,
		Period period) {
		TimeFmtValue timeFmt = new TimeFmtValue(period.getUnit());
		persistor.getField(StatesSource.Fields.Open).setFormatter(new PipValue(session, instrument));
		persistor.getField(StatesSource.Fields.High).setFormatter(new PipValue(session, instrument));
		persistor.getField(StatesSource.Fields.Low).setFormatter(new PipValue(session, instrument));
		persistor.getField(StatesSource.Fields.Close).setFormatter(new PipValue(session, instrument));
		persistor.getField(StatesSource.Fields.TimeFmt).setFormatter(timeFmt);
		persistor.getField(StatesSource.Fields.TimeFmt).setCalculator(timeFmt);
		
		Record record = persistor.getDefaultRecord();
		for (int i = 7; i < record.getFieldCount(); i++) {
			persistor.getField(i).setFormatter(new DataValue(session,15));
		}
	}
}
