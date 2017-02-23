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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.qtplaf.library.statistics.Statistics#getTask()
	 */
	@Override
	public Task getTask() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.qtplaf.library.statistics.Statistics#getTable()
	 */
	@Override
	public Table getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.qtplaf.library.statistics.Statistics#getRecordSet()
	 */
	@Override
	public RecordSet getRecordSet() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.qtplaf.library.statistics.Statistics#getPlotDataList()
	 */
	@Override
	public List<PlotData> getPlotDataList() {
		// TODO Auto-generated method stub
		return null;
	}

}
