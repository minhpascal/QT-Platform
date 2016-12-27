/**
 * 
 */
package com.qtplaf.library.trading.server;

import com.qtplaf.library.trading.server.feed.FeedListener;

/**
 * Interface responsible to manage live feeds.
 * 
 * @author Miquel Sas
 */
public interface FeedManager {

	/**
	 * Add a feed listener to receive notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to add.
	 */
	void addFeedListener(FeedListener listener);

	/**
	 * Removes a feed listener from receiving notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to remove.
	 */
	void removeFeedListener(FeedListener listener);
}
