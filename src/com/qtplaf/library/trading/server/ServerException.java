/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Exceptions thrown by the server.
 * 
 * @author Miquel Sas
 */
public class ServerException extends Exception {

	/**
	 * Default constructor.
	 */
	public ServerException() {
		super();
	}

	/**
	 * Constructor assigning the message.
	 * 
	 * @param message The message.
	 */
	public ServerException(String message) {
		super(message);
	}

	/**
	 * Constructor assigning the cause.
	 * 
	 * @param cause The cause.
	 */
	public ServerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor assigning the message and the cause..
	 * 
	 * @param message The message.
	 * @param cause The cause.
	 */
	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
