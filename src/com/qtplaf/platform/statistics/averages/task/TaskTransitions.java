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
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.Transitions;

/**
 * Calculates transitions.
 *
 * @author Miquel Sas
 */
public class TaskTransitions extends TaskAverages {

	/** Underlying transitions statistics. */
	private Transitions transitions;
	/** Transitions persistor. */
	private Persistor transitionsPersistor;
	/** Underlying transitions statistics. */
	private States states;
	/** Source states data list. */
	private DataPersistor statesPersistor;

	/** Short cut to state name. */
	private String keyName;
	/** Short cut to input key name. */
	private String keyInputName;
	/** Short cut to output key name. */
	private String keyOutputName;
	/** Short cut to input index name. */
	private String indexInputName;
	/** Short cut to output index name. */
	private String indexOutputName;
	/** Short cut to index group name. */
	private String indexGroupName;
	/** Short cut to index name. */
	private String indexName;
	/** Transition value hight. */
	private String valueHighName;
	/** Transition value low. */
	private String valueLowName;
	/** Transition value close. */
	private String valueCloseName;
	/** Short cut to delta high name. */
	private String deltaHighName;
	/** Short cut to delta low name. */
	private String deltaLowName;
	/** Short cut to delta close name. */
	private String deltaCloseName;

	/**
	 * Constructor.
	 * 
	 * @param transitions The underlying transitions statistics.
	 */
	public TaskTransitions(Transitions transitions) {
		super(transitions.getSession());

		this.transitions = transitions;
		this.transitionsPersistor = transitions.getTable().getPersistor();

		Manager manager = new Manager(getSession());
		this.states = manager.getStates(
			transitions.getServer(),
			transitions.getInstrument(),
			transitions.getPeriod(),
			transitions.getConfiguration());
		this.statesPersistor = states.getDataList().getDataPersistor();

		keyName = transitions.getFieldDefState().getName();
		keyInputName = transitions.getFieldDefStateInput().getName();
		keyOutputName = transitions.getFieldDefStateOutput().getName();
		indexInputName = transitions.getFieldDefIndexInput().getName();
		indexOutputName = transitions.getFieldDefIndexOutput().getName();
		indexGroupName = transitions.getFieldDefIndexGroup().getName();
		indexName = transitions.getFieldDefIndex().getName();
		valueHighName = transitions.getFieldDefTransitionValueHigh().getName();
		valueLowName = transitions.getFieldDefTransitionValueLow().getName();
		valueCloseName = transitions.getFieldDefTransitionValueClose().getName();

		deltaHighName = states.getFieldDefDeltaHight().getName();
		deltaLowName = states.getFieldDefDeltaLow().getName();
		deltaCloseName = states.getFieldDefDeltaClose().getName();

		setNameAndDescription(transitions, "Transitions values");
	}

	/**
	 * Returns the states source statistics.
	 * 
	 * @return The states source statistics.
	 */
	private States getStates() {
		return states;
	}

	/**
	 * Returns the transitions source statistics.
	 * 
	 * @return The transitions source statistics.
	 */
	private Transitions getTransitions() {
		return transitions;
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
		int count = Long.valueOf(statesPersistor.size()).intValue();

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

			// Drop and create the table.
			if (transitionsPersistor.getDDL().existsTable(getTransitions().getTable())) {
				transitionsPersistor.getDDL().dropTable(getTransitions().getTable());
			}
			transitionsPersistor.getDDL().buildTable(getTransitions().getTable());

			// Map of processed keys
			Map<String, String> processedKeys = new HashMap<>();

			// Source (states) iterator.
			Order order = new Order();
			order.add(getStates().getFieldDefIndex());
			iterator = statesPersistor.iterator(null, order);

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

				// Record.
				Record record = iterator.next();
				String key = record.getValue(keyName).getString();

				// Process the key if not already processed.
				if (!processedKeys.containsKey(key)) {
					List<Record> transitions = getTransitions(key);
					for (Record transition : transitions) {
						transitionsPersistor.insert(transition);
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

			Record stateInput = recordSet.get(i);
			Record stateOutput = null;

			int indexInput = stateInput.getValue(indexName).getInteger();
			int indexOutput = indexInput + 1;

			// If not the last record, check if next record is index output (same output key correlative).
			if (i < recordSet.size() - 1) {
				if (indexOutput == recordSet.get(i + 1).getValue(indexName).getInteger()) {
					stateOutput = recordSet.get(i + 1);
				}
			}

			// Not correlative.
			if (stateOutput == null) {
				stateOutput = statesPersistor.getRecord(Long.valueOf(indexOutput));
			}

			// Transition available.
			if (stateOutput != null) {
				String keyOutput = stateOutput.getValue(keyName).getString();
				if (group < 0) {
					group = indexInput;
					keyOutputPrevious = keyOutput;
				}
				if (!keyOutput.equals(keyOutputPrevious)) {
					group = indexInput;
				}
				if (!map.containsKey(indexInput)) {
					Record transition = transitionsPersistor.getDefaultRecord();
					transition.setValue(keyInputName, keyInput);
					transition.setValue(keyOutputName, keyOutput);
					transition.setValue(indexInputName, indexInput);
					transition.setValue(indexOutputName, indexOutput);
					transition.setValue(indexGroupName, group);
					transition.setValue(valueHighName, stateOutput.getValue(deltaHighName));
					transition.setValue(valueLowName, stateOutput.getValue(deltaLowName));
					transition.setValue(valueCloseName, stateOutput.getValue(deltaCloseName));

					 List<Field> spreads = getStates().getFieldListSpreadsNormalizedDiscrete();
					 List<Field> spreadsIn = getTransitions().getFieldListSpreadsDiscreteInput();
					 List<Field> spreadsOut = getTransitions().getFieldListSpreadsDiscreteOutput();
					 for (int j = 0; j < spreads.size(); j++) {
						 Field spread = spreads.get(j);
						 Field spreadIn = spreadsIn.get(j);
						 Field spreadOut = spreadsOut.get(j);
						 transition.setValue(spreadIn.getName(), stateInput.getValue(spread.getName()));
						 transition.setValue(spreadOut.getName(), stateOutput.getValue(spread.getName()));
					 }
					 
					 List<Field> speeds = getStates().getFieldListSpeedsNormalizedDiscrete();
					 List<Field> speedsIn = getTransitions().getFieldListSpeedsDiscreteInput();
					 List<Field> speedsOut = getTransitions().getFieldListSpeedsDiscreteOutput();
					 for (int j = 0; j < speeds.size(); j++) {
						 Field speed = speeds.get(j);
						 Field speedIn = speedsIn.get(j);
						 Field speedOut = speedsOut.get(j);
						 transition.setValue(speedIn.getName(), stateInput.getValue(speed.getName()));
						 transition.setValue(speedOut.getName(), stateOutput.getValue(speed.getName()));
					 }

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

		Field indexField = statesPersistor.getField(indexName);
		Field keyField = statesPersistor.getField(keyName);
		Value keyValue = new Value(key);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(keyField, keyValue));

		Order order = new Order();
		order.add(indexField);

		return statesPersistor.select(criteria, order);
	}

}
