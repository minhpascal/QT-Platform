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

package incubator.nnet.graph.learning;

/**
 * A source of patterns for learning or performance check.
 *
 * @author Miquel Sas
 */
public interface PatternSource {

	/**
	 * Check if the source has more patterns.
	 * 
	 * @return A boolean.
	 */
	boolean hasNext();

	/**
	 * Returns the next pattern.
	 * 
	 * @return The next pattern.
	 */
	Pattern next();

	/**
	 * Rewind the source and move to the first pattern.
	 */
	void rewind();

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	int size();

	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	boolean isEmpty();
}
