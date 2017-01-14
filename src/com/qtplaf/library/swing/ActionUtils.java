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

import java.util.List;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.core.JFormRecord;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.util.ImageIconUtils;
import com.qtplaf.library.util.Properties;

/**
 * Action utilities.
 * 
 * @author Miquel Sas
 */
public class ActionUtils {

	/** A key for generic user properties. */
	private static final String KeyProperties = "Properties";

	/** Integer key value. */
	private static int index = 0;

	/** The current working session. */
	private static final Integer KeySession = Integer.valueOf(index++);
	/** The key for the action group list of buttons. */
	private static final Integer KeyActionGroup = Integer.valueOf(index++);
	/**
	 * Original or source for the name, useful if the name is formatted HTML and a mnemonic is set to the associated
	 * button.
	 */
	private static final Integer KeySourceName = Integer.valueOf(index++);
	/** Menu item text if different from the name or there if no name for the action (small icon buttons). */
	private static final Integer KeyMenuItemSourceText = Integer.valueOf(index++);
	/** Original or source tooltip for the short description. */
	private static final Integer KeyToolTip = Integer.valueOf(index++);
	/** Key for the previous value, used by value actions to indicate the value prior to be changed. */
	private static final Integer KeyPreviousValue = Integer.valueOf(index++);
	/** Key for the current value, used by value actions to indicate the value after the change. */
	private static final Integer KeyCurrentValue = Integer.valueOf(index++);
	/** Key for the edit context. */
	private static final Integer KeyEditContext = Integer.valueOf(index++);
	/** Key for the edit mode. */
	private static final Integer KeyEditMode = Integer.valueOf(index++);
	/** The button container of the action. */
	private static final Integer KeyButton = Integer.valueOf(index++);
	/** A sort key index to sort actions in a buttons panel or in any other means. */
	private static final Integer KeySortIndex = Integer.valueOf(index++);
	/** A list of selected records, mainly used on a lookup action. */
	private static final Integer KeySelectedRecords = Integer.valueOf(index++);
	/** A boolean to indicate that a lookup action is multiple selection. */
	private static final Integer KeyMultipleSelection = Integer.valueOf(index++);
	/** Form record key. When an action is added to a record form, the form is accessible through this key. */
	private static final Integer KeyFormRecord = Integer.valueOf(index++);
	/** A boolean that indicates if the action (button action) is visible in the buttons panel. Default is true. */
	private static final Integer KeyVisibleInButtonsPanel = Integer.valueOf(index++);
	/** A boolean that indicates if the action (button action) is visible in a popup menu. Default is true. */
	private static final Integer KeyVisibleInPopupMenu = Integer.valueOf(index++);
	/** A boolean to indicate that the action is the default close action in dialog and frames. */
	private static final Integer KeyDefaultCloseAction = Integer.valueOf(index++);
	/** A an object used as launch arguments fromm the menu. */
	private static final Integer KeyLauchArgs = Integer.valueOf(index++);
	/** A list of tasks, used in the progress manager to pass the tasks to actions. */
	private static final Integer KeyTasks = Integer.valueOf(index++);
	/** A generic user object. */
	private static final Integer KeyUserObject = Integer.valueOf(index++);

	/**
	 * Returns the sort index value.
	 * 
	 * @param action The action.
	 * @return The sort index value.
	 */
	public static int getSortIndex(Action action) {
		return getProperties(action).getInteger(KeySortIndex, -1);
	}

	/**
	 * Returns the visible in popup menu value.
	 * 
	 * @param action The action.
	 * @return The visible in popup menu value.
	 */
	public static boolean isVisibleInPopupMenu(Action action) {
		return getProperties(action).getBoolean(KeyVisibleInPopupMenu, true);
	}

	/**
	 * Returns the user properties value.
	 * 
	 * @param action The action.
	 * @return The user properties.
	 */
	public static Properties getProperties(Action action) {
		Properties properties = (Properties) action.getValue(KeyProperties);
		if (properties == null) {
			properties = new Properties();
			action.putValue(KeyProperties, properties);
		}
		return properties;
	}

	/**
	 * Returns the visible in buttons panel value.
	 * 
	 * @param action The action.
	 * @return The visible in buttons panel value.
	 */
	public static boolean isVisibleInButtonsPanel(Action action) {
		return getProperties(action).getBoolean(KeyVisibleInButtonsPanel, true);
	}

	/**
	 * Returns the edit mode value.
	 * 
	 * @param action The action.
	 * @return The edit mode value.
	 */
	public static EditMode getEditMode(Action action) {
		return (EditMode) getProperties(action).getObject(KeyEditMode);
	}

