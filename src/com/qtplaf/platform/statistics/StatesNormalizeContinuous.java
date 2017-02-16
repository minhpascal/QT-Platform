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

import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.task.TaskStatesNormalizeContinuous;
import com.qtplaf.platform.util.PersistorUtils;

/**
 *
 *
 * @author Miquel Sas
 */
public class StatesNormalizeContinuous extends StatesAverages {

	/** States ranges related statistics. */
	private StatesRanges statesRanges;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesNormalizeContinuous(StatesRanges statesRanges) {
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
	public StatesRanges getStatesRanges() {
		return statesRanges;
	}

	/**
	 * Returns the states source related statistics.
	 * 
	 * @return The states source related statistics.
	 */
	public StatesSource getStatesSource() {
		return statesRanges.getStatesSource();
	}

	/**
	 * Setup after adding the averages.
	 */
	@Override
	protected void setup() {
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

		// Index, time and price fields.
		table.addField(getFieldIndex());
		table.addField(getFieldTime());
		table.addField(getFieldTimeFmt());
		table.addField(getFieldOpen());
		table.addField(getFieldHigh());
		table.addField(getFieldLow());
		table.addField(getFieldClose());

		// Averages fields.
		table.addFields(getAverageFields());

		// Price spreads over the first (fastest) average.
		table.addFields(getSpreadFieldsFastAverage());

		// Spreads between averages.
		table.addFields(getSpreadFields());

		// Speed (tangent) of averages.
		table.addFields(getSpeedFields());

		// Primary key on Time.
		getFieldTime().setPrimaryKey(true);

		// Unique index on Index.
		Index index = new Index();
		index.add(getFieldIndex());
		index.setUnique(true);
		table.addIndex(index);

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
		
		List<PlotData> plotDataList = new ArrayList<>();
		plotDataList.add(getPlotDataMain(dataList));
		plotDataList.add(getPlotData(dataList, getSpreadFields()));
		plotDataList.add(getPlotData(dataList, getSpeedFields()));

		return plotDataList;
	}
}
