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
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.action.DefaultActionClose;
import com.qtplaf.library.swing.core.JOptionFrame;
import com.qtplaf.library.swing.core.JPanelProgressGroup;
import com.qtplaf.library.task.Task;

/**
 * Progress manager utility.
 * <p>
 * To perform actions or checkings prior to close the frame, add the actions to the list of pre-close actions. If the
 * closing should be cancelled, the action must thow any kind of exception, normally <tt>java.lang</tt> exception
 * compatible with the <tt>actionPerformed</tt> method like for instance <tt>IllegalStateException</tt>.
 * 
 * @author Miquel Sas
 */
public class ProgressManager {
	/**
	 * Action close.
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
			try {
				List<Task> tasks = progress.getTasks();
				for (Action action : preCloseActions) {
					ActionUtils.setTasks(action, tasks);
					action.actionPerformed(e);
				}
				frame.setVisible(false);
				frame.dispose();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Progress group monitor.
	 */
	private JPanelProgressGroup progress;
	/**
	 * The option frame to display the progress group.
	 */
	private JOptionFrame frame;
	/**
	 * List of pre-close actions.
	 */
	private List<Action> preCloseActions = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public ProgressManager(Session session) {
		super();
		progress = new JPanelProgressGroup(session);
		frame = new JOptionFrame(session);
		frame.setComponent(progress);
		frame.addAction(new ActionClose(session));
	}

	/**
	 * Add a task to be monitored.
	 * 
	 * @param task The task
	 */
	public void addTask(Task task) {
		progress.add(task);
	}

	/**
	 * Add a pre-close action.
	 * 
	 * @param action The action to execute prior to close the frame.
	 */
	public void addPreCloseAction(Action action) {
		preCloseActions.add(action);
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return progress.getSession();
	}
	
	/**
	 * Sets the panel progress width.
	 * 
	 * @param panelProgressWidth The panel progress width.
	 */
	public void setPanelProgressWidth(int panelProgressWidth) {
		progress.setPanelProgressWidth(panelProgressWidth);;
	}

	/**
	 * Show the frame resizable.
	 */
	public void showFrame() {
		showFrame(true);
	}

	/**
	 * Show the frame..
	 * 
	 * @param resizable A boolean that indicates if the frame is resizable.
	 */
	public void showFrame(boolean resizable) {
		frame.showFrame(resizable);
	}
}
