/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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
	 * Returns the name of the server.
	 * 
	 * @return The name of the server.
	 */
	String getName();

	/**
	 * Returns an unique and short id.
	 * 
	 * @return The server id.
	 */
	String getId();

	/**
	 * Returns the server title or long name.
	 * 
	 * @return The server title or long name.
	 */
	String getTitle();

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
