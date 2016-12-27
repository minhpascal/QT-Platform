/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Contains the partial order close/fill event when an order has been partially closed/filled.
 * 
 * @author Miquel Sas
 */
public interface OrderHistory {
	/**
	 * Returns the amount of the closed part.
	 * 
	 * @return The amount of the closed part.
	 */
	double getAmount();

	/**
	 * Returns the close price.
	 * 
	 * @return The close price.
	 */
	double getPrice();

	/**
	 * Returns the time when the order was closed.
	 * 
	 * @return The time when the order was closed.
	 */
	long getTime();
}
