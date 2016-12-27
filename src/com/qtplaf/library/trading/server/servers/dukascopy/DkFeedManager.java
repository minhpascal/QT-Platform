/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.dukascopy.api.IContext;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IBarFeedListener;
import com.qtplaf.library.trading.server.FeedManager;
import com.qtplaf.library.trading.server.feed.FeedListener;
import com.qtplaf.library.trading.server.feed.OHLCVSubscription;
import com.qtplaf.library.trading.server.feed.TickSubscription;
import com.qtplaf.library.trading.server.servers.dukascopy.listeners.DkBarFeedListener;

/**
 * Dukascopy feed manager implementation.
 * 
 * @author Miquel Sas
 */
public class DkFeedManager implements FeedManager {

	/**
	 * Server reference.
	 */
	private DkServer server;
	/**
	 * The feed dispatcher reference.
	 */
	private DkFeedDispatcher dispatcher;

	/**
	 * Constructor assigning the reference server.
	 * 
	 * @param server The Dukascopy server.
	 */
	public DkFeedManager(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Add a feed listener to receive notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to add.
	 */
	public void addFeedListener(FeedListener listener) {

		// If the dispatcher is not started, start it.
		if (dispatcher == null) {
			dispatcher = new DkFeedDispatcher();
			// Install the dispatcher in the strategy listener.
			server.getStrategyListener().setDispatcher(dispatcher);
			new Thread(dispatcher, "Dukascopy feed dispatcher").start();
		}

		// Subscribe required instruments.
		IContext context = server.getStrategyListener().getContext();
		context.setSubscribedInstruments(getDkInstruments(listener));

		// Add the listener to strategy listener and the dispatcher, and subscribe to completed bars.
		server.getStrategyListener().addListener(listener);
		dispatcher.addFeedListener(listener);
		subscribeToCompletedBars(listener);
	}

	/**
	 * Removes a feed listener from receiving notificatios for its subscriptions.
	 * 
	 * @param listener The feed listener to remove.
	 */
	public void removeFeedListener(FeedListener listener) {
		// Unsubscribe.
		unsubscribeToCompletedBars(listener);
		// Remove it from the strategy listener.
		server.getStrategyListener().removeListener(listener);
	}

	/**
	 * Subscribes the listener to completed bars using <i>IContext.subscribeToBarsFeed</i>.
	 * 
	 * @param listener The system listener.
	 */
	private void subscribeToCompletedBars(FeedListener listener) {
		IContext context = server.getStrategyListener().getContext();
		List<OHLCVSubscription> ohlcvSubscriptions = listener.getOHLCVSubscriptions();
		for (OHLCVSubscription subscription : ohlcvSubscriptions) {
			Instrument dkInstrument = DkUtilities.toDkInstrument(subscription.getInstrument());
			Period dkPeriod = DkUtilities.toDkPeriod(subscription.getPeriod());
			OfferSide dkOfferSide = DkUtilities.toDkOfferSide(subscription.getOfferSide());
			IBarFeedListener dkListener = new DkBarFeedListener(dispatcher);
			// Set the user object to the subscription in order to be able to unsubscribe when the listener is removed.
			subscription.setObject(dkListener);
			context.subscribeToBarsFeed(dkInstrument, dkPeriod, dkOfferSide, dkListener);
		}
	}

	/**
	 * Unsubscribes the listener from completed bars.
	 * 
	 * @param listener The listener to unsubscribe.
	 */
	private void unsubscribeToCompletedBars(FeedListener listener) {
		IContext context = server.getStrategyListener().getContext();
		List<OHLCVSubscription> ohlcvSubscriptions = listener.getOHLCVSubscriptions();
		for (OHLCVSubscription subscription : ohlcvSubscriptions) {
			DkBarFeedListener dkListener = (DkBarFeedListener)subscription.getObject();
			if (dkListener != null) {
				context.unsubscribeFromBarsFeed(dkListener);
			}
		}
	}

	/**
	 * Returns the Dukascopy instruments that the listener requires.
	 * 
	 * @param listener This system listener.
	 * @return The Dukascopy instruments that the listener requires.
	 */
	private Set<Instrument> getDkInstruments(FeedListener listener) {
		ArrayList<com.qtplaf.library.trading.data.Instrument> instruments = new ArrayList<>();
		List<TickSubscription> tickSubscriptions = listener.getTickSubscriptions();
		for (TickSubscription subscription : tickSubscriptions) {
			if (!instruments.contains(subscription.getInstrument())) {
				instruments.add(subscription.getInstrument());
			}
		}
		List<OHLCVSubscription> currentSubscriptions = listener.getCurrentOHLCVSubscriptions();
		for (OHLCVSubscription subscription : currentSubscriptions) {
			if (!instruments.contains(subscription.getInstrument())) {
				instruments.add(subscription.getInstrument());
			}
		}
		List<OHLCVSubscription> ohlcvSubscriptions = listener.getOHLCVSubscriptions();
		for (OHLCVSubscription subscription : ohlcvSubscriptions) {
			if (!instruments.contains(subscription.getInstrument())) {
				instruments.add(subscription.getInstrument());
			}
		}
		Set<Instrument> dkInstruments = new LinkedHashSet<>();
		for (com.qtplaf.library.trading.data.Instrument instrument : instruments) {
			Instrument dkInstrument = DkUtilities.toDkInstrument(instrument);
			dkInstruments.add(dkInstrument);
		}
		return dkInstruments;
	}
}
