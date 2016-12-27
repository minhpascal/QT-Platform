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

package com.qtplaf.library.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.View;
import com.qtplaf.library.swing.ActionGroup;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.EditMode;

/**
 * A usefull and quite generic properties table with typed accessors for most used objects. Using a map to store the
 * properties of an object has several advantages, like for instance a natural copy mechanism.
 * 
 * @author Miquel Sas
 */
public class Properties {

	/**
	 * The properties map.
	 */
	private Map<Object, Object> properties = new HashMap<>();

	/**
	 * Constructor.
	 */
	public Properties() {
		super();
	}

	/**
	 * Vlear this properties.
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * Fill this properties with the argument properties.
	 * 
	 * @param properties The properties used to fill this properties.
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties.properties);
	}

	/**
	 * Returns a stored boolean value, returning <code>false<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored boolean value.
	 */
	public boolean getBoolean(Object key) {
		return getBoolean(key, false);
	}

	/**
	 * Returns a stored boolean value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored boolean value.
	 */
	public boolean getBoolean(Object key, boolean defaultValue) {
		Boolean value = (Boolean) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a boolean value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setBoolean(Object key, boolean value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored string value.
	 */
	public String getString(Object key) {
		return getString(key, null);
	}

	/**
	 * Returns a stored string value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored string value.
	 */
	public String getString(Object key, String defaultValue) {
		String value = (String) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a string value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setString(Object key, String value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored integer value, returning <code>0<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored integer value.
	 */
	public int getInteger(Object key) {
		return getInteger(key, 0);
	}

	/**
	 * Returns a stored integer value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored integer value.
	 */
	public int getInteger(Object key, int defaultValue) {
		Integer value = (Integer) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an integer value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setInteger(Object key, int value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored double value, returning <code>0<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored double value.
	 */
	public double getDouble(Object key) {
		return getDouble(key, 0);
	}

	/**
	 * Returns a stored double value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored double value.
	 */
	public double getDouble(Object key, double defaultValue) {
		Double value = (Double) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a double value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setDouble(Object key, double value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored type, returning <code>Types.String<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored type.
	 */
	public Types getType(Object key) {
		return getType(key, Types.String);
	}

	/**
	 * Returns a stored type, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored type.
	 */
	public Types getType(Object key, Types defaultValue) {
		Types value = (Types) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a type.
	 * 
	 * @param key The key.
	 * @param value The type.
	 */
	public void setType(Object key, Types value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored Value, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored value.
	 */
	public Value getValue(Object key) {
		return getValue(key, null);
	}

	/**
	 * Returns a stored Value, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored value.
	 */
	public Value getValue(Object key, Value defaultValue) {
		Value value = (Value) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a Value.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setValue(Object key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored alignment, returning <code>Alignment.Left<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored alignment.
	 */
	public Alignment getAlignment(Object key) {
		return getAlignment(key, Alignment.Left);
	}

	/**
	 * Returns a stored alignment, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored alignment.
	 */
	public Alignment getAlignment(Object key, Alignment defaultValue) {
		Alignment value = (Alignment) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an Alignment.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setAlignment(Object key, Alignment value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored field, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored field.
	 */
	public Field getField(Object key) {
		return getField(key, null);
	}

	/**
	 * Returns a stored field, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored field.
	 */
	public Field getField(Object key, Field defaultValue) {
		Field value = (Field) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a field.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setField(Object key, Field value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored edit context, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored edit context.
	 */
	public EditContext getEditContext(Object key) {
		return getEditContext(key, null);
	}

	/**
	 * Returns a stored edit context, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored edit context.
	 */
	public EditContext getEditContext(Object key, EditContext defaultValue) {
		EditContext value = (EditContext) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an edit context.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setEditContext(Object key, EditContext value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored edit mode, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored edit mode.
	 */
	public EditMode getEditMode(Object key) {
		return getEditMode(key, null);
	}

	/**
	 * Returns a stored edit mode, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored edit mode.
	 */
	public EditMode getEditMode(Object key, EditMode defaultValue) {
		EditMode value = (EditMode) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an edit mode.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setEditMode(Object key, EditMode value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored locale, returning <code>Locale.UK<code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored locale.
	 */
	public Locale getLocale(Object key) {
		return getLocale(key, Locale.UK);
	}

	/**
	 * Returns a stored locale, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored locale.
	 */
	public Locale getLocale(Object key, Locale defaultValue) {
		Locale value = (Locale) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a locale.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setLocale(Object key, Locale value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored button, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored button.
	 */
	public JButton getButton(Object key) {
		return getButton(key, null);
	}

	/**
	 * Returns a stored button, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored button.
	 */
	public JButton getButton(Object key, JButton defaultValue) {
		JButton value = (JButton) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a button.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setButton(Object key, JButton value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored action group, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @return The stored action group.
	 */
	public ActionGroup getActionGroup(Object key) {
		return getActionGroup(key, ActionGroup.Default);
	}

	/**
	 * Returns a stored action group, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored action group.
	 */
	public ActionGroup getActionGroup(Object key, ActionGroup defaultValue) {
		ActionGroup value = (ActionGroup) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store an action group.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setActionGroup(Object key, ActionGroup value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored record, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored record.
	 */
	public Record getRecord(Object key) {
		return getRecord(key, null);
	}

	/**
	 * Returns a stored record, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored record.
	 */
	public Record getRecord(Object key, Record defaultValue) {
		Record value = (Record) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a record.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setRecord(Object key, Record value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored session, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored session.
	 */
	public Session getSession(Object key) {
		return getSession(key, null);
	}

	/**
	 * Returns a stored session, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored session.
	 */
	public Session getSession(Object key, Session defaultValue) {
		Session value = (Session) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a sessin.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setSession(Object key, Session value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored table, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored table.
	 */
	public Table getTable(Object key) {
		return getTable(key, null);
	}

	/**
	 * Returns a stored table, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored table.
	 */
	public Table getTable(Object key, Table defaultValue) {
		Table value = (Table) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a table.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setTable(Object key, Table value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored view, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored view.
	 */
	public View getView(Object key) {
		return getView(key, null);
	}

	/**
	 * Returns a stored view, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored view.
	 */
	public View getView(Object key, View defaultValue) {
		View value = (View) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a view.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setView(Object key, View value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored list, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored list.
	 */
	public List<?> getList(Object key) {
		return getList(key, null);
	}

	/**
	 * Returns a stored list, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored list.
	 */
	public List<?> getList(Object key, List<?> defaultValue) {
		List<?> list = (List<?>) properties.get(key);
		return (list == null ? defaultValue : list);
	}

	/**
	 * Store a list.
	 * 
	 * @param key The key.
	 * @param value The list.
	 */
	public void setList(Object key, List<?> value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored <code>KeyStroke</code>, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored <code>KeyStroke</code>.
	 */
	public KeyStroke getKeyStroke(Object key) {
		return getKeyStroke(key, null);
	}

	/**
	 * Returns a stored <code>KeyStroke</code>, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored <code>KeyStroke</code>.
	 */
	public KeyStroke getKeyStroke(Object key, KeyStroke defaultValue) {
		KeyStroke value = (KeyStroke) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a <code>KeyStroke</code>.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setKeyStroke(Object key, KeyStroke value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored <code>JLabel</code>, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored <code>JLabel</code>.
	 */
	public JLabel getLabel(Object key) {
		return getLabel(key, null);
	}

	/**
	 * Returns a stored <code>JLabel</code>, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored <code>JLabel</code>.
	 */
	public JLabel getLabel(Object key, JLabel defaultValue) {
		JLabel value = (JLabel) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a <code>JLabel</code>.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setLabel(Object key, JLabel value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored <code>JPanel</code>, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored <code>JPanel</code>.
	 */
	public JPanel getPanel(Object key) {
		return getPanel(key, null);
	}

	/**
	 * Returns a stored <code>JPanel</code>, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored <code>JPanel</code>.
	 */
	public JPanel getPanel(Object key, JPanel defaultValue) {
		JPanel value = (JPanel) properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a <code>JPanel</code>.
	 * 
	 * @param key The key.
	 * @param value The value.
	 */
	public void setPanel(Object key, JPanel value) {
		properties.put(key, value);
	}

	/**
	 * Returns a stored object, returning <code>null</code> if not set.
	 * 
	 * @param key The key.
	 * @return The stored object.
	 */
	public Object getObject(Object key) {
		return getObject(key, null);
	}

	/**
	 * Returns a stored object, returning the default one if not set.
	 * 
	 * @param key The key.
	 * @param defaultValue The default value.
	 * @return The stored object.
	 */
	public Object getObject(Object key, Object defaultValue) {
		Object value = properties.get(key);
		return (value == null ? defaultValue : value);
	}

	/**
	 * Store a object.
	 * 
	 * @param key The key.
	 * @param value The object.
	 */
	public void setObject(Object key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Remove the property at key.
	 * 
	 * @param key The key.
	 */
	public void remove(Object key) {
		properties.remove(key);
	}
}
