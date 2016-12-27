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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miquel Sas
 *
 */
public class FeedListenerAdapter implements FeedListener {

	/**
	 * List of current OHLCV subscriptions.
	 */
	List<OHLCVSubscription> currentOHLCVSubscriptions = new ArrayList<>();
	/**
	 * List of completed OHLCV subscriptions.
	 */
	List<OHLCVSubscription> ohlcvSubscriptions = new ArrayList<>();
	/**
	 * List of tick subscriptions.
	 */
	List<TickSubscription> tickSubscriptions = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public FeedListenerAdapter() {
		super();
	}

	/**
	 * Adds a subscription to current OHLCV data.
	 * 
	 * @param subscription The subscription.
	 */
	public void addCurrentOHLCVSubscription(OHLCVSubscription subscription) {
		currentOHLCVSubscriptions.add(subscription);
	}

	/**
	 * Adds a subscription to current OHLCV data.
	 * 
	 * @param subscription The subscription.
	 */
	public void addOHLCVSubscription(OHLCVSubscription subscription) {
		ohlcvSubscriptions.add(subscription);
	}

	/**
	 * Add a tick subscription.
	 * 
	 * @param subscription The subscription.
	 */
	public void addTickSubscription(TickSubscription subscription) {
		tickSubscriptions.add(subscription);
	}

	/**
	 * Returns the list of current instrument OHLCV data this listener is subscribed on.
	 * 
	 * @return The list of current OHLCV this listener is subscribed on.
	 */
	public List<OHLCVSubscription> getCurrentOHLCVSubscriptions() {
		return currentOHLCVSubscriptions;
	}

	/**
	 * Returns the list of instrument OHLCV data this listener is subscribed on.
	 * 
	 * @return The list of OHLCV this listener is subscribed on.
	 */
	public List<OHLCVSubscription> getOHLCVSubscriptions() {
		return ohlcvSubscriptions;
	}

	/**
	 * Returns the list instrument ticks this listener is subscribed on.
	 * 
	 * @return The list instrument ticks this listener is subscribed on.
	 */
	public List<TickSubscription> getTickSubscriptions() {
		return tickSubscriptions;
	}

	/**
	 * Notifies the current forming OHLCV data for the subscribed instruments.
	 * 
	 * @param ohlcvEvent The OHLCV data event.
	 */
	public void onCurrentOHLCV(OHLCVEvent ohlcvEvent) {
	}

	/**
	 * Notifies the completed OHLCV data for the subscribed instruments.
	 * 
	 * @param ohlcvEvent The OHLCV data event.
	 */
	public void onOHLCV(OHLCVEvent ohlcvEvent) {
	}

	/**
	 * Notifies every tick data event for the subscribed instruments.
	 * 
	 * @param tickEvent The tick event.
	 */
	public void onTick(TickEvent tickEvent) {
	}

}
