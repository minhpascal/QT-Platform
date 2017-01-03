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

package com.qtplaf.platform;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JFrameMenu;
import com.qtplaf.library.util.TextServer;

/**
 * Main entry of the QT-Platform.
 * 
 * @author Miquel Sas
 */
public class QTPlatform {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		TextServer.addBaseResource("StringsQTPlatform.xml");
		Session session = new Session(Locale.UK);

		// Frame menu.
		JFrameMenu frameMenu = new JFrameMenu(session);
		frameMenu.setTitle(session.getString("qtMenuTitle"));
		frameMenu.setLocation(20, 20);
		frameMenu.setSize(0.25, 0.65);
		
		// Start showing the console.
		frameMenu.showConsole();
		
		// Re-direct out and err.
		System.setOut(frameMenu.getConsole().getPrintStream());
		System.setErr(frameMenu.getConsole().getPrintStream());
		
		// Show the menu.
		frameMenu.setVisible(true);
		
		try { Thread.sleep(100); } catch (Exception ignore) {}
		
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		System.out.println("Hello console");
		try {Thread.sleep(50);} catch (Exception ignore) {}
		
		// Show the menu.
		frameMenu.showTreeMenu();
	}

}
