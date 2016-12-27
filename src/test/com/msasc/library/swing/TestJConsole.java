package test.com.msasc.library.swing;

import java.awt.Dimension;
import java.io.PrintStream;

import javax.swing.JScrollPane;

import com.qtplaf.library.swing.JConsole;
import com.qtplaf.library.util.TextServer;

public class TestJConsole {
	
	static class Writer implements Runnable {
		private PrintStream ps;
		private String message;
		private long sleep;
		public Writer(PrintStream ps, String message, long sleep) {
			this.ps = ps;
			this.message = message;
			this.sleep = sleep;
		}
		public void run() {
			try {
				Thread.sleep(1000);
				while (true) {
					for (int i = 0; i < 50; i++) {
						ps.println(message);
					}
//					Thread.yield();
					Thread.sleep(sleep);
				}
			} catch (InterruptedException exc) {
				exc.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		TextServer.addBaseResource("SysString.xml");
		
		JConsole console = new JConsole(10000);
		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setPreferredSize(new Dimension(400, 800));
		
		new Thread(new Writer(console.getPrintStream(), "This is a line of text tat should be printed in the console.",500)).start();
		new Thread(new Writer(console.getPrintStream(), "Cacharrufo puturré.",200)).start();
		
		TestBox.show(scrollPane);
		
		System.exit(0);
	}

}
