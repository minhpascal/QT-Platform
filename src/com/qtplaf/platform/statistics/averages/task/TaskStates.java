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

package com.qtplaf.platform.statistics.averages.task;

import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.IndicatorInfo;
import com.qtplaf.platform.indicators.StatesIndicator;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;

/**
 * Calculates source states values.
 *
 * @author Miquel Sas
 */
public class TaskStates extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** States indicator. */
	private StatesIndicator indicator;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics.
	 */
	public TaskStates(States states) {
		super(states.getSession());
		this.states = states;
		this.indicator = new StatesIndicator(states);

		setNameAndDescription(states, "States raw values");
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * This task supports counting steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {

		// Notify counting.
		notifyCounting();

		// The source price data list.
		PersistorDataList price = indicator.getDataListPrice();

		// Number of steps.
		int count = price.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Count steps.
		countSteps();

		// Result table and persistor.
		Table table = states.getTable();
		DataPersistor persistor = new DataPersistor(table.getPersistor());

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// And the result indicator info and data list.
		IndicatorInfo info = indicator.getIndicatorInfo();
		IndicatorDataList indicatorList = indicator.getDataList();
		// The list of indicator data lists that must be calculated prior as sources.
		List<IndicatorDataList> sources = indicator.getIndicatorDataListsToCalculate();

		// The current index to calculate.
		int index = 0;

		// Step and steps.
		long step = 0;
		long steps = getSteps();
		while (step < steps) {

			// Check request of cancel.
			if (checkCancel()) {
				break;
			}

			// Check pause resume.
			if (checkPause()) {
				continue;
			}

			// Increase step.
			step++;
			// Notify step start.
			notifyStepStart(step, getStepMessage(step, steps, null, null));

			// Calculate required sources for the current index.
			for (IndicatorDataList source : sources) {
				source.calculate(index);
			}
			// Calculate the result indicator and save the data.
			Data data = indicatorList.calculate(index);

			// Indicator data contains open, high, low, close and the averages. Raw spreads and speed will be calculated
			// here.
			Record record = persistor.getDefaultRecord();

			// Time.
			record.getValue(states.getFieldDefTime().getName()).setLong(data.getTime());

			// Open, high, low, close.
			{
				String open = states.getFieldDefOpen().getName();
				String high = states.getFieldDefHigh().getName();
				String low = states.getFieldDefLow().getName();
				String close = states.getFieldDefClose().getName();
				record.getValue(open).setDouble(data.getValue(info.getOutputIndex(open)));
				record.getValue(high).setDouble(data.getValue(info.getOutputIndex(high)));
				record.getValue(low).setDouble(data.getValue(info.getOutputIndex(low)));
				record.getValue(close).setDouble(data.getValue(info.getOutputIndex(close)));
			}

			// Averages.
			{
				List<Field> fields = states.getFieldListAverages();
				for (Field field : fields) {
					String name = field.getName();
					record.getValue(name).setDouble(data.getValue(info.getOutputIndex(name)));
				}
			}

			// Raw price spreads vs the fastest average.
			{
				List<Field> fields = states.getFieldListSpreadsAverageRaw();
				for (Field field : fields) {
					String srcName = states.getPropertySourceField(field).getName();
					String avgName = states.getPropertyAverage(field).getName();
					double srcValue = data.getValue(info.getOutputIndex(srcName));
					double avgValue = data.getValue(info.getOutputIndex(avgName));
					double spread = (srcValue / avgValue) - 1;
					record.getValue(field.getName()).setDouble(spread);
				}
			}

			// Raw spreads between averages.
			{
				List<Field> fields = states.getFieldListSpreadsRaw();
				for (Field field : fields) {
					Spread spread = states.getPropertySpread(field);
					String avgFastName = spread.getFastAverage().getName();
					String avgSlowName = spread.getSlowAverage().getName();
					double valueFast = data.getValue(info.getOutputIndex(avgFastName));
					double valueSlow = data.getValue(info.getOutputIndex(avgSlowName));
					double valueSpread = (valueFast / valueSlow) - 1;
					record.getValue(field.getName()).setDouble(valueSpread);
				}
			}
			
			// Raw speeds of averages.
			if (index > 0) {
				Data prev = indicatorList.get(index - 1);
				List<Field> fields = states.getFieldListSpeedsRaw();
				for (Field field : fields) {
					Speed speed = states.getPropertySpeed(field);
					String avgName = speed.getAverage().getName();
					double valueCurr = data.getValue(info.getOutputIndex(avgName));
					double valuePrev = prev.getValue(info.getOutputIndex(avgName));
					double valueSpeed = (valueCurr / valuePrev) - 1;
					record.getValue(field.getName()).setDouble(valueSpeed);
				}
			}
			
			// Insert.
			persistor.insert(record);

			// Skip to next index.
			index++;

			// Notify step end.
			notifyStepEnd();
			// Yield.
			Thread.yield();
		}

	}

}
