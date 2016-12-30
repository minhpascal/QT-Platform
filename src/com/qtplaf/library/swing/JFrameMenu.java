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

import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.action.DefaultActionExit;

/**
 * The frame that handles a tree menu and a console.
 * 
 * @author Miquel Sas
 */
public class JFrameMenu extends JFrameSession {

	/**
	 * Exit action.
	 */
	class ActionExit extends DefaultActionExit {

		/**
		 * Constructor.
		 */
		ActionExit() {
			super(getSession());
		}

		/**
		 * Perform the action, exit the application.
		 */
		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 * Tree menu panel.
	 */
	private JPanelTreeMenu panelMenu;
	/**
	 * The console for <tt>out</tt> and <tt>err</tt>.
	 */
	private JConsole console;
	/**
	 * The buttons panel.
	 */
	private JPanelButtons panelButtons;
	/**
	 * The tabbed pane.
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JFrameMenu(Session session) {
		super(session);
	}

	/**
	 * Returns the tree menu panel.
	 * 
	 * @return The tree menu panel.
	 */
	protected JPanelTreeMenu getPanelTreeMenu() {
		if (panelMenu == null) {
			panelMenu = new JPanelTreeMenu(getSession());
		}
		return panelMenu;
	}

	/**
	 * Returns the console for <tt>out</tt> and <tt>err</tt>.
	 * 
	 * @return The console.
	 */
	protected JConsole getConsole() {
		if (console == null) {
			console = new JConsole();
		}
		return console;
	}

	/**
	 * Returns the buttons panel.
	 * 
	 * @return The buttons panel.
	 */
	protected JPanelButtons getPanelButtons() {
		if (panelButtons == null) {
			panelButtons = new JPanelButtons();
		}
		return panelButtons;
	}

	/**
	 * Returns the tabbed pane.
	 * 
	 * @return The tabbed pane.
	 */
	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		}
		return tabbedPane;
	}
}
