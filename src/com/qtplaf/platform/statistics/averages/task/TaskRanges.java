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
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.configuration.Range;
import com.qtplaf.platform.statistics.averages.Ranges;

/**
 * Calculate ranges (min-max) values.
 *
 * @author Miquel Sas
 */
public class TaskRanges extends TaskAverages {

	/** Underlying ranges statistics. */
	private Ranges ranges;
	/** States data list. */
	private PersistorDataList statesList;

	/**
	 * Constructor.
	 * 
	 * @param ranges The ranges statistics.
	 */
	public TaskRanges(Ranges ranges) {
		super(ranges.getSession());
		this.ranges = ranges;
		this.statesList = ranges.getStates().getDataListStates();

		setNameAndDescription(ranges, "Ranges (min-max) values");
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
		int count = statesList.size();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returns the result record.
	 * 
	 * @param persistor The persistor.
	 * @param name The field name.
	 * @param period The period.
	 * @param minimum Minimum/maximum.
	 * @param value The value.
	 * @param index Source index.
	 * @param time Source time.
	 * @return The record.
	 */
	private Record getRecord(
		Persistor persistor,
		String name,
		int period,
		boolean minimum,
		double value,
		int index,
		long time) {
		Record record = persistor.getDefaultRecord();
		record.setValue(ranges.getFields().getName().getName(), name);
		record.setValue(Fields.Period, period);
		record.setValue(Fields.MinMax, (minimum ? "min" : "max"));
		record.setValue(Fields.Value, value);
		record.setValue(Fields.Index, index);
		record.setValue(Fields.Time, time);
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
		Table table = ranges.getTable();
		Persistor persistor = table.getPersistor();

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// List of ranges.
		List<Range> rangeList = ranges.getConfiguration().getRanges();

		// Set the states list cache size.
		int cacheSize = -1;
		for (Range range : rangeList) {
			cacheSize = Math.max(cacheSize, range.getPeriod());
		}
		statesList.setCacheSize(cacheSize * 10);

		// Fields to calculate ranges.
		List<Field> fields = ranges.getFieldListToCalculateRanges();

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
			for (Field field : fields) {
				String name = field.getName();
				int valueIndex = statesList.getDataInfo().getOutputIndex(name);
				double value = statesList.get(index).getValue(valueIndex);
				if (value == 0) {
					continue;
				}
				for (Range range : rangeList) {
					int period = range.getPeriod();
					if (value < 0) {
						if (statesList.isMinimum(index, valueIndex, period)) {
							long time = statesList.get(index).getTime();
							Record record = getRecord(persistor, name, period, true, value, index, time);
							persistor.insert(record);
						}
					}
					if (value > 0) {
						if (statesList.isMaximum(index, valueIndex, period)) {
							long time = statesList.get(index).getTime();
							Record record = getRecord(persistor, name, period, false, value, index, time);
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

}
