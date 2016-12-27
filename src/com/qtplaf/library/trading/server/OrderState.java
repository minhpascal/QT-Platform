/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates the possible order states.
 * 
 * @author Miquel Sas
 */
public enum OrderState {
	/**
	 * After order was cancelled.
	 */
	Cancelled,
	/**
	 * Set after the order was closed.
	 */
	Closed,
	/**
	 * Set right after order submission and before order acceptance by the server.
	 */
	Created,
	/**
	 * Set after order was fully or partially filled.
	 */
	Filled,
	/**
	 * Set after order submission for conditional orders.
	 */
	Opened;
}
