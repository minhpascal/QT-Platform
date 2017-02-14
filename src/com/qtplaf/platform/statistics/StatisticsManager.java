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

	private static final String StateSource_01 = "stsrc_01";
	private static final String StateRanges_01 = "strng_01";
	private static final String StateNormalize_01 = "stnrm_01";

	private static final String StateSource_02 = "stsrc_02";
	private static final String StateRanges_02 = "strng_02";
	private static final String StateNormalize_02 = "stnrm_02";

	/**
	 * An item is a defined statistics, identified by a code or id and a description.
	 */
	public static class Reference implements Comparable<Reference> {

		public static final String Id = "id";
		public static final String Title = "title";
		public static final String Description = "desc";

		private String id;
		private String title;
		private String description;
		private AveragesConfiguration configuration;

		/**
		 * Constructor.
		 * 
		 * @param id The id or code.
		 * @param title The title or short description.
		 */
		public Reference(String id, String title) {
			super();
			this.id = id;
			this.title = title;
			this.description = title;
		}

		/**
		 * Constructor.
		 * 
		 * @param id The id or code.
		 * @param title The title or short description.
		 * @param description The description.
		 */
		public Reference(String id, String title, String description) {
			super();
			this.id = id;
			this.title = title;
			this.description = description;
		}

		/**
		 * Returns the averages configuration.
		 * 
		 * @return The averages configuration.
		 */
		public AveragesConfiguration getConfiguration() {
			return configuration;
		}

		/**
		 * Set the averages configuration.
		 * 
		 * @param configuration The averages configuration.
		 */
		public void setConfiguration(AveragesConfiguration configuration) {
			this.configuration = configuration;
		}

		/**
		 * Returns the statistics id.
		 * 
		 * @return The id.
		 */
		public String getId() {
			return id;
		}

		/**
		 * Returns the statistics title.
		 * 
		 * @return The title or short description.
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Returns the statistics description.
		 * 
		 * @return The description.
		 */
		public String getDescription() {
			return description;
		}

		@Override
		public int hashCode() {
			return getId().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Reference) {
				Reference item = (Reference) obj;
				return getId().equals(item.getId());
			}
			return false;
		}

		@Override
		public int compareTo(Reference item) {
			return getId().compareTo(item.getId());
		}

		@Override
		public String toString() {
			return getId() + " - " + getTitle();
		}
	}

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
		AveragesConfiguration configuration;

		// 5-21-89-377-1597-6765
		configuration = new AveragesConfiguration();
		configuration.getAverages().add(new Average(5, 3, 3));
		configuration.getAverages().add(new Average(21, 5, 5));
		configuration.getAverages().add(new Average(89, 13, 13));
		configuration.getAverages().add(new Average(377, 21, 21));
		configuration.getAverages().add(new Average(1597, 34, 34));
		configuration.getAverages().add(new Average(6765, 55, 55));
		configuration.getRanges().add(new Average(89));
		configuration.getRanges().add(new Average(377));

		id = StateSource_01;
		title = "States source (" + configuration.toStringAverages() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateRanges_01;
		title = "States ranges (" + configuration.toStringAverages() + ") (" + configuration.toStringRanges() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateNormalize_01;
		title = "States normalized (" + configuration.toStringAverages() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);
		
		// 5-21-89-377-1597
		configuration = new AveragesConfiguration();
		configuration.getAverages().add(new Average(5, 3, 3));
		configuration.getAverages().add(new Average(21, 5, 5));
		configuration.getAverages().add(new Average(89, 13, 13));
		configuration.getAverages().add(new Average(377, 21, 21));
		configuration.getRanges().add(new Average(89));
		configuration.getRanges().add(new Average(377));
		
		id = StateSource_02;
		title = "States source (" + configuration.toStringAverages() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateRanges_02;
		title = "States ranges (" + configuration.toStringAverages() + ") (" + configuration.toStringRanges() + ")";
		reference = new Reference(id, title);
		reference.setConfiguration(configuration);
		references.add(reference);

		id = StateNormalize_02;
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
		if (id.equals(StateSource_01) || id.equals(StateSource_02)) {
			return getStatesSource(session, server, instrument, period, id);
		}
		if (id.equals(StateRanges_01) || id.equals(StateRanges_02)) {
			return getStatesRanges(session, server, instrument, period, id);
		}
		if (id.equals(StateNormalize_01) || id.equals(StateNormalize_02)) {
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
	private static StatesNormalize getStatesNormalize(
		Session session,
		Server server,
		Instrument instrument,
		Period period,
		String id) {

		String idRanges = null;
		if (id.equals(StateNormalize_01)) {
			idRanges = StateRanges_01;
		}
		if (id.equals(StateNormalize_02)) {
			idRanges = StateRanges_02;
		}

		StatesNormalize stnrm = new StatesNormalize(getStatesRanges(session, server, instrument, period, idRanges));

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
		if (id.equals(StateRanges_01)) {
			idSource = StateSource_01;
		}
		if (id.equals(StateRanges_02)) {
			idSource = StateSource_02;
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
