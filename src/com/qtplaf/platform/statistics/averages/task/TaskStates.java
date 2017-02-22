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

import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.platform.indicators.StatesIndicator;
import com.qtplaf.platform.statistics.averages.States;

/**
 *
 *
 * @author Miquel Sas
 */
public class TaskStates extends TaskAverages {

	/** Underlying states source statistics. */
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

		setNameAndDescription(states);
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

		// And the result indicator data list.
		IndicatorDataList indicatorList = indicator.getDataList();
		// The list of indicator data lists that must be calculated prior as sources.
		List<IndicatorDataList> sources = indicator.getIndicatorDataListsToCalculate();

		// All lists involved.
		List<DataList> dataLists = DataList.getDataLists(indicatorList);

		// No need to maintain all data cached in the indicator data lists. Remove when max lookback is achieved.
		int lookBackward = 0;
		for (IndicatorDataList source : sources) {
			lookBackward = Math.max(lookBackward, source.getIndicator().getIndicatorInfo().getLookBackward());
		}
		lookBackward = Math.max(lookBackward, indicatorList.getIndicator().getIndicatorInfo().getLookBackward());

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
			persistor.insert(data);

			// Remove when index greater than max look backward.
			if (index > lookBackward) {
				int start = index - lookBackward;
				for (DataList dataList : dataLists) {
					for (int i = start; i >= 0; i--) {
						if (dataList.remove(i) == null) {
							break;
						}
					}
				}
			}

			// Skip to next index.
			index++;

			// Notify step end.
			notifyStepEnd();
			// Yield.
			Thread.yield();
		}

	}

}
