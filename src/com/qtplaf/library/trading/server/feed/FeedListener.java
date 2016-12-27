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
package com.qtplaf.library.trading.server.feed;

import java.util.List;

/**
 * Listener of feed data subscribed.
 * 
 * @author Miquel Sas
 */
public interface FeedListener {

	/**
	 * Returns the list of current instrument OHLCV data this listener is subscribed on.
	 * 
	 * @return The list of current OHLCV this listener is subscribed on.
	 */
	List<OHLCVSubscription> getCurrentOHLCVSubscriptions();

	/**
	 * Returns the list of instrument OHLCV data this listener is subscribed on.
	 * 
	 * @return The list of OHLCV this listener is subscribed on.
	 */
	List<OHLCVSubscription> getOHLCVSubscriptions();

	/**
	 * Returns the list instrument ticks this listener is subscribed on.
	 * 
	 * @return The list instrument ticks this listener is subscribed on.
	 */
	List<TickSubscription> getTickSubscriptions();

	/**
	 * Notifies the current forming OHLCV data for the subscribed instruments.
	 * 
	 * @param ohlcvEvent The OHLCV data event.
	 */
	void onCurrentOHLCV(OHLCVEvent ohlcvEvent);

	/**
	 * Notifies the completed OHLCV data for the subscribed instruments.
	 * 
	 * @param ohlcvEvent The OHLCV data event.
	 */
	void onOHLCV(OHLCVEvent ohlcvEvent);

	/**
	 * Notifies every tick data event for the subscribed instruments.
	 * 
	 * @param tickEvent The tick event.
	 */
	void onTick(TickEvent tickEvent);
}
