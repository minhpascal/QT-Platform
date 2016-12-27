package test.other;

import java.text.ParseException;

public class TestBooleanLock {

	public static class Switch extends Thread {
		@Override
		public void run() {
			while(System.currentTimeMillis() - startTime < maxTime) {
				try {
					sleep(100);
				} catch (InterruptedException ignored) {}
				synchronized (b) {
					b = !b;
					System.out.println(b);
				}
			}
		}
	}
	
	private static Boolean b = null;
	private static long startTime = System.currentTimeMillis();
	private static long maxTime = 1000 * 60;
	
	public static void main(String[] args) throws Exception {
		new Switch().start();
		new Switch().start();
		new Switch().start();
		new Switch().start();
	}
	
	
}
