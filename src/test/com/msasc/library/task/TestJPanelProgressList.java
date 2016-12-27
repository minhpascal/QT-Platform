package test.com.msasc.library.task;

import java.util.Locale;

import javax.swing.JScrollPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JPanelProgress;
import com.qtplaf.library.swing.JPanelProgressGroup;
import com.qtplaf.library.task.TaskRunnerList;
import com.qtplaf.library.task.sample.SampleTask;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.TextServer;

import test.com.msasc.library.swing.TestBox;

public class TestJPanelProgressList {

	public static void main(String[] args) {
		TextServer.addBaseResource("SysString.xml");
		Session session = new Session(Locale.UK);
		
		JPanelProgressGroup panel = new JPanelProgressGroup(session);
		panel.setColumns(2);

		JScrollPane scrollPane = new JScrollPane(panel);

		int processes = 10;
		int tasks = 200;
		for (int i = 0; i < processes; i++) {

			JPanelProgress panelProgress = new JPanelProgress(session);
			panel.add(panelProgress);

			TaskRunnerList taskList = new TaskRunnerList(session);
			taskList.setName(StringUtils.leftPad(Integer.toString(i), 2, "0"));
			taskList.setDescription("Test process " + i);
			taskList.setNotifyModulus(1);

			for (int j = 0; j < tasks; j++) {
				SampleTask task = new SampleTask(session);
				// task.setupIndeterminate(true, true);
				// task.setupDeterminateCountStepsNotSupported(true, true);
				task.setupDeterminateCountStepsSupported(true, true);
				task.setName(StringUtils.leftPad(Integer.toString(j), 2, "0"));
				task.setDescription("Test process " + i + " task " + j);
				task.setNotifyModulus(1);
				task.setCountSleep(100);
				task.setStepsBase(100);
				task.setStepSleep(50);
				taskList.addTask(task);
			}

			panelProgress.monitorTask(taskList);
			new Thread(taskList, "Progress process " + i).start();
		}

		TestBox.show(scrollPane);
		System.exit(0);
	}

}
