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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
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

	private String keyName;
	private String keyInputName;
	private String keyOutputName;
	private String indexInputName;
	private String indexOutputName;
	private String groupName;
	private String indexName;

	/**
	 * Constructor.
	 * 
	 * @param statesTransitions Origin states transitions statistics.
	 */
	public TaskStatesTransitions(StatesTransitions statesTransitions) {
		super(statesTransitions.getSession());
		this.statesTransitions = statesTransitions;
		setNameAndDescription(statesTransitions);

		keyName = statesTransitions.getFieldKey().getName();
		keyInputName = statesTransitions.getFieldKeyInput().getName();
		keyOutputName = statesTransitions.getFieldKeyOutput().getName();
		indexInputName = statesTransitions.getFieldIndexInput().getName();
		indexOutputName = statesTransitions.getFieldIndexOutput().getName();
		groupName = statesTransitions.getFieldGroup().getName();
		indexName = statesTransitions.getFieldIndex().getName();
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

			// Map of processed keys
			Map<String, String> processedKeys = new HashMap<>();

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
				if (!processedKeys.containsKey(key)) {
					List<Record> transitions = getTransitions(key);
					for (Record transition : transitions) {
						getPersistorTransitions().insert(transition);
					}
					processedKeys.put(key, key);
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
	 * Returns the list of transitions for the given key.
	 * 
	 * @param keyInput The input key to analyze.
	 * @return The list of transitions.
	 */
	private List<Record> getTransitions(String keyInput) throws Exception {
		Map<Integer, Integer> map = new HashMap<>();
		List<Record> transitions = new ArrayList<>();
		RecordSet recordSet = getRecordSet(keyInput);
		int group = -1;
		String keyOutputPrevious = null;
		for (int i = 0; i < recordSet.size(); i++) {
			
			int indexInput = recordSet.get(i).getValue(indexName).getInteger();
			int indexOutput = indexInput + 1;
			
			String keyOutput = null;
			
			// If not the last record, check if next record is index output (same output key correlative).
			if (i < recordSet.size() - 1) {
				int indexNext = recordSet.get(i + 1).getValue(indexName).getInteger();
				if (indexNext == indexOutput) {
					keyOutput = keyInput;
				}
			}
			// Not correlative.
			if (keyOutput == null) {
				keyOutput = getKey(indexOutput);
			}
			
			// Create the transition.
			if (keyOutput != null) {
				if (group < 0) {
					group = indexInput;
					keyOutputPrevious = keyOutput;
				}
				if (!keyOutput.equals(keyOutputPrevious)) {
					group = indexInput;
				}
				if (!map.containsKey(indexInput)) {
					Record transition = getPersistorTransitions().getDefaultRecord();
					transition.setValue(keyInputName, keyInput);
					transition.setValue(keyOutputName, keyOutput);
					transition.setValue(indexInputName, indexInput);
					transition.setValue(indexOutputName, indexOutput);
					transition.setValue(groupName, group);
					transitions.add(transition);
					map.put(indexInput, indexOutput);
				}
				keyOutputPrevious = keyOutput;
			}
		}
		return transitions;
	}

	/**
	 * Returns the record set of discrete source with same key, ordered by index.
	 * 
	 * @param key The key.
	 * @return The record set of discrete source with same key, ordered by index.
	 */
	private RecordSet getRecordSet(String key) throws Exception {

		Field indexField = getPersistorDiscrete().getField(indexName);
		Field keyField = getPersistorDiscrete().getField(keyName);
		Value keyValue = new Value(key);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(keyField, keyValue));

		Order order = new Order();
		order.add(indexField);

		return getPersistorDiscrete().select(criteria, order);
	}

	/**
	 * Returns the key (or null) in the source given the index.
	 * 
	 * @param index The index in the source.
	 * @return The key or null.
	 */
	private String getKey(int index) throws Exception {
		Field indexField = getPersistorDiscrete().getField(indexName);
		Value indexValue = new Value(index);
		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(indexField, indexValue));
		String key = null;
		RecordIterator iterator = getPersistorDiscrete().iterator(criteria);
		if (iterator.hasNext()) {
			Record record = iterator.next();
			key = record.getValue(keyName).getString();
		}
		iterator.close();
		return key;
	}

}
