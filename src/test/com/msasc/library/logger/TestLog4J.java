package test.com.msasc.library.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;

public class TestLog4J {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("log4j.configurationFile", "Logger.xml");
		
		
		Logger logger = LogManager.getLogger();
		long time = System.currentTimeMillis();
		while (true) {
			if (true) break;
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
		
		System.out.println(ConsoleAppender.PLUGIN_NAME);
		System.out.println(RollingFileAppender.PLUGIN_NAME);
		

		System.exit(0);
	}

}
