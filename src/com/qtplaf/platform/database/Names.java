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
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.StringUtils;

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
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(Instrument instrument, Period period) {
		return getName(instrument.getId(), period.getId());
	}
	
	/**
	 * Returns the name for a table of OHLCV data.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param suffix The suffix.
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(Instrument instrument, Period period, String suffix) {
		return getName(instrument.getId(), period.getId(), suffix);
	}

	/**
	 * Returns the name for a table of times data.
	 * 
	 * @param instrument The instrument id.
	 * @param period The period id.
	 * @param suffix The suffix.
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(String instrument, String period) {
		return getName(instrument, period, null);
	}

	/**
	 * Returns the name for a table of timed data.
	 * 
	 * @param instrument The instrument id.
	 * @param period The period id.
	 * @param suffix The suffix.
	 * @return The name for a table of OHLCV data.
	 */
	public static String getName(String instrument, String period, String suffix) {
		StringBuilder b = new StringBuilder();
		b.append(instrument.toLowerCase());
		b.append("_");
		b.append(period.toLowerCase());
		if (suffix != null) {
			b.append("_");
			b.append(suffix.toLowerCase());
		}
		return b.toString();
	}

	/**
	 * Returns the period id given an OHLCV tablle name.
	 * 
	 * @param tableName The table name.
	 * @return The period id.
	 */
	public static String getPeriodId(String tableName) {
		return StringUtils.split(tableName,"_")[1];
	}
}
