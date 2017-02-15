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
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.library.util.list.ListUtils;

/**
 * Manager of access to statistics.
 *
 * @author Miquel Sas
 */
public class StatisticsManager {

	private static final String StateSource = "stsrc";
	private static final String StateRanges = "strng";
	private static final String StateNormalize = "stnrm";

	/**
	 * The list of defined statistics.
	 */
	private static List<Reference> references = new ArrayList<Reference>();

	/**
	 * Initialize the statistics references.
	 */
	private static void initializeReferences() {
		if (!references.isEmpty()) {
			return;
		}
		String id, title;
		Reference reference;
		Configuration configuration;

		// 5-21-89-377
		configuration = new Configuration("00");
		configuration.getAverages().add(new Average(5, 5, 3));
		configuration.getAverages().add(new Average(21, 13, 5));
		configuration.getAverages().add(new Average(89, 21, 13));
		configuration.getAverages().add(new Average(377, 34, 21));
		configuration.getRanges().add(new Average(89));
		configuration.getRanges().add(new Average(377));

		id = StateSource;
		title = "States source (" + configuration.toStringAverages() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateRanges;
		title = "States ranges (" + configuration.toStringAverages() + ") (" + configuration.toStringRanges() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateNormalize;
		title = "States normalized (" + configuration.toStringAverages() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);
		
	}

	/**
	 * Returns the reference with the given id or null.
	 * 
	 * @param id The id.
	 * @return The reference or null.
	 */
	private static Reference getReference(String id) {
		initializeReferences();
		for (Reference reference : references) {
			if (reference.getId().toLowerCase().equals(id.toLowerCase())) {
				return reference;
			}
		}
		return null;
	}

	/**
	 * Returns the list of defined statistics references.
	 * 
	 * @return The list of defined statistics references.
	 */
	public static List<Reference> getReferences() {
		initializeReferences();
		ListUtils.sort(StatisticsManager.references);
		List<Reference> references = new ArrayList<Reference>(StatisticsManager.references);
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
		if (id.equals(StateSource)) {
			return getStatesSource(session, server, instrument, period, id);
		}
		if (id.equals(StateRanges)) {
			return getStatesRanges(session, server, instrument, period, id);
		}
		if (id.equals(StateNormalize)) {
			return getStatesNormalize(session, server, instrument, period, id);
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
	private static StatesSource getStatesSource(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		StatesSource stsrc = new StatesSource(session, server, instrument, period);

		Reference reference = getReference(id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		stsrc.setId(reference.getId());
		stsrc.setTitle(reference.getTitle());
		stsrc.setDescription(reference.getDescription());
		for (Average average : reference.getConfiguration().getAverages()) {
			stsrc.addAverage(average);
		}

		return stsrc;
	}

	/**
	 * Returns the statictics of smoothed averages normalized: 5, 21, 89, 377, 1597, 6765
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesNormalizeContinuous getStatesNormalize(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idRanges = null;
		if (id.equals(StateNormalize)) {
			idRanges = StateRanges;
		}

		StatesNormalizeContinuous stnrm = new StatesNormalizeContinuous(getStatesRanges(session, server, instrument, period, idRanges));

		Reference reference = getReference(id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		stnrm.setId(reference.getId());
		stnrm.setTitle(reference.getTitle());
		stnrm.setDescription(reference.getDescription());
		for (Average average : reference.getConfiguration().getAverages()) {
			stnrm.addAverage(average);
		}

		return stnrm;
	}

	/**
	 * Returns the statictics of ranges for the states source: 5, 21, 89, 377, 1597, 6765
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The statistics definition.
	 */
	private static StatesRanges getStatesRanges(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idSource = null;
		if (id.equals(StateRanges)) {
			idSource = StateSource;
		}

		StatesRanges strng = new StatesRanges(getStatesSource(session, server, instrument, period, idSource));

		Reference reference = getReference(id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		strng.setId(reference.getId());
		strng.setTitle(reference.getTitle());
		strng.setDescription(reference.getDescription());

		for (Average range : reference.getConfiguration().getRanges()) {
			strng.addAverage(range);
		}

		return strng;
	}
}
