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
import java.util.Map;

import com.qtplaf.library.ai.rlearning.function.Normalizer;
import com.qtplaf.library.database.Calculator;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Fields.Family;
import com.qtplaf.platform.database.configuration.Calculation;
import com.qtplaf.platform.statistics.averages.States;

/**
 * Calculates normalized states values.
 *
 * @author Miquel Sas
 */
public class TaskNormalizes extends TaskAverages {

	/** Underlying states statistics. */
	private States states;

	/**
	 * Constructor.
	 * 
	 * @param states The states statistics.
	 */
	public TaskNormalizes(States states) {
		super(states.getSession());
		this.states = states;

		setNameAndDescription(states, "Normalized values");
	}

	/**
	 * Returns the persistor data list to read and update data.
	 * 
	 * @return The persistor.
	 */
	private DataPersistor getPersistor() {
		return new DataPersistor(states.getTableStates().getPersistor());
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
		int count = Long.valueOf(countPenging()).intValue();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Count pending records.
	 * 
	 * @return The number of records to process.
	 */
	private long countPenging() throws Exception {
		return getPersistor().count(new Criteria());
	}

	/**
	 * Returns the select order.
	 * 
	 * @return The select order.
	 */
	private Order getSelectOrder() {
		Order order = new Order();
		order.add(states.getTableStates().getField(Fields.Index));
		return order;
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
			// Normalizers map.
			Map<String, Normalizer> mapNormalizers = states.getMapNormalizers();

			// Count steps.
			countSteps();

			// Persistor.
			DataPersistor persistor = getPersistor();

			// Source iterator.
			iterator = persistor.iterator(new Criteria(), getSelectOrder());

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
				Record record = iterator.next();

				// Spreads between averages.
				{
					List<Field> fieldsRaw = states.getFieldListSpreads(Fields.Suffix.raw);
					List<Field> fieldsNrm = states.getFieldListSpreads(Fields.Suffix.nrm);
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldNrm = fieldsNrm.get(i);
						Normalizer normalizer = mapNormalizers.get(fieldRaw.getName());
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueNrm = normalizer.getValue(valueRaw);
						record.getValue(fieldNrm.getName()).setDouble(valueNrm);
					}
				}

				// Slopes.
				{
					List<Field> fieldsRaw = states.getFieldListSlopes(Fields.Suffix.raw);
					List<Field> fieldsNrm = states.getFieldListSlopes(Fields.Suffix.nrm);
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldNrm = fieldsNrm.get(i);
						Normalizer normalizer = mapNormalizers.get(fieldRaw.getName());
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueCont = normalizer.getValue(valueRaw);
						record.getValue(fieldNrm.getName()).setDouble(valueCont);
					}
				}

				// Calculations: default family
				{
					List<Field> fieldsRaw = states.getFieldListCalculations(Family.Default, Fields.Suffix.raw);
					List<Field> fieldsNrm = states.getFieldListCalculations(Family.Default, Fields.Suffix.nrm);
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldNrm = fieldsNrm.get(i);
						Normalizer normalizer = mapNormalizers.get(fieldRaw.getName());
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueCont = normalizer.getValue(valueRaw);
						record.getValue(fieldNrm.getName()).setDouble(valueCont);
					}
				}
				
				// Calculations: family weighted sum nrm and dsc.
				{
					List<Field> fieldsNrm = states.getFieldListCalculations(Family.WeightedSum, Fields.Suffix.nrm);
					List<Field> fieldsDsc = states.getFieldListCalculations(Family.WeightedSum, Fields.Suffix.dsc);
					for (int i = 0; i < fieldsNrm.size(); i++) {
						Field fieldNrm = fieldsNrm.get(i);
						Field fieldDsc = fieldsDsc.get(i);
						Calculation calculation = (Calculation) fieldNrm.getProperty(Fields.Properties.Calculation);
						Calculator calculator = calculation.getCalculator();
						Normalizer normalizer = calculation.getNormalizer();
						double valueNrm = calculator.getValue(record).getDouble();
						double valueDsc = normalizer.getValue(valueNrm);
						record.setValue(fieldNrm.getName(), valueNrm);
						record.setValue(fieldDsc.getName(), valueDsc);
					}
				}

				// Update the record.
				persistor.update(record);

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
