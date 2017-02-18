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

import java.util.List;

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.task.TaskStatesTransitions;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Calculates transitions from discrete normalized values.
 *
 * @author Miquel Sas
 */
public class StatesTransitions extends StatesAverages {

	/** States discrete related statistics. */
	private StatesNormalizeDiscrete statesNormalizeDiscrete;

	/**
	 * Constructor.
	 * 
	 * @param statesNormalizeDiscrete The states discrete related statistics.
	 */
	public StatesTransitions(StatesNormalizeDiscrete statesNormalizeDiscrete) {
		super(
			statesNormalizeDiscrete.getSession(),
			statesNormalizeDiscrete.getServer(),
			statesNormalizeDiscrete.getInstrument(),
			statesNormalizeDiscrete.getPeriod());
		this.statesNormalizeDiscrete = statesNormalizeDiscrete;
	}

	/**
	 * Returns the states discrete related statistics.
	 * 
	 * @return The states discrete related statistics.
	 */
	public StatesNormalizeDiscrete getStatesNormalizeDiscrete() {
		return statesNormalizeDiscrete;
	}

	/**
	 * Setup after adding the averages.
	 */
	@Override
	protected void setup() {
		setupFromTable();
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		return new TaskStatesTransitions(this);
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

		// Input and output keys.
		table.addField(getFieldKeyInput());
		table.addField(getFieldKeyOutput());
		// Input and output indexes.
		table.addField(getFieldIndexInput());
		table.addField(getFieldIndexOutput());
		// Group.
		table.addField(getFieldGroup());

		// Primary key on each field.
		getFieldKeyInput().setPrimaryKey(true);
		getFieldKeyOutput().setPrimaryKey(true);
		getFieldIndexInput().setPrimaryKey(true);
		getFieldIndexOutput().setPrimaryKey(true);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	@Override
	public RecordSet getRecordSet() {
		return null;
	}

	/**
	 * Returns the list of plot datas to configure a chart and show the statistics results.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		// TODO Auto-generated method stub
		return null;
	}

}
