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

package com.qtplaf.library.ai.fuzzy;

import java.util.ArrayList;
import java.util.List;

/**
 * A fuzzy control.
 *
 * @author Miquel Sas
 */
public class Control {

	/**
	 * The list of control segments.
	 */
	private List<Segment> segments = new ArrayList<>();

	/**
	 * Constructor.
	 * 
	 * @param segments The list of segments.
	 */
	public Control(List<Segment> segments) {
		super();
		validateSegments(segments);
		this.segments.addAll(segments);
	}

	/**
	 * Check if the value is in the range of the label.
	 * 
	 * @param value The value to check.
	 * @param label The segment label.
	 * @return A boolean.
	 */
	public boolean isLabel(double value, String label) {
		return getSegment(label).inRange(value);
	}
	
	/**
	 * Returns the segment of the label.
	 * @param label The label.
	 * @return The segment of the label (or throws an IllegalArgumentException)
	 */
	public Segment getSegment(String label) {
		for (Segment segment : segments) {
			if (segment.getLabel().equals(label)) {
				return segment;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Validate the list of segments.
	 * 
	 * @param segments The list of segments.
	 */
	private void validateSegments(List<Segment> segments) {
		for (int i = 0; i < segments.size(); i++) {
			if (i > 0) {
				Segment prev = segments.get(i - 1);
				Segment curr = segments.get(i);
				if (curr.getMinimum() <= prev.getMinimum()) {
					throw new IllegalArgumentException();
				}
				if (prev.getMaximum() >= curr.getMaximum()) {
					throw new IllegalArgumentException();
				}
				double prevMax = prev.getMaximum();
				double prevNext = Math.nextUp(prevMax);
				if (curr.getMinimum() > prevNext) {
					throw new IllegalArgumentException();
				}
			}
		}

		// Sign 0 count, only one admitted.
		int countZeroSign = countZeroSign(segments);
		if (countZeroSign > 1) {
			throw new IllegalArgumentException();
		}
		if (countZeroSign == 1) {
			int index = getZeroSignIndex(segments);
			for (int i = 0; i < segments.size(); i++) {
				if (i < index && segments.get(i).getSign() != -1) {
					throw new IllegalArgumentException();
				}
				if (i > index && segments.get(i).getSign() != 1) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * Count the number of zero sign segments.
	 * 
	 * @param segments The list of segments.
	 * @return The number of segments with zero sign (must be 0 or 1)
	 */
	private int countZeroSign(List<Segment> segments) {
		int count = 0;
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSign() == 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the index of the zero sign.
	 * 
	 * @param segments The list of segments.
	 * @return The index of the zero sign.
	 */
	private int getZeroSignIndex(List<Segment> segments) {
		for (int i = 0; i < segments.size(); i++) {
			if (segments.get(i).getSign() == 0) {
				return i;
			}
		}
		return -1;
	}
}
