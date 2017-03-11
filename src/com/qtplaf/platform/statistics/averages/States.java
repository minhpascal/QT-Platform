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

package com.qtplaf.platform.statistics.averages;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.trading.data.DataPersistor;
import com.qtplaf.library.trading.data.DataRecordSet;
import com.qtplaf.library.trading.data.PersistorDataList;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.platform.database.Names.Schemas;
import com.qtplaf.platform.database.Names.Tables;
import com.qtplaf.platform.statistics.action.ActionBrowse;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.action.ActionNavigateStatistics;
import com.qtplaf.platform.statistics.averages.task.TaskNormalizes;
import com.qtplaf.platform.statistics.averages.task.TaskStates;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * States based on averages.
 *
 * @author Miquel Sas
 */
public class States extends Averages {
	
	/**
	 * Browse states.
	 */
	class ActionBrowseStates extends ActionBrowse {
		ActionBrowseStates(States states) {
			super(states);
		}
		@Override
		public RecordSet getRecordSet() {
			DataPersistor persistor = new DataPersistor(getTable().getPersistor());
			return new DataRecordSet(persistor);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public States(Session session) {
		super(session);
	}
	/**
	 * Returns the persistor data list for this states statistics.
	 * 
	 * @return The persistor data list.
	 */
	public PersistorDataList getDataListStates() {

		DataPersistor persistor = new DataPersistor(getTable().getPersistor());

		DataInfo info = new DataInfo(getSession());
		info.setName("States");
		info.setDescription("States data info");
		info.setInstrument(getInstrument());
		info.setPeriod(getPeriod());
		DataPersistor.setDataInfoOutput(info, persistor);

		return new PersistorDataList(getSession(), info, persistor);
	}


	/**
	 * Returns the list of actions associated with the statistics. Actions are expected to be suitably configurated to
	 * be selected for instance from a popup menu.
	 * 
	 * @return The list of actions.
	 */
	public List<Action> getActions() {

		List<Action> actions = new ArrayList<>();

		// Calculate states.
		ActionCalculate actionCalcStates = new ActionCalculate(this, new TaskStates(this));
		ActionUtils.setName(actionCalcStates, "Calculate states");
		ActionUtils.setShortDescription(actionCalcStates, "Calculate states from scratch");
		ActionUtils.setActionGroup(actionCalcStates, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcStates);

		// Normalize values.
		ActionCalculate actionCalcNorm = new ActionCalculate(this, new TaskNormalizes(this));
		ActionUtils.setName(actionCalcNorm, "Normalize values");
		ActionUtils.setShortDescription(actionCalcNorm, "Calculate normalized values");
		ActionUtils.setActionGroup(actionCalcNorm, new ActionGroup("Calculate", 10000));
		actions.add(actionCalcNorm);

		// Standard browse of data.
		ActionBrowseStates actionBrowse = new ActionBrowseStates(this);
		ActionUtils.setName(actionBrowse, "Browse data");
		ActionUtils.setShortDescription(actionBrowse, "Browse calculated data");
		ActionUtils.setActionGroup(actionBrowse, new ActionGroup("Browse", 10100));
		actions.add(actionBrowse);

		// Standard navigate.
		actions.add(new ActionNavigateStatistics(this));
		
		return actions;
	}

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	@Override
	public Table getTable() {

		Table table = new Table();

		table.setName(Tables.ticker(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Schemas.server(getServer()));

		// Index and time.
		table.addField(getFields().getIndex());
		table.addField(getFields().getTime());

		// Time formatted.
		table.addField(getFields().getTimeFmt());

		// Open, high, low, close.
		table.addField(getFields().getOpen());
		table.addField(getFields().getHigh());
		table.addField(getFields().getLow());
		table.addField(getFields().getClose());

		// Averages fields.
		table.addFields(getFieldListAverages());

		// Deltas high, low, close, raw values.
		table.addFields(getFieldListDeltas(Suffix.raw));

		// Spreads between averages, raw values.
		table.addFields(getFieldListSpreads(Suffix.raw));

		// Speed (tangent) of averages, raw values
		table.addFields(getFieldListSpeeds(Suffix.raw));

		// Sum of spreads and sum of speeds, raw values.
		table.addFields(getFieldListCalculations(Suffix.raw));

		// Deltas high, low, close, normalized values continuous.
		table.addFields(getFieldListDeltas(Suffix.nrm));

		// Spreads between averages, normalized values continuous.
		table.addFields(getFieldListSpreads(Suffix.nrm));

		// Speed (tangent) of averages, normalized values continuous.
		table.addFields(getFieldListSpeeds(Suffix.nrm));

		// Sum of spreads and sum of speeds, normalizes continuous.
		table.addFields(getFieldListCalculations(Suffix.nrm));

		// Spreads between averages, normalized values discrete.
		table.addFields(getFieldListSpreads(Suffix.dsc));

		// Speed (tangent) of averages, normalized values discrete.
		table.addFields(getFieldListSpeeds(Suffix.dsc));

		// Sum of spreads and sum of speeds, normalizes continuous.
		table.addFields(getFieldListCalculations(Suffix.dsc));

		// The state key.
		table.addField(getFields().getState());

		// Primary key on Time.
		getFields().getIndex().setPrimaryKey(true);

		// Unique index on Index.
		Index indexOnIndex = new Index();
		indexOnIndex.add(getFields().getIndex());
		indexOnIndex.setUnique(true);
		table.addIndex(indexOnIndex);

		// Non unique index on the state key.
		Index indexOnKeyState = new Index();
		indexOnKeyState.add(getFields().getState());
		indexOnKeyState.setUnique(false);
		table.addIndex(indexOnKeyState);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

}
