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
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.statistics.Output;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.task.TaskStatesSource;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Retrieves the source values for the series of analisys to build the wave descriptor states and transitions. Contains
 * the source candle values, the series of rainbow averages smoothed, their relative spreads and their speed (tangent),
 * and finally the spreads of high, low and close over the fastest average. First add the averages and then call setup.
 *
 * @author Miquel Sas
 */
public class StatesSource extends StatesAverages {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesSource(Session session, Server server, Instrument instrument, Period period) {
		super(session, server, instrument, period);
	}

	/**
	 * Setup after adding the averages.
	 */
	protected void setup() {
		if (getAverages().isEmpty()) {
			throw new IllegalStateException();
		}
		clear();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			Field field = table.getField(i);
			String name = field.getName();
			String description = field.getDisplayDescription();
			add(new Output(name, description, Types.Double));
		}

	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	public Task getTask() {
		return new TaskStatesSource(this);
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
	 * Returns the list of fields that should be included in a <tt>Data</tt> of the corresponding indicator. It excludes
	 * index, time and formatted time.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getIndicatorOutputFields() {
		List<Field> fields = new ArrayList<>();
		Table table = getTable();
		for (int i = 0; i < table.getFieldCount(); i++) {
			if (table.getField(i).getName().equals(Fields.Index)) {
				continue;
			}
			if (table.getField(i).getName().equals(Fields.Time)) {
				continue;
			}
			if (table.getField(i).getName().equals(Fields.TimeFmt)) {
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

		Table table = new Table();

		table.setName(getTableName());
		table.setSchema(Names.getSchema(getServer()));

		// Index, time and price fields.
		table.addField(DomainUtils.getIndex(getSession(), Fields.Index));
		table.addField(DomainUtils.getTime(getSession(), Fields.Time));
		table.addField(DomainUtils.getTimeFmt(getSession(), Fields.TimeFmt));
		table.addField(DomainUtils.getOpen(getSession(), Fields.Open));
		table.addField(DomainUtils.getHigh(getSession(), Fields.High));
		table.addField(DomainUtils.getLow(getSession(), Fields.Low));
		table.addField(DomainUtils.getClose(getSession(), Fields.Close));

		// Averages fields.
		table.addFields(getAverageFields());

		// Price spreads over the first (fastest) average.
		table.addFields(getSpreadFieldsFastAverage());

		// Spreads between averages.
		table.addFields(getSpreadFields());

		// Speed (tangent) of averages.
		table.addFields(getSpeedFields());

		// Primary key on Time.
		table.getField(Fields.Time).setPrimaryKey(true);

		// Unique index on Index.
		Index index = new Index();
		index.add(table.getField(Fields.Index));
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
