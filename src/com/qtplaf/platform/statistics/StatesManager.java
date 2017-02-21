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

/**
 * Manager to centralize states statistics access.
 *
 * @author Miquel Sas
 */
public class StatesManager {

	/** Working session. */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public StatesManager(Session session) {
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
		norm_5_21.setSegments(40);
		Spread spread_5_21 = new Spread();
		spread_5_21.setFastAverage(avg_5);
		spread_5_21.setSlowAverage(avg_21);
		spread_5_21.setStateKey(true);
		spread_5_21.setNormalizer(norm_5_21);
		cfg.addSpread(spread_5_21);
		
		// Spread 21-89 and normalizer with 20 segments.
		Normalizer norm_21_89 = new Normalizer();
		norm_21_89.setSegments(20);
		Spread spread_21_89 = new Spread();
		spread_21_89.setFastAverage(avg_21);
		spread_21_89.setSlowAverage(avg_89);
		spread_21_89.setStateKey(true);
		spread_21_89.setNormalizer(norm_21_89);
		cfg.addSpread(spread_21_89);
		
		// Spread 89-377 and normalizer with 20 segments.
		Normalizer norm_89_377 = new Normalizer();
		norm_89_377.setSegments(20);
		Spread spread_89_377 = new Spread();
		spread_89_377.setFastAverage(avg_89);
		spread_89_377.setSlowAverage(avg_377);
		spread_89_377.setStateKey(true);
		spread_89_377.setNormalizer(norm_89_377);
		cfg.addSpread(spread_89_377);
		
		// Speed 89 and normalizer with 20 segments.
		Normalizer norm_89 = new Normalizer();
		norm_89.setSegments(20);
		Speed speed_89 = new Speed();
		speed_89.setAverage(avg_89);
		speed_89.setStateKey(true);
		speed_89.setNormalizer(norm_89);
		cfg.addSpeed(speed_89);

		return cfg;
	}
}
