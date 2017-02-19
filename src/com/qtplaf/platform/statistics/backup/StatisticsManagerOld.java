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

package com.qtplaf.platform.statistics.backup;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.platform.statistics.backup.AverageOld.Range;
import com.qtplaf.platform.statistics.backup.AverageOld.Speed;
import com.qtplaf.platform.statistics.backup.AverageOld.Spread;

/**
 * Manager of access to statistics.
 *
 * @author Miquel Sas
 */
public class StatisticsManagerOld {

	/** Configuration id: soft 5-21-89-377 */
	private static final String ConfigurationSoft = "sf";

	/** Source statistics: generates source values. */
	private static final String StateSource = "st1src";
	/** Ranges: calculates minimums and maximums. */
	private static final String StateRanges = "st2rng";
	/** Normalize continuous: normalized values continuous. */
	private static final String StateNormalizeContinuous = "st3nmc";
	/** Normalize discrete: normalized values discrete. */
	private static final String StateNormalizeDiscrete = "st4nmd";
	/** States transitions: transitions from discrete values. */
	private static final String StateTransition = "st5trs";

	/**
	 * The list of defined statistics.
	 */
	private static List<ReferenceOld> references = new ArrayList<ReferenceOld>();

	/**
	 * Initialize the statistics references.
	 * 
	 * @param session The session.
	 */
	private static void initializeReferences(Session session) {
		if (!references.isEmpty()) {
			return;
		}
		references.add(getReference(session, ConfigurationSoft, StateSource));
		references.add(getReference(session, ConfigurationSoft, StateRanges));
		references.add(getReference(session, ConfigurationSoft, StateNormalizeContinuous));
		references.add(getReference(session, ConfigurationSoft, StateNormalizeDiscrete));
		references.add(getReference(session, ConfigurationSoft, StateTransition));
	}

	/**
	 * Returns the configuration given the id, or null.
	 * 
	 * @param session The session.
	 * @param id The configuration id.
	 * @return The configuration.
	 */
	private static ConfigurationOld getConfiguration(Session session, String id) {
		if (id.equals(ConfigurationSoft)) {
			ConfigurationOld configuration = new ConfigurationOld(session, id, "Soft");

			List<AverageOld> averages = new ArrayList<>();
			averages.add(new AverageOld(5, 5, 3));
			averages.add(new AverageOld(21, 13, 5));
			averages.add(new AverageOld(89, 21, 13));
			averages.add(new AverageOld(377, 34, 21));

			// Averages.
			for (AverageOld average : averages) {
				configuration.addAverage(average);
			}

			// Spread 5-21.
			NormalizedStateValueDescriptor norm_5_21 = new NormalizedStateValueDescriptor(1.0, -1.0, 4, 40);
			Spread spread_5_21 = new Spread(averages.get(0), averages.get(1), norm_5_21);
			configuration.addSpread(spread_5_21);
			// Spread 21-89.
			NormalizedStateValueDescriptor norm_21_89 = new NormalizedStateValueDescriptor(1.0, -1.0, 4, 20);
			Spread spread_21_89 = new Spread(averages.get(1), averages.get(2), norm_21_89);
			configuration.addSpread(spread_21_89);
			// Spread 89-377.
			NormalizedStateValueDescriptor norm_89_377 = new NormalizedStateValueDescriptor(1.0, -1.0, 2, 10);
			Spread spread_89_377 = new Spread(averages.get(2), averages.get(3), norm_89_377);
			configuration.addSpread(spread_89_377);

			// Speeds only for slow averages.
			AverageOld speedSlow = averages.get(averages.size() - 1);
			NormalizedStateValueDescriptor normSlow = new NormalizedStateValueDescriptor(1.0, -1.0, 2, 10);
			configuration.addSpeed(new Speed(speedSlow, normSlow));

			// Ranges (periods) from min-max
			configuration.getRanges().add(new Range(89));
			configuration.getRanges().add(new Range(377));

			return configuration;
		}
		return null;
	}

	private static String getId(String cfgId, String refId) {
		return cfgId + refId;
	}

	/**
	 * Returns the reference for the configuration.
	 * 
	 * @param session The session.
	 * @param cfgId The configuration id.
	 * @param refId The reference id.
	 * @return The reference.
	 */
	private static ReferenceOld getReference(Session session, String cfgId, String refId) {
		if (refId.equals(StateSource)) {
			ConfigurationOld cfg = getConfiguration(session, cfgId);
			String id = getId(cfgId, refId);
			String title = cfg.getTitle() + " states source (" + cfg.toStringAverages() + ")";
			ReferenceOld ref = new ReferenceOld(id, title);
			ref.setConfiguration(cfg);
			return ref;
		}
		if (refId.equals(StateRanges)) {
			ConfigurationOld cfg = getConfiguration(session, cfgId);
			String id = cfgId + refId;
			String title = cfg.getTitle() + " states ranges (" + cfg.toStringAverages() + ")";
			ReferenceOld ref = new ReferenceOld(id, title);
			ref.setConfiguration(cfg);
			return ref;
		}
		if (refId.equals(StateNormalizeContinuous)) {
			ConfigurationOld cfg = getConfiguration(session, cfgId);
			String id = cfgId + refId;
			String title = cfg.getTitle() + " states normalized continuous (" + cfg.toStringAverages() + ")";
			ReferenceOld ref = new ReferenceOld(id, title);
			ref.setConfiguration(cfg);
			return ref;
		}
		if (refId.equals(StateNormalizeDiscrete)) {
			ConfigurationOld cfg = getConfiguration(session, cfgId);
			String id = cfgId + refId;
			String title = cfg.getTitle() + " states normalized discrete (" + cfg.toStringAverages() + ")";
			ReferenceOld ref = new ReferenceOld(id, title);
			ref.setConfiguration(cfg);
			return ref;
		}
		if (refId.equals(StateTransition)) {
			ConfigurationOld cfg = getConfiguration(session, cfgId);
			String id = cfgId + refId;
			String title = cfg.getTitle() + " states transitions (" + cfg.toStringAverages() + ")";
			ReferenceOld ref = new ReferenceOld(id, title);
			ref.setConfiguration(cfg);
			return ref;
		}
		return null;
	}

