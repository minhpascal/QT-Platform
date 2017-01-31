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

package com.qtplaf.platform.database.util;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.tables.DataFilters;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.database.tables.OHLCVS;
import com.qtplaf.platform.database.tables.OfferSides;
import com.qtplaf.platform.database.tables.Periods;
import com.qtplaf.platform.database.tables.Servers;

/**
 * Centralizes record operations.
 *
 * @author Miquel Sas
 */
public class RecordUtils {

	/**
	 * Returns the filled record for the server.
	 * 
	 * @param record The blank server record.
	 * @param server The server.
	 * @return The record.
	 */
	public static Record getRecordServer(Record record, Server server) {
		record.setValue(Servers.Fields.ServerId, server.getId());
		record.setValue(Servers.Fields.ServerName, server.getName());
		record.setValue(Servers.Fields.ServerTitle, server.getTitle());
		return record;
	}

	/**
	 * Returns the period record from the database, given the period id.
	 * 
	 * @param session The working session.
	 * @param period The period id value.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public static Record getRecordPeriod(Session session, Value period) throws PersistorException {
		Persistor persistor = PersistorUtils.getPersistorPeriods(session);
		return persistor.getRecord(period);
	}

	/**
	 * Returns the filled record for the period.
	 * 
	 * @param record The blank period record.
	 * @param period The period.
	 * @return The record.
	 */
	public static Record getRecordPeriod(Record record, Period period) {
		record.setValue(Periods.Fields.PeriodId, period.getId());
		record.setValue(Periods.Fields.PeriodName, period.toString());
		record.setValue(Periods.Fields.PeriodSize, period.getSize());
		record.setValue(Periods.Fields.PeriodUnitIndex, period.getUnit().ordinal());
		return record;
	}

	/**
	 * Returns the OHLCV record.
	 * 
	 * @param record The default OHLCV record.
	 * @param ohlcv The OHLCV bar.
	 * @return The filled record.
	 */
	public static Record getRecordOHLCV(Record record, OHLCV ohlcv) {
		record.getValue(OHLCVS.Fields.Time).setLong(ohlcv.getTime());
		record.getValue(OHLCVS.Fields.Open).setDouble(ohlcv.getOpen());
		record.getValue(OHLCVS.Fields.High).setDouble(ohlcv.getHigh());
		record.getValue(OHLCVS.Fields.Low).setDouble(ohlcv.getLow());
		record.getValue(OHLCVS.Fields.Close).setDouble(ohlcv.getClose());
		record.getValue(OHLCVS.Fields.Volume).setDouble(ohlcv.getVolume());
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
		record.setValue(Instruments.Fields.InstrumentId, instrument.getId());
		record.setValue(Instruments.Fields.InstrumentDesc, instrument.getDescription());
		record.setValue(Instruments.Fields.InstrumentPipValue, instrument.getPipValue());
		record.setValue(Instruments.Fields.InstrumentPipScale, instrument.getPipScale());
		record.setValue(Instruments.Fields.InstrumentTickValue, instrument.getTickValue());
		record.setValue(Instruments.Fields.InstrumentTickScale, instrument.getTickScale());
		record.setValue(Instruments.Fields.InstrumentVolumeScale, instrument.getVolumeScale());
		record.setValue(Instruments.Fields.InstrumentPrimaryCurrency, instrument.getPrimaryCurrency().toString());
		record.setValue(Instruments.Fields.InstrumentSecondaryCurrency, instrument.getSecondaryCurrency().toString());
		return record;
	}

	/**
	 * Returns the instrument record from the database, given the server and instruments ids.
	 * 
	 * @param session The working session.
	 * @param serverId The server id.
	 * @param instrumentId The instrument id.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public static Record getRecordInstrument(Session session, String serverId, String instrumentId)
		throws PersistorException {
		return getRecordInstrument(session, new Value(serverId), new Value(instrumentId));
	}

	/**
	 * Returns the instrument record from the database, given the server and instruments ids.
	 * 
	 * @param session The working session.
	 * @param server The server id value.
	 * @param instrument The instrument id value.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public static Record getRecordInstrument(Session session, Value server, Value instrument)
		throws PersistorException {
		Persistor persistor = PersistorUtils.getPersistorInstruments(session);
		return persistor.getRecord(server, instrument);
	}

	/**
	 * Returns the filled record for the offer side.
	 * 
	 * @param record The blank offer side record.
	 * @param offerSide The offer side.
	 * @return The record.
	 */
	public static Record getRecordOfferSide(Record record, OfferSide offerSide) {
		record.setValue(OfferSides.Fields.OfferSide, offerSide.name());
		return record;
	}

	/**
	 * Returns the offer side record from the database, given the offer side.
	 * 
	 * @param session The working session.
	 * @param offerSide The period id value.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public static Record getRecordOfferSide(Session session, Value offerSide) throws PersistorException {
		Persistor persistor = PersistorUtils.getPersistorOfferSides(session);
		return persistor.getRecord(offerSide);
	}

	/**
	 * Returns the filled record for the data filter.
	 * 
	 * @param record The blank offer side record.
	 * @param dataFilter The data filter.
	 * @return The record.
	 */
	public static Record getRecordDataFilter(Record record, Filter dataFilter) {
		record.setValue(DataFilters.Fields.DataFilter, dataFilter.name());
		return record;
	}

	/**
	 * Returns the data filter record from the database, given the filter.
	 * 
	 * @param session The working session.
	 * @param dataFilter The period id value.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public static Record getRecordDataFilter(Session session, Value dataFilter) throws PersistorException {
		Persistor persistor = PersistorUtils.getPersistorDataFilters(session);
		return persistor.getRecord(dataFilter);
	}

}
