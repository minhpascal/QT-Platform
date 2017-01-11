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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JScrollPane;

import com.qtplaf.library.app.Argument;
import com.qtplaf.library.app.ArgumentManager;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.JOptionDialog;
import com.qtplaf.library.swing.JPanelProgressGroup;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.action.DefaultActionClose;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.util.TextServer;
import com.qtplaf.library.util.file.FileCopy;
import com.qtplaf.library.util.list.ListUtils;

/**
 * This is an utility class to update a project (CMA), quality and production, that is external from QT-Platform.
 * 
 * @author Miquel Sas
 */
public class CMAUpdate {

	/**
	 * Action close.
	 */
	static class ActionClose extends DefaultActionClose {
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
			Session session = ActionUtils.getSession(this);
			String message = "Close and stop any executing task?";
			if (MessageBox.question(session, message, MessageBox.yesNo, MessageBox.no) == MessageBox.yes) {
				JOptionDialog dialog = (JOptionDialog) ActionUtils.getUserObject(this);
				dialog.setVisible(false);
				dialog.dispose();
				System.exit(0);
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// System text resources and session.
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);

		// Command line argument: environment
		// - quality
		// - production
		Argument argEnvironment =
			new Argument("environment", "Environment: quality/production", true, false, "quality", "production");

		// Command line argument: target
		// - local (local image)
		// - remote (remote Citrix image)
		Argument argTarget =
			new Argument("target", "Target: local/remote", true, false, "local", "remote");

		// Command line argument: modules (multiple values)
		// - central
		// - dictionary
		// - local
		Argument argModules =
			new Argument("modules", "Modules: central/dictionary/local", true, true, "central", "dictionary", "local");

		// Command line argument: purge (flag)
		Argument argPurge =
			new Argument("purge", "Purge destination directories", false, false, false);

		// Argument manager.
		ArgumentManager argMngr = new ArgumentManager(argEnvironment, argTarget, argModules, argPurge);
		if (!argMngr.parse(args)) {
			for (String error : argMngr.getErrors()) {
				System.out.println(error);
			}
			System.exit(1);
		}

		// The list of tasks.
		List<Task> tasks = getTaskList(session, argMngr);

		// Progress group.
		JPanelProgressGroup panelGroup = new JPanelProgressGroup(session);
		panelGroup.setPanelProgressWidth(1000);
		for (Task task : tasks) {
			panelGroup.add(task);
		}

		// Option dialog.
		JOptionDialog dialog = new JOptionDialog(session);
		dialog.setTitle("CMA Update utility");
		dialog.setComponent(new JScrollPane(panelGroup));
		dialog.addOption(new ActionClose(session));
		dialog.setModal(false);
		dialog.showDialog(true);
	}

