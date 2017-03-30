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

package com.qtplaf.library.ai.nnet;

import java.util.List;

/**
 * A list pattern source.
 *
 * @author Miquel Sas
 */
public class ListPatternSource implements PatternSource {

	/** The underlying pattern list. */
	private List<Pattern> patterns;
	/** Scan index. */
	private int index = 0;

	/**
	 * Constructor.
	 * 
	 * @param patterns The list of patterns.
	 */
	public ListPatternSource(List<Pattern> patterns) {
		super();
		this.patterns = patterns;
	}

	/**
	 * Check if the source has more patterns.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean hasNext() {
		return index < patterns.size();
	}

	/**
	 * Returns the next pattern.
	 * 
	 * @return The next pattern.
	 */
	@Override
	public Pattern next() {
		return patterns.get(index++);
	}

	/**
	 * Rewind the source and move to the first pattern.
	 */
	@Override
	public void rewind() {
		index = 0;
	}

	/**
	 * Returns the size or number of patterns in the source.
	 * 
	 * @return The size.
	 */
	@Override
	public int size() {
		return patterns.size();
	}


	/**
	 * Check if the source is empty.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isEmpty() {
		return patterns.isEmpty();
	}
}
