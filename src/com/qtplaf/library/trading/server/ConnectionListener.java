/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Interface for listeners interested in receiving connection events.
 * 
 * @author Miquel Sas
 */
public interface ConnectionListener {
	/**
	 * Notification received when the status of the connection changes.
	 * 
	 * @param e The connection event.
	 */
	void status(ConnectionEvent e);
}
