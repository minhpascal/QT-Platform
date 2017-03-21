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

import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.View;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.util.list.ListUtils;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Register performance on patterns indexes.
 *
 * @author Miquel Sas
 */
public class TaskPerformance extends TaskAverages {

	/** Underlying states statistics. */
	private States states;
	/** States data list. */
	private PersistorDataList statesList;

	/**
	 * Construtor.
	 * 
	 * @param states Underlying states statistics.
	 */
	public TaskPerformance(States states) {
		super(states.getSession());
		this.states = states;
		this.statesList = states.getStates().getDataListStates();

		setNameAndDescription(states, "Performance");
	}

	private View getIndexView() {
		Table table = states.getTablePatterns();

		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Index field.
		view.addField(table.getField(Fields.Index));

		// Group by.
		view.addGroupBy(table.getField(Fields.Index));

		// Order by.
		view.addOrderBy(table.getField(Fields.Index));

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));

		return view;
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
		Persistor persistor = getIndexView().getPersistor();
		int count = (int) persistor.count(null);

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

		// Source iterator.
		RecordIterator iterator = null;

		try {
			// Count steps.
			countSteps();

			// Result table and persistor.
			Table table = states.getTablePerformance();
			Persistor persistor = table.getPersistor();

			// Drop and create the table.
			if (persistor.getDDL().existsTable(table)) {
				persistor.getDDL().dropTable(table);
			}
			persistor.getDDL().buildTable(table);

			// Source iterator.
			iterator = getIndexView().getPersistor().iterator(new Criteria());

			// List of periods for performance.
			List<Integer> periods = states.getPerformancePeriods();
			int maxPeriod = ListUtils.getLast(periods);

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

				// End achieved? should not happen.
				if (!iterator.hasNext()) {
					break;
				}

				// Do calculate.
				Record rcIndex = iterator.next();
				int index = rcIndex.getValue(Fields.Index).getInteger();
				long time = statesList.getRecord(index).getValue(Fields.Time).getLong();
				double maximum = Double.MIN_VALUE;
				double minimum = Double.MAX_VALUE;
				for (int period = 1; period <= maxPeriod; period++) {
					int stateIndex = index + period;
					if (stateIndex < statesList.size()) {
						Record rcState = statesList.getRecord(stateIndex);
						double high = rcState.getValue(Fields.High).getDouble();
						double low = rcState.getValue(Fields.Low).getDouble();
						maximum = Math.max(maximum, high);
						minimum = Math.min(minimum, low);
						if (periods.contains(period)) {
							Record rcPerform = persistor.getDefaultRecord();
							rcPerform.setValue(Fields.Index, index);
							rcPerform.setValue(Fields.Time, time);
							rcPerform.setValue(Fields.Period, period);
							rcPerform.setValue(Fields.Maximum, maximum);
							rcPerform.setValue(Fields.Minimum, minimum);
							persistor.insert(rcPerform);
						}
					}
				}

				// Notify step end.
				notifyStepEnd();
				// Yield.
				Thread.yield();
			}

		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}

}
