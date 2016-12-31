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

import java.io.File;
import java.util.Locale;

import javax.swing.JOptionPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JFileChooser;
import com.qtplaf.library.util.TextServer;

/**
 * A file encryptor.
 * 
 * @author Miquel Sas
 */
public class FileEncrypt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Strings and session.
		TextServer.addBaseResource("SysString.xml");
		Session session = new Session(Locale.UK);
		Locale.setDefault(Locale.UK);

		// Ask whether to encrypt or decrypt.
		String title = "Please, indicate the option.";
		String message = "Encrypt or decrypt a file?";
		int messageType = JOptionPane.QUESTION_MESSAGE;
		String[] options = new String[] { "Encrypt", "Decrypt", "Cancel" };
		String initialOption = "Encrypt";
		String cancelOption = "Cancel";

		String option = OptionDialog.show(message, title, messageType, options, initialOption, cancelOption);
		if (option.equals("Cancel")) {
			System.exit(0);
			return;
		}
		boolean encrypt = option.equals("Encrypt");

		// Select the source file.
		JFileChooser chooserSource = new JFileChooser(session);
		chooserSource.setDialogTitle("Please, select the source file to "+(encrypt ? "encrypt" : "decrypt"));
		chooserSource.setDialogType(JFileChooser.OPEN_DIALOG);
		chooserSource.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooserSource.setAcceptAllFileFilterUsed(true);
		File fileSource = null;
		if (chooserSource.showDialog(null) == JFileChooser.APPROVE_OPTION) {
			fileSource = chooserSource.getSelectedFile();
		}
		if (fileSource == null) {
			System.exit(0);
			return;
		}
		
		
		
		System.out.println(fileSource);
		
	}

}
