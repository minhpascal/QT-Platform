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
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class StatesAverages extends Statistics {

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
	public StatesAverages(Session session, Server server, Instrument instrument, Period period) {
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
	 * @param smooths Smoothing periods.
	 */
	public void addAverage(int period, int... smooths) {
		addAverage(new Average(period, smooths));
	}

	/**
	 * Add an average
	 * 
	 * @param average The average.
	 */
	public void addAverage(Average average) {
		averages.add(average);
		ListUtils.sort(averages);
		setup();
	}

	/**
	 * Setup after adding the averages.
	 */
	protected abstract void setup();

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
	 * Returns the list of average fields.
	 * 
	 * @return The list of average fields.
	 */
	protected List<Field> getAverageFields() {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < getAverages().size(); i++) {
			fields.add(Average.getAverageField(getSession(), getAverages().get(i)));
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @return The list of spread fields.
	 */
	protected List<Field> getSpreadFields() {
		List<Field> fields = new ArrayList<>();
		for (int i = 1; i < getAverages().size(); i++) {
			Average averageFast = getAverages().get(i+1);
			Average averageSlow = getAverages().get(i);
			fields.add(Average.getSpreadField(getSession(), averageFast, averageSlow));
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields.
	 * 
	 * @return The list of speed fields.
	 */
	protected List<Field> getSpeedFields() {
		List<Field> fields = new ArrayList<>();
		for (int i = 0; i < getAverages().size(); i++) {
			fields.add(Average.getSpeedField(getSession(), getAverages().get(i)));
		}
		return fields;
	}
}
