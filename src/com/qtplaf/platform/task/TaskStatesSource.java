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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.trading.data.Data;
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
public class TaskStatesSource extends TaskRunner {
	
	/** The indicator used to perform calculations. */
	private StatesSourceIndicator indicator;

	/**
	 * @param session
	 */
	public TaskStatesSource(Session session, StatesSource statesSource) {
		super(session);
		this.indicator = new StatesSourceIndicator(session, statesSource);
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
		
		// The list of indicator data lists that must be calculated prior as sources.
		List<IndicatorDataList> sourceLists = indicator.getIndicatorDataListsToCalculate();
		// And the result indicator data list.
		IndicatorDataList indicatorList = indicator.getDataList();
		
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
			for (IndicatorDataList sourceList : sourceLists) {
				sourceList.calculate(index);
			}
			// Calculate the result indicator and save the data.
			Data data = indicatorList.calculate(index);
			persistor.insert(data);
			
			// Skip to next index.
			index++;
			
			// Notify step end.
			notifyStepEnd();
			// Yield.
			Thread.yield();
		}
		
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests. This task supports cancel.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task supports counting steps.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This task is not indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. This task supports pause.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}

}
