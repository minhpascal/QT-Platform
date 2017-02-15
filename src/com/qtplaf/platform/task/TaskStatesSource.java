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

package com.qtplaf.platform.task;

import java.util.List;

import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.IndicatorDataList;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.platform.indicators.StatesSourceIndicator;
import com.qtplaf.platform.statistics.StatesSource;

/**
 * The task that calculates the states source statistics.
 *
 * @author Miquel Sas
 */
public class TaskStatesSource extends TaskStatesAverages {

	/** The indicator used to perform calculations. */
	private StatesSourceIndicator indicator;

	/**
	 * Constructor.
	 * 
	 * @param statesSource The states source statistics.
	 */
	public TaskStatesSource(StatesSource statesSource) {
		super(statesSource.getSession());
		this.indicator = new StatesSourceIndicator(statesSource);

		StringBuilder name = new StringBuilder();
		name.append(statesSource.getServer().getId());
		name.append("-");
		name.append(statesSource.getInstrument().getId());
		name.append("-");
		name.append(statesSource.getPeriod().toString());
		name.append("-");
		name.append(statesSource.getId());
		setName(name.toString());

		StringBuilder desc = new StringBuilder();
		desc.append(statesSource.getServer().getName());
		desc.append(" - ");
		desc.append(statesSource.getInstrument().getId());
		desc.append(" - ");
		desc.append(statesSource.getPeriod().toString());
		desc.append(" - ");
		desc.append(statesSource.getDescription());
		setDescription(desc.toString());
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
		Table table = indicator.getStatesSource().getTable();
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
