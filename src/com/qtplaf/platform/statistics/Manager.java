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
import com.qtplaf.platform.database.calculators.SumFields;
import com.qtplaf.platform.database.configuration.Average;
import com.qtplaf.platform.database.configuration.Calculation;
import com.qtplaf.platform.database.configuration.Configuration;
import com.qtplaf.platform.database.configuration.Range;
import com.qtplaf.platform.database.configuration.Speed;
import com.qtplaf.platform.database.configuration.Spread;
import com.qtplaf.platform.statistics.averages.Ranges;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.Transitions;

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
		configurations.add(getConfigurationMF());
		configurations.add(getConfigurationSF());
		configurations.add(getConfigurationWF());
		configurations.add(getConfigurationWM());
		return configurations;
	}

	/**
	 * Returns a standard normalizer (-1,1) with the given number of segments.
	 * 
	 * @param segments The number of segments.
	 * @return The normalizer.
	 */
	private Normalizer getNormalizer(int segments) {
		Normalizer norm = new Normalizer();
		norm.setMaximum(1.0);
		norm.setMinimum(-1.0);
		norm.setSegments(segments);
		return norm;
	}

	/**
	 * Returns the spread for the averages with a normalizer of segments.
	 * 
	 * @param fast Fast average.
	 * @param slow Slow average.
	 * @param segments Segments of the normalizer.
	 * @return The spread.
	 */
	private Spread getSpread(Average fast, Average slow, int segments) {
		Spread spread = new Spread();
		spread.setFastAverage(fast);
		spread.setSlowAverage(slow);
		spread.setStateKey(true);
		spread.setNormalizer(getNormalizer(segments));
		return spread;
	}

	/**
	 * Returns the speed of the average with a normalizer of segments.
	 * 
	 * @param avg The average.
	 * @param segments Segments of the normalizer.
	 * @return The speed.
	 */
	private Speed getSpeed(Average avg, int segments) {
		Speed speed = new Speed();
		speed.setAverage(avg);
		speed.setStateKey(true);
		speed.setNormalizer(getNormalizer(segments));
		return speed;
	}

	/**
	 * Returns the calculation to sum spreads.
	 * 
	 * @param spreads The spreads.
	 * @param segments Segments of the normalizer.
	 * @return The calculation.
	 */
	private Calculation getCalculationSumSpreads(List<Spread> spreads, int segments) {
		Calculation calculation = new Calculation("spread_sum", "Spread sum", "Sum of spreads");
		SumFields calcSum = new SumFields();
		for (Spread spread : spreads) {
			calcSum.add(spread.getName() + "_raw");
		}
		calculation.setCalculator(calcSum);
		calculation.setNormalizer(getNormalizer(segments));
		calculation.setStateKey(true);
		return calculation;
	}

	/**
	 * Returns the calculation to sum speeds.
	 * 
	 * @param spreads The speeds.
	 * @param segments Segments of the normalizer.
	 * @return The calculation.
	 */
	private Calculation getCalculationSumSpeeds(List<Speed> speeds, int segments) {
		Calculation calculation = new Calculation("speed_sum", "Speed sum", "Sum of speeds");
		SumFields calcSum = new SumFields();
		for (Speed speed : speeds) {
			calcSum.add(speed.getName() + "_raw");
		}
		calculation.setCalculator(calcSum);
		calculation.setNormalizer(getNormalizer(segments));
		calculation.setStateKey(true);
		return calculation;
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
		Average avg_610 = new Average(610, 55, 34);
		cfg.addAverage(avg_5);
		cfg.addAverage(avg_21);
		cfg.addAverage(avg_89);
		cfg.addAverage(avg_377);
		cfg.addAverage(avg_610);

		// Spreads.
		cfg.addSpread(getSpread(avg_5, avg_21, 10));
		cfg.addSpread(getSpread(avg_21, avg_89, 10));
		cfg.addSpread(getSpread(avg_89, avg_377, 10));
		cfg.addSpread(getSpread(avg_377, avg_610, 10));

		// Speeds.
		cfg.addSpeed(getSpeed(avg_89, 10));
		cfg.addSpeed(getSpeed(avg_377, 10));
		cfg.addSpeed(getSpeed(avg_610, 10));
		
		// Sum of spreads and speeds.
		cfg.addCalculation(getCalculationSumSpreads(cfg.getSpreads(), 10));
		cfg.addCalculation(getCalculationSumSpeeds(cfg.getSpeeds(), 10));

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}

	/**
	 * Returns the configuration with 5-21-89-377 averages. Normalizers are discrete normalizers over values that have
	 * already been normalized continuous.
	 * 
	 * @return The configuration.
	 */
	private Configuration getConfigurationWF() {
		Configuration cfg = new Configuration(getSession());
		cfg.setId("wf");
		cfg.setScale(3);

		// Averages.
		Average avg_5 = new Average(5, 5, 3);
		Average avg_21 = new Average(21, 13, 3);
		Average avg_89 = new Average(89, 21, 13);
		Average avg_377 = new Average(377, 34, 21);
		Average avg_610 = new Average(610, 55, 34);
		avg_5.setType(Average.Type.WMA);
		avg_21.setType(Average.Type.WMA);
		avg_89.setType(Average.Type.WMA);
		avg_377.setType(Average.Type.WMA);
		avg_610.setType(Average.Type.WMA);
		cfg.addAverage(avg_5);
		cfg.addAverage(avg_21);
		cfg.addAverage(avg_89);
		cfg.addAverage(avg_377);
		cfg.addAverage(avg_610);

		// Spreads.
		cfg.addSpread(getSpread(avg_5, avg_21, 10));
		cfg.addSpread(getSpread(avg_21, avg_89, 10));
		cfg.addSpread(getSpread(avg_89, avg_377, 10));
		cfg.addSpread(getSpread(avg_377, avg_610, 10));

		// Speeds.
		cfg.addSpeed(getSpeed(avg_89, 10));
		cfg.addSpeed(getSpeed(avg_377, 10));
		cfg.addSpeed(getSpeed(avg_610, 10));
		
		// Sum of spreads and speeds.
		cfg.addCalculation(getCalculationSumSpreads(cfg.getSpreads(), 10));
		cfg.addCalculation(getCalculationSumSpeeds(cfg.getSpeeds(), 10));

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}

	/**
	 * Returns the configuration with 5-21-89-377 averages. Normalizers are discrete normalizers over values that have
	 * already been normalized continuous.
	 * 
	 * @return The configuration.
	 */
	private Configuration getConfigurationMF() {
		Configuration cfg = new Configuration(getSession());
		cfg.setId("mf");
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

		// Spreads.
		cfg.addSpread(getSpread(avg_5, avg_21, 10));
		cfg.addSpread(getSpread(avg_21, avg_89, 10));
		cfg.addSpread(getSpread(avg_89, avg_377, 10));

		// Speeds.
		cfg.addSpeed(getSpeed(avg_89, 10));
		cfg.addSpeed(getSpeed(avg_377, 10));
	
		// Sum of spreads and speeds.
		cfg.addCalculation(getCalculationSumSpreads(cfg.getSpreads(), 10));
		cfg.addCalculation(getCalculationSumSpeeds(cfg.getSpeeds(), 10));

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}

	/**
	 * Returns the configuration with 5-21-89-377 averages. Normalizers are discrete normalizers over values that have
	 * already been normalized continuous.
	 * 
	 * @return The configuration.
	 */
	private Configuration getConfigurationWM() {
		Configuration cfg = new Configuration(getSession());
		cfg.setId("wm");
		cfg.setScale(3);

		// Averages.
		Average avg_5 = new Average(5, 5, 3);
		Average avg_21 = new Average(21, 13, 3);
		Average avg_89 = new Average(89, 21, 13);
		Average avg_377 = new Average(377, 34, 21);
		avg_5.setType(Average.Type.WMA);
		avg_21.setType(Average.Type.WMA);
		avg_89.setType(Average.Type.WMA);
		avg_377.setType(Average.Type.WMA);
		cfg.addAverage(avg_5);
		cfg.addAverage(avg_21);
		cfg.addAverage(avg_89);
		cfg.addAverage(avg_377);

		// Spreads.
		cfg.addSpread(getSpread(avg_5, avg_21, 10));
		cfg.addSpread(getSpread(avg_21, avg_89, 10));
		cfg.addSpread(getSpread(avg_89, avg_377, 10));

		// Speeds.
		cfg.addSpeed(getSpeed(avg_89, 10));
		cfg.addSpeed(getSpeed(avg_377, 10));
	
		// Sum of spreads and speeds.
		cfg.addCalculation(getCalculationSumSpreads(cfg.getSpreads(), 10));
		cfg.addCalculation(getCalculationSumSpeeds(cfg.getSpeeds(), 10));

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}

}