	/**
	 * Returns the list of tasks to update.
	 * 
	 * @param session The working session.
	 * @param argMngr The argument manager.
	 * @return The list of tasks.
	 */
	private static List<Task> getTaskList(Session session, ArgumentManager argMngr) {
		// A task per central/dictionary/local.
		List<Task> tasks = new ArrayList<>();

		// Environment and target.
		boolean purge = argMngr.isPassed("purge");
		boolean production = argMngr.getValue("environment").equals("production");
		boolean local = argMngr.getValue("target").equals("local");
		boolean remote = !local;

		// Local: copy from workspace to exec image.
		if (local) {

			// Central.
			if (argMngr.getValues("modules").contains("central")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(production, local, "C"));
				fc.setDescription(getDescription(production, local, "central"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, production, "CMA_Central");
				addLocalModuleBudgetDictionary(fc, production, "CMA_Central");
				addLocalModuleBudgetLocal(fc, production, "CMA_Central", false);
				addLocalModuleMarginsCentral(fc, production, "CMA_Central", true);
				addLocalModuleMarginsDictionary(fc, production, "CMA_Central", false);
				addLocalModuleMarginsLibrary(fc, production, "CMA_Central");
				addLocalModuleMarginsLocal(fc, production, "CMA_Central");
				addLocalModuleStrategicPlanCentral(fc, production, "CMA_Central");
				addLocalModuleStrategicPlanLocal(fc, production, "CMA_Central");
				addLocalModuleWorkingCapitalCentral(fc, production, "CMA_Central");
				addLocalModuleWorkingCapitalLocal(fc, production, "CMA_Central");
				addLocalModuleSecurity(fc, production, "CMA_Central");
				tasks.add(fc);
			}

			// Dictionary
			if (argMngr.getValues("modules").contains("dictionary")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(production, local, "D"));
				fc.setDescription(getDescription(production, local, "dictionary"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, production, "CMA_Dictionary");
				addLocalModuleBudgetDictionary(fc, production, "CMA_Dictionary");
				addLocalModuleMarginsCentral(fc, production, "CMA_Dictionary", false);
				addLocalModuleMarginsDictionary(fc, production, "CMA_Dictionary", true);
				addLocalModuleMarginsLibrary(fc, production, "CMA_Dictionary");
				addLocalModuleMarginsLocal(fc, production, "CMA_Dictionary");
				addLocalModuleSecurity(fc, production, "CMA_Dictionary");
				tasks.add(fc);
			}

			// Local.
			if (argMngr.getValues("modules").contains("local")) {
				FileCopy fc = new FileCopy(session);
				fc.setName(getName(production, local, "L"));
				fc.setDescription(getDescription(production, local, "local"));
				fc.setPurgeDestination(purge);
				addLocalLibrary(fc, production, "CMA_Local");
				addLocalModuleBudgetDictionary(fc, production, "CMA_Local");
				addLocalModuleBudgetLocal(fc, production, "CMA_Local", true);
				addLocalModuleMarginsCentral(fc, production, "CMA_Local", false);
				addLocalModuleMarginsDictionary(fc, production, "CMA_Local", false);
				addLocalModuleMarginsLibrary(fc, production, "CMA_Local");
				addLocalModuleMarginsLocal(fc, production, "CMA_Local");
				addLocalModuleStrategicPlanLocal(fc, production, "CMA_Local");
				addLocalModuleWorkingCapitalCentral(fc, production, "CMA_Local");
				addLocalModuleWorkingCapitalLocal(fc, production, "CMA_Local");
				addLocalModuleSecurity(fc, production, "CMA_Local");
				tasks.add(fc);
			}
		}

		// Remote: copy from exec image to destination drives.
		if (remote) {

			// The list of destination drives.
			List<String> drives = new ArrayList<>();
			if (production) {
				drives.addAll(ListUtils.asList("U", "V", "W", "X", "Y", "Z"));
			} else {
				drives.addAll(ListUtils.asList("T"));
			}

			// Central.
			if (argMngr.getValues("modules").contains("central")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(production, local, "C"));
					fc.setDescription(getDescription(production, local, "central") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "library", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_budget_local", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_security", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_stplan_central", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_stplan_local", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_wcapital_central", drive);
					addRemoteDirs(fc, production, "CMA_Central\\mads", "module_wcapital_local", drive);
					addRemoteFiles(fc, production, "CMA_Central", "CMA_Central.cmd", drive);
					addRemoteFiles(fc, production, "CMA_Central", "JLoad.cmd", drive);
					tasks.add(fc);
				}
			}

			// Dictionary.
			if (argMngr.getValues("modules").contains("dictionary")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(production, local, "D"));
					fc.setDescription(getDescription(production, local, "dictionary") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "library", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, production, "CMA_Dictionary\\mads", "module_security", drive);
					addRemoteFiles(fc, production, "CMA_Dictionary", "CMA_Dictionary.cmd", drive);
					addRemoteFiles(fc, production, "CMA_Dictionary", "JLoad.cmd", drive);
					tasks.add(fc);
				}
			}

			// Local.
			if (argMngr.getValues("modules").contains("local")) {
				for (String drive : drives) {
					FileCopy fc = new FileCopy(session);
					fc.setName(getName(production, local, "L"));
					fc.setDescription(getDescription(production, local, "local") + " (" + drive + ")");
					fc.setPurgeDestination(purge);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "library", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_budget_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_budget_local", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_margins_central", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_margins_dictionary", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_margins_library", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_margins_local", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_security", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_stplan_local", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_wcapital_central", drive);
					addRemoteDirs(fc, production, "CMA_Local\\mads", "module_wcapital_local", drive);
					addRemoteFiles(fc, production, "CMA_Local", "CMA_Local.cmd", drive);
					addRemoteFiles(fc, production, "CMA_Local", "JLoad.cmd", drive);
					tasks.add(fc);
				}
			}
		}

		return tasks;
	}

	/**
	 * Add the local library copy task
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalLibrary(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Lib";
		String dstParent = module + "\\mads\\library";
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		addLocalDirs(fc, production, srcParent, dstParent, "res");
		addLocalDirs(fc, production, srcParent, dstParent, "xsd");
		addLocalDirs(fc, production, srcParent, dstParent, "xml");
	}

	/**
	 * Add the local margins central module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indocate if the menu file should be copied.
	 */
	private static void addLocalModuleMarginsCentral(FileCopy fc, boolean production, String module, boolean menu) {
		String srcParent = "XVR COM Module Margins Central";
		String dstParent = module + "\\mads\\module_margins_central";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Central_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Central_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Central_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Central_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Central_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Central_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Central_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Central_Strings.xml");
		if (menu) {
			addLocalFiles(fc, production, srcParent, dstParent, "xml\\CMA_Central_Menu.xml");
		}
	}

	/**
	 * Add the local budget local module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indicate if the menu file should be copied.
	 */
	private static void addLocalModuleBudgetLocal(FileCopy fc, boolean production, String module, boolean menu) {
		String srcParent = "XVR COM Module Budget Local";
		String dstParent = module + "\\mads\\module_budget_local";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Local_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Local_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Local_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Local_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Local_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Local_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Local_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Local_Strings.xml");
		if (menu) {
			addLocalFiles(fc, production, srcParent, dstParent, "xml\\CMA_Local_Menu.xml");
		}
	}

	/**
	 * Add the local margins dictionary module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 * @param menu A boolean to indicate if the menu file should be copied.
	 */
	private static void addLocalModuleMarginsDictionary(FileCopy fc, boolean production, String module, boolean menu) {
		String srcParent = "XVR COM Module Margins Dictionary";
		String dstParent = module + "\\mads\\module_margins_dictionary";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Dictionary_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Dictionary_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Dictionary_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Dictionary_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Dictionary_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Dictionary_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Dictionary_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Dictionary_Strings.xml");
		if (menu) {
			addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Dictionary_Menu.xml");
		}
	}

	/**
	 * Add the local margins library module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleMarginsLibrary(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module Margins Library";
		String dstParent = module + "\\mads\\module_margins_library";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Library_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Library_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Library_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Library_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Library_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Library_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Library_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Library_Strings.xml");
	}

	/**
	 * Add the local margins local module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleMarginsLocal(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module Margins Local";
		String dstParent = module + "\\mads\\module_margins_local";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Local_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Local_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Local_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Margins_Local_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Local_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Local_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Local_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Margins_Local_Strings.xml");
	}

	/**
	 * Add the local working capital central module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleWorkingCapitalCentral(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module WorkingCapital Central";
		String dstParent = module + "\\mads\\module_wcapital_central";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Central_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Central_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Central_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Central_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Central_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Central_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Central_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Central_Strings.xml");
	}

	/**
	 * Add the local working capital local module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleWorkingCapitalLocal(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module WorkingCapital Local";
		String dstParent = module + "\\mads\\module_wcapital_local";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Local_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Local_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Local_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\WorkingCapital_Local_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Local_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Local_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Local_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\WorkingCapital_Local_Strings.xml");
	}

	/**
	 * Add the local strategic plan central module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleStrategicPlanCentral(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module StrategicPlan Central";
		String dstParent = module + "\\mads\\module_stplan_central";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Central_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Central_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Central_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Central_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Central_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Central_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Central_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Central_Strings.xml");
	}

	/**
	 * Add the local strategic plan local module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleStrategicPlanLocal(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module StrategicPlan Local";
		String dstParent = module + "\\mads\\module_stplan_local";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Local_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Local_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Local_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\StrategicPlan_Local_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Local_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Local_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Local_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\StrategicPlan_Local_Strings.xml");
	}

	/**
	 * Add the local budget dictionary module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleBudgetDictionary(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module Budget Dictionary";
		String dstParent = "CMA_Central\\mads\\module_budget_dictionary";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Dictionary_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Dictionary_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Dictionary_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Budget_Dictionary_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Dictionary_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Dictionary_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Dictionary_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Budget_Dictionary_Strings.xml");
	}

	/**
	 * Add the local security module copy task.
	 * 
	 * @param fc File copy.
	 * @param production Production/Development.
	 * @param module Module (CMA_Central/CMA_Dictionary/CMA_Local)
	 */
	private static void addLocalModuleSecurity(FileCopy fc, boolean production, String module) {
		String srcParent = "XVR COM Module Seguridad";
		String dstParent = module + "\\mads\\module_security";
		// bin
		addLocalDirs(fc, production, srcParent, dstParent, "bin");
		// res
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_DBSchema_en.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_Descriptor_en.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_Strings.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Module_Seguridad_Strings_en.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_DBSchema.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_DBSchema_en.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_DBSchema_es.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_Descriptor.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_Descriptor_en.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_Descriptor_es.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_Domains.txt");
		addLocalFiles(fc, production, srcParent, dstParent, "res\\Seguridad_Strings.txt");
		// xml
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Module_Seguridad_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Seguridad_DBSchema.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Seguridad_Descriptor.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Seguridad_Domains.xml");
		addLocalFiles(fc, production, srcParent, dstParent, "xml\\Seguridad_Strings.xml");
	}

	/**
	 * Returns a suitable name.
	 * 
	 * @param production Production/Quality
	 * @param local Local/Remote
	 * @param module Module (C/D/L)
	 * @return The name.
	 */
	private static String getName(boolean production, boolean local, String module) {
		StringBuilder b = new StringBuilder();
		b.append("CP");
		if (production) {
			b.append("EP");
		} else {
			b.append("ED");
		}
		if (local) {
			b.append("TL");
		} else {
			b.append("TR");
		}
		b.append("M" + module);
		return b.toString();
	}

	/**
	 * Returns a suitable description.
	 * 
	 * @param production Production/Quality
	 * @param local Local/Remote
	 * @param module Module (central/dictionary/local)
	 * @return The name.
	 */
	private static String getDescription(boolean production, boolean local, String module) {
		StringBuilder b = new StringBuilder();
		b.append("Copy task");
		if (production) {
			b.append(" environment:production");
		} else {
			b.append(" environment:quality");
		}
		if (local) {
			b.append(" target:local");
		} else {
			b.append(" target:remote");
		}
		b.append(" module:" + module);
		return b.toString();
	}

	/**
	 * Add directories for a remote copy task.
	 * 
	 * @param fc The file copy.
	 * @param prod Production/Development.
	 * @param parent Source (for instance CMA_Central\\mads)
	 * @param name Last directory name.
	 * @param drive Destination drive.
	 */
	private static void addRemoteDirs(FileCopy fc, boolean prod, String parent, String name, String drive) {
		File fileSrcRoot = new File(getSrcRootRemote(prod));
		File fileSrcParent = new File(fileSrcRoot, parent);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstParent = new File(drive + ":" + (prod ? "\\CMA\\" : "\\") + parent);
		File fileDst = new File(fileDstParent, name);
		fc.addDirectories(fileSrc, fileDst);
	}

	/**
	 * Add files for a remote copy task.
	 * 
	 * @param fc The file copy.
	 * @param prod Production/Development.
	 * @param parent Source (for instance CMA_Central\\mads)
	 * @param name Last directory name.
	 * @param drive Destination drive.
	 */
	private static void addRemoteFiles(FileCopy fc, boolean prod, String parent, String name, String drive) {
		File fileSrcRoot = new File(getSrcRootRemote(prod));
		File fileSrcParent = new File(fileSrcRoot, parent);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstParent = new File(drive + ":" + (prod ? "\\CMA\\" : "\\") + parent);
		File fileDst = new File(fileDstParent, name);
		fc.addFiles(fileSrc, fileDst);
	}

	/**
	 * Add local directories to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param prod Production/Development.
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocalDirs(FileCopy fc, boolean prod, String src, String dst, String name) {
		addLocal(fc, prod, true, src, dst, name);
	}

	/**
	 * Add local files to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param prod Production/Development.
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocalFiles(FileCopy fc, boolean prod, String src, String dst, String name) {
		addLocal(fc, prod, false, src, dst, name);
	}

	/**
	 * Add local directories/files to the copy task.
	 * 
	 * @param fc The file copy.
	 * @param prod Production/Development.
	 * @param dirs Directories/Files.
	 * @param src Base source.
	 * @param dst Base destination.
	 * @param name Name.
	 */
	private static void addLocal(FileCopy fc, boolean prod, boolean dirs, String src, String dst, String name) {
		File fileSrcRoot = new File(getSrcRootLocal(prod));
		File fileSrcParent = new File(fileSrcRoot, src);
		File fileSrc = new File(fileSrcParent, name);
		File fileDstRoot = new File(getDstRootLocal(prod));
		File fileDstParent = new File(fileDstRoot, dst);
		File fileDst = new File(fileDstParent, name);
		if (dirs) {
			fc.addDirectories(fileSrc, fileDst);
		} else {
			fc.addFiles(fileSrc, fileDst);
		}
	}

	/**
	 * Returns the source root for a local target task.
	 * 
	 * @param prod Production/Development
	 * @return The source root.
	 */
	private static String getSrcRootLocal(boolean prod) {
		String srcRoot;
		if (prod) {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-head";
		} else {
			srcRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\workspace-development";
		}
		return srcRoot;
	}

	/**
	 * Returns the destination root for a local target task.
	 * 
	 * @param prod Production/Development
	 * @return The source root.
	 */
	private static String getDstRootLocal(boolean prod) {
		String dstRoot;
		if (prod) {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-head";
		} else {
			dstRoot = "c:\\Development\\Eclipse-Workspaces\\Roca\\cma-development";
		}
		return dstRoot;
	}

	/**
	 * Returns the source root for a remote target task.
	 * 
	 * @param prod Production/Development
	 * @return The source root.
	 */
	private static String getSrcRootRemote(boolean prod) {
		return getDstRootLocal(prod);
	}
}