	/**
	 * Returns the form record value.
	 * 
	 * @param action The action.
	 * @return The form record value.
	 */
	public static JFormRecord getFormRecord(Action action) {
		return (JFormRecord) getProperties(action).getObject(KeyFormRecord);
	}

	/**
	 * Returns the current value.
	 * 
	 * @param action The action.
	 * @return The current value.
	 */
	public static Value getCurrentValue(Action action) {
		return (Value) getProperties(action).getObject(KeyCurrentValue);
	}

	/**
	 * Returns the previous value.
	 * 
	 * @param action The action.
	 * @return The previous value.
	 */
	public static Value getPreviousValue(Action action) {
		return (Value) getProperties(action).getObject(KeyPreviousValue);
	}

	/**
	 * Returns the multiple selection value.
	 * 
	 * @param action The action.
	 * @return The multiple selection value.
	 */
	public static boolean isMultipleSelection(Action action) {
		return getProperties(action).getBoolean(KeyMultipleSelection);
	}

	/**
	 * Returns a boolean indicating whethe the action is a default close action.
	 * 
	 * @param action The action.
	 * @return A boolean.
	 */
	public static boolean isDefaultCloseAction(Action action) {
		return getProperties(action).getBoolean(KeyDefaultCloseAction);
	}

	/**
	 * Returns the edit context value.
	 * 
	 * @param action The action.
	 * @return The edit context value.
	 */
	public static EditContext getEditContext(Action action) {
		return (EditContext) getProperties(action).getObject(KeyEditContext);
	}

