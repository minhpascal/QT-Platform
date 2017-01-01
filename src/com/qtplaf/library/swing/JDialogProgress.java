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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.action.DefaultActionClose;
import com.qtplaf.library.swing.event.WindowHandler;

/**
 * A dialog to monitor the progress of tasks.
 * 
 * @author Miquel Sas
 */
public class JDialogProgress extends JDialogSession {

	/**
	 * Window adapter to handle the close operation.
	 */
	class WindowAdapter extends WindowHandler {
		@Override
		public void windowClosing(WindowEvent e) {
			SwingUtils.executeButtonAction(JDialogProgress.this, ActionClose.class);
		}
	}

	/**
	 * Close action.
	 */
	class ActionClose extends DefaultActionClose {

		/**
		 * Constructor.
		 */
		ActionClose() {
			super(getSession());
		}

		/**
		 * Perform the action, just close the window.
		 */
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JDialogProgress(Session session) {
		super(session);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param owner The parent window owner.
	 */
	public JDialogProgress(Session session, Window owner) {
		super(session, owner);
		setWindowHandler(new WindowAdapter());
	}

	/**
	 * Layout components.
	 */
	@SuppressWarnings("unused")
	private void layoutComponents() {

		// Set the content pane to be a layout panel.
		setContentPane(new JPanel(new GridBagLayout()));

		// Add the panel progress group.
		JPanelProgressGroup panelGroup = new JPanelProgressGroup(getSession());
		panelGroup.setName("ProgressGroup");

		GridBagConstraints constraintsGroup = new GridBagConstraints();
		constraintsGroup.anchor = GridBagConstraints.NORTH;
		constraintsGroup.fill = GridBagConstraints.BOTH;
		constraintsGroup.gridx = 0;
		constraintsGroup.gridy = 0;
		constraintsGroup.gridheight = 1;
		constraintsGroup.gridwidth = 1;
		constraintsGroup.insets = new Insets(0, 0, 0, 0);
		constraintsGroup.weightx = 1;
		constraintsGroup.weighty = 1;
		getContentPane().add(panelGroup, constraintsGroup);

		// Panel buttons.
		JPanelButtons panelButtons = new JPanelButtons();
		panelButtons.setName("PanelButtons");
		panelButtons.add(new ActionClose());

	}

	/**
	 * Set this dialog visible.
	 * 
	 * @param b A boolean.
	 */
	@Override
	public void setVisible(boolean b) {
		if (b) {
			WindowManager.add(this);
		} else {
			WindowManager.remove(this);
		}
		super.setVisible(b);
	}
}
