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

import com.qtplaf.library.ai.rlearning.function.Normalizer;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.statistics.averages.Ranges;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.Transitions;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Configuration;
import com.qtplaf.platform.statistics.averages.configuration.Range;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;

/**
 * Manager to centralize states statistics access.
 *
 * @author Miquel Sas
 */
public class Manager {

	/** Working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Manager(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns the list of possible statistics id values.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The list of possible statistics id values.
	 */
	public List<Value> getStatisticsIdPossibleValues(Server server, Instrument instrument, Period period) {
		List<TickerStatistics> statistics = getListStatistics(server, instrument, period);
		List<Value> values = new ArrayList<>();
		for (TickerStatistics stats : statistics) {
			Value value = new Value(stats.getId());
			value.setLabel(stats.getTitle());
			values.add(value);
		}
		return values;
	}

	/**
	 * Returns the statictics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param id The statistics id.
	 * @return The statistics definition.
	 */
	public TickerStatistics getStatistics(Server server, Instrument instrument, Period period, String id) {
		List<TickerStatistics> statistics = getListStatistics(server, instrument, period);
		for (TickerStatistics stats : statistics) {
			if (stats.getId().equals(id)) {
				return stats;
			}
		}
		return null;
	}

	/**
	 * Returns the states statistics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param cfg The configuration.
	 * @return The states statistics.
	 */
	public States getStates(Server server, Instrument instrument, Period period, Configuration cfg) {
		States states = new States(getSession());
		states.setId(cfg.getId() + "st");
		states.setTitle("States " + cfg.getTitle());
		states.setServer(server);
		states.setInstrument(instrument);
		states.setPeriod(period);
		states.setConfiguration(cfg);
		return states;
	}

	/**
	 * Returns the ranges statistics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param cfg The configuration.
	 * @return The ranges statistics.
	 */
	public Ranges getRanges(Server server, Instrument instrument, Period period, Configuration cfg) {
		Ranges ranges = new Ranges(getSession());
		ranges.setId(cfg.getId() + "rn");
		ranges.setTitle("Ranges " + cfg.getTitle());
		ranges.setServer(server);
		ranges.setInstrument(instrument);
		ranges.setPeriod(period);
		ranges.setConfiguration(cfg);
		return ranges;
	}

	/**
	 * Returns the transitions statistics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param cfg The configuration.
	 * @return The transitions statistics.
	 */
	public Transitions getTransitions(Server server, Instrument instrument, Period period, Configuration cfg) {
		Transitions transitions = new Transitions(getSession());
		transitions.setId(cfg.getId() + "tr");
		transitions.setTitle("Transitions " + cfg.getTitle());
		transitions.setServer(server);
		transitions.setInstrument(instrument);
		transitions.setPeriod(period);
		transitions.setConfiguration(cfg);
		return transitions;
	}

	/**
	 * Returns the list of statictics.
	 * 
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The list of statistics definition.
	 */
	public List<TickerStatistics> getListStatistics(Server server, Instrument instrument, Period period) {
		List<TickerStatistics> statistics = new ArrayList<>();
		List<Configuration> configurations = getConfigurations();
		for (Configuration cfg : configurations) {
			statistics.add(getStates(server, instrument, period, cfg));
			statistics.add(getRanges(server, instrument, period, cfg));
			statistics.add(getTransitions(server, instrument, period, cfg));
		}
		return statistics;
	}

	/**
	 * Returns the list of defined configurations.
	 * 
	 * @return The list of defined configurations.
	 */
	public List<Configuration> getConfigurations() {
		List<Configuration> configurations = new ArrayList<>();
		configurations.add(getConfigurationSF());
		return configurations;
	}

	/**
	 * Returns the configuration with 5-21-89-377 averages. Normalizers are discrete normalizers over values that have
	 * already been normalized continuous.
	 * 
	 * @return The configuration.
	 */
	private Configuration getConfigurationSF() {
		Configuration cfg = new Configuration(getSession());
		cfg.setId("sf");
		cfg.setScale(3);

		// Averages.
		Average avg_5 = new Average(5, 5, 3);
		Average avg_21 = new Average(21, 13, 3);
		Average avg_89 = new Average(89, 21, 13);
		Average avg_377 = new Average(377, 34, 21);
		cfg.addAverage(avg_5);
		cfg.addAverage(avg_21);
		cfg.addAverage(avg_89);
		cfg.addAverage(avg_377);

		// Spread 5-21 and normalizer with 40 segments.
		Normalizer norm_5_21 = new Normalizer();
		norm_5_21.setMaximum(1.0);
		norm_5_21.setMinimum(-1.0);
		norm_5_21.setSegments(40);
		Spread spread_5_21 = new Spread();
		spread_5_21.setFastAverage(avg_5);
		spread_5_21.setSlowAverage(avg_21);
		spread_5_21.setStateKey(true);
		spread_5_21.setNormalizer(norm_5_21);
		cfg.addSpread(spread_5_21);

		// Spread 21-89 and normalizer with 20 segments.
		Normalizer norm_21_89 = new Normalizer();
		norm_21_89.setMaximum(1.0);
		norm_21_89.setMinimum(-1.0);
		norm_21_89.setSegments(20);
		Spread spread_21_89 = new Spread();
		spread_21_89.setFastAverage(avg_21);
		spread_21_89.setSlowAverage(avg_89);
		spread_21_89.setStateKey(true);
		spread_21_89.setNormalizer(norm_21_89);
		cfg.addSpread(spread_21_89);

		// Spread 89-377 and normalizer with 20 segments.
		Normalizer norm_89_377 = new Normalizer();
		norm_89_377.setMaximum(1.0);
		norm_89_377.setMinimum(-1.0);
		norm_89_377.setSegments(20);
		Spread spread_89_377 = new Spread();
		spread_89_377.setFastAverage(avg_89);
		spread_89_377.setSlowAverage(avg_377);
		spread_89_377.setStateKey(true);
		spread_89_377.setNormalizer(norm_89_377);
		cfg.addSpread(spread_89_377);

		// Speed 89 and normalizer with 10 segments.
		Normalizer norm_89 = new Normalizer();
		norm_89.setMaximum(1.0);
		norm_89.setMinimum(-1.0);
		norm_89.setSegments(10);
		Speed speed_89 = new Speed();
		speed_89.setAverage(avg_89);
		speed_89.setStateKey(true);
		speed_89.setNormalizer(norm_89);
		cfg.addSpeed(speed_89);

		// Speed 377 and normalizer with 20 segments.
		Normalizer norm_377 = new Normalizer();
		norm_377.setMaximum(1.0);
		norm_377.setMinimum(-1.0);
		norm_377.setSegments(20);
		Speed speed_377 = new Speed();
		speed_377.setAverage(avg_377);
		speed_377.setStateKey(true);
		speed_377.setNormalizer(norm_377);
		cfg.addSpeed(speed_377);

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}
}
