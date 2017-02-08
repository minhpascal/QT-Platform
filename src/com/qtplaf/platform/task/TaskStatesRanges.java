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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.indicators.StatesSourceIndicator;
import com.qtplaf.platform.statistics.Average;
import com.qtplaf.platform.statistics.StatesRanges;
import com.qtplaf.platform.statistics.StatesSource;

/**
 *
 *
 * @author Miquel Sas
 */
public class TaskStatesRanges extends TaskRunner {

	/** The parent states ranges statistics. */
	private StatesRanges statesRanges;
	/** Origin states source statistics. */
	private StatesSource statesSource;
	/** The states source indicator used to retrieve indexes of values. */
	private StatesSourceIndicator indicator;
	/** The persistor data list to retrieve states source data. */
	private PersistorDataList sourceList;

	/**
	 * Constructor.
	 * 
	 * @param statesRanges The parent states ranges statistics.
	 */
	public TaskStatesRanges(StatesRanges statesRanges) {
		super(statesRanges.getSession());
		this.statesRanges = statesRanges;
		this.statesSource = statesRanges.getStatesSource();
		this.indicator = new StatesSourceIndicator(statesSource);

		DataInfo info = indicator.getIndicatorInfo();
		Table table = indicator.getStatesSource().getTable();
		DataPersistor persistor = new DataPersistor(table.getPersistor());
		this.sourceList = new PersistorDataList(getSession(), info, persistor);
		StringBuilder name = new StringBuilder();
		name.append(statesRanges.getServer().getId());
		name.append("-");
		name.append(statesRanges.getInstrument().getId());
		name.append("-");
		name.append(statesRanges.getPeriod().toString());
		name.append("-");
		name.append(statesRanges.getId());
		setName(name.toString());

		StringBuilder desc = new StringBuilder();
		desc.append(statesRanges.getServer().getName());
		desc.append(" - ");
		desc.append(statesRanges.getInstrument().getId());
		desc.append(" - ");
		desc.append(statesRanges.getPeriod().toString());
		desc.append(" - ");
		desc.append(statesRanges.getDescription());
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

		// Number of steps.
		int count = sourceList.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returtns the list of periods to calculate min-max values.
	 * 
	 * @return The list of periods.
	 */
	private List<Integer> getPeriods() {
		List<Integer> periods = new ArrayList<>();
		List<Average> averages = statesRanges.getAverages();
		for (Average average : averages) {
			periods.add(average.getPeriod());
		}
		return periods;
	}

	/**
	 * Returns the result record.
	 * 
	 * @param persistor The persistor.
	 * @param name The field name.
	 * @param period The period.
	 * @param minimum Minimum/maximum.
	 * @param value The value.
	 * @return The record.
	 */
	private Record getRecord(Persistor persistor, String name, int period, boolean minimum, double value) {
		Record record = persistor.getDefaultRecord();
		record.setValue(StatesRanges.Fields.Name, name);
		record.setValue(StatesRanges.Fields.Period, period);
		record.setValue(StatesRanges.Fields.MinMax, (minimum ? "min" : "max"));
		record.setValue(StatesRanges.Fields.Value, value);
		return record;
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
		Table table = statesRanges.getTable();
		Persistor persistor = table.getPersistor();

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// Names of fields to calculate ranges.
		List<String> names = statesSource.getNamesToCalculateRanges();
		// List of periods for min-max.
		List<Integer> periods = getPeriods();

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

			// Do calculate if min-max for each name and period.
			for (String name : names) {
				int valueIndex = indicator.getIndicatorInfo().getOutputIndex(name);
				double value = sourceList.get(index).getValue(valueIndex);
				if (value == 0) {
					continue;
				}
				for (int period : periods) {
					if (value < 0) {
						if (sourceList.isMinimum(index, valueIndex, period)) {
							Record record = getRecord(persistor, name, period, true, value);
							persistor.insert(record);
						}
					}
					if (value > 0) {
						if (sourceList.isMaximum(index, valueIndex, period)) {
							Record record = getRecord(persistor, name, period, false, value);
							persistor.insert(record);
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
