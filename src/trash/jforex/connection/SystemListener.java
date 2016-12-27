/**
 * 
 */
package trash.jforex.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.system.ISystemListener;

/**
 * System listener to be added when connected.
 * 
 * @author Miquel Sas
 */
public class SystemListener implements ISystemListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemListener.class);

	/**
	 * Flags that indicates if this listener is installed in the IClient or the ITesterClient interface.
	 */
	private boolean tester = false;

	/**
	 * Default constructor.
	 */
	public SystemListener() {
		super();
		this.tester = false;
	}

	/**
	 * Constructor assigning the tester flag.
	 * 
	 * @param tester A boolean indicating if this listener is installed in the IClient or ITesterClient.
	 */
	public SystemListener(boolean tester) {
		super();
		this.tester = tester;
	}

	/**
	 * Check if this listener is installed in the IClient or ITesterClient.
	 * 
	 * @return A boolean
	 */
	public boolean isTester() {
		return tester;
	}

	/**
	 * Returns a string indicating the environment.
	 * 
	 * @return The environment
	 */
	public String getEnvironment() {
		return (isTester() ? "tester" : "client") + " environment";
	}

	/**
	 * Called on start of a strategy, receives the strategy id.
	 */
	public void onStart(long processId) {
		LOGGER.info("Strategy started: " + processId + " from " + getEnvironment());
	}

	/**
	 * Called on stop of a strategy, receives the strategy id.
	 */
	public void onStop(long processId) {
		LOGGER.info("Strategy stopped: " + processId + " from " + getEnvironment());
	}

	/**
	 * Called on successful connect
	 */
	public void onConnect() {
		LOGGER.info("Connected to " + getEnvironment());
	}

	/**
	 * Called on disconnect
	 */
	public void onDisconnect() {
		LOGGER.info("Disconnected from " + getEnvironment());
	}

}
