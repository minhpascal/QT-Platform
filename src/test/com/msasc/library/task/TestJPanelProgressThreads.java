package test.com.msasc.library.task;

import java.util.Locale;

import javax.swing.JScrollPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.JPanelProgress;
import com.qtplaf.library.swing.core.JPanelProgressGroup;
import com.qtplaf.library.task.TaskRunnerThreads;
import com.qtplaf.library.task.sample.SampleTask;
import com.qtplaf.library.util.StringUtils;
import com.qtplaf.library.util.TextServer;

import test.com.msasc.library.swing.TestBox;

public class TestJPanelProgressThreads {

	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		
		JPanelProgressGroup panel = new JPanelProgressGroup(session);
		panel.setColumns(2);

		JScrollPane scrollPane = new JScrollPane(panel);

		int processes = 10;
		int tasks = 200;
		for (int i = 0; i < processes; i++) {

			TaskRunnerThreads taskThreads = new TaskRunnerThreads(session);
			taskThreads.setName(StringUtils.leftPad(Integer.toString(i), 2, "0"));
			taskThreads.setDescription("Test process " + i);
			taskThreads.setNotifyModulus(10);
			taskThreads.setMaximumConcurrentTasks(5);
//			taskThreads.setMaximumConcurrentTasks(tasks);
			for (int j = 0; j < tasks; j++) {
				SampleTask task = new SampleTask(session);
				// task.setupIndeterminate(true, true);
				// task.setupDeterminateCountStepsNotSupported(true, true);
				task.setupDeterminateCountStepsSupported(true, true);
				task.setName(StringUtils.leftPad(Integer.toString(j), 2, "0"));
				task.setDescription("Test process " + i + " task " + j);
				task.setNotifyModulus(1);
				task.setCountSleep(50);
				task.setStepsBase(100);
				task.setStepSleep(20);
				taskThreads.addTask(task);
			}

			panel.add(taskThreads);
		}

		TestBox.show(scrollPane);
		System.exit(0);
	}

}
