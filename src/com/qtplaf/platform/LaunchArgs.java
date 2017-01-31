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
import com.qtplaf.library.trading.server.Server;

/**
 * Defines the launch arguments on the main menu.
 *
 * @author Miquel Sas
 */
public class LaunchArgs {
	/** KeyServer. */
	public static final String KeyServer = "server";

	/**
	 * Returns the server stored in the launch arguments and passed to the action.
	 *
	 * @param action The action.
	 * @return The server.
	 */
	public static Server getServer(Action action) {
		return (Server) ActionUtils.getLaunchArgs(action).getObject(KeyServer);
	}
}
