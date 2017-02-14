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

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.DelegateDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Formatters;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.task.TaskStatesNormalize;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 *
 *
 * @author Miquel Sas
 */
public class StatesNormalize extends StatesAverages {

	/** Field names. */
	public static class Fields {
		public static final String Index = "index";
		public static final String Time = "time";
		public static final String TimeFmt = "time_fmt";
		public static final String Open = "open";
		public static final String High = "high";
		public static final String Low = "low";
		public static final String Close = "close";
		public static final String Range = "range";
		public static final String Key = "state_key";
	}

	/** States ranges related statistics. */
	private StatesRanges statesRanges;
	/** The scale for normalization. */
	private int scale = 1;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesNormalize(StatesRanges statesRanges) {
		super(
			statesRanges.getSession(),
			statesRanges.getServer(),
			statesRanges.getInstrument(),
			statesRanges.getPeriod());
		this.statesRanges = statesRanges;
		setScale(4);
	}

	/**
	 * Returns the normalize scale.
	 * 
	 * @return The normalize scale.
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Set the normalize scale.
	 * 
	 * @param scale The normalize scale.
	 */
	public void setScale(int scale) {
		this.scale = scale;
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
	 * Returns the list of field names to calculate the status key.
	 * 
	 * @return The list of fieldd names.
	 */
	public List<String> getNamesStateKey() {
		List<String> names = new ArrayList<>();

		// Spreads between averages.
		for (int i = 0; i < getAverages().size(); i++) {
			Average averageFast = getAverages().get(i);
			for (int j = i + 1; j < getAverages().size(); j++) {
				Average averageSlow = getAverages().get(j);
				names.add(Average.getSpreadName(averageFast, averageSlow));
			}
		}

		// Speed (tangent) of averages.
		for (int i = 0; i < getAverages().size(); i++) {
			Average average = getAverages().get(i);
			names.add(Average.getSpeedName(average));
		}

		return names;
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	@Override
	public Task getTask() {
		return new TaskStatesNormalize(this);
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
		table.addField(DomainUtils.getIndex(getSession(), Fields.Index));
		table.addField(DomainUtils.getTime(getSession(), Fields.Time));
		table.addField(DomainUtils.getTimeFmt(getSession(), Fields.TimeFmt));
		table.addField(DomainUtils.getOpen(getSession(), Fields.Open));
		table.addField(DomainUtils.getHigh(getSession(), Fields.High));
		table.addField(DomainUtils.getLow(getSession(), Fields.Low));
		table.addField(DomainUtils.getClose(getSession(), Fields.Close));

		// Averages fields.
		table.addFields(getAverageFields());

		// Percentual range
		{
			String name = Fields.Range;
			String header = Fields.Range;
			String label = "Range factor";
			table.addField(DomainUtils.getDouble(getSession(), name, name, header, label, label));
		}

		// Price spreads over the first (fastest) average.
		table.addField(Average.getSpreadField(getSession(), Fields.High, getAverages().get(0)));
		table.addField(Average.getSpreadField(getSession(), Fields.Low, getAverages().get(0)));
		table.addField(Average.getSpreadField(getSession(), Fields.Close, getAverages().get(0)));

		// Spreads between averages.
		table.addFields(getSpreadFields());

		// Speed (tangent) of averages.
		table.addFields(getSpeedFields());

		// State key field.
		Field stkey = new Field();
		stkey.setName(Fields.Key);
		stkey.setType(Types.String);
		stkey.setLength(100);
		stkey.setHeader("Key");
		stkey.setLabel("State key");
		table.addField(stkey);

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
		Formatters.configureStatesNormalize(persistor, this);
		return new DataRecordSet(persistor);
	}

	/**
	 * Returns the list of plot datas to configure a chart and show the statistics results.
	 * 
	 * @return The list of plot datas.
	 */
	@Override
	public List<PlotData> getPlotDataList() {

		// First data list: price and indicators.
		DataInfo info = new DataInfo(getSession());
		info.setDataType(DataType.Indicator);
		info.setInstrument(getInstrument());
		info.setName(getInstrument().getId());
		info.setDescription(getInstrument().getDescription());
		info.setPeriod(getPeriod());

		// The data list.
		PersistorDataList dataList = new PersistorDataList(getSession(), info, getTable().getPersistor());

		// Candlestick on price: info
		info.addOutput("Open", "O", dataList.getDataIndex(Fields.Open), "Open data value");
		info.addOutput("High", "H", dataList.getDataIndex(Fields.High), "High data value");
		info.addOutput("Low", "L", dataList.getDataIndex(Fields.Low), "Low data value");
		info.addOutput("Close", "C", dataList.getDataIndex(Fields.Close), "Close data value");

		// Candlestick on price: plotter.
		CandlestickPlotter plotterCandle = new CandlestickPlotter();
		plotterCandle.setIndexes(new int[] {
			dataList.getDataIndex(Fields.Open),
			dataList.getDataIndex(Fields.High),
			dataList.getDataIndex(Fields.Low),
			dataList.getDataIndex(Fields.Close) });
		dataList.addDataPlotter(plotterCandle);

		// Line plotter for each average. Skip the rane percentage.
		for (int i = 0; i < getAverages().size(); i++) {
			Average average = getAverages().get(i);
			String name = Average.getAverageName(average);
			String label = Average.getAverageLabel(average);
			String header = Average.getAverageHeader(average);
			int index = dataList.getDataIndex(name);

			// Output info.
			info.addOutput(label, header, index, label);

			// Plotter.
			LinePlotter plotterAvg = new LinePlotter();
			plotterAvg.setIndex(index);
			dataList.addDataPlotter(plotterAvg);
		}

		PlotData plotData = new PlotData();
		plotData.add(dataList);

		// Data list for spreads
		DataInfo infoSpread = new DataInfo(getSession());
		DataList dataSpread = new DelegateDataList(getSession(), infoSpread, dataList);

		infoSpread.setDataType(DataType.Indicator);
		infoSpread.setInstrument(getInstrument());
		infoSpread.setName(getInstrument().getId());
		infoSpread.setDescription(getInstrument().getDescription());
		infoSpread.setPeriod(getPeriod());
		// Spreads between averages.
		for (int i = 0; i < getAverages().size(); i++) {
			Average averageFast = getAverages().get(i);
			for (int j = i + 1; j < getAverages().size(); j++) {
				Average averageSlow = getAverages().get(j);
				String name = Average.getSpreadName(averageFast, averageSlow);
				String header = Average.getSpreadHeader(averageFast, averageSlow);
				String label = Average.getSpreadLabel(averageFast, averageSlow);
				int index = dataList.getDataIndex(name);

				// Output info.
				info.addOutput(label, header, index, label);

				// Plotter.
				LinePlotter plotterSpread = new LinePlotter();
				plotterSpread.setIndex(index);
				dataSpread.addDataPlotter(plotterSpread);
			}
		}

		PlotData plotSpread = new PlotData();
		plotSpread.add(dataSpread);

		List<PlotData> plotDataList = new ArrayList<>();
		plotDataList.add(plotData);
		plotDataList.add(plotSpread);

		return plotDataList;
	}
}
