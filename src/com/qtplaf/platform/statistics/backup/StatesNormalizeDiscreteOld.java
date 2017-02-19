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

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.task.TaskStatesNormalizeDiscrete;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Normalizes source values in discrete mode.
 *
 * @author Miquel Sas
 */
public class StatesNormalizeDiscreteOld extends StatesAveragesOld {

	/**
	 * Keys.
	 */
	public static class Keys {
		/** Soft key. */
		public static final String Soft = "soft";
		/** Hard key. */
		public static final String Hard = "hard";
	}

	/** States continuous related statistics. */
	private StatesNormalizeContinuousOld statesNormalizeContinuous;
	/** Normalize scale. */
	private int scale = 2;

	/**
	 * Constructor.
	 * 
	 * @param statesNormalizeContinuous The parent states normalized continuous statistics.
	 */
	public StatesNormalizeDiscreteOld(StatesNormalizeContinuousOld statesNormalizeContinuous) {
		super(
			statesNormalizeContinuous.getSession(),
			statesNormalizeContinuous.getServer(),
			statesNormalizeContinuous.getInstrument(),
			statesNormalizeContinuous.getPeriod());
		this.statesNormalizeContinuous = statesNormalizeContinuous;
	}

	/**
	 * Returns the scale.
	 * 
	 * @return The scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Set the scale.
	 * 
	 * @param scale The scale.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * Setup after adding the averages.
	 */
	@Override
	protected void setup() {
		setupFromTable();
	}

	/**
	 * Returns the parent or source statistics.
	 * 
	 * @return The parent or source statistics.
	 */
	public StatesNormalizeContinuousOld getStatesNormalizeContinuous() {
		return statesNormalizeContinuous;
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		return new TaskStatesNormalizeDiscrete(this);
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {
		Table table = getTableForSourceAndNormalizedStatistics();
		// Keys.
		table.addField(getFieldKey());

		// Unique index on Index.
		Index index = new Index();
		index.add(getFieldKey());
		index.setUnique(false);
		table.addIndex(index);
		
		// Must set persistor.
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

	/**
	 * Returns the list of fields for the key.
	 * 
	 * @return The list of fields for the key.
	 */
	public List<Field> getFieldsKey() {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getSpreadFields());
		fields.addAll(getSpeedFields());
		return fields;
	}
}
