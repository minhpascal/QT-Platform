/**
 * 
 */
package com.qtplaf.library.trading.server.feed;

import java.util.EventObject;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Tick;

/**
 * Tick event.
 * 
 * @author Miquel Sas
 */
public class TickEvent extends EventObject {

	/**
	 * Instrument.
	 */
	private Instrument instrument;
	/**
	 * Tick data.
	 */
	private Tick tick;

	/**
	 * Constructor assigning fields.
	 * 
	 * @param source The source that generated the event.
	 * @param instrument The instruent.
	 * @param tick The tick data.
	 */
	public TickEvent(Object source, Instrument instrument, Tick tick) {
		super(source);
		this.instrument = instrument;
		this.tick = tick;
	}

	/**
	 * Returns the instruent.
	 * 
	 * @return The instruent.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Sets the instruent.
	 * 
	 * @param instrument The instrument.
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	/**
	 * Returns the tick data.
	 * 
	 * @return The tick data.
	 */
	public Tick getTick() {
		return tick;
	}

	/**
	 * Sets the tick data.
	 * 
	 * @param tick The tick data.
	 */
	public void setTick(Tick tick) {
		this.tick = tick;
	}

	/**
	 * Returns a string representation of this event.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getSource().getClass().getSimpleName());
		b.append(", ");
		b.append(getInstrument().getDescription());
		b.append(", ");
		b.append(getTick());
		return b.toString();
	}
}
