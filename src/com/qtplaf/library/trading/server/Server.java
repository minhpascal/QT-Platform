/**
 * 
 */
package com.qtplaf.library.trading.server;

import java.util.List;

import com.qtplaf.library.trading.data.Instrument;

/**
 * Interface resposible to provide access to all the server services.
 * 
 * @author Miquel Sas
 */
public interface Server {

	/**
	 * Returns a list with all available instruments.
	 * 
	 * @return A list with all available instruments.
	 */
	List<Instrument> getAvailableInstruments();

	/**
	 * Returns the connection manager associated to this server.
	 * 
	 * @return The connection manager.
	 * @throws ServerException
	 */
	ConnectionManager getConnectionManager() throws ServerException;

	/**
	 * Returns the order manager associated to this server.
	 * 
	 * @return The order manager.
	 * @throws ServerException
	 */
	OrderManager getOrderManager() throws ServerException;

	/**
	 * Returns the history manager associated to this server.
	 * 
	 * @return The history manager.
	 * @throws ServerException
	 */
	HistoryManager getHistoryManager() throws ServerException;

	/**
	 * Returns the feed manager to receive live feed events.
	 * 
	 * @return The feed manager.
	 * @throws ServerException
	 */
	FeedManager getFeedManager() throws ServerException;
}
