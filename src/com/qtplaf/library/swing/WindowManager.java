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

package com.qtplaf.library.swing;

import java.awt.Window;

import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

/**
 * The window manager keeps track of dialogs and frames that extend <tt>JDialogSession</tt> or <tt>JFrameSession</tt>.
 * 
 * @author Miquel Sas
 */
public class WindowManager {

	/**
	 * The list of windows.
	 */
	private static Delist<Window> windows = new ArrayDelist<>();

	/**
	 * Add a window.
	 * 
	 * @param window The window to add.
	 */
	public static synchronized void add(Window window) {
		windows.addLast(window);
	}

	/**
	 * Remove a window.
	 * 
	 * @param window The window to remove.
	 */
	public static synchronized void remove(Window window) {
		windows.remove(window);
	}

	/**
	 * Accept only static methods.
	 */
	private WindowManager() {
	}
}
