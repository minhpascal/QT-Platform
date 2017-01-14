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

package com.qtplaf.platform.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.action.DefaultActionClose;
import com.qtplaf.library.swing.core.JOptionDialog;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelProgressGroup;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.task.SynchronizeServerInstruments;

/**
 * Synchronize server available instruments.
 * 
 * @author Miquel Sas
 */
public class ActionSynchronizeServerInstruments extends AbstractAction {

	/**
	 * Action to close the frame.
	 */
	class ActionClose extends DefaultActionClose {
		/**
		 * Constructor.
		 * 
		 * @param session The working session.
		 */
		public ActionClose(Session session) {
			super(session);
			ActionUtils.setDefaultCloseAction(this, true);
		}

		/**
		 * Perform the action.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionFrame frame = (JOptionFrame) ActionUtils.getUserObject(this);
			frame.setVisible(false);
			frame.dispose();
		}

	}

	/**
	 * Constructor.
	 */
	public ActionSynchronizeServerInstruments() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Session session = ActionUtils.getSession(this);
		Server server = (Server) ActionUtils.getLaunchArgs(this);
		
		// Progress group.
		JPanelProgressGroup panelGroup = new JPanelProgressGroup(session);
		SynchronizeServerInstruments task = new SynchronizeServerInstruments(session, server);
		panelGroup.add(task);
		
		JOptionDialog dialog = new JOptionDialog(session);
		dialog.setTitle("CMA Update utility");
		dialog.setComponent(new JScrollPane(panelGroup));
		dialog.addOption(new ActionClose(session));
		dialog.setModal(false);
		dialog.showDialog(true);
		
	}

}
