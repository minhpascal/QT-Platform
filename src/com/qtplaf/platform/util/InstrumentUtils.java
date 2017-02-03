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

package com.qtplaf.platform.util;

import java.util.Currency;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.Tickers;

/**
 * Centralizes instrument operations.
 *
 * @author Miquel Sas
 */
public class InstrumentUtils {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * returns the instrument.
	 * 
	 * @param session Working session.
	 * @param serverId Server id.
	 * @param instrumentId Instrument id.
	 * @return The instrument.
	 */
	public static Instrument getInstrument(Session session, String serverId, String instrumentId) {
		try {
			Record recordInstr = RecordUtils.getRecordInstrument(session, serverId, instrumentId);
			return getInstrumentFromRecordInstruments(recordInstr);
		} catch (Exception exc) {
			logger.catching(exc);
		}
		return null;
	}

	/**
	 * Returns the instrument from the tickers record.
	 * 
	 * @param session Working session.
	 * @param record Tickers record.
	 * @return The instrument.
	 */
	public static Instrument getInstrumentFromRecordTickers(Session session, Record record) {
		if (record == null) {
			return null;
		}
		try {
			Value vSERVER_ID = record.getValue(Tickers.Fields.ServerId);
			Value vINSTR_ID = record.getValue(Tickers.Fields.InstrumentId);
			Record recordInstr = RecordUtils.getRecordInstrument(session, vSERVER_ID, vINSTR_ID);
			return getInstrumentFromRecordInstruments(recordInstr);
		} catch (Exception exc) {
			logger.catching(exc);
		}
		return null;
	}

	/**
	 * Returns the instrument definition given the instrument record.
	 * 
	 * @param record The instrument record.
	 * @return The instrument definition.
	 */
	public static Instrument getInstrumentFromRecordInstruments(Record record) {
		if (record == null) {
			return null;
		}
		Instrument instrument = new Instrument();
		instrument.setId(record.getValue(Instruments.Fields.InstrumentId).getString());
		instrument.setDescription(record.getValue(Instruments.Fields.InstrumentDesc).getString());
		instrument.setPipValue(record.getValue(Instruments.Fields.InstrumentPipValue).getDouble());
		instrument.setPipScale(record.getValue(Instruments.Fields.InstrumentPipScale).getInteger());
		instrument.setTickValue(record.getValue(Instruments.Fields.InstrumentTickValue).getDouble());
		instrument.setTickScale(record.getValue(Instruments.Fields.InstrumentTickScale).getInteger());
		instrument.setVolumeScale(record.getValue(Instruments.Fields.InstrumentVolumeScale).getInteger());
		String primaryCurrency = record.getValue(Instruments.Fields.InstrumentPrimaryCurrency).getString();
		instrument.setPrimaryCurrency(Currency.getInstance(primaryCurrency));
		String secondaryCurrency = record.getValue(Instruments.Fields.InstrumentSecondaryCurrency).getString();
		instrument.setSecondaryCurrency(Currency.getInstance(secondaryCurrency));
		return instrument;
	}

}
