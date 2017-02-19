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

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.task.TaskStatesNormalizeContinuous;

/**
 * Normalizes source values in continuous mode.
 *
 * @author Miquel Sas
 */
public class StatesNormalizeContinuousOld extends StatesAveragesOld {

	/** States ranges related statistics. */
	private StatesRangesOld statesRanges;

	/**
	 * Constructor.
	 * 
	 * @param statesRanges The states ranges statistics.
	 */
	public StatesNormalizeContinuousOld(StatesRangesOld statesRanges) {
		super(
			statesRanges.getSession(),
			statesRanges.getServer(),
			statesRanges.getInstrument(),
			statesRanges.getPeriod());
		this.statesRanges = statesRanges;
	}

	/**
	 * Returns the states ranges related statistics.
	 * 
	 * @return The states ranges related statistics.
	 */
	public StatesRangesOld getStatesRanges() {
		return statesRanges;
	}

	/**
	 * Returns the states source related statistics.
	 * 
	 * @return The states source related statistics.
	 */
	public StatesSourceOld getStatesSource() {
		return statesRanges.getStatesSource();
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
		return new TaskStatesNormalizeContinuous(this);
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		return getTableForSourceAndNormalizedStatistics();
	}

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	@Override
	public RecordSet getRecordSet() {
		DataPersistor persistor = new DataPersistor(getTable().getPersistor());
		configure(persistor, false);
		return new DataRecordSet(persistor);
	}

	/**
	 * Returns the list of plot datas to configure a chart and show the statistics results.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {

		// The data list.
		PersistorDataList dataList =
			new PersistorDataList(getSession(), new DataInfo(getSession()), getTable().getPersistor());
		dataList.setCacheSize(10000);
		
		List<PlotData> plotDataList = new ArrayList<>();
		plotDataList.add(getPlotDataMain(dataList));
		plotDataList.add(getPlotData(dataList, getSpreadFields()));
		plotDataList.add(getPlotData(dataList, getSpeedFields()));

		return plotDataList;
	}
}
