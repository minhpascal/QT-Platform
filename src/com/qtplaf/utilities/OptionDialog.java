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

package com.qtplaf.utilities;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.qtplaf.library.swing.SwingUtils;

/**
 * Useful option dialogs using a <code>JOptionPane</code>.
 * 
 * @author Miquel Sas
 */
public class OptionDialog {

	/**
	 * Show the option dialog.
	 * 
	 * @param message The message.
	 * @param title The dialog title.
	 * @param messageType The message type.
	 * @param options The array of string options.
	 * @param initialOption The initial option.
	 * @param cancelOption The cancel option, returned when the dialog is closed.
	 * @return The selected option or cancel or null.
	 */
	public static String show(
		Object message,
		String title,
		int messageType,
		String[] options,
		String initialOption,
		String cancelOption) {

		JOptionPane pane = new JOptionPane();
		pane.setMessage(message);
		pane.setMessageType(messageType);
		pane.setOptionType(0);
		pane.setOptions(options);
		if (initialOption != null) {
			pane.setInitialValue(initialOption);
		}
		
		JDialog dialog = pane.createDialog(title);
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		SwingUtils.setMnemonics(SwingUtils.getAllButtons(dialog));
		dialog.pack();
		dialog.setVisible(true);

		Object value = pane.getValue();
		if (value instanceof String) {
			String option = (String) value;
			return option;
		}
		
		if (cancelOption != null) {
			return cancelOption;
		}
		
		return null;
	}
}
