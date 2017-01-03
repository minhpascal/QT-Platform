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

package test.com.msasc.library.task;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JScrollPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JPanelProgressGroup;
import com.qtplaf.library.task.TaskRunnerList;
import com.qtplaf.library.task.sample.SampleTask;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.TextServer;

import test.com.msasc.library.swing.TestBox;

/**
 * Test the task monitor.
 * 
 * @author Miquel Sas
 */
public class TestTaskMonitor {
	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);

		JPanelProgressGroup monitor = new JPanelProgressGroup(session);
		monitor.setPreferredSize(new Dimension(800,400));
		JScrollPane scrollPane = new JScrollPane(monitor);

		List<TaskRunnerList> processes = new ArrayList<>();

		int processCount = 1;
		int taskCount = 4;
		int subtaskCount = 4;
		for (int i = 0; i < processCount; i++) {

			TaskRunnerList process = new TaskRunnerList(session);
			process.setName("I-"+StringUtils.leftPad(Integer.toString(i+1), 2, "0"));
			process.setDescription("Test process " + (i+1));
			process.setNotifyModulus(1);
			monitor.add(process);
			process.setMonitor(monitor);
			processes.add(process);

			for (int j = 0; j < taskCount; j++) {
				TaskRunnerList task = new TaskRunnerList(session);
				task.setName("J-"+StringUtils.leftPad(Integer.toString(j+1), 2, "0"));
				task.setDescription("Test process " + (i+1) + " task " + (j+1));
				task.setNotifyModulus(1);

				for (int k = 0; k < subtaskCount; k++) {
					SampleTask stask = new SampleTask(session);
					stask.setupDeterminateCountStepsSupported(true, true);
					stask.setName("K-"+StringUtils.leftPad(Integer.toString(k+1), 2, "0"));
					stask.setDescription("Test process " + (i+1) + " task " + (j+1) + " sub-task " + (k+1));
					stask.setCountSleep(0);
					stask.setExtent(5000);
					task.addTask(stask);
				}

				process.addTask(task);
			}

		}

//		for (int i = 0; i < processes.size(); i++) {
//			TaskRunnerList process = processes.get(i);
//			new Thread(process, process.toString()).start();
//		}
		try {
			Thread.sleep(500);
		} catch (Exception ignored) {}

		TestBox.show(scrollPane);
		System.exit(0);
	}

}
