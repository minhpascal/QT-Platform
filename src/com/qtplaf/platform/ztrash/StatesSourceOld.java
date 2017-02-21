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

package com.qtplaf.platform.ztrash;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.server.Server;

/**
 * Retrieves the source values for the series of analisys to build the wave descriptor states and transitions. Contains
 * the source candle values, the series of rainbow averages smoothed, their relative spreads and their speed (tangent),
 * and finally the spreads of high, low and close over the fastest average. First add the averages and then call setup.
 *
 * @author Miquel Sas
 */
public class StatesSourceOld extends StatesAveragesOld {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesSourceOld(Session session, Server server, Instrument instrument, Period period) {
		super(session, server, instrument, period);
	}

	/**
	 * Setup after adding the averages.
	 */
	protected void setup() {
		setupFromTable();
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	public Task getTask() {
		return new TaskStatesSourceOld(this);
	}

	/**
	 * Returns the list of fields that should be included in a <tt>Data</tt> of the corresponding indicator. It excludes
	 * index, time and formatted time.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getIndicatorOutputFields() {
		List<Field> fields = new ArrayList<>();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			if (table.getField(i).equals(getFieldIndex())) {
				continue;
			}
			if (table.getField(i).equals(getFieldTime())) {
				continue;
			}
			if (table.getField(i).equals(getFieldTimeFmt())) {
				continue;
			}
			fields.add(table.getField(i));
		}
		return fields;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
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
		configure(persistor, true);
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

		return plotDataList;
	}
}
