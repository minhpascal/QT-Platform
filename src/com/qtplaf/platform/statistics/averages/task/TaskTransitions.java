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
import com.qtplaf.platform.database.Fields;
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
		this.statesPersistor = transitions.getStates().getDataListStates().getDataPersistor();

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
			order.add(getStates().getTable().getField(Fields.Index));
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
				String key = record.getValue(Fields.State).getString();

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

			int indexInput = stateInput.getValue(Fields.Index).getInteger();
			int indexOutput = indexInput + 1;

			// If not the last record, check if next record is index output (same output key correlative).
			if (i < recordSet.size() - 1) {
				if (indexOutput == recordSet.get(i + 1).getValue(Fields.Index).getInteger()) {
					stateOutput = recordSet.get(i + 1);
				}
			}

			// Not correlative.
			if (stateOutput == null) {
				stateOutput = statesPersistor.getRecord(Long.valueOf(indexOutput));
			}

			// Transition available.
			if (stateOutput != null) {
				String keyOutput = stateOutput.getValue(Fields.State).getString();
				if (group < 0) {
					group = indexInput;
					keyOutputPrevious = keyOutput;
				}
				if (!keyOutput.equals(keyOutputPrevious)) {
					group = indexInput;
				}
				if (!map.containsKey(indexInput)) {
					Record transition = transitionsPersistor.getDefaultRecord();
					transition.setValue(Fields.StateIn, keyInput);
					transition.setValue(Fields.StateOut, keyOutput);
					transition.setValue(Fields.IndexIn, indexInput);
					transition.setValue(Fields.IndexOut, indexOutput);
					transition.setValue(Fields.IndexGroup, group);

					 List<Field> spreads = getStates().getFieldListSpreads(Fields.Suffix.dsc);
					 List<Field> spreadsIn = getTransitions().getFieldListSpreads(Fields.Suffix.in);
					 List<Field> spreadsOut = getTransitions().getFieldListSpreads(Fields.Suffix.out);
					 for (int j = 0; j < spreads.size(); j++) {
						 Field spread = spreads.get(j);
						 Field spreadIn = spreadsIn.get(j);
						 Field spreadOut = spreadsOut.get(j);
						 transition.setValue(spreadIn.getName(), stateInput.getValue(spread.getName()));
						 transition.setValue(spreadOut.getName(), stateOutput.getValue(spread.getName()));
					 }
					 
					 List<Field> slopes = getStates().getFieldListSlopes(Fields.Suffix.dsc);
					 List<Field> slopesIn = getTransitions().getFieldListSlopes(Fields.Suffix.in);
					 List<Field> slopesOut = getTransitions().getFieldListSlopes(Fields.Suffix.out);
					 for (int j = 0; j < slopes.size(); j++) {
						 Field slope = slopes.get(j);
						 Field slopeIn = slopesIn.get(j);
						 Field slopeOut = slopesOut.get(j);
						 transition.setValue(slopeIn.getName(), stateInput.getValue(slope.getName()));
						 transition.setValue(slopeOut.getName(), stateOutput.getValue(slope.getName()));
					 }
					 
					 List<Field> calcs = getStates().getFieldListCalculations(Fields.Suffix.dsc);
					 List<Field> calcsIn = getTransitions().getFieldListCalculations(Fields.Suffix.in);
					 List<Field> calcsOut = getTransitions().getFieldListCalculations(Fields.Suffix.out);
					 for (int j = 0; j < calcs.size(); j++) {
						 Field calc = calcs.get(j);
						 Field calcIn = calcsIn.get(j);
						 Field calcOut = calcsOut.get(j);
						 Value in = stateInput.getValue(calc.getName());
						 Value out = stateOutput.getValue(calc.getName());
						 transition.setValue(calcIn.getName(), in);
						 transition.setValue(calcOut.getName(), out);
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

		Field indexField = statesPersistor.getField(Fields.Index);
		Field keyField = statesPersistor.getField(Fields.State);
		Value keyValue = new Value(key);

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldEQ(keyField, keyValue));

		Order order = new Order();
		order.add(indexField);

		return statesPersistor.select(criteria, order);
	}

}
