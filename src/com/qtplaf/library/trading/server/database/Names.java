/**
 * 
 */
package com.qtplaf.library.trading.server.database;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * An utility to generate names based on instruments, periods, filters and offer sides.
 * 
 * @author Miquel Sas
 */
public class Names {
	/**
	 * Returns a standard name for the arguments.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @return The standard file name.
	 */
	public static String getOHLCVName(Instrument instrument, Period period) {
		return getOHLCVName(instrument, period, null);
	}

	/**
	 * Returns a standard name for the arguments.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param offerSide The offer side.
	 * @return The standard file name.
	 */
	public static String getOHLCVName(Instrument instrument, Period period, OfferSide offerSide) {
		return getOHLCVName(instrument, period, null, offerSide);
	}

	/**
	 * Returns a standard name for the arguments.
	 * 
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The filter.
	 * @param offerSide The offer side.
	 * @return The standard file name.
	 */
	public static String getOHLCVName(Instrument instrument, Period period, Filter filter, OfferSide offerSide) {
		return getName("OHLCV", instrument, period, filter, offerSide);
	}

	/**
	 * Returns a standard name for the tick persistor.
	 * 
	 * @param instrument The instrument.
	 * @return The standard file name.
	 */
	public static String getTickName(Instrument instrument) {
		return getName("TICK", instrument, null, null, null);
	}

	/**
	 * Returns a standard name for the arguments.
	 * 
	 * @param prefix The prefix.
	 * @param instrument The instrument.
	 * @param period The period.
	 * @param filter The filter.
	 * @param offerSide The offer side.
	 * @return The standard file name.
	 */
	public static String getName(String prefix, Instrument instrument, Period period, Filter filter, OfferSide offerSide) {
		StringBuilder b = new StringBuilder();
		if (prefix != null) {
			b.append(prefix);
			b.append("_");
		}
		if (instrument != null) {
			b.append(instrument.getId());
		}
		if (period != null) {
			b.append(period.getSize());
			b.append(period.getUnit().getId());
		}
		if (filter != null) {
			b.append(filter.name().substring(0, 1));
		}
		if (offerSide != null) {
			b.append(offerSide.name().substring(0, 1));
		}
		return b.toString();
	}
}
