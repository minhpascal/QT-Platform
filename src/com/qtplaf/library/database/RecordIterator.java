/**
 * 
 */
package com.qtplaf.library.database;

import java.util.NoSuchElementException;

/**
 * A simple record iterator used to iterate forward only cursors.
 * 
 * @author Miquel Sas
 */
public interface RecordIterator {
	/**
	 * Returns {@code true} if the iteration has more records. (In other words, returns {@code true} if {@link #next}
	 * would return a record rather than throwing an exception.)
	 *
	 * @return {@code true} if the iteration has more elements
	 */
	boolean hasNext();

	/**
	 * Returns the next record in the iteration.
	 *
	 * @return the next record in the iteration
	 * @throws NoSuchElementException if the iteration has no more records
	 */
	Record next();

	/**
	 * Close underlying resources.
	 * 
	 * @throws PersistorException if any errors occur closing the underlying resources
	 */
	void close() throws PersistorException;
}
