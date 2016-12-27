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
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.AccessMode;
import com.qtplaf.library.app.Session;

/**
 * An item of tree menu system.
 * 
 * @author Miquel Sas
 */
public class TreeMenuItem {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger();

	/**
	 * Optional open icon.
	 */
	private Icon openIcon;
	/**
	 * Optional closed icon.
	 */
	private Icon closedIcon;
	/**
	 * Optional leaf icon.
	 */
	private Icon leafIcon;
	/**
	 * The list of labels.
	 */
	private List<String> labels = new ArrayList<>();
	/**
	 * Accelerator key.
	 */
	private KeyStroke acceleratorKey;
	/**
	 * The node that holds this tree menu item.
	 */
	private DefaultMutableTreeNode node;
	/**
	 * A boolean that indicates if the level should be displayed.
	 */
	private boolean displayLevel = true;
	/**
	 * The action class to execute.
	 */
	private Class<? extends Action> actionClass;
	/**
	 * The access key to check security access.
	 */
	private String accessKey;
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public TreeMenuItem(Session session) {
		super();
		this.session = session;
	}

	/**
	 * Returns a boolean indicating whether this tree menu item action, if any, can be executed by the working session
	 * (and user) configuration.
	 * 
	 * @return A boolean.
	 */
	public boolean canExecute() {
		AccessMode accessMode = getSession().getAccessMode(getAccessKey());
		if (accessMode.equals(AccessMode.Denied)) {
			String message = getSession().getString("treeMenuAccessDenied");
			MessageBox.warning(getSession(), message);
			return false;
		}
		return true;
	}

