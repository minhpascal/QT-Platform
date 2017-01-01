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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.task.TaskMonitor;
import com.qtplaf.library.util.NumberUtils;

/**
 * A container for progress panels. Fills the content from left to right, up to down, with the fixed number of columns
 * and any necessary number of rows.
 * 
 * @author Miquel Sas
 */
public class JPanelProgressGroup extends JPanel implements TaskMonitor {

	/**
	 * Runnable to invoke later adding a panel.
	 */
	class AddPanelProgress implements Runnable {
		JPanelProgress panelProgress;

		AddPanelProgress(JPanelProgress panelProgress) {
			this.panelProgress = panelProgress;
		}

		public void run() {
			List<JPanelProgress> panels = getPanels();
			panels.add(panelProgress);
			layoutPanels(panels);
			repaint();
		}
	}

	/**
	 * Runnable to invoke later removing terminated tasks.
	 */
	class RemoveTerminated implements Runnable {
		public void run() {
			removeTerminated();
		}
	}

	/** Number of columns. */
	private int columns = 1;
	/** Working session. */
	private Session session;
	/** Initial panel width. */
	private int panelProgressWidth = 500;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JPanelProgressGroup(Session session) {
		super();
		this.session = session;
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
	}

	/**
	 * Add a task and monitor its progress.
	 * 
	 * @param task The task to monitor.
	 */
	@Override
	synchronized public void add(Task task) {
		// Check already added.
		if (containsTask(task)) {
			return;
		}
		JPanelProgress panelProgress = new JPanelProgress(getSession());
		panelProgress.setPanelProgressWidth(getPanelProgressWidth());
		panelProgress.monitorTask(task);
		add(panelProgress);
	}

