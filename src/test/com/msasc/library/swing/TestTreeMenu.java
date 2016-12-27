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

package test.com.msasc.library.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.JFormRecord;
import com.qtplaf.library.swing.JPanelTreeMenu;
import com.qtplaf.library.swing.TreeMenuItem;
import com.qtplaf.library.util.TextServer;

/**
 * Deeply test the tree menu.
 * 
 * @author Miquel Sas
 */
public class TestTreeMenu {
	
	private static
		TreeMenuItem
		getItem(Session session, Icon open, Icon close, Icon leaf, boolean level, KeyStroke accKey, String... labels) {
		TreeMenuItem item = new TreeMenuItem(session);
		item.setOpenIcon(open);
		item.setClosedIcon(close);
		item.setLeafIcon(UIManager.getIcon("WARNING_MESSAGE"));
		item.setDisplayLevel(level);
		item.setAcceleratorKey(accKey);
		item.addLabels(labels);
		return item;
	}

	private static TreeMenuItem getItem(Session session, boolean level, String... labels) {
		return getItem(session, null, null, null, level, null, labels);
	}
	private static TreeMenuItem getItem(Session session, boolean level, KeyStroke accKey, String... labels) {
		return getItem(session, null, null, null, level, accKey, labels);
	}
	private static TreeMenuItem getItem(Session session, Class<? extends Action> actionClass, String... labels) {
		TreeMenuItem item = new TreeMenuItem(session);
		item.setActionClass(actionClass);
		item.addLabels(labels);
		item.setLeafIcon(UIManager.getIcon("WARNING_MESSAGE"));
		return item;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TextServer.addBaseResource("SysString.xml");
		Session session = new Session(Locale.UK);
		
		KeyStroke[] keyStrokes = new KeyStroke[]{
			KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK),
			KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK)
		};

		JPanelTreeMenu panelMenu = new JPanelTreeMenu(session);
		panelMenu.setPreferredSize(new Dimension(600, 300));
		
		for (int i = 0; i < 10; i++) {
			panelMenu.addMenuItem(getItem(session, true, "Option " + (i + 1), "This is the option tacatá"));
			for (int j = 0; j < 10; j++) {
				String level = Integer.toString(i + 1);
				String suffix = Integer.toString(i + 1) + "." + Integer.toString(j + 1);
				KeyStroke keyStroke = null;
				if (i == 0) {
					keyStroke = keyStrokes[j];
				}
				if (i % 2 == 0) {
					panelMenu.addMenuItem(
						level,
						getItem(session, true, keyStroke, "Option " + suffix, "This is the option tacatá ", "tucutu"));
				} else {
					panelMenu.addMenuItem(
						level,
						getItem(session, ActionForm.class, "Option " + suffix, "This is the option tacatá ", suffix));
				}
			}
		}
		panelMenu.refreshTree();

		TestBox.show(panelMenu);
		
		System.exit(0);
	}

	
	public static class ActionForm extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			FieldList fieldList = Util.getFieldList();
			Record record = new Record(fieldList);
			for (int i = 0; i < record.getFieldCount(); i++) {
				record.getField(i).setFieldGroup(null);
			}

			record.setValue("CARTICLE", "A525345000");

			JFormRecord form = new JFormRecord(new Session(Locale.UK));
			form.setTitle("Testing form record");
			form.setRecord(record);
			// form.setScrollGroupPanels(true);
			// form.setEditMode(EditMode.ReadOnly);

			form.addField("CARTICLE", 0, 0);
			// form.addField("DARTICLE", 0, 0);
			form.addField("CBUSINESS", 0, 0);
			form.addField("IREQUIRED", 0, 0);
			form.addField("ISTATUS", 0, 1);
			form.addField("ICHECKED", 0, 1);
			form.addField("TCREATED", 0, 1);
			form.addField("TCREATED", 0, 1);
			form.addField("TCREATED", 0, 1);

			form.addField("CARTICLE", 1, 0);
			// form.addField("DARTICLE", 1, 0);
			form.addField("CBUSINESS", 1, 0);
			form.addField("IREQUIRED", 1, 0);
			form.addField("IREQUIRED", 1, 0);
			form.addField("IREQUIRED", 1, 0);
			// form.addField("ISTATUS", 1, 1);
			form.addField("ICHECKED", 1, 1);
			form.addField("ICHECKED", 1, 1);
			form.addField("ICHECKED", 1, 1);
			form.addField("ICHECKED", 1, 1);
			form.addField("TCREATED", 1, 1);

			form.addField("CARTICLE", 2, 0);
			// form.addField("DARTICLE", 2, 0);
			form.addField("CBUSINESS", 2, 0);
			form.addField("IREQUIRED", 2, 0);
			form.addField("ISTATUS", 2, 1);
			form.addField("ICHECKED", 2, 1);
			form.addField("TCREATED", 2, 1);

			form.edit();
		}
		
	}
}