	/**
	 * Executes this menu item action if present.
	 * 
	 * @param e The action event that promoted this execution.
	 */
	public void execute(ActionEvent e) {
		if (getActionClass() != null) {
			if (canExecute()) {
				Action action = getAction();
				action.actionPerformed(e);
			}
		} else {
			String message = getSession().getString("treeMenuNoAction");
			MessageBox.info(getSession(), message);
			return;
		}
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
	 * Returns the access key. If an access key has not been set, the action class name is returned.
	 * 
	 * @return the accessKey The access key.
	 */
	public String getAccessKey() {
		if (accessKey == null) {
			if (getActionClass() != null) {
				return getActionClass().toString();
			}
		}
		return accessKey;
	}

	/**
	 * Sets the security access key.
	 * 
	 * @param accessKey The security access key.
	 */
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	/**
	 * Returns the optional leaf icon or null.
	 * 
	 * @return The optional leaf icon or null.
	 */
	public Icon getLeafIcon() {
		return leafIcon;
	}

	/**
	 * Sets the optional leaf icon.
	 * 
	 * @param leafIcon The leaf icon.
	 */
	public void setLeafIcon(Icon leafIcon) {
		this.leafIcon = leafIcon;
	}

	/**
	 * Returns the optional open icon.
	 * 
	 * @return The optional open icon.
	 */
	public Icon getOpenIcon() {
		return openIcon;
	}

	/**
	 * Sets the optional open icon.
	 * 
	 * @param openIcon The optional open icon.
	 */
	public void setOpenIcon(Icon openIcon) {
		this.openIcon = openIcon;
	}

	/**
	 * Returns the optional closed icon.
	 * 
	 * @return The optional closed icon.
	 */
	public Icon getClosedIcon() {
		return closedIcon;
	}

	/**
	 * Sets the optional closed icon.
	 * 
	 * @param closedIcon The optional closed icon.
	 */
	public void setClosedIcon(Icon closedIcon) {
		this.closedIcon = closedIcon;
	}

	/**
	 * Adds a label to the list of labels.
	 * 
	 * @param label The label.
	 */
	public void addLabel(String label) {
		labels.add(label);
	}

	/**
	 * Add a list of labels.
	 * 
	 * @param labels The list of labels.
	 */
	public void addLabels(String... labels) {
		for (String label : labels) {
			addLabel(label);
		}
	}

	/**
	 * Returns the action to execute conveniently initialized.
	 * 
	 * @return The action.
	 */
	public Action getAction() {
		if (getActionClass() != null) {
			try {
				Constructor<? extends Action> constructor = getActionClass().getConstructor();
				Action action = (Action) constructor.newInstance();
				
				// Set the session.
				ActionUtils.setSession(action, getSession());
				
				// Access mode readonly to edit mode readonly.
				AccessMode accessMode = getSession().getAccessMode(getAccessKey());
				if (accessMode.equals(AccessMode.ReadOnly)) {
					ActionUtils.setEditMode(action, EditMode.ReadOnly);
				}
				
				return action;
			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
		return null;
	}

	/**
	 * Returns the action class.
	 * 
	 * @return The action class.
	 */
	public Class<? extends Action> getActionClass() {
		return actionClass;
	}

	/**
	 * Sets the action class.
	 * 
	 * @param actionClass The action class.
	 */
	public void setActionClass(Class<? extends Action> actionClass) {
		this.actionClass = actionClass;
	}

	/**
	 * Returns the number of labels.
	 * 
	 * @return The number of labels.
	 */
	public int getLabelCount() {
		return labels.size();
	}

	/**
	 * Returns the label at the given index.
	 * 
	 * @param index The index of the label.
	 * @return The label.
	 */
	public String getLabel(int index) {
		return labels.get(index);
	}

	/**
	 * Returns the optional accelerator key.
	 * 
	 * @return The optional accelerator key.
	 */
	public KeyStroke getAcceleratorKey() {
		return acceleratorKey;
	}

	/**
	 * Sets the optional accelerator key.
	 * 
	 * @param acceleratorKey The optional accelerator key.
	 */
	public void setAcceleratorKey(KeyStroke acceleratorKey) {
		this.acceleratorKey = acceleratorKey;
	}

	/**
	 * Returns the node that holds this menu item.
	 * 
	 * @return The node that holds this menu item.
	 */
	public DefaultMutableTreeNode getNode() {
		return node;
	}

	/**
	 * Sets the node that holds this menu item.
	 * 
	 * @param node The node that holds this menu item.
	 */
	public void setNode(DefaultMutableTreeNode node) {
		this.node = node;
	}

	/**
	 * Returns a boolean that indicates whether the level should be displayed.
	 * 
	 * @return A boolean.
	 */
	public boolean isDisplayLevel() {
		return displayLevel;
	}

	/**
	 * Sets a boolean that indicates whether the level should be displayed.
	 * 
	 * @param displayLevel A boolean.
	 */
	public void setDisplayLevel(boolean displayLevel) {
		this.displayLevel = displayLevel;
	}

	/**
	 * Returns the parent menu item or null.
	 * 
	 * @return The parent menu item.
	 */
	public TreeMenuItem getParent() {
		if (getNode() != null) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getNode().getParent();
			if (parentNode != null) {
				return (TreeMenuItem) parentNode.getUserObject();
			}
		}
		return null;
	}

	/**
	 * Returns the list of children menu items.
	 * 
	 * @return The list of children menu items.
	 */
	public List<TreeMenuItem> getChildren() {
		List<TreeMenuItem> children = new ArrayList<>();
		if (getNode() != null) {
			int count = getNode().getChildCount();
			for (int i = 0; i < count; i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) getNode().getChildAt(i);
				TreeMenuItem child = (TreeMenuItem) childNode.getUserObject();
				if (child != null) {
					children.add(child);
				}
			}
		}
		return children;
	}

	/**
	 * Returns a string that represents the level in the form 1.12.4 etc.
	 * 
	 * @return A string representation of the level.
	 */
	public String getLevel() {
		StringBuilder b = new StringBuilder();
		TreeMenuItem child = this;
		while (child.getParent() != null) {
			// Retrieve the child index.
			int index = child.getParent().getChildren().indexOf(child) + 1;
			b.insert(0, Integer.toString(index));
			// Move to parent.
			child = child.getParent();
			// If still another level, insert the dot.
			if (child.getParent() != null) {
				b.insert(0, ".");
			}
		}
		return b.toString();
	}

	/**
	 * Returns a string representation of the menu item.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getLevel());
		if (!labels.isEmpty()) {
			for (String label : labels) {
				if (b.length() > 0) {
					b.append(", ");
				}
				b.append(label);
			}
		}
		return b.toString();
	}

}
