/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates the three possible types of connections.
 * 
 * @author Miquel Sas
 */
public enum ConnectionType {
	/**
	 * Normal live connection, supported by all servers (brokers).
	 */
	Live,
	/**
	 * Normal demo connection, also supported by the majority of servers.
	 */
	Demo,
	/**
	 * Test connection. This kind of connection is implemented by this system to provide a way to reproduce the history
	 * in order to test strategies and studies.
	 */
	Test;
}
