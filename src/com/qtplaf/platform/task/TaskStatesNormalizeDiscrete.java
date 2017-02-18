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

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.platform.statistics.StatesAverages;
import com.qtplaf.platform.statistics.StatesAverages.Fields;
import com.qtplaf.platform.statistics.StatesNormalizeDiscrete;

/**
 * Task to calculate the normalized states values discrete. Values have been previously normalized continuous [-1, 1].
 *
 * @author Miquel Sas
 */
public class TaskStatesNormalizeDiscrete extends TaskStatesAverages {

	/** The parent states normalize statistics. */
	private StatesNormalizeDiscrete statesNormalize;

	/**
	 * Constructor.
	 * 
	 * @param statesNormalize The parent states normalize statistics.
	 */
	public TaskStatesNormalizeDiscrete(StatesNormalizeDiscrete statesNormalize) {
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
		return statesNormalize.getStatesNormalizeContinuous().getTable().getPersistor();
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
			order.add(sourcePersistor.getField(Fields.Index));

			// Source iterator.
			iterator = sourcePersistor.iterator(null, order);
			
			// Names to calculate ranges and descriptor.
			List<Field> normalizeDiscreteFields = statesNormalize.getFieldsToNormalizeDiscrete();
			int scale = statesNormalize.getScale();
			double maximum = 1.0;
			double minimum = -1.0;
			NormalizedStateValueDescriptor descriptor = new NormalizedStateValueDescriptor(maximum, minimum, scale);
			descriptor.setSegments(20);
		
			// List of fields for the key.
			List<Field> fieldsKey = statesNormalize.getFieldsKey();
			String key = statesNormalize.getFieldKey().getName();
			
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
				List<Field> averageFields = statesNormalize.getAverageFields();
				for (Field field : averageFields) {
					String name = field.getName();
					normalizeRecord.setValue(name, sourceRecord.getValue(name));
				}
				
				// Spread fields over the fast average are not discretized.
				List<Field> spreadFast = statesNormalize.getSpreadFieldsFastAverage();
				for (Field field : spreadFast) {
					String name = field.getName();
					normalizeRecord.setValue(name, sourceRecord.getValue(name));
				}
				
				// Ranges.
				for (Field field : normalizeDiscreteFields) {
					String name = field.getName();
					NormalizedStateValueDescriptor normalizer = StatesAverages.getNormalizer(field);
					double raw = sourceRecord.getValue(name).getDouble();
					double value = normalizer.getValue(raw);
					normalizeRecord.getValue(name).setDouble(value);
				}
				
				// Keys.
				double[] keyValues = new double[fieldsKey.size()];
				for (int i = 0; i < fieldsKey.size(); i++) {
					String name = fieldsKey.get(i).getName();
					keyValues[i] = normalizeRecord.getValue(name).getDouble();
				}
				normalizeRecord.getValue(key).setString(NumberUtils.getStringKey(keyValues, scale));
				
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