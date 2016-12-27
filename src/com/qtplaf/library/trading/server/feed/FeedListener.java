/**
 * 
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
