/**
 * 
 */
package com.qtplaf.library.trading.server;

import java.util.EventObject;

/**
 * Events generated by the connection manager.
 * 
 * @author Miquel Sas
 */
public class ConnectionEvent extends EventObject {

	/**
	 * The event message, that will depend on the back server.
	 */
	private String message;

	/**
	 * Constructor assigning the source.
	 * 
	 * @param source The source of this event.
	 */
	public ConnectionEvent(Object source) {
		super(source);
	}

	/**
	 * Constructor assigning source and message.
	 * 
	 * @param source The source of this event.
	 * @param message The event message.
	 */
	public ConnectionEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

	/**
	 * Returns the event message.
	 * 
	 * @return The event message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the event message.
	 * 
	 * @param message The event message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
