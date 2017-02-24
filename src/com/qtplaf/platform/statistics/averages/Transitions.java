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

package com.qtplaf.platform.statistics.averages;

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.platform.statistics.averages.task.TaskTransitions;

/**
 * Transitions statistics.
 *
 * @author Miquel Sas
 */
public class Transitions extends Averages {

	/**
	 * @param session
	 */
	public Transitions(Session session) {
		super(session);
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		return new TaskTransitions(this);
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		return getTableTransitions();
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
