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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.statistics.StatesSource;

/**
 * Utilities to manage statistics.
 *
 * @author Miquel Sas
 */
public class StatsUtils {
	
	public static final String Id_Src_01 = "SCR_01";
	public static final String Desc_Src_01 = "Source (5-21-89-377-1597-6765)";

	/**
	 * Returns the list of defined statistics id's.
	 * 
	 * @return The list of ids.
	 */
	public static List<String> getStatisticsIds() {
		List<String> ids = new ArrayList<>();
		ids.add(Id_Src_01);
		return ids;
	}

	/**
	 * Returns a statistic over an instrument and period given its id.
	 * 
	 * @param id The id.
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistic.
	 */
	public static Statistics getStatistics(
		String id,
		Session session,
		Server server,
		Instrument instrument,
		Period period) {
		if (id.equals(Id_Src_01)) {
			return getStatesSource_5_21_89_377_1597_6765(session, server, instrument, period);
		}
		return null;
	}

	/**
	 * Returns the statictics of smoothed averages: 5, 21, 89, 377, 1597, 6765
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	public static Statistics getStatesSource_5_21_89_377_1597_6765(
		Session session,
		Server server,
		Instrument instrument,
		Period period) {

		StatesSource ss = new StatesSource(session, server, instrument, period);
		ss.setId(Id_Src_01);
		ss.setDescription(Desc_Src_01);

		ss.addAverage(5, 3, 3);
		ss.addAverage(21, 5, 5);
		ss.addAverage(89, 13, 13);
		ss.addAverage(377, 21, 21);
		ss.addAverage(1597, 34, 34);
		ss.addAverage(6765, 55, 55);
		return ss;
	}
}
