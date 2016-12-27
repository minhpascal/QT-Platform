package test.com.msasc.library.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLog4J {

	public static void main(String[] args) throws InterruptedException {
		Logger logger = LogManager.getLogger();
		long time = System.currentTimeMillis();
		while (true) {
			logger.trace("TRACE");
			logger.debug("DEBUG");
			logger.info("INFO");
			logger.warn("WARNING");
			logger.error("ERROR");
			logger.fatal("FATAL");
			Thread.sleep(100);
			if (System.currentTimeMillis() - time > 1000 * 60) {
				break;
			}
		}
		

		System.exit(0);
	}

}
