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
package com.qtplaf.library.trading.data;

import java.util.List;

/**
 * An arbitrary number of double values with the starting time. It can be an OHLCV (open, high, low, close, volume) pack
 * or the list of values of an indicator.
 * 
 * @author Miquel Sas
 */
public class Data {

	/**
	 * Returns a data item that is the union or consolidation of a list of data items.
	 * <p>
	 * For OHLCV data items the open is the open of the first, the high is the higher, the low the lower, the close is
	 * the close of the last, and the volume is the sum.The time is the time of the first item.
	 * <p>
	 * For generic data items the last item is returned.
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 * @return The union data item.
	 */
	public static Data union(List<Data> list) {
		if (list.isEmpty()) {
			throw new IllegalStateException("The sub list can not be empty.");
		}
		Data data = list.get(0);
		if (!OHLCV.class.isInstance(data)) {
			return list.get(list.size() - 1);
		}
		double open = 0;
		double high = 0;
		double low = 0;
		double close = 0;
		double volume = 0;
		long time = 0;
		for (int i = 0; i < list.size(); i++) {
			OHLCV ohlcv = new OHLCV(list.get(i));
			if (i == 0) {
				open = ohlcv.getOpen();
				time = ohlcv.getTime();
			}
			high = Math.max(high, ohlcv.getHigh());
			low = Math.min(low, ohlcv.getLow());
			if (i == list.size() - 1) {
				close = ohlcv.getClose();
			}
			volume += ohlcv.getVolume();
		}
		return new OHLCV(time, open, high, low, close, volume);
	}


	/**
	 * The data.
	 */
	private double[] data;
	/**
	 * The start time in millis.
	 */
	private long time;
	/**
	 * A boolean that indicates if the data is valid and, for instance, should be plotted. Recall that some indicators
	 * can need a look backward or necessary number of bars to be calculated, and sometimes also a look forward, while
	 * the list of data has the same number of elements that the origin instrument list.
	 */
	private boolean valid = true;

	/**
	 * Default constructor.
	 */
	public Data() {
		super();
	}

	/**
	 * Returns the start time in millis.
	 * 
	 * @return The start time in millis.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the start time in millis.
	 * 
	 * @param time The start time in millis.
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Returns the data structure.
	 * 
	 * @return The data structure.
	 */
	public double[] getData() {
		return data;
	}

	/**
	 * Returns the value at the given index.
	 * 
	 * @param index The index.
	 * @return The value.
	 */
	public double getValue(int index) {
		return data[index];
	}

	/**
	 * Sets the value at the given index.
	 * 
	 * @param index The index.
	 * @param value The value.
	 */
	public void setValue(int index, double value) {
		data[index] = value;
	}

	/**
	 * Returns the internal data length.
	 * 
	 * @return The internal data length.
	 */
	public int size() {
		return data.length;
	}

	/**
	 * Sets the data structure.
	 * 
	 * @param data The data structure.
	 */
	public void setData(double[] data) {
		this.data = data;
	}

	/**
	 * Sets the data structure.
	 * 
	 * @param values The data structure.
	 */
	public void setValues(double[] values) {
		this.data = values;
	}

	/**
	 * Check if this data is valid.
	 * 
	 * @return A boolean that indicates if the data is valid and should be plotted or considered.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Set if this data is valid and should be plotted or considered.
	 * 
	 * @param valid A boolean that indicates if the data is valid and should be plotted or considered.
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * Returns a string representation of this data object.
	 * 
	 * @return A string representation.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (data != null) {
			boolean comma = false;
			for (double d : data) {
				if (comma) {
					b.append(", ");
				}
				comma = true;
				b.append(d);
			}
		}
		return b.toString();
	}
}
