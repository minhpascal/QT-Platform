/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Enumerates the filters applicable to OHLCV data.
 * 
 * @author Miquel Sas
 */
public enum Filter {
	/**
	 * No filter, all available data is retrieved, including flat days or days without data.
	 */
	NoFilter,
	/**
	 * Weekends are removed from data.
	 */
	Weekends,
	/**
	 * All flats (periods without data) are removed.
	 */
	AllFlats;
}
