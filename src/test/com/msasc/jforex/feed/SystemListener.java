/**
 * 
 */
package test.com.msasc.jforex.feed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.system.ISystemListener;

/**
 * System listener.
 * 
 * @author Miquel Sas
 */
public class SystemListener implements ISystemListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemListener.class);
	public SystemListener() {
	}
	public void onStart(long processId) {
		LOGGER.info("Strategy started: " + processId);
	}
	public void onStop(long processId) {
		LOGGER.info("Strategy stopped: " + processId);
	}
	public void onConnect() {
		LOGGER.info("Connected");
	}
	public void onDisconnect() {
		LOGGER.warn("Disconnected");
	}
}
