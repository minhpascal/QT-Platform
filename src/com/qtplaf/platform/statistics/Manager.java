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
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Fields.Family;
import com.qtplaf.platform.database.Fields.Suffix;
import com.qtplaf.platform.database.calculators.CalculatorSpreadPrice;
import com.qtplaf.platform.database.calculators.CalculatorWeightedSum;
import com.qtplaf.platform.database.configuration.Average;
import com.qtplaf.platform.database.configuration.Calculation;
import com.qtplaf.platform.database.configuration.Configuration;
import com.qtplaf.platform.database.configuration.Range;
import com.qtplaf.platform.database.configuration.Slope;
import com.qtplaf.platform.database.configuration.Spread;
import com.qtplaf.platform.statistics.averages.States;

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
		states.setId(cfg.getId());
		states.setTitle("States " + cfg.getTitle());
		states.setServer(server);
		states.setInstrument(instrument);
		states.setPeriod(period);
		states.setConfiguration(cfg);
		return states;
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
		configurations.add(getConfigurationWeightedMedium());
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
		norm.setScale(4);
		return norm;
	}

	/**
	 * Returns the spread for the averages with a normalizer of segments.
	 * 
	 * @param fast Fast average.
	 * @param slow Slow average.
	 * @param segments Segments of the normalizer.
	 * @param key State key flag.
	 * @return The spread.
	 */
	private Spread getSpread(Average fast, Average slow, int segments, boolean key) {
		Spread spread = new Spread();
		spread.setFastAverage(fast);
		spread.setSlowAverage(slow);
		spread.setStateKey(key);
		spread.setNormalizer(getNormalizer(segments));
		return spread;
	}

	/**
	 * Returns the slope of the average with a normalizer of segments.
	 * 
	 * @param avg The average.
	 * @param segments Segments of the normalizer.
	 * @param key State key flag.
	 * @return The slope.
	 */
	private Slope getSlope(Average avg, int segments, boolean key) {
		Slope slope = new Slope();
		slope.setAverage(avg);
		slope.setStateKey(key);
		slope.setNormalizer(getNormalizer(segments));
		return slope;
	}

	/**
	 * Calculation for the price spread .
	 * 
	 * @param field The field.
	 * @param average The average.
	 * @param segments Segments of the normalizer.
	 * @param key State key flag.
	 * @return The calculator.
	 */
	private Calculation getCalculationSpreadPrice(Average average, int segments, boolean key) {
		String family = Family.Default;
		String name = "spread_wcp_" + average.getPeriod();
		String header = "Spread WCP-Avg-" + average.getPeriod();
		Calculation calculation = new Calculation(family, name, header, header);
		CalculatorSpreadPrice calculator = new CalculatorSpreadPrice(average);
		calculation.setCalculator(calculator);
		calculation.setNormalizer(getNormalizer(segments));
		calculation.setStateKey(key);
		return calculation;
	}

	/**
	 * Returns the calculation for the weighted sum of price spread, averaregs spreads and averages slopes.
	 * 
	 * @param weightedSum The calculator.
	 * @param segments The number of segment to discretize.
	 * @return The calculation.
	 */
	public Calculation getCalculationWeightedSum(CalculatorWeightedSum weightedSum, int segments) {
		String family = Family.WeightedSum;
		String name = Fields.WeightedSum;
		String header = "Weighted sum";
		Calculation calculation = new Calculation(family, name, header, header);
		calculation.setCalculator(weightedSum);
		calculation.setNormalizer(getNormalizer(segments));
		calculation.setStateKey(false);
		return calculation;
	}

	/**
	 * Returns the configuration with 5-21-89-377 averages. Normalizers are discrete normalizers over values that have
	 * already been normalized continuous. Weighted averages.
	 * 
	 * @return The configuration.
	 */
	public Configuration getConfigurationWeightedMedium() {
		Configuration cfg = new Configuration(getSession());
		cfg.setId("wm");

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
		Spread spread_5_21 = getSpread(avg_5, avg_21, 10, true);
		Spread spread_21_89 = getSpread(avg_21, avg_89, 10, true);
		Spread spread_89_377 = getSpread(avg_89, avg_377, 10, true);
		cfg.addSpread(spread_5_21);
		cfg.addSpread(spread_21_89);
		cfg.addSpread(spread_89_377);

		// Slopes.
		Slope slope_5 = getSlope(avg_5, 10, false);
		Slope slope_21 = getSlope(avg_21, 10, false);
		Slope slope_89 = getSlope(avg_89, 10, true);
		Slope slope_377 = getSlope(avg_377, 10, true);
		cfg.addSlope(slope_5);
		cfg.addSlope(slope_21);
		cfg.addSlope(slope_89);
		cfg.addSlope(slope_377);

		// Spread price.
		Calculation calcSpreadPrice = getCalculationSpreadPrice(avg_5, 10, true);
		cfg.addCalculation(calcSpreadPrice);

		// Calculation weighted sum of spreads, slopes and price spread.
		CalculatorWeightedSum weightedSum = new CalculatorWeightedSum();
		weightedSum.add(Fields.calculation(calcSpreadPrice, Suffix.nrm), 1.0);
		weightedSum.add(Fields.spread(spread_5_21, Suffix.nrm), 2.0);
		weightedSum.add(Fields.spread(spread_21_89, Suffix.nrm), 3.0);
		weightedSum.add(Fields.spread(spread_89_377, Suffix.nrm), 4.0);
		weightedSum.add(Fields.slope(slope_5, Suffix.nrm), 1.0);
		weightedSum.add(Fields.slope(slope_21, Suffix.nrm), 2.0);
		weightedSum.add(Fields.slope(slope_89, Suffix.nrm), 3.0);
		weightedSum.add(Fields.slope(slope_377, Suffix.nrm), 4.0);
		Calculation calcWeightedSum = getCalculationWeightedSum(weightedSum, 40);
		cfg.addCalculation(calcWeightedSum);

		// Ranges for min-max values.
		cfg.addRange(new Range(89));
		cfg.addRange(new Range(377));

		return cfg;
	}

}
