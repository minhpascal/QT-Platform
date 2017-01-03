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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.action.DefaultActionClear;
import com.qtplaf.library.swing.action.DefaultActionExit;
import com.qtplaf.library.swing.event.WindowHandler;
import com.qtplaf.library.util.Alignment;

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
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = getSession().getString("frameMenuExitApplication");
			if (MessageBox.question(getSession(), message, MessageBox.yesNo, MessageBox.yes) == MessageBox.yes) {
				if (getPreExitAction() != null) {
					getPreExitAction().actionPerformed(e);
				}
				System.exit(0);
			}
		}
	}

	/**
	 * Clear the console.
	 */
	class ActionClear extends DefaultActionClear {

		/**
		 * Constructor.
		 */
		ActionClear() {
			super(getSession());
		}

		/**
		 * Perform the action, clear the console.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			getConsole().clear();
		}
	}

	/**
	 * Change listener to listen to tab selection.
	 */
	class TabChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			setupPanelButtons();
		}
	}

	/**
	 * Window adapter to handle closing.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			getActionExit().actionPerformed(new ActionEvent(e.getSource(), 0, "EXIT APP"));
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
			setupPanelButtons();
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
	 * Action exit.
	 */
	private ActionExit actionExit;
	/**
	 * Action clear.
	 */
	private ActionClear actionClear;
	/**
	 * An optional action to be executed prior to exit the application.
	 */
	private Action preExitAction;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JFrameMenu(Session session) {
		super(session);
		layoutComponents();
	}

	/**
	 * Layout components.
	 */
	private void layoutComponents() {

		// Configure tabbed pane.
		getTabbedPane().removeAll();

		// Menu pane.
		String menuTitle = getSession().getString("frameMenuTabMenuLabel");
		String menuTooltip = getSession().getString("frameMenuTabMenuTooltip");
		getTabbedPane().addTab(menuTitle, null, getPanelTreeMenu(), menuTooltip);

		// Console pane.
		String consoleTitle = getSession().getString("frameMenuTabConsoleLabel");
		String consoleTooltip = getSession().getString("frameMenuTabConsoleTooltip");
		getTabbedPane().addTab(consoleTitle, null, getConsole(), consoleTooltip);

		// Set the content pane.
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = null;

		// Tabbed pane.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		getContentPane().add(getTabbedPane(), constraints);

		// Buttons panel.
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 10, 0, 10);
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 4;
		getContentPane().add(getPanelButtons(), constraints);

		// This window listener.
		WindowAdapter windowAdapter = new WindowAdapter();
		addWindowListener(windowAdapter);
		addWindowFocusListener(windowAdapter);
	}

	/**
	 * Setup the buttons panel depending on the seletected tab.
	 */
	private void setupPanelButtons() {
		getPanelButtons().clear();
		int index = getTabbedPane().getSelectedIndex();
		if (index < 0) {
			return;
		}

		// Selected tab is menu: action exit.
		if (index == getTabbedPane().indexOfComponent(getPanelTreeMenu())) {
			getPanelButtons().add(getActionExit());
		}

		// Selected tab is console: actions clear and exit.
		if (index == getTabbedPane().indexOfComponent(getConsole())) {
			getPanelButtons().add(getActionClear());
			getPanelButtons().add(getActionExit());
		}

		// Setup accelerator key listeners.
		SwingUtils.removeAcceleratorKeyListener(this);
		SwingUtils.installAcceleratorKeyListener(this);
	}

	/**
	 * Returns the tree menu panel.
	 * 
	 * @return The tree menu panel.
	 */
	public JPanelTreeMenu getPanelTreeMenu() {
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
	public JConsole getConsole() {
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
			panelButtons = new JPanelButtons(Alignment.Right);
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
			tabbedPane.addChangeListener(new TabChangeListener());
		}
		return tabbedPane;
	}

	/**
	 * Returns the exit action.
	 * 
	 * @return The exit action.
	 */
	private ActionExit getActionExit() {
		if (actionExit == null) {
			actionExit = new ActionExit();
		}
		return actionExit;
	}

	/**
	 * Returns the clear action.
	 * 
	 * @return The clear action.
	 */
	private ActionClear getActionClear() {
		if (actionClear == null) {
			actionClear = new ActionClear();
		}
		return actionClear;
	}

	/**
	 * Returns the pre-exit action.
	 * 
	 * @return The pre-exit action.
	 */
	public Action getPreExitAction() {
		return preExitAction;
	}

	/**
	 * Sets the pre-exit action.
	 * 
	 * @param preExitAction The pre-exit action.
	 */
	public void setPreExitAction(Action preExitAction) {
		this.preExitAction = preExitAction;
	}

	/**
	 * Show the console.
	 */
	public void showConsole() {
		getTabbedPane().setSelectedComponent(getConsole());
		setupPanelButtons();
	}

	/**
	 * Show the tree menu.
	 */
	public void showTreeMenu() {
		getTabbedPane().setSelectedComponent(getPanelTreeMenu());
		setupPanelButtons();
	}
}
