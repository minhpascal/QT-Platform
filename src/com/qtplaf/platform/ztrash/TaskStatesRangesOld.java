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

import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.ztrash.AverageOld.Range;
import com.qtplaf.platform.ztrash.StatesAveragesOld.Fields;

/**
 * Calculates minimums and maximums for states source values.
 *
 * @author Miquel Sas
 */
public class TaskStatesRangesOld extends TaskStatesAveragesOld {

	/** The parent states ranges statistics. */
	private StatesRangesOld statesRanges;
	/** Origin states source statistics. */
	private StatesSourceOld statesSource;
	/** The states source indicator used to retrieve indexes of values. */
	private StatesSourceIndicatorOld indicator;
	/** The persistor data list to retrieve states source data. */
	private PersistorDataList sourceList;

	/**
	 * Constructor.
	 * 
	 * @param statesRanges The parent states ranges statistics.
	 */
	public TaskStatesRangesOld(StatesRangesOld statesRanges) {
		super(statesRanges.getSession());
		this.statesRanges = statesRanges;
		this.statesSource = statesRanges.getStatesSource();
		this.indicator = new StatesSourceIndicatorOld(statesSource);

		DataInfo info = indicator.getIndicatorInfo();
		Table table = indicator.getStatesSource().getTable();
		DataPersistor persistor = new DataPersistor(table.getPersistor());
		this.sourceList = new PersistorDataList(getSession(), info, persistor);
		
		setNameAndDescription(statesRanges);
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
		record.setValue(Fields.Name, name);
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
		Table table = statesRanges.getTable();
		Persistor persistor = table.getPersistor();

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);
		
		// List of ranges.
		List<Range> ranges = statesSource.getConfiguration().getRanges();

		// Fields to calculate ranges.
		List<Field> fields = statesSource.getFieldsToCalculateRanges();

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
				int valueIndex = indicator.getIndicatorInfo().getOutputIndex(name);
				double value = sourceList.get(index).getValue(valueIndex);
				if (value == 0) {
					continue;
				}
				for (Range range : ranges) {
					int period = range.getPeriod();
					if (value < 0) {
						if (sourceList.isMinimum(index, valueIndex, period)) {
							long time = sourceList.get(index).getTime();
							Record record = getRecord(persistor, name, period, true, value, index, time);
							persistor.insert(record);
						}
					}
					if (value > 0) {
						if (sourceList.isMaximum(index, valueIndex, period)) {
							long time = sourceList.get(index).getTime();
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