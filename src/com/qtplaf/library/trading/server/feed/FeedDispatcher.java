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

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.server.OfferSide;
import com.qtplaf.library.trading.server.ServerException;

/**
 * A feed event dispatcher that runs in a separated thread and is aimed to dispatch feed events, like OHLCV or tick
 * data, without blocking the input from the server, because listener implementation may delay significantly when
 * processing event.
 * 
 * @author Miquel Sas
 */
public class FeedDispatcher implements Runnable {

	/**
	 * Input buffer for current OHLCV data events.
	 */
	private List<OHLCVEvent> inputCurrentOHLCVEvents = new ArrayList<>();
	/**
	 * Input lock for current OHLCV data events.
	 */
	private Object inputCurrentOHLCVLock = new Object();
	/**
	 * Input buffer for completed OHLCV data events.
	 */
	private List<OHLCVEvent> inputOHLCVEvents = new ArrayList<>();
	/**
	 * Input lock for completed OHLCV data events.
	 */
	private Object inputOHLCVLock = new Object();
	/**
	 * Input buffer for tick data events.
	 */
	private List<TickEvent> inputTickEvents = new ArrayList<>();
	/**
	 * Input lock for tick data events.
	 */
	private Object inputTickLock = new Object();
	/**
	 * Output buffer for current OHLCV data events.
	 */
	private List<OHLCVEvent> outputCurrentOHLCVEvents = new ArrayList<>();
	/**
	 * Output buffer for completed OHLCV data events.
	 */
	private List<OHLCVEvent> outputOHLCVEvents = new ArrayList<>();
	/**
	 * Output buffer for tick data events.
	 */
	private List<TickEvent> outputTickEvents = new ArrayList<>();
	/**
	 * The list of listeners.
	 */
	private List<FeedListener> listeners = new ArrayList<>();
	/**
	 * A boolean that indicates that this running dispatcher has terminated.
	 */
	private boolean terminated = false;
	/**
	 * A boolean that indicates that this running dispatcher has been explicitly stopped.
	 */
	private boolean stop = false;
	/**
	 * The exception when the dispatcher terminated due to an error.
	 */
	private ServerException exception;

	/**
	 * Constructor.
	 */
	public FeedDispatcher() {
		super();
	}

	/**
	 * Adds a feed listener to the list of feed listeners. Before adding the listener to the dispatcher, the
	 * <i>FeeddManager</i> would normally have to initialize the listener subscriptions in the backend server. If the
	 * listerner has o subscriptions, an <i>IllegalArgumentException</i> is thrown.
	 * 
	 * @param listener
	 */
	public void addFeedListener(FeedListener listener) {
		boolean subscriptions = false;
		if (!listener.getCurrentOHLCVSubscriptions().isEmpty()) {
			subscriptions = true;
		}
		if (!listener.getOHLCVSubscriptions().isEmpty()) {
			subscriptions = true;
		}
		if (!listener.getTickSubscriptions().isEmpty()) {
			subscriptions = true;
		}
		if (!subscriptions) {
			throw new IllegalArgumentException("A feed listener must have feed subscriptions.");
		}
		listeners.add(listener);
	}

	/**
	 * Removes the feed listener from the list of listeners. Previously to remove the listener, the feed manager
	 * normally would unsubscribe from the underlying server.
	 * 
	 * @param listener
	 */
	public void removeFeedListener(FeedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Adds a current OHLCV data event to the input queue.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide Offer side.
	 * @param ohlcv The OHLCV data.
	 */
	public void addCurrentOHLCV(Instrument instrument, Period period, OfferSide offerSide, OHLCV ohlcv) {
		synchronized (inputCurrentOHLCVLock) {
			inputCurrentOHLCVEvents.add(new OHLCVEvent(this, instrument, period, offerSide, ohlcv));
		}
	}

	/**
	 * Adds a completed OHLCV data event to the input queue.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide Offer side.
	 * @param ohlcv The OHLCV data.
	 */
	public void addOHLCV(Instrument instrument, Period period, OfferSide offerSide, OHLCV ohlcv) {
		synchronized (inputOHLCVLock) {
			inputOHLCVEvents.add(new OHLCVEvent(this, instrument, period, offerSide, ohlcv));
		}
	}

	/**
	 * Add a tick data event to the input queue.
	 * 
	 * @param instrument The instrument.
	 * @param tick The tick data.
	 */
	public void addTick(Instrument instrument, Tick tick) {
		synchronized (inputTickLock) {
			inputTickEvents.add(new TickEvent(this, instrument, tick));
		}
	}

	/**
	 * Move events from input to output and dispatch them.
	 */
	private void dispatchEvents() {

		// Move tick data from input to output.
		synchronized (inputTickLock) {
			while (!inputTickEvents.isEmpty()) {
				outputTickEvents.add(inputTickEvents.remove(0));
			}
		}

		// Move current OHLCV data from input to output.
		synchronized (inputCurrentOHLCVLock) {
			while (!inputCurrentOHLCVEvents.isEmpty()) {
				outputCurrentOHLCVEvents.add(inputCurrentOHLCVEvents.remove(0));
			}
		}

		// Move completed OHLCV data from input to output.
		synchronized (inputOHLCVLock) {
			while (!inputOHLCVEvents.isEmpty()) {
				outputOHLCVEvents.add(inputOHLCVEvents.remove(0));
			}
		}

		// Notify tick data and clear the buffer.
		while (!outputTickEvents.isEmpty()) {
			TickEvent event = outputTickEvents.remove(0);
			Instrument instrument = event.getInstrument();
			for (FeedListener listener : listeners) {
				List<TickSubscription> subscriptions = listener.getTickSubscriptions();
				for (TickSubscription subscription : subscriptions) {
					if (subscription.acceptsTick(instrument)) {
						listener.onTick(event);
					}
				}
			}
		}

		// Notify current OHLCV data and clear the buffer.
		while (!outputCurrentOHLCVEvents.isEmpty()) {
			OHLCVEvent event = outputCurrentOHLCVEvents.remove(0);
			for (FeedListener listener : listeners) {
				List<OHLCVSubscription> subscriptions = listener.getCurrentOHLCVSubscriptions();
				for (OHLCVSubscription subscription : subscriptions) {
					Instrument instrument = event.getInstrument();
					Period period = event.getPeriod();
					OfferSide offerSide = event.getOfferSide();
					if (subscription.acceptsOHLCV(instrument, period, offerSide)) {
						listener.onCurrentOHLCV(event);
					}
				}
			}
		}

		// Notify completed OHLCV data and clear the buffer.
		while (!outputOHLCVEvents.isEmpty()) {
			OHLCVEvent event = outputOHLCVEvents.remove(0);
			for (FeedListener listener : listeners) {
				List<OHLCVSubscription> subscriptions = listener.getOHLCVSubscriptions();
				for (OHLCVSubscription subscription : subscriptions) {
					Instrument instrument = event.getInstrument();
					Period period = event.getPeriod();
					OfferSide offerSide = event.getOfferSide();
					if (subscription.acceptsOHLCV(instrument, period, offerSide)) {
						listener.onOHLCV(event);
					}
				}
			}
		}
	}

	/**
	 * Run the dispacher.
	 */
	public void run() {
		// Loop.
		try {
			while (!terminated && !stop) {
				dispatchEvents();
				Thread.yield();
			}
		} catch (Exception cause) {
			exception = new ServerException(cause);
		} finally {
			terminated = true;
		}
	}

	/**
	 * Explicitly stop this dispatcher.
	 */
	synchronized public void stop() {
		stop = true;
	}

	/**
	 * Returns the exception.
	 * 
	 * @return The server exception if any.
	 */
	synchronized public ServerException getException() {
		return exception;
	}

	/**
	 * Check if the dispacher has terminated.
	 * 
	 * @return A boolean that indicates if the dispacher has terminated.
	 */
	synchronized public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Check if the dispatcher has been explicitly terminated, not by an exception.
	 * 
	 * @return A boolean that indicates if the dispatcher has been explicitly terminated, not by an exception.
	 */
	synchronized public boolean hasBeenStopped() {
		return stop;
	}
}
