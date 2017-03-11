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

/**
 * Centralize names of schemas, tables and fields.
 * 
 * @author Miquel Sas
 */
public class Names {

	/**
	 * Field names.
	 */
	public static class Fields {
		public static final String Average = "average";
		public static final String AvgStd1 = "avgstd1";
		public static final String AvgStd2 = "avgstd2";
		public static final String Close = "close";
		public static final String Count = "count";
		public static final String DataFilter = "data_filter";
		public static final String High = "high";
		public static final String Index = "index";
		public static final String IndexGroup = "index_group";
		public static final String IndexIn = "index_in";
		public static final String IndexOut = "index_out";
		public static final String InstrumentId = "instr_id";
		public static final String InstrumentDesc = "instr_desc";
		public static final String InstrumentPipValue = "instr_pipv";
		public static final String InstrumentPipScale = "instr_pips";
		public static final String InstrumentPrimaryCurrency = "instr_currp";
		public static final String InstrumentSecondaryCurrency = "instr_currs";
		public static final String InstrumentTickValue = "instr_tickv";
		public static final String InstrumentTickScale = "instr_ticks";
		public static final String InstrumentVolumeScale = "instr_vols";
		public static final String Low = "low";
		public static final String Maximum = "maximum";
		public static final String Minimum = "minimum";
		public static final String MinMax = "min_max";
		public static final String Name = "name";
		public static final String OfferSide = "offer_side";
		public static final String Open = "open";
		public static final String Period = "period";
		public static final String PeriodId = "period_id";
		public static final String PeriodName = "period_name";
		public static final String PeriodSize = "period_size";
		public static final String PeriodUnitIndex = "period_unit_index";
		public static final String ServerId = "server_id";
		public static final String ServerName = "server_name";
		public static final String ServerTitle = "server_title";
		public static final String State = "state";
		public static final String StateIn = "state_in";
		public static final String StateOut = "state_out";
		public static final String StatisticsId = "stats_id";
		public static final String StdDev = "stddev";
		public static final String TableName = "table_name";
		public static final String Time = "time";
		public static final String TimeFmt = "time_fmt";
		public static final String Value = "value";
		public static final String Volume = "volume";
	}

	/**
	 * Schema names.
	 */
	public static class Schemas {
		public static final String qtp = "qtp";

		public static String server(Server server) {
			return qtp + "_" + server.getId();
		}
	}

	/**
	 * Table names.
	 */
	public static class Tables {
		public static final String DataFilters = "data_filters";
		public static final String Instruments = "instruments";
		public static final String OfferSides = "offer_sides";
		public static final String Periods = "periods";
		public static final String Servers = "servers";
		public static final String Statistics = "statistics";
		public static final String Tickers = "tickers";

		public static String ticker(Instrument instrument, Period period) {
			return ticker(instrument.getId(), period.getId());
		}

		public static String ticker(Instrument instrument, Period period, String suffix) {
			return ticker(instrument.getId(), period.getId(), suffix);
		}

		public static String ticker(String instrument, String period) {
			return ticker(instrument, period, null);
		}

		public static String ticker(String instrument, String period, String suffix) {
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
	}
}
