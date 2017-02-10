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
	 * Add a statistics reference.
	 * 
	 * @param id The id.
	 * @param title The title.
	 * @param description The description.
	 */
	private static void add(String id, String title, String description) {
		references.add(new Reference(id, title, description));
	}
	
	/**
	 * Initialize the statistics references.
	 */
	private static void initializeReferences() {
		if (!references.isEmpty()) {
			return;
		}
		String id, title, description;

		id = StateSource_01;
		title = "States source (5-21-89-377-1597-6765)";
		description = "First step in states statistics using a rainbow of averages.";
		add(id, title, description);

		id = StateRanges_01;
		title = "States ranges (5-21-89-377-1597-6765)";
		description = "Ranges (min-max) of percentual values of " + StateSource_01 + " statistics.";
		add(id, title, description);
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
		if (id.equals(StateSource_01)) {
			return getStatesSource(session, server, instrument, period, id);
		}
		if (id.equals(StateRanges_01)) {
			return getStatesRanges(session, server, instrument, period, id);
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

		stsrc.addAverage(5, 3, 3);
		stsrc.addAverage(21, 5, 5);
		stsrc.addAverage(89, 13, 13);
		stsrc.addAverage(377, 21, 21);
		stsrc.addAverage(1597, 34, 34);
		stsrc.addAverage(6765, 55, 55);

		return stsrc;
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

		StatesRanges strng = new StatesRanges(getStatesSource(session, server, instrument, period, idSource));
		
		Reference reference = getReference(id);
		if (reference == null) {
			throw new IllegalStateException();
		}
		strng.setId(reference.getId());
		strng.setTitle(reference.getTitle());
		strng.setDescription(reference.getDescription());
		
		// Only consider these periods.
		strng.addAverage(377);

		return strng;
	}
}
