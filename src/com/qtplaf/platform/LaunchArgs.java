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

package com.qtplaf.platform;

import javax.swing.Action;

import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.JPanelTreeMenu;
import com.qtplaf.library.swing.core.TreeMenuItem;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;

/**
 * Defines the launch arguments on the main menu.
 *
 * @author Miquel Sas
 */
public class LaunchArgs {
	/** KeyServer. */
	public static final String KeyServer = "server";
	/** Instrument. */
	public static final String KeyInstrument = "instrument";
	/** Period. */
	public static final String KeyPeriod = "period";
	/** Table name. */
	public static final String KeyTableName = "table_name";
	/** Statistics menu item. */
	public static final String KeyStatistics = "statistics_menuitem";
	/** Statistics menu item. */
	public static final String KeyPanelMenu = "panel_menu";

	/**
	 * Returns the server stored in the launch arguments and passed to the action.
	 *
	 * @param action The action.
	 * @return The server.
	 */
	public static Server getServer(Action action) {
		return (Server) ActionUtils.getLaunchArgs(action).getObject(KeyServer);
	}

	/**
	 * Returns the instrument.
	 * 
	 * @param action The action.
	 * @return The instrument.
	 */
	public static Instrument getInstrument(Action action) {
		return (Instrument) ActionUtils.getLaunchArgs(action).getObject(KeyInstrument);
	}

	/**
	 * Returns the period.
	 * 
	 * @param action The action.
	 * @return The period.
	 */
	public static Period getPeriod(Action action) {
		return (Period) ActionUtils.getLaunchArgs(action).getObject(KeyPeriod);
	}

	/**
	 * Returns the table name.
	 * 
	 * @param action The action.
	 * @return The table name.
	 */
	public static String getTableName(Action action) {
		return (String) ActionUtils.getLaunchArgs(action).getObject(KeyTableName);
	}

	/**
	 * Returns the server stored in the launch arguments and passed to the action.
	 *
	 * @param action The action.
	 * @return The server.
	 */
	public static TreeMenuItem getMenuItem(Action action) {
		return (TreeMenuItem) ActionUtils.getLaunchArgs(action).getObject(KeyStatistics);
	}

	/**
	 * Returns the server stored in the launch arguments of the menu item.
	 *
	 * @param menuItem The menuItem.
	 * @return The server.
	 */
	public static Server getServer(TreeMenuItem menuItem) {
		return (Server) menuItem.getLaunchArgs().getObject(KeyServer);
	}

	public static JPanelTreeMenu getPanelMenu(TreeMenuItem menuItem) {
		return (JPanelTreeMenu) menuItem.getLaunchArgs().getObject(KeyPanelMenu);
	}
}
