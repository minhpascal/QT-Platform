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

package test.com.msasc.library.util.file;

import java.io.File;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JPanelProgressGroup;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileCopy;

import test.com.msasc.library.swing.TestBox;

/**
 * Test the file copy task.
 * 
 * @author Miquel Sas
 */
public class TestFileCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");
		File source = new File("C:\\Development\\Eclipse-Workspaces\\Trading\\workspace-trading-backup");
		File destination = new File("C:\\Development\\Eclipse-Workspaces\\Trading\\workspace-trading-backup-2");
//		File source = new File("C:\\Development\\Eclipse-Workspaces\\Roca\\cma-head\\CMA_Central");
//		File destination = new File("C:\\Development\\ZTest\\CMA_Central");
//		File destination = new File("Z:\\CMA\\CMA_Central\\mads");

		Session session = new Session(Locale.UK);
		FileCopy fileCopy = new FileCopy(session);
		fileCopy.setName("TestFileCopy");
		fileCopy.setDescription("First test of file copy utility");
		fileCopy.addDirectories(source, destination);
		
		
		fileCopy.setPurgeDestination(true);
		
		JPanelProgressGroup panelGroup = new JPanelProgressGroup(session);
		panelGroup.setPanelProgressWidth(1000);
		panelGroup.add(fileCopy);
		
//		new Thread(fileCopy,"File copy util").start();
		
		TestBox.show(panelGroup);
		System.exit(0);
		
	}

}
