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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.View;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.pattern.Pattern;
import com.qtplaf.library.trading.pattern.candle.CandlePattern;
import com.qtplaf.library.trading.pattern.candle.patterns.BigPiercingBearishBullish;
import com.qtplaf.library.trading.pattern.candle.patterns.BigPiercingBullishBearish;
import com.qtplaf.platform.database.Domains;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Fields.Family;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Identify patterns.
 *
 * @author Miquel Sas
 */
public class TaskPatterns extends TaskAverages {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** Underlying states statistics. */
	private States states;
	/** States data list. */
	private PersistorDataList statesList;
	/** List of patterns to identify. */
	private List<Pattern> patternList = new ArrayList<>();

	/**
	 * Construtor.
	 * 
	 * @param states Underlying states statistics.
	 */
	public TaskPatterns(States states) {
		super(states.getSession());
		this.states = states;
		this.statesList = states.getStates().getDataListStates();

		setNameAndDescription(states, "Patterns");
	}

	/**
	 * Returns the list of candle patterns with the range average and standard deviation.
	 * 
	 * @return The list of candle patterns.
	 */
	private List<Pattern> getCandlePatterns() {
		List<Pattern> candlePatterns = new ArrayList<>();
		
		// States table.
		Table table = states.getStates().getTableStates();

		// View.
		View view = new View(getSession());
		view.setMasterTable(table);
		view.setName(table.getName());

		// Aggregate field name (range_raw)
		String rangeName = Fields.High + "-" + Fields.Low;

		// Aggregate function average.
		Field average = Domains.getDouble(getSession(), Fields.Average);
		average.setPersistent(false);
		average.setFunction("avg(" + rangeName + ")");
		average.setFormatter(new DataValue(getSession(), 10));
		view.addField(average);

		// Aggregate function stddev.
		Field stddev = Domains.getDouble(getSession(), Fields.StdDev, "Std Dev", "Standard deviation value");
		stddev.setPersistent(false);
		stddev.setFunction("stddev(" + rangeName + ")");
		stddev.setFormatter(new DataValue(getSession(), 10));
		view.addField(stddev);

		// Persistor.
		view.setPersistor(PersistorUtils.getPersistor(view));

		RecordSet recordSet = null;
		try {
			recordSet = view.getPersistor().select(null);
			double rangeAverage = recordSet.get(0).getValue(Fields.Average).getDouble();
			double rangeStdDev = recordSet.get(0).getValue(Fields.StdDev).getDouble();
			for (Pattern pattern : candlePatterns) {
				CandlePattern candlePattern = (CandlePattern) pattern;
				candlePattern.setRangeAverage(rangeAverage);
				candlePattern.setRangeStdDev(rangeStdDev);
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		}

		return candlePatterns;
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
		int count = statesList.size();

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

		// Fill the list of patterns to identify.
		patternList.addAll(getCandlePatterns());

		// Count steps.
		countSteps();

		// Result table and persistor.
		Table table = states.getTablePatterns();
		Persistor persistor = table.getPersistor();

		// Drop and create the table.
		if (persistor.getDDL().existsTable(table)) {
			persistor.getDDL().dropTable(table);
		}
		persistor.getDDL().buildTable(table);

		// Set the states list cache size.
		int cacheSize = -1;
		for (Pattern pattern : patternList) {
			cacheSize = Math.max(cacheSize, pattern.getLookBackward());
		}
		statesList.setPageSize(100);
		statesList.setCacheSize(Math.max(1000, cacheSize * 10));
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
			
			// Process patterns.
			for (Pattern pattern : patternList) {
				if (pattern.isPattern(statesList, index)) {
					Record rcState = statesList.getRecord(index);
					Record rcPattern = persistor.getDefaultRecord();
					rcPattern.setValue(Fields.Index, index);
					rcPattern.setValue(Fields.Time, rcState.getValue(Fields.Time));
					rcPattern.setValue(Fields.PatternFamily, pattern.getFamily());
					rcPattern.setValue(Fields.PatternId, pattern.getId());
					
					List<Field> fields = states.getFieldListCalculations(Family.State, Fields.Suffix.dsc);
					for (Field field : fields) {
						String name = field.getName();
						rcPattern.setValue(name, rcState.getValue(name));
					}
					persistor.insert(rcPattern);
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