	/**
	 * Returns the accelerator key value.
	 * 
	 * @param action The action.
	 * @return The accelerator key value.
	 */
	public static KeyStroke getAcceleratorKey(Action action) {
		return (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
	}

	/**
	 * Returns the session value.
	 * 
	 * @param action The action.
	 * @return The session value.
	 */
	public static Session getSession(Action action) {
		return (Session) getProperties(action).getObject(KeySession);
	}

	/**
	 * Returns the name.
	 * 
	 * @param action The action.
	 * @return The name.
	 */
	public static String getName(Action action) {
		return (String) action.getValue(Action.NAME);
	}

	/**
	 * Returns the short description.
	 * 
	 * @param action The action.
	 * @return The short description.
	 */
	public static String getShortDescription(Action action) {
		return (String) action.getValue(Action.SHORT_DESCRIPTION);
	}

	/**
	 * Returns the long description value.
	 * 
	 * @param action The action.
	 * @return The long description value.
	 */
	public static String getLongDescription(Action action) {
		return (String) action.getValue(Action.LONG_DESCRIPTION);
	}

	/**
	 * Returns the source name value.
	 * 
	 * @param action The action.
	 * @return The source name value.
	 */
	public static String getSourceName(Action action) {
		return getProperties(action).getString(KeySourceName);
	}

	/**
	 * Returns the user object installed in the action.
	 * 
	 * @param action The action.
	 * @return The user object or null.
	 */
	public static Object getUserObject(Action action) {
		return getProperties(action).getObject(KeyUserObject);
	}

	/**
	 * Returns the launch argument object.
	 * 
	 * @param action The action.
	 * @return The launch argument object or null.
	 */
	public static Object getLaunchArgs(Action action) {
		return getProperties(action).getObject(KeyLauchArgs);
	}

	/**
	 * Returns the small icon.
	 * 
	 * @param action The action.
	 * @return The small icon.
	 */
	public static Icon getSmallIcon(Action action) {
		return (Icon) action.getValue(Action.SMALL_ICON);
	}

	/**
	 * Returns the menu item source text value.
	 * 
	 * @param action The action.
	 * @return The menu item source text value.
	 */
	public static String getMenuItemSourceText(Action action) {
		return getProperties(action).getString(KeyMenuItemSourceText);
	}

	/**
	 * Returns the tool tip value.
	 * 
	 * @param action The action.
	 * @return The tool tip value.
	 */
	public static String getToolTip(Action action) {
		return getProperties(action).getString(KeyToolTip);
	}

	/**
	 * Returns the action group value.
	 * 
	 * @param action The action.
	 * @return The action group value.
	 */
	public static ActionGroup getActionGroup(Action action) {
		return (ActionGroup) getProperties(action).getObject(KeyActionGroup);
	}

	/**
	 * Returns the button value.
	 * 
	 * @param action The action.
	 * @return The button value.
	 */
	public static JButton getButton(Action action) {
		return (JButton) getProperties(action).getObject(KeyButton);
	}

	/**
	 * Returns the list of selected records.
	 * 
	 * @param action The action.
	 * @return The list of selected records.
	 */
	@SuppressWarnings("unchecked")
	public static List<Record> getSelectedRecords(Action action) {
		return (List<Record>) getProperties(action).getObject(KeySelectedRecords);
	}

	/**
	 * Returns the list of tasks.
	 * 
	 * @param action The action.
	 * @return The list of tasks.
	 */
	@SuppressWarnings("unchecked")
	public static List<Task> getTasks(Action action) {
		return (List<Task>) getProperties(action).getObject(KeyTasks);
	}

	/**
	 * Set the sort index value.
	 * 
	 * @param action The action.
	 * @param sortIndex The sort index value.
	 */
	public static void setSortIndex(Action action, int sortIndex) {
		getProperties(action).setInteger(KeySortIndex, sortIndex);
	}

	/**
	 * Set the edit mode value.
	 * 
	 * @param action The action.
	 * @param editMode The edit mode value.
	 */
	public static void setEditMode(Action action, EditMode editMode) {
		getProperties(action).setObject(KeyEditMode, editMode);
	}

	/**
	 * Set the edit context value.
	 * 
	 * @param action The action.
	 * @param editContext The edit context value.
	 */
	public static void setEditContext(Action action, EditContext editContext) {
		getProperties(action).setObject(KeyEditContext, editContext);
	}

	/**
	 * Set the current value.
	 * 
	 * @param action The action.
	 * @param value The value.
	 */
	public static void setCurrentValue(Action action, Value value) {
		getProperties(action).setObject(KeyCurrentValue, value);
	}

	/**
	 * Set the previous value.
	 * 
	 * @param action The action.
	 * @param value The value.
	 */
	public static void setPreviousValue(Action action, Value value) {
		getProperties(action).setObject(KeyPreviousValue, value);
	}

	/**
	 * Set the session.
	 * 
	 * @param action The action.
	 * @param session The session.
	 */
	public static void setSession(Action action, Session session) {
		getProperties(action).setObject(KeySession, session);
	}

	/**
	 * Set the name.
	 * 
	 * @param action The action.
	 * @param name The name.
	 */
	public static void setName(Action action, String name) {
		action.putValue(Action.NAME, name);
	}

	/**
	 * Set the source name value.
	 * 
	 * @param action The action.
	 * @param sourceName The text value.
	 */
	public static void setSourceName(Action action, String sourceName) {
		getProperties(action).setString(KeySourceName, sourceName);
	}

	/**
	 * Set a generic user object.
	 * 
	 * @param action The action.
	 * @param userObject The user object.
	 */
	public static void setUserObject(Action action, Object userObject) {
		getProperties(action).setObject(KeyUserObject, userObject);
	}

	/**
	 * Sets the launch arguments object.
	 * 
	 * @param action The action.
	 * @param launchArgs The launch arguments object.
	 */
	public static void setLaunchArgs(Action action, Object launchArgs) {
		getProperties(action).setObject(KeyLauchArgs, launchArgs);
	}

	/**
	 * Set the menu item source text value.
	 * 
	 * @param action The action.
	 * @param sourceText The text value.
	 */
	public static void setMenuItemSourceText(Action action, String sourceText) {
		getProperties(action).setString(KeyMenuItemSourceText, sourceText);
	}

	/**
	 * PSetut the tool tip value.
	 * 
	 * @param action The action.
	 * @param toolTip The tool tip value.
	 */
	public static void setToolTip(Action action, String toolTip) {
		getProperties(action).setString(KeyToolTip, toolTip);
	}

	/**
	 * Put the short description.
	 * 
	 * @param action The action.
	 * @param shortDescription The short description.
	 */
	public static void setShortDescription(Action action, String shortDescription) {
		action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
	}

	/**
	 * Set the long description value.
	 * 
	 * @param action The action.
	 * @param longDescription The long description value.
	 */
	public static void setLongDescription(Action action, String longDescription) {
		action.putValue(Action.LONG_DESCRIPTION, longDescription);
	}

	/**
	 * Set the visible in buttons panel value.
	 * 
	 * @param action The action.
	 * @param visibleInButtonsPanel The visible in buttons panel value.
	 */
	public static void setVisibleInButtonsPanel(Action action, boolean visibleInButtonsPanel) {
		getProperties(action).setBoolean(KeyVisibleInButtonsPanel, visibleInButtonsPanel);
	}

	/**
	 * Set the visible in popup menu value.
	 * 
	 * @param action The action.
	 * @param visibleInPopupMenu The visible in popup menu value.
	 */
	public static void setVisibleInPopupMenu(Action action, boolean visibleInPopupMenu) {
		getProperties(action).setBoolean(KeyVisibleInPopupMenu, visibleInPopupMenu);
	}

	/**
	 * Set the multiple selection value.
	 * 
	 * @param action The action.
	 * @param multipleSelection The multiple selection value.
	 */
	public static void setMultipleSelection(Action action, boolean multipleSelection) {
		getProperties(action).setBoolean(KeyMultipleSelection, multipleSelection);
	}
	
	/**
	 * Set the action as default close action..
	 * 
	 * @param action The action.
	 * @param defaultClose A boolean.
	 */
	public static void setDefaultCloseAction(Action action, boolean defaultClose) {
		getProperties(action).setBoolean(KeyDefaultCloseAction, defaultClose);
	}
	
	/**
	 * Set the button value.
	 * 
	 * @param action The action.
	 * @param button The button value.
	 */
	public static void setButton(Action action, JButton button) {
		getProperties(action).setObject(KeyButton, button);
	}

	/**
	 * Put the accelerator key value.
	 * 
	 * @param action The action.
	 * @param acceleratorKey The accelerator key value.
	 */
	public static void setAcceleratorKey(Action action, KeyStroke acceleratorKey) {
		action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
	}

	/**
	 * Set the action group value.
	 * 
	 * @param action The action.
	 * @param actionGroup The action group value.
	 */
	public static void setActionGroup(Action action, ActionGroup actionGroup) {
		getProperties(action).setObject(KeyActionGroup, actionGroup);
	}

	/**
	 * Set the form record value.
	 * 
	 * @param action The action.
	 * @param formRecord The form record value.
	 */
	public static void setFormRecord(Action action, JFormRecord formRecord) {
		getProperties(action).setObject(KeyFormRecord, formRecord);
	}

	/**
	 * Set the list of selected records value.
	 * 
	 * @param action The action.
	 * @param selectedRecords The list of selected records value.
	 */
	public static void setSelectedRecords(Action action, List<Record> selectedRecords) {
		getProperties(action).setObject(KeySelectedRecords, selectedRecords);
	}

	/**
	 * Set the list of tasks.
	 * 
	 * @param action The action.
	 * @param tasks The list of tasks.
	 */
	public static void setTasks(Action action, List<Task> tasks) {
		getProperties(action).setObject(KeyTasks, tasks);
	}

	/**
	 * Set the small icon value.
	 * 
	 * @param action The action.
	 * @param iconPath The icon path in the images jar files.
	 */
	public static void setSmallIcon(Action action, String iconPath) {
		setSmallIcon(action, ImageIconUtils.getImageIcon(iconPath));
	}

	/**
	 * Set the small icon value.
	 * 
	 * @param action The action.
	 * @param iconPath The icon path in the images jar files.
	 */
	public static void setSmallIcon(Action action, Icon icon) {
		action.putValue(Action.SMALL_ICON, icon);
	}

	/**
	 * Returns the action name that should apply. If the action has a source name and a key stroke, an HTML name that
	 * informs of the key is builded.
	 * 
	 * @param action
	 * @return
	 */
	public static String getActionName(Action action) {
		Session session = ActionUtils.getSession(action);
		Locale locale = (session == null ? Locale.UK : session.getLocale());

		StringBuilder b = new StringBuilder();
		String name = ActionUtils.getName(action);
		String sourceName = ActionUtils.getSourceName(action);
		KeyStroke keyStroke = ActionUtils.getAcceleratorKey(action);
		if (sourceName != null) {
			if (keyStroke == null) {
				b.append(sourceName);
			} else {
				b.append("<html>");
				b.append(sourceName);
				b.append(" ");
				b.append("<font color=\"gray\">");
				// b.append("&lt;");
				b.append("(");
				b.append(SwingUtils.translate(keyStroke, locale));
				b.append(")");
				// b.append("&gt;");
				b.append("</font>");
				b.append("</html>");
			}
		} else {
			if (name != null) {
				b.append(name);
			}
		}
		return b.toString();
	}

	/**
	 * Set the name returned by <code>getActionName()</code> to the action.
	 * 
	 * @param action The action to setup.
	 */
	public static void setActionName(Action action) {
		setName(action, getActionName(action));
	}
}
