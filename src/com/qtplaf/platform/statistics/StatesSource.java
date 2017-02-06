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

package com.qtplaf.platform.statistics;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.statistics.Output;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.task.TaskStatesSource;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Retrieves the source values for the series of analisys to build the wave descriptor states and transitions. Contains
 * the source candle values, the series of rainbow averages smoothed, their relative spreads and their speed (tangent),
 * and finally the spreads of high, low and close over the fastest average. First add the averages and then call setup.
 *
 * @author Miquel Sas
 */
public class StatesSource extends Statistics {

	/** Field names. */
	public static class Fields {
		public static final String Index = "index";
		public static final String Time = "time";
		public static final String TimeFmt = "time_fmt";
		public static final String High = "high";
		public static final String Low = "low";
		public static final String Close = "close";
		public static final String Range = "range";
	}

	/**
	 * Working session.
	 */
	private Session session;
	/**
	 * The server.
	 */
	private Server server;
	/**
	 * The instrument.
	 */
	private Instrument instrument;
	/**
	 * The period.
	 */
	private Period period;

	/**
	 * The list of smoothed SMA definitions.
	 */
	private List<Average> averages = new ArrayList<Average>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesSource(Session session, Server server, Instrument instrument, Period period) {
		super();
		this.session = session;
		this.server = server;
		this.instrument = instrument;
		this.period = period;
	}

	/**
	 * Add a smoothed simple moving average
	 * 
	 * @param period
	 * @param averages
	 */
	public void addAverage(int period, int... smooths) {
		averages.add(new Average(period, smooths));
		ListUtils.sort(averages);
		setup();
	}

	/**
	 * Returns the list of averages that defines this statistics.
	 * 
	 * @return The list of smoothed averages.
	 */
	public List<Average> getAverages() {
		return averages;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the server.
	 * 
	 * @return The server.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Setup after adding the averages.
	 */
	private void setup() {
		if (averages.isEmpty()) {
			throw new IllegalStateException();
		}
		clear();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			Field field = table.getField(i);
			String name = field.getName();
			String description = field.getDisplayDescription();
			add(new Output(name, description, Types.Double));
		}

	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	public Task getTask() {
		return new TaskStatesSource(getSession(), this);
	}

	/**
	 * Returns the table name.
	 * 
	 * @return The table name.
	 */
	public String getTableName() {
		return Names.getName(getInstrument(), getPeriod(), getId().toLowerCase());
	}

	/**
	 * Returns the list of fields that should be included in a <tt>Data</tt> of the corresponding indicator. It excludes
	 * index, time and formatted time.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getIndicatorOutputFields() {
		List<Field> fields = new ArrayList<>();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			if (table.getField(i).getName().equals(Fields.Index)) {
				continue;
			}
			if (table.getField(i).getName().equals(Fields.Time)) {
				continue;
			}
			if (table.getField(i).getName().equals(Fields.TimeFmt)) {
				continue;
			}
			fields.add(table.getField(i));
		}
		return fields;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	public Table getTable() {

		Table table = new Table();

		table.setName(getTableName());
		table.setSchema(Names.getSchema(getServer()));

		// Index, time and price fields.
		table.addField(DomainUtils.getIndex(getSession(), Fields.Index));
		table.addField(DomainUtils.getTime(getSession(), Fields.Time));
		table.addField(DomainUtils.getHigh(getSession(), Fields.High));
		table.addField(DomainUtils.getLow(getSession(), Fields.Low));
		table.addField(DomainUtils.getClose(getSession(), Fields.Close));
		table.addField(DomainUtils.getTimeFmt(getSession(), Fields.TimeFmt));

		// Percentual range
		{
			String name = Fields.Range;
			String header = Fields.Range;
			String label = "Range factor";
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Averages fields.
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Average.getAverageName(average);
			String header = Average.getAverageHeader(average);
			String label = Average.getAverageLabel(average);
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Price spreads.
		Average fastAvg = averages.get(0);
		// High spread.
		{
			String name = Average.getSpreadName(Fields.High, fastAvg);
			String header = Average.getSpreadHeader(Fields.High, fastAvg);
			String label = Average.getSpreadLabel(Fields.High, fastAvg);
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}
		// Low spread.
		{
			String name = Average.getSpreadName(Fields.Low, fastAvg);
			String header = Average.getSpreadHeader(Fields.Low, fastAvg);
			String label = Average.getSpreadLabel(Fields.Low, fastAvg);
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}
		// Close spread.
		{
			String name = Average.getSpreadName(Fields.Close, fastAvg);
			String header = Average.getSpreadHeader(Fields.Close, fastAvg);
			String label = Average.getSpreadLabel(Fields.Close, fastAvg);
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Spreads between averages.
		for (int i = 0; i < averages.size(); i++) {
			Average averageFast = averages.get(i);
			for (int j = i + 1; j < averages.size(); j++) {
				Average averageSlow = averages.get(j);
				String name = Average.getSpreadName(averageFast, averageSlow);
				String header = Average.getSpreadHeader(averageFast, averageSlow);
				String label = Average.getSpreadLabel(averageFast, averageSlow);
				table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
			}
		}

		// Speed (tangent) of averages.
		for (int i = 0; i < averages.size(); i++) {
			Average average = averages.get(i);
			String name = Average.getSpeedName(average);
			String header = Average.getSpeedHeader(average);
			String label = Average.getSpeedLabel(average);
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Primary key on Time.
		table.getField(Fields.Time).setPrimaryKey(true);

		// Unique index on Index.
		Index index = new Index();
		index.add(table.getField(Fields.Index));
		index.setUnique(true);
		table.addIndex(index);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));

		return table;
	}
}
