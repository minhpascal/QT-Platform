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

import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Value;
import com.qtplaf.platform.statistics.StatesAverages.Fields;
import com.qtplaf.platform.statistics.StatesTransitions;

/**
 * Task to calculate transitions among discrete states.
 *
 * @author Miquel Sas
 */
public class TaskStatesTransitions extends TaskStatesAverages {

	/** Origin states transitions statistics. */
	private StatesTransitions statesTransitions;
	/** States transitions persistor. */
	private Persistor persistorTransitions;
	/** States discrete persistor. */
	private Persistor persistorDiscrete;

	/**
	 * Constructor.
	 * 
	 * @param statesTransitions Origin states transitions statistics.
	 */
	public TaskStatesTransitions(StatesTransitions statesTransitions) {
		super(statesTransitions.getSession());
		this.statesTransitions = statesTransitions;
		setNameAndDescription(statesTransitions);
	}

	/**
	 * Returns the transitions persistor.
	 * 
	 * @return The transitions persistor.
	 */
	private Persistor getPersistorTransitions() {
		if (persistorTransitions == null) {
			persistorTransitions = statesTransitions.getTable().getPersistor();
		}
		return persistorTransitions;
	}

	/**
	 * Returns the states discrete persistor.
	 * 
	 * @return The states discrete persistor.
	 */
	private Persistor getPersistorDiscrete() {
		if (persistorDiscrete == null) {
			persistorDiscrete = statesTransitions.getStatesNormalizeDiscrete().getTable().getPersistor();
		}
		return persistorDiscrete;
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
		long count = getPersistorDiscrete().count(null);

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
			Table tableTransitions = statesTransitions.getTable();

			// Drop and create the table.
			if (getPersistorTransitions().getDDL().existsTable(tableTransitions)) {
				getPersistorTransitions().getDDL().dropTable(tableTransitions);
			}
			getPersistorTransitions().getDDL().buildTable(tableTransitions);

			// Source (discrete) iterator.
			Order order = new Order();
			order.add(getPersistorDiscrete().getField(Fields.Index));
			iterator = getPersistorDiscrete().iterator(null, order);

			// Working field names.
			String keyName = statesTransitions.getFieldKey().getName();
			String indexName = statesTransitions.getFieldIndex().getName();
			String keyInputName = statesTransitions.getFieldKeyInput().getName();
			String keyOutputName = statesTransitions.getFieldKeyOutput().getName();

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

				// The source discrete record, key and index values.
				Record discrete = iterator.next();
				String key = discrete.getValue(keyName).getString();

				// Process the key if not already processed.
				if (!processed(key, keyInputName)) {

					// The list of indexes in the source that have the key.
					List<Integer> indexes = getDiscreteIndexes(key, keyName, indexName);
					
					// Create the transitions.
					for (int index : indexes) {
						String nextKey = getNextKey(index + 1, indexName, keyName);
						if (nextKey != null) {
							Record transition = getPersistorTransitions().getDefaultRecord();
							transition.setValue(keyInputName, key);
							transition.setValue(keyOutputName, nextKey);
							transition.setValue(indexName, index + 1);
							getPersistorTransitions().insert(transition);
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

	/**
	 * Check if the key has been processed.
	 * 
	 * @param key The key.
	 * @param keyInputName The name of the key input field in the transitions persistor.
	 * @return A boolean.
	 */
	private boolean processed(String key, String keyInputName) throws Exception {
		Field keyField = getPersistorTransitions().getField(keyInputName);
		Value keyValue = new Value(key);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(keyField, keyValue));
		boolean processed = false;
		RecordIterator iterator = getPersistorTransitions().iterator(criteria);
		if (iterator.hasNext()) {
			processed = true;
		}
		iterator.close();
		return processed;
	}

	/**
	 * Returns the list od indexes of the discrete source that have the key.
	 * 
	 * @param key The key.
	 * @param keyName The key field name in the source persistor.
	 * @param indexName The index field name.
	 * @return The list of indexes tthat have the key.
	 */
	private List<Integer> getDiscreteIndexes(String key, String keyName, String indexName) throws Exception {
		Field keyField = getPersistorDiscrete().getField(keyName);
		Value keyValue = new Value(key);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(keyField, keyValue));
		List<Integer> indexes = new ArrayList<>();
		RecordIterator iterator = getPersistorDiscrete().iterator(criteria);
		while (iterator.hasNext()) {
			Record record = iterator.next();
			int index = record.getValue(indexName).getInteger();
			indexes.add(index);
		}
		iterator.close();
		return indexes;
	}

	/**
	 * Returns the next key (or null) in the source given the index.
	 * 
	 * @param index The index in the source.
	 * @param indexName The index field name.
	 * @param keyName The key field name.
	 * @return The next key or null.
	 */
	private String getNextKey(int index, String indexName, String keyName) throws Exception {
		Field indexField = getPersistorDiscrete().getField(indexName);
		Value indexValue = new Value(index);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(indexField, indexValue));
		String key = null;
		RecordIterator iterator = getPersistorDiscrete().iterator(criteria);
		while (iterator.hasNext()) {
			Record record = iterator.next();
			key = record.getValue(keyName).getString();
		}
		iterator.close();
		return key;
	}
}