	/**
	 * Returns the reference with the given id or null.
	 * 
	 * @param session The session.
	 * @param id The id.
	 * @return The reference or null.
	 */
	private static ReferenceOld getReference(Session session, String id) {
		initializeReferences(session);
		for (ReferenceOld reference : references) {
			if (reference.getId().toLowerCase().equals(id.toLowerCase())) {
				return reference;
			}
		}
		return null;
	}

	/**
	 * Returns the list of defined statistics references.
	 * 
	 * @param session The session.
	 * @return The list of defined statistics references.
	 */
	public static List<ReferenceOld> getReferences(Session session) {
		initializeReferences(session);
		ListUtils.sort(StatisticsManagerOld.references);
		List<ReferenceOld> references = new ArrayList<ReferenceOld>(StatisticsManagerOld.references);
		return references;
	}

	/**
	 * Returns the statictics.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	public static Statistics getStatistics(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {
		if (id.equals(getId(ConfigurationSoft, StateSource))) {
			return getStatesSource(session, server, instrument, period, id);
		}
		if (id.equals(getId(ConfigurationSoft, StateRanges))) {
			return getStatesRanges(session, server, instrument, period, id);
		}
		if (id.equals(getId(ConfigurationSoft, StateNormalizeContinuous))) {
			return getStatesNormalizeContinuous(session, server, instrument, period, id);
		}
		if (id.equals(getId(ConfigurationSoft, StateNormalizeDiscrete))) {
			return getStatesNormalizeDiscrete(session, server, instrument, period, id);
		}
		if (id.equals(getId(ConfigurationSoft, StateTransition))) {
			return getStatesTransitions(session, server, instrument, period, id);
		}
		return null;
	}

	/**
	 * Returns the statictics of smoothed averages.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesSourceOld getStatesSource(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		StatesSourceOld stsrc = new StatesSourceOld(session, server, instrument, period);

		ReferenceOld reference = getReference(session, id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		stsrc.setId(reference.getId());
		stsrc.setTitle(reference.getTitle());
		stsrc.setDescription(reference.getTitle());
		stsrc.setConfiguration(reference.getConfiguration());
		stsrc.setup();

		return stsrc;
	}

	/**
	 * Returns the statictics of smoothed averages normalized continuous.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesNormalizeContinuousOld getStatesNormalizeContinuous(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idRanges = null;
		if (id.equals(getId(ConfigurationSoft, StateNormalizeContinuous))) {
			idRanges = getId(ConfigurationSoft, StateRanges);
		}

		StatesNormalizeContinuousOld stnrmc =
			new StatesNormalizeContinuousOld(getStatesRanges(session, server, instrument, period, idRanges));

		ReferenceOld reference = getReference(session, id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		stnrmc.setId(reference.getId());
		stnrmc.setTitle(reference.getTitle());
		stnrmc.setDescription(reference.getTitle());
		stnrmc.setConfiguration(reference.getConfiguration());
		stnrmc.setup();

		return stnrmc;
	}

	/**
	 * Returns the statictics of smoothed averages normalized discrete.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesNormalizeDiscreteOld getStatesNormalizeDiscrete(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idStCont = null;
		if (id.equals(getId(ConfigurationSoft, StateNormalizeDiscrete))) {
			idStCont = getId(ConfigurationSoft, StateNormalizeContinuous);
		}

		StatesNormalizeDiscreteOld stnrmd =
			new StatesNormalizeDiscreteOld(getStatesNormalizeContinuous(session, server, instrument, period, idStCont));

		ReferenceOld reference = getReference(session, id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		stnrmd.setId(reference.getId());
		stnrmd.setTitle(reference.getTitle());
		stnrmd.setDescription(reference.getTitle());
		stnrmd.setConfiguration(reference.getConfiguration());
		stnrmd.setup();

		return stnrmd;
	}

	/**
	 * Returns the statictics of transitions.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesTransitionsOld getStatesTransitions(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idStDiscr = null;
		if (id.equals(getId(ConfigurationSoft, StateTransition))) {
			idStDiscr = getId(ConfigurationSoft, StateNormalizeDiscrete);
		}

		StatesTransitionsOld sttrs =
			new StatesTransitionsOld(getStatesNormalizeDiscrete(session, server, instrument, period, idStDiscr));

		ReferenceOld reference = getReference(session, id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		sttrs.setId(reference.getId());
		sttrs.setTitle(reference.getTitle());
		sttrs.setDescription(reference.getTitle());
		sttrs.setConfiguration(reference.getConfiguration());
		sttrs.setup();

		return sttrs;
	}

	/**
	 * Returns the statictics of ranges for the states source.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesRangesOld getStatesRanges(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idSource = null;
		if (id.equals(getId(ConfigurationSoft, StateRanges))) {
			idSource = getId(ConfigurationSoft, StateSource);
		}

		StatesRangesOld strng = new StatesRangesOld(getStatesSource(session, server, instrument, period, idSource));

		ReferenceOld reference = getReference(session, id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		strng.setId(reference.getId());
		strng.setTitle(reference.getTitle());
		strng.setDescription(reference.getTitle());
		strng.setConfiguration(reference.getConfiguration());
		strng.setup();

		return strng;
	}
}
