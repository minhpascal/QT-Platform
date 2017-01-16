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

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.Server;

/**
 * An utility to generate names based on instruments, periods, filters and offer sides.
 * 
 * @author Miquel Sas
 */
public class Names {

	/**
	 * Returns the system schema name.
	 * 
	 * @return The system schema name.
	 */
	public static String getSchema() {
		return "qtp";
	}

	/**
	 * Returns the schema name for a given server.
	 * 
	 * @param server The server.
	 * @return The schema name.
	 */
	public static String getSchema(Server server) {
		return getSchema() + "_" + server.getId();
	}

	/**
	 * Returns the name for a table of OHLCV data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The data filter.
	 * @param offerSide The offer side.
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(Instrument instrument, Period period, Filter filter, OfferSide offerSide) {
		return getName(instrument.getId(), period.getId(), filter.name(), offerSide.name());
	}

	/**
	 * Returns the name for a table of OHLCV data.
	 * 
	 * @param instrument The instrument id.
	 * @param period The period id.
	 * @param filter The data filter id (first character).
	 * @param offerSide The offer side id (first character).
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(String instrument, String period, String filter, String offerSide) {
		if (filter.length() > 0) {
			filter = filter.substring(0, 1);
		}
		if (offerSide.length() > 0) {
			offerSide = offerSide.substring(0, 1);
		}
		StringBuilder b = new StringBuilder();
		b.append(instrument.toLowerCase());
		b.append("_");
		b.append(period.toLowerCase());
		b.append("_");
		b.append(filter.toLowerCase());
		b.append(offerSide.toLowerCase());
		return b.toString();
	}
}
