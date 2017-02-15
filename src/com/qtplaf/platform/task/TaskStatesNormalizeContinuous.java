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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.platform.statistics.Average;
import com.qtplaf.platform.statistics.StatesAverages.Fields;
import com.qtplaf.platform.statistics.StatesNormalizeContinuous;
import com.qtplaf.platform.statistics.StatesRanges;
import com.qtplaf.platform.statistics.StatesSource;

/**
 * Task to calculate the normalized states values.
 *
 * @author Miquel Sas
 */
public class TaskStatesNormalizeContinuous extends TaskStatesAverages {

	/** The parent states normalize statistics. */
	private StatesNormalizeContinuous statesNormalize;

	/** A map with normailzed state value descriptors by field name. */
	private Map<String, NormalizedStateValueDescriptor> descriptorsMap = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param statesNormalize The parent states normalize statistics.
	 */
	public TaskStatesNormalizeContinuous(StatesNormalizeContinuous statesNormalize) {
		super(statesNormalize.getSession());
		this.statesNormalize = statesNormalize;
		setNameAndDescription(statesNormalize);
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

		// Do count.
		long count = getSourcePersistor().count(null);

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Returns the source persistor.
	 * 
	 * @return The source persistor.
	 */
	private Persistor getSourcePersistor() {
		return statesNormalize.getStatesSource().getTable().getPersistor();
	}

	/**
	 * Fill the map of normalized state value descriptors.
	 * 
	 * @throws Exception
	 */
	private void fillDescriptorsMap() throws Exception {
		double stddevs = 2;
		RecordSet recordSet = statesNormalize.getStatesRanges().getRecordSet(false);
		for (int i = 0; i < recordSet.size(); i++) {
			Record record = recordSet.get(i);
			String fieldName = record.getValue(StatesRanges.Fields.Name).getString();
			String minMax = record.getValue(StatesRanges.Fields.MinMax).getString();
			double average = record.getValue(StatesRanges.Fields.Average).getDouble();
			double stddev = record.getValue(StatesRanges.Fields.StdDev).getDouble();
			NormalizedStateValueDescriptor descriptor = descriptorsMap.get(fieldName);
			if (descriptor == null) {
				descriptor = new NormalizedStateValueDescriptor();
				descriptor.setMaximum(0);
				descriptor.setMinimum(0);
				descriptorsMap.put(fieldName, descriptor);
			}
			if (minMax.equals("min")) {
				descriptor.setMinimum(average - (stddev * stddevs));
			} else {
				descriptor.setMaximum(average + (stddev * stddevs));
			}
		}
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
			
			// Fill descriptors map.
			fillDescriptorsMap();

			// Result table and persistor.
			Table tableNormalize = statesNormalize.getTable();
			Persistor normalizePersistor = new DataPersistor(tableNormalize.getPersistor());

			// Drop and create the table.
			if (normalizePersistor.getDDL().existsTable(tableNormalize)) {
				normalizePersistor.getDDL().dropTable(tableNormalize);
			}
			normalizePersistor.getDDL().buildTable(tableNormalize);

			// Source persistor.
			Persistor sourcePersistor = getSourcePersistor();
			Order order = new Order();
			order.add(sourcePersistor.getField(StatesSource.Fields.Index));

			// Source iterator.
			iterator = sourcePersistor.iterator(null, order);
			
			// Names to calculate ranges.
			List<String> ranges = statesNormalize.getStatesSource().getNamesToCalculateRanges();
			
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
				Record sourceRecord = iterator.next();
				Value time = sourceRecord.getValue(Fields.Time);
				Value open = sourceRecord.getValue(Fields.Open);
				Value high = sourceRecord.getValue(Fields.High);
				Value low = sourceRecord.getValue(Fields.Low);
				Value close = sourceRecord.getValue(Fields.Close);
				
				Record normalizeRecord = normalizePersistor.getDefaultRecord();
				normalizeRecord.setValue(Fields.Time, time);
				normalizeRecord.setValue(Fields.Open, open);
				normalizeRecord.setValue(Fields.High, high);
				normalizeRecord.setValue(Fields.Low, low);
				normalizeRecord.setValue(Fields.Close, close);
				
				// Averages.
				for (int i = 0; i < statesNormalize.getAverages().size(); i++) {
					Average average = statesNormalize.getAverages().get(i);
					String name = Average.getAverageName(average);
					normalizeRecord.setValue(name, sourceRecord.getValue(name));
				}
				
				// Ranges.
				for (String range : ranges) {
					NormalizedStateValueDescriptor descriptor = descriptorsMap.get(range);
					double raw = sourceRecord.getValue(range).getDouble();
					double value = descriptor.getValue(raw);
					normalizeRecord.getValue(range).setDouble(value);
				}
				
				// Save record.
				normalizePersistor.insert(normalizeRecord);
				
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