	/**
	 * Check if this monitor already constains the task.
	 * 
	 * @param task The task to check.
	 * @return A boolean.
	 */
	private boolean containsTask(Task task) {
		List<Component> components = SwingUtils.getAllComponents(this);
		for (Component component : components) {
			if (component instanceof JPanelProgress) {
				JPanelProgress panelProgress = (JPanelProgress) component;
				if (panelProgress.getMonitoringTask() == task) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Remove the task and stop monitoring it.
	 * 
	 * @param task The task to remove.
	 */
	@Override
	synchronized public void remove(Task task) {
		SwingUtils.invokeLater(new RemoveTerminated());
	}

	/**
	 * Add a progress panel.
	 * 
	 * @param panelProgress The progress panel to add.
	 */
	public void add(JPanelProgress panelProgress) {
		SwingUtils.invokeLater(new AddPanelProgress(panelProgress));
	}

	/**
	 * Remove the progress panel.
	 * 
	 * @param panelProgress The progress panel to remove.
	 */
	public void remove(JPanelProgress panelProgress) {
		List<JPanelProgress> panels = getPanels();
		panels.remove(panelProgress);
		layoutPanels(panels);
		doLayout();
		repaint();
		if (SwingUtils.getWindowAncestor(this) != null) {
			SwingUtils.getWindowAncestor(this).revalidate();
		}
	}

	/**
	 * Remove panels that contain a terminated task that is to remove from monitor.
	 */
	private void removeTerminated() {
		List<JPanelProgress> panels = getPanels();
		for (JPanelProgress panel : panels) {
			Task task = panel.getMonitoringTask();
			if (task.isTerminated() && task.isRemoveFromMonitorWhenTerminated()) {
				remove(panel);
			}
		}
	}

	/**
	 * Layout panels.
	 * 
	 * @param panels The list of panels.
	 */
	private void layoutPanels(List<JPanelProgress> panels) {
		removeAll();
		int x = 0;
		int y = 0;
		for (int i = 0; i < panels.size(); i++) {
			JPanelProgress panel = panels.get(i);
			if (x == columns) {
				x = 0;
				y++;
			}
			GridBagConstraints constrains = new GridBagConstraints();
			constrains.gridx = x;
			constrains.gridy = y;
			constrains.gridwidth = 1;
			constrains.gridheight = 1;
			constrains.weightx = 1;
			int reminder = NumberUtils.remainder(panels.size(), columns);
			int indexWeight = panels.size() - (reminder == 0 ? columns : reminder);
			constrains.weighty = (i >= indexWeight ? 1 : 0);
			constrains.anchor = GridBagConstraints.NORTHWEST;
			constrains.fill = GridBagConstraints.HORIZONTAL;
			constrains.insets = new Insets(1, 1, 1, 1);
			add(panel, constrains);
			x++;
		}

		if (!panels.isEmpty() && panels.size() == getMaximumPanelLevels(panels)) {
			Window window = SwingUtils.getWindowAncestor(this);
			if (window != null) {
				window.pack();
			}
		}
	}

	/**
	 * Returns the maximum number of panel levels using the task levels of each panel task.
	 * 
	 * @param panels The list of panels.
	 * @return The maximum number of levels.
	 */
	private int getMaximumPanelLevels(List<JPanelProgress> panels) {

		// Fill a list of bottom tasks.
		List<Task> bottomTasks = new ArrayList<>();
		for (JPanelProgress panel : panels) {
			Task task = panel.getMonitoringTask();
			if (task != null && task.getParent() == null) {
				fillBottomTasks(bottomTasks, task);
			}
		}

		// Calculate levels.
		int levels = 0;
		for (Task task : bottomTasks) {
			levels = Math.max(levels, getTaskLevels(task));
		}

		return levels;
	}

	/**
	 * Returns the number of task levels starting at the bottom task and moving up.
	 * 
	 * @param bottomTask The source bottom task.
	 * @return The number of levels to up.
	 */
	private int getTaskLevels(Task bottomTask) {
		int levels = 1;
		Task parent = bottomTask.getParent();
		while (parent != null) {
			levels++;
			parent = parent.getParent();
		}
		return levels;
	}

	/**
	 * Fill the list of bottom tasks with the bottom children of the task argument.
	 * 
	 * @param bottomTasks The list of bottom tasks to fill.
	 * @param task The sourc task.
	 */
	private void fillBottomTasks(List<Task> bottomTasks, Task task) {
		List<Task> children = task.getChildren();
		if (children == null || children.isEmpty()) {
			bottomTasks.add(task);
			return;
		}
		for (Task child : children) {
			fillBottomTasks(bottomTasks, child);
		}
	}

	/**
	 * Returns the list of <code>JPanelProgress</code> unique components.
	 * 
	 * @return The list of <code>JPanelProgress</code> unique components.
	 */
	private List<JPanelProgress> getPanels() {
		validateComponents();
		List<JPanelProgress> panels = new ArrayList<>();
		Component[] components = getComponents();
		for (Component component : components) {
			panels.add((JPanelProgress) component);
		}
		return panels;
	}

	/**
	 * Validates that the components contained in the panel are <code>JPanelProgress</code> panels.
	 */
	private void validateComponents() {
		Component[] components = getComponents();
		for (Component component : components) {
			if (!(component instanceof JPanelProgress)) {
				throw new IllegalStateException("All components must be of JPanelProgress class");
			}
		}
	}

	/**
	 * Returns the number of columns.
	 * 
	 * @return The number of columns.
	 */
	public synchronized int getColumns() {
		return columns;
	}

	/**
	 * Sets the number of columns.
	 * 
	 * @param columns The number of columns.
	 */
	public synchronized void setColumns(int columns) {
		if (columns <= 0) {
			throw new IllegalArgumentException("Columns must be greater than zero.");
		}
		this.columns = columns;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}


	/**
	 * Returns the panel progress width.
	 * 
	 * @return The panel progress width.
	 */
	public int getPanelProgressWidth() {
		return panelProgressWidth;
	}

	/**
	 * Sets the panel progress width.
	 * 
	 * @param panelProgressWidth The panel progress width.
	 */
	public void setPanelProgressWidth(int panelProgressWidth) {
		this.panelProgressWidth = panelProgressWidth;
	}
}
