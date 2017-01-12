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

import java.util.Currency;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
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
	 * Returns the instrument definition given the instrument record.
	 * 
	 * @param record The instrument record.
	 * @return The instrument definition.
	 */
	public static Instrument fromRecordInstrument(Record record) {
		if (record == null) {
			return null;
		}
		Instrument instrument = new Instrument();
		instrument.setId(record.getValue(Fields.InstrumentId).getString());
		instrument.setDescription(record.getValue(Fields.InstrumentDesc).getString());
		instrument.setPipValue(record.getValue(Fields.InstrumentPipValue).getDouble());
		instrument.setPipScale(record.getValue(Fields.InstrumentPipScale).getInteger());
		instrument.setTickValue(record.getValue(Fields.InstrumentTickValue).getDouble());
		instrument.setTickScale(record.getValue(Fields.InstrumentTickScale).getInteger());
		instrument.setVolumeScale(record.getValue(Fields.InstrumentVolumeScale).getInteger());
		String primaryCurrency = record.getValue(Fields.InstrumentPrimaryCurrency).getString();
		instrument.setPrimaryCurrency(Currency.getInstance(primaryCurrency));
		String secondaryCurrency = record.getValue(Fields.InstrumentSecondaryCurrency).getString();
		instrument.setSecondaryCurrency(Currency.getInstance(secondaryCurrency));
		return instrument;
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

	/**
	 * Returns the filled record for the offer side.
	 * 
	 * @param record The blank offer side record.
	 * @param offerSide The offer side.
	 * @return The record.
	 */
	public static Record getRecordOfferSide(Record record, OfferSide offerSide) {
		record.setValue(Fields.OfferSide, offerSide.name());
		return record;
	}

	/**
	 * Returns the filled record for the data filter.
	 * 
	 * @param record The blank offer side record.
	 * @param dataFilter The data filter.
	 * @return The record.
	 */
	public static Record getRecordDataFilter(Record record, Filter dataFilter) {
		record.setValue(Fields.DataFilter, dataFilter.name());
		return record;
	}
}
