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
import com.qtplaf.library.database.Table;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.platform.database.tables.TablePatterns;
import com.qtplaf.platform.statistics.action.ActionCalculate;
import com.qtplaf.platform.statistics.averages.task.TaskPatterns;

/**
 * Patterns identified over a ticker (related to the states table).
 *
 * @author Miquel Sas
 */
public class Patterns extends Averages {

	/** Cached table. */
	private Table table;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Patterns(Session session) {
		super(session);
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
		ActionCalculate actionPatterns = new ActionCalculate(this, new TaskPatterns(this));
		ActionUtils.setName(actionPatterns, "Process patterns");
		ActionUtils.setShortDescription(actionPatterns, "Process to identify patterns");
		ActionUtils.setActionGroup(actionPatterns, new ActionGroup("Calculate", 10000));
		actions.add(actionPatterns);

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
		if (table == null) {
			table = new TablePatterns(getSession(), this);
		}
		return table;
	}

}
