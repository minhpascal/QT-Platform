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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.util.DomainUtils;
import com.qtplaf.platform.database.util.PersistorUtils;

/**
 * Source table to calculate RL states for given instrument and period.
 *
 * @author Miquel Sas
 */
public class StatesSource extends Table {

	public static class Fields {
		public static final String Index = "index";
		public static final String Time = "time";
		public static final String TimeFmt = "time_fmt";
		public static final String Open = "open";
		public static final String High = "high";
		public static final String Low = "low";
		public static final String Close = "close";

		public static String averageName(int period) {
			return "sma_" + period;
		}

		public static String averageHeader(int period) {
			return averageName(period);
		}

		public static String averageLabel(int period, int... smooths) {
			StringBuilder b = new StringBuilder();
			b.append("SMA ");
			b.append(period);
			for (int smooth : smooths) {
				b.append(", ");
				b.append(smooth);
			}
			return b.toString();
		}

		public static String spreadName(int periodFast, int periodSlow) {
			return "spread_" + periodFast + "_" + periodSlow;
		}

		public static String spreadHeader(int periodFast, int periodSlow) {
			return spreadName(periodFast, periodSlow);
		}

		public static String spreadLabel(int periodFast, int periodSlow) {
			return "Spread " + periodFast + " - " + periodSlow;
		}
		
		public static String speedName(int period) {
			return "speed_" + period;
		}

		public static String speedHeader(int period) {
			return averageName(period);
		}

		public static String speedLabel(int period) {
			return "Spread " + period;
		}
	}

	/**
	 * Defines a smoothed SMA used as a movement descriptor.
	 */
	class Average  implements Comparable<Average> {
		/** SMA period. */
		private int period;
		/** Smoothing periods. */
		private int[] smooths;

		/**
		 * Constructor.
		 * 
		 * @param period SMA period.
		 * @param averages Smoothing periods.
		 */
		public Average(int period, int[] smooths) {
			super();
			this.period = period;
			this.smooths = smooths;
		}

		/**
		 * Returns the SMA period.
		 * 
		 * @return The SMA period.
		 */
		public int getPeriod() {
			return period;
		}

		/**
		 * Returns the smoothing periods.
		 * 
		 * @return The smoothing periods.
		 */
		public int[] getSmooths() {
			return smooths;
		}

		/**
		 * Compare to sort.
		 */
		@Override
		public int compareTo(Average avg) {
			return Integer.valueOf(getPeriod()).compareTo(Integer.valueOf(avg.getPeriod()));
		}

	}

	/**
	 * The list of smoothed SMA definitions.
	 */
	private List<Average> averages = new ArrayList<Average>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param name The table name.
	 */
	public StatesSource(Session session, Server server, String name) {
		super(session);
		setName(name);
		setSchema(Names.getSchema(server));
	}

	/**
	 * Add a smoothed simple moving average
	 * 
	 * @param period
	 * @param averages
	 */
	public void addAverage(int period, int... smooths) {
		averages.add(new Average(period, smooths));
	}

	/**
	 * Configure this table field, primary key and indexes.
	 */
	public void configure() {

		// Index, time and price fields.
		addField(DomainUtils.getIndex(getSession(), Fields.Index));
		addField(DomainUtils.getTime(getSession(), Fields.Time));
		addField(DomainUtils.getOpen(getSession(), Fields.Open));
		addField(DomainUtils.getHigh(getSession(), Fields.High));
		addField(DomainUtils.getLow(getSession(), Fields.Low));
		addField(DomainUtils.getClose(getSession(), Fields.Close));
		addField(DomainUtils.getTimeFmt(getSession(), Fields.TimeFmt));

		// Averages fields.
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Fields.averageName(average.getPeriod());
			String header = Fields.averageHeader(average.getPeriod());
			String label = Fields.averageLabel(average.getPeriod(), average.getSmooths());
			addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}
		
		// Spreads between averages.
		ListUtils.sort(averages);
		for (int i = 0; i < averages.size(); i++) {
			Average averageFast = averages.get(i);
			for (int j = i + 1; j < averages.size(); j++) {
				Average averageSlow = averages.get(j);
				String name = Fields.spreadName(averageFast.getPeriod(), averageSlow.getPeriod());
				String header = Fields.spreadHeader(averageFast.getPeriod(), averageSlow.getPeriod());
				String label = Fields.spreadLabel(averageFast.getPeriod(), averageSlow.getPeriod());
				addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
			}
		}
		
		// Speed (tangent) of averages.
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Fields.speedName(average.getPeriod());
			String header = Fields.speedHeader(average.getPeriod());
			String label = Fields.speedLabel(average.getPeriod());
			addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Primary key on Time.
		getField(Fields.Time).setPrimaryKey(true);
		
		// Unique index on Index.
		Index index = new Index();
		index.add(getField(Fields.Index));
		index.setUnique(true);
		addIndex(index);

		setPersistor(PersistorUtils.getPersistor(getSimpleView()));

	}
}
