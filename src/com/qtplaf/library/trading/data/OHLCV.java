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

import com.qtplaf.library.util.Timestamp;

/**
 * An OHLCV (open, high, low, close, volume) bar or candlestick container.
 * 
 * @author Miquel Sas
 */
public class OHLCV extends Data {

	/**
	 * Enumerates the possible indexes for an OHLCV data item.
	 */
	public enum Index {
		/**
		 * Open value.
		 */
		Open(0),
		/**
		 * Hight value.
		 */
		High(1),
		/**
		 * Low value.
		 */
		Low(2),
		/**
		 * Close value.
		 */
		Close(3),
		/**
		 * Volume.
		 */
		Volume(4),
		/**
		 * Median price: (High + Low) / 2
		 */
		MedianPrice(100),
		/**
		 * Typical price: (High + Low + Close) / 3
		 */
		TypicalPrice(101),
		/**
		 * Weighted close price: (High + Low + 2*Close) / 4
		 */
		WeightedClosePrice(102);

		/**
		 * The data index.
		 */
		private int index = -1;

		/**
		 * Constructor assigning the index.
		 * 
		 * @param index The data index.
		 */
		Index(int index) {
			this.index = index;
		}

		/**
		 * Returns the data index. For <i>MedianPrice</i>, <i>TypicalPrice</i> and <i>WeightedClosePrice</i> the index
		 * is not a valid data index.
		 * 
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
	}

	/**
	 * Returns a data item that is the union or consolidation of a list of data items.
	 * <p>
	 * For OHLCV data items the open is the open of the first, the high is the higher, the low the lower, the close is
	 * the close of the last, and the volume is the sum. The time is the time of the first item.
	 * 
	 * @param ohlcvs The array of OHLCV data items.
	 * @return The union OHLCV data item.
	 */
	public static OHLCV union(OHLCV... ohlcvs) {
		if (ohlcvs.length == 0) {
			throw new IllegalStateException("The list of OHLCV data items can not be empty.");
		}
		double open = 0;
		double high = 0;
		double low = 0;
		double close = 0;
		double volume = 0;
		long time = 0;
		for (int i = 0; i < ohlcvs.length; i++) {
			OHLCV ohlcv = ohlcvs[i];
			if (i == 0) {
				open = ohlcv.getOpen();
				time = ohlcv.getTime();
			}
			high = Math.max(high, ohlcv.getHigh());
			low = Math.min(low, ohlcv.getLow());
			if (i == ohlcvs.length - 1) {
				close = ohlcv.getClose();
			}
			volume += ohlcv.getVolume();
		}
		return new OHLCV(time, open, high, low, close, volume);
	}

	/**
	 * Default constructor.
	 */
	public OHLCV() {
		super();
		setData(new double[] { 0, 0, 0, 0, 0 });
	}

	/**
	 * Constructor assigning a <tt>Data</tt> item that must have five and only five values.
	 * 
	 * @param data The <tt>Data</tt> item.
	 */
	public OHLCV(Data data) {
		super();
		if (data.getData().length != 5) {
			throw new IllegalArgumentException("Data is not OHLCV");
		}
		setTime(data.getTime());
		setData(data.getData());
	}

	/**
	 * Constructor assigning field values.
	 * 
	 * @param time The time in milliseconds.
	 * @param open Open value.
	 * @param high Hiigh value.
	 * @param low Low value.
	 * @param close Close value.
	 * @param volume Volume.
	 */
	public OHLCV(long time, double open, double high, double low, double close, double volume) {
		super();
		setData(new double[] { open, high, low, close, volume });
		setTime(time);
	}

	/**
	 * Returns the value given the enumerated index.
	 * 
	 * @param index The enumerated index.
	 * @return The value.
	 */
	public double getValue(Index index) {
		switch (index) {
		case Open:
		case High:
		case Low:
		case Close:
		case Volume:
			return getValue(index.index);
		case MedianPrice:
			return getMedianPrice();
		case TypicalPrice:
			return getTypicalPrice();
		case WeightedClosePrice:
			return getWeightedClosePrice();
		default:
			return getClose();
		}
	}

