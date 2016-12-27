/**
 * 
 */
package com.qtplaf.library.trading.server;

import com.qtplaf.library.trading.data.OHLCV;

/**
 * Iterator interface aimed to download huge amounts of OHLCV data. If the underlying server does not support this
 * operation, the server interface must implement it using the normal list retrievement procedures.
 * 
 * @author Miquel Sas
 */
public interface OHLCVIterator {

	/**
	 * Closes the iterator and any related resources.
	 * 
	 * @throws ServerException
	 */
	void close() throws ServerException;

	/**
	 * Returns a boolean indicating if there are remaining elements to retrieve.
	 * 
	 * @return A boolean indicating if there are remaining elements to retrieve.
	 * @throws ServerException
	 */
	boolean hasNext() throws ServerException;

	/**
	 * Returns the next element or throws an exception if there are no more elements.
	 * 
	 * @return The next element or throws an exception if there are no more elements.
	 * @throws ServerException
	 */
	OHLCV next() throws ServerException;
}
