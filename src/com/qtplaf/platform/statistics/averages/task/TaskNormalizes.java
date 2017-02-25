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
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.util.NumberUtils;
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
		return new DataPersistor(states.getTable().getPersistor());
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
	 * @return The number of records to process.
	 */
	private long countPenging() throws Exception {
		return getPersistor().count(getSelectCriteria());
	}

	/**
	 * Returns the select criteria.
	 * 
	 * @return The select criteria.
	 */
	private Criteria getSelectCriteria() {
		Field f_key_state = states.getFieldDefState();
		Value v_key_state = new Value("");
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(f_key_state, v_key_state));
		return criteria;
	}

	/**
	 * Returns the select order.
	 * 
	 * @return The select order.
	 */
	private Order getSelectOrder() {
		Order order = new Order();
		order.add(states.getFieldDefIndex());
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
			// Scale to calculate the key.
			int keyScale = states.getConfiguration().getScale();

			// Count steps.
			countSteps();

			// Persistor.
			DataPersistor persistor = getPersistor();

			// Source iterator.
			iterator = persistor.iterator(getSelectCriteria(), getSelectOrder());

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
				
				// Deltas high, low, close.
				{
					List<Field> fieldsRaw = states.getFieldListDeltasRaw();
					List<Field> fieldsCont = states.getFieldListDeltasNormalizedContinuous();
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldCont = fieldsCont.get(i);
						Normalizer normCont = mapNormalizers.get(fieldRaw.getName());
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueCont = normCont.getValue(valueRaw);
						record.getValue(fieldCont.getName()).setDouble(valueCont);
					}					
				}

				// Spreads between averages.
				{
					List<Field> fieldsRaw = states.getFieldListSpreadsRaw();
					List<Field> fieldsCont = states.getFieldListSpreadsNormalizedContinuous();
					List<Field> fieldsDisc = states.getFieldListSpreadsNormalizedDiscrete();
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldCont = fieldsCont.get(i);
						Field fieldDisc = fieldsDisc.get(i);
						Normalizer normCont = mapNormalizers.get(fieldRaw.getName());
						Normalizer normDisc = states.getPropertySpread(fieldDisc).getNormalizer();
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueCont = normCont.getValue(valueRaw);
						double valueDisc = normDisc.getValue(valueCont);
						record.getValue(fieldCont.getName()).setDouble(valueCont);
						record.getValue(fieldDisc.getName()).setDouble(valueDisc);
					}
				}

				// Speeds.
				{
					List<Field> fieldsRaw = states.getFieldListSpeedsRaw();
					List<Field> fieldsCont = states.getFieldListSpeedsNormalizedContinuous();
					List<Field> fieldsDisc = states.getFieldListSpeedsNormalizedDiscrete();
					for (int i = 0; i < fieldsRaw.size(); i++) {
						Field fieldRaw = fieldsRaw.get(i);
						Field fieldCont = fieldsCont.get(i);
						Field fieldDisc = fieldsDisc.get(i);
						Normalizer normCont = mapNormalizers.get(fieldRaw.getName());
						Normalizer normDisc = states.getPropertySpeed(fieldDisc).getNormalizer();
						double valueRaw = record.getValue(fieldRaw.getName()).getDouble();
						double valueCont = normCont.getValue(valueRaw);
						double valueDisc = normDisc.getValue(valueCont);
						record.getValue(fieldCont.getName()).setDouble(valueCont);
						record.getValue(fieldDisc.getName()).setDouble(valueDisc);
					}
				}

				// Key state.
				{
					Field fieldKey = states.getFieldDefState();
					List<Field> fieldsKey = states.getFieldListStateKey();
					double[] keyValues = new double[fieldsKey.size()];
					for (int i = 0; i < fieldsKey.size(); i++) {
						String name = fieldsKey.get(i).getName();
						keyValues[i] = record.getValue(name).getDouble();
					}
					record.getValue(fieldKey.getName()).setString(NumberUtils.getStringKey(keyValues, keyScale));
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
