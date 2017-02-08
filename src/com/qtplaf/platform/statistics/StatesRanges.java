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

import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.util.DomainUtils;

/**
 * Calculates the ranges min-max of the percentual values calculated in the correspondent <tt>StatesSource</tt>:
 * <ul>
 * <li>Range.</li>
 * <li>Price (high, low and close) spreads vs the fast average.</li>
 * <li>Averages spreads.</li>
 * <li>Averages speeds.</li>
 * </ul>
 * To consider a value a maximum (or minimum), it must be the maximum of a certain number of periods before and after.
 * For each average period, min-max values will be calculated for each target value.
 *
 * @author Miquel Sas
 */
public class StatesRanges extends StatesAverages {

	/** Field names. */
	public static class Fields {
		public static final String Name = "name";
		public static final String MinMax = "min_max";
		public static final String Period = "period";
		public static final String Value = "value";
	}

	/**
	 * The parent states source statistics.
	 */
	private StatesSource statesSource;

	/**
	 * Constructor.
	 * 
	 * @param statesSource The parent states source statistics.
	 */
	public StatesRanges(StatesSource statesSource) {
		super(
			statesSource.getSession(),
			statesSource.getServer(),
			statesSource.getInstrument(),
			statesSource.getPeriod());
		this.statesSource = statesSource;
	}

	/**
	 * Returns the parent states source statistics.
	 * 
	 * @return The parent states source statistics.
	 */
	public StatesSource getStatesSource() {
		return statesSource;
	}

	/**
	 * Setup after adding the averages.
	 */
	protected void setup() {
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		// TODO Auto-generated method stub
		return null;
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
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		Table table = new Table();

		table.setName(getTableName());
		table.setSchema(Names.getSchema(getServer()));

		table.addField(DomainUtils.getName(getSession(), Fields.Name));
		table.addField(DomainUtils.getMinMax(getSession(), Fields.MinMax));
		table.addField(DomainUtils.getPeriod(getSession(), Fields.Period));
		table.addField(DomainUtils.getDouble(getSession(), Fields.Value));

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(table.getField(Fields.Name));
		index.add(table.getField(Fields.MinMax));
		index.add(table.getField(Fields.Period));
		index.setUnique(false);
		table.addIndex(index);

		return table;
	}

}