	/**
	 * Returns the value at the given index.
	 * 
	 * @param index The index.
	 * @return The value.
	 */
	public double getValue(int index) {
		if (index < size()) {
			return super.getValue(index);
		}
		if (index == Index.MedianPrice.index) {
			return getMedianPrice();
		} else if (index == Index.TypicalPrice.index) {
			return getTypicalPrice();
		} else if (index == Index.WeightedClosePrice.index) {
			return getWeightedClosePrice();
		} else {
			throw new IllegalArgumentException("Invalid index");
		}
	}

	/**
	 * Returns the open value.
	 * 
	 * @return The open value.
	 */
	public double getOpen() {
		return getValue(Index.Open);
	}

	/**
	 * Sets the open value.
	 * 
	 * @param open The open value.
	 */
	public void setOpen(double open) {
		setValue(Index.Open.index, open);
	}

	/**
	 * Returns the high value.
	 * 
	 * @return The high value.
	 */
	public double getHigh() {
		return getValue(Index.High);
	}

	/**
	 * Sets the high value.
	 * 
	 * @param high The high value.
	 */
	public void setHigh(double high) {
		setValue(Index.High.index, high);
	}

	/**
	 * Returns the low value.
	 * 
	 * @return The low value.
	 */
	public double getLow() {
		return getValue(Index.Low);
	}

	/**
	 * Sets the low value.
	 * 
	 * @param low The low value.
	 */
	public void setLow(double low) {
		setValue(Index.Low.index, low);
	}

	/**
	 * Returns the close value.
	 * 
	 * @return The close value.
	 */
	public double getClose() {
		return getValue(Index.Close);
	}

	/**
	 * Sets the close value.
	 * 
	 * @param close The close value.
	 */
	public void setClose(double close) {
		setValue(Index.Close.index, close);
	}

	/**
	 * Returns the volume.
	 * 
	 * @return The volume
	 */
	public double getVolume() {
		return getValue(Index.Volume);
	}

	/**
	 * Sets the volume.
	 * 
	 * @param volume The volume.
	 */
	public void setVolume(double volume) {
		setValue(Index.Volume.index, volume);
	}

	/**
	 * Returns the median price: (H + L) / 2
	 * 
	 * @return The median price.
	 */
	public double getMedianPrice() {
		return (getHigh() + getLow()) / 2;
	}

	/**
	 * Returns the typical price: (H + L + C) / 3
	 * 
	 * @return
	 */
	public double getTypicalPrice() {
		return (getHigh() + getLow() + getClose()) / 3;
	}

	/**
	 * Returns the weighted close price: (H + L + (2*C)) / 4
	 * 
	 * @return The weighted close price.
	 */
	public double getWeightedClosePrice() {
		return (getHigh() + getLow() + (2 * getClose())) / 4;
	}

	/**
	 * Returns the body of this bar (close - open).
	 * 
	 * @return The body of the bar.
	 */
	public double getBody() {
		return getClose() - getOpen();
	}

	/**
	 * Returns the range (high - low) of this bar.
	 * 
	 * @return The range.
	 */
	public double getRange() {
		return getHigh() - getLow();
	}

	/**
	 * Returns a boolean indicating if this bar is bullish.
	 * 
	 * @return A boolean indicating if this bar is bullish.
	 */
	public boolean isBullish() {
		return getClose() >= getOpen();
	}

	/**
	 * Returns a boolean indicating if this bar is bearish.
	 * 
	 * @return A boolean indicating if this bar is bearish.
	 */
	public boolean isBearish() {
		return !isBullish();
	}

	/**
	 * Returns a boolean indicating whether this bar (OHLC) is flat, e.g. open == close == high == low.
	 * 
	 * @return A boolean.
	 */
	public boolean isFlat() {
		double ohlc = getOpen();
		return getOpen() == ohlc && getClose() == ohlc && getHigh() == ohlc && getLow() == ohlc;
	}

	/**
	 * Returns a string representation of this OHLCV data item.
	 * 
	 * @return A string representation of this OHLCV data item.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(new Timestamp(getTime()));
		b.append(", ");
		b.append("O: ");
		b.append(getOpen());
		b.append(", ");
		b.append("H: ");
		b.append(getHigh());
		b.append(", ");
		b.append("L: ");
		b.append(getLow());
		b.append(", ");
		b.append("C: ");
		b.append(getClose());
		b.append(", ");
		b.append("V: ");
		b.append(getVolume());
		return b.toString();
	}
}
