/**
 * 
 */
package com.qtplaf.library.trading.data;

/**
 * An arbitrary number of double values with the starting time. It can be an OHLCV (open, high, low, close, volume) pack
 * or the list of values of an indicator.
 * 
 * @author Miquel Sas
 */
public class Data {

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
