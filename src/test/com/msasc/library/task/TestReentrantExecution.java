package test.com.msasc.library.task;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.sample.SampleTask;
import com.qtplaf.library.util.TextServer;

public class TestReentrantExecution {

	public static void main(String[] args) {
		TextServer.addBaseResource("SysString.xml");
		Session session = new Session(Locale.UK);
		try {
			SampleTask task = new SampleTask(session);
			task.setupDeterminateCountStepsSupported(true, true);
			task.setName("01");
			task.setDescription("Test reeantrant ");
			task.setNotifyModulus(1);
			task.setCountSleep(10);
			task.setStepsBase(100);
			task.setStepSleep(50);
			
			new Thread(task, "Thread 01").start();
			Thread.sleep(100);
			new Thread(task, "Thread 02").start();
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
