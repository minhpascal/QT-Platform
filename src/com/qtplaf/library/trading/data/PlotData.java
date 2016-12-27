/**
 * 
 */
package com.qtplaf.library.trading.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qtplaf.library.util.Calendar;

/**
 * A container for the data to plot in a <i>JChartContainer</i>.
 * 
 * @author Miquel Sas
 */
public class PlotData implements Iterable<DataList>, DataListListener {

	/**
	 * The number of bars to show at start when start and end indexes are not defined.
	 */
	private static final int startNumberOfBars = 2000;

	/**
	 * A list of data lists.
	 */
	private List<DataList> dataLists = new ArrayList<>();
	/**
	 * The start index to plot.
	 */
	private int startIndex = Integer.MIN_VALUE;
	/**
	 * The end index to plot, can greater than the dataBag size.
	 */
	private int endIndex = Integer.MIN_VALUE;
	/**
	 * The maximum value to plot (retrieving dataBag from start index to end index).
	 */
	private double maximumValue = Double.MIN_VALUE;
	/**
	 * The minimum value to plot (retrieving dataBag from start index to end index).
	 */
	private double minimumValue = Double.MAX_VALUE;
	/**
	 * The scale to plot the data.
	 */
	private PlotScale plotScale = PlotScale.Linear;
	/**
	 * A list of a boolean per data list element that indicates if the period is odd or even. The first period, of index
	 * 0, is odd.
	 */
	private List<Boolean> odds = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public PlotData() {
		super();
	}

	/**
	 * Returns the size of this plot data, the number of data lists.
	 * 
	 * @return The number of data lists.
	 */
	public int size() {
		return dataLists.size();
	}

	/**
	 * Returns a boolean indicating if this plot data is empty.
	 * 
	 * @return Aboolean indicating if this plot data is empty.
	 */
	public boolean isEmpty() {
		return dataLists.isEmpty();
	}

	/**
	 * Adds a data list to this plot data. Data lists must be added in the correct order.
	 * <ul>
	 * <li>If the container is the main price container, the the price data list must be the first added, and no volume
	 * data list can be added. A subsequent indicator data list is considered an on chart indicator.</li>
	 * <li>If the container is the volume container, the volume data list must be the first, no price data list can be
	 * added, and subsequent indocator lists are considered on chart indicators.</li>
	 * <li>If the container is an indicator container, the indicator must be first added and only indicators can be
	 * added.</li>
	 * </ul>
	 * 
	 * @param dataList
	 * @return A boolean indicating if it was added.
	 */
	public boolean add(DataList dataList) {
		// Validate subsequent lists.
		if (!isEmpty()) {
			// If it's price, volume or indicator only, data list can only be indicator.
			if (isPrice() || isVolume() || isIndicatorOnly()) {
				if (!dataList.getDataInfo().getDataType().equals(DataType.Indicator)) {
					throw new IllegalArgumentException("Data list must be of type indicator.");
				}
			}
			// The period must be the same.
			DataList firstList = get(0);
			if (!firstList.getDataInfo().getPeriod().equals(dataList.getDataInfo().getPeriod())) {
				throw new IllegalArgumentException("Data lists in the same plot data must have the same period.");
			}
			// Merge data lists so all have the same size.
			mergeDataLists();
		}
		boolean added = dataLists.add(dataList);
		dataList.addListener(this);
		setStartAndEndIndexes();
		return added;
	}

	/**
	 * Merge data lists so all have the same size.
	 */
	private void mergeDataLists() {
		int maxSize = 0;
		for (DataList dataList : dataLists) {
			maxSize = Math.max(maxSize, dataList.size());
		}
		for (DataList dataList : dataLists) {
			int length = dataList.get(0).size();
			for (int i = dataList.size(); i < maxSize; i++) {
				Data data = new Data();
				data.setData(new double[length]);
				data.setValid(false);
				dataList.add(data);
			}
		}
	}

	/**
	 * Set the start and end indexes if they are not set.
	 */
	private void setStartAndEndIndexes() {
		if (dataLists.isEmpty()) {
			startIndex = endIndex = Integer.MIN_VALUE;
			return;
		}
		if (startIndex != Integer.MIN_VALUE && endIndex != Integer.MIN_VALUE) {
			return;
		}
		int size = dataLists.get(0).size();
		if (size > startNumberOfBars) {
			endIndex = size - 1;
			startIndex = endIndex - startNumberOfBars + 1;
		} else {
			endIndex = size - 1;
			startIndex = 0;
		}
	}

	/**
	 * Clear this plot data.
	 */
	public void clear() {
		dataLists.clear();
		setStartAndEndIndexes();
	}

	/**
	 * Returns the data list inthe argument index.
	 * 
	 * @param index The index.
	 * @return The data list.
	 */
	public DataList get(int index) {
		return dataLists.get(index);
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<DataList> iterator() {
		return dataLists.iterator();
	}

	/**
	 * Removes the data list at the given index.
	 * 
	 * @param index The idex.
	 * @return The removed data list.
	 */
	public DataList remove(int index) {
		setStartAndEndIndexes();
		return dataLists.remove(index);
	}

	/**
	 * Returns the data period.
	 * 
	 * @return The data period.
	 */
	public Period getPeriod() {
		if (isEmpty()) {
			return null;
		}
		return get(0).getDataInfo().getPeriod();
	}

	/**
	 * Returns the scale to plot this data.
	 * 
	 * @return The scale.
	 */
	public PlotScale getPlotScale() {
		return plotScale;
	}

	/**
	 * Sets the scale to plot this data.
	 * 
	 * @param plotScale The scale.
	 */
	public void setPlotScale(PlotScale plotScale) {
		this.plotScale = plotScale;
	}

	/**
	 * Returns the start index to plot.
	 * 
	 * @return The start index.
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * Sets the start index to plot.
	 * 
	 * @param startIndex The start index t plot.
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * Returns the end index to plot.
	 * 
	 * @return The end index.
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * Sets the end index to plot.
	 * 
	 * @param endIndex The end index.
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * Returns the maximum value to plot.
	 * 
	 * @return The maximum value.
	 */
	public double getMaximumValue() {
		return maximumValue;
	}

	/**
	 * Sets the maximum value to plot.
	 * 
	 * @param maximumValue The maximum value.
	 */
	public void setMaximumValue(double maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * Returns the minimum value to plot.
	 * 
	 * @return The minimum value.
	 */
	public double getMinimumValue() {
		return minimumValue;
	}

	/**
	 * Sets the minimum value to plot.
	 * 
	 * @param minimumValue The minimum value.
	 */
	public void setMinimumValue(double minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * Returns the pip scale to use.
	 * 
	 * @return The pip scale.
	 * @throws UnsupportedOperationException If the pip scale can not be resolved.
	 */
	public int getPipScale() {
		if (!isEmpty()) {
			int pipScale = -1;
			for (int i = 0; i < size(); i++) {
				pipScale = Math.max(pipScale, get(i).getDataInfo().getPipScale());
			}
			return pipScale;
		}
		throw new UnsupportedOperationException("Pip scale can not be resolved");
	}

	/**
	 * Returns the tick scale to use.
	 * 
	 * @return The tick scale.
	 * @throws UnsupportedOperationException If the tick scale can not be resolved.
	 */
	public int getTickScale() {
		if (!isEmpty()) {
			int tickScale = -1;
			for (int i = 0; i < size(); i++) {
				tickScale = Math.max(tickScale, get(i).getDataInfo().getTickScale());
			}
			return tickScale;
		}
		throw new UnsupportedOperationException("Tick scale can not be resolved");
	}

	/**
	 * Returns the volume scale.
	 * 
	 * @return The volume scale.
	 * @throws UnsupportedOperationException If the tick scale can not be resolved.
	 */
	public int getVolumeScale() {
		if (!isEmpty()) {
			int volumeScale = -1;
			for (int i = 0; i < size(); i++) {
				volumeScale = Math.max(volumeScale, get(i).getDataInfo().getVolumeScale());
			}
			return volumeScale;
		}
		throw new UnsupportedOperationException("Volume scale can not be resolved");
	}

	/**
	 * Check if the maximum and minimum values have been calculated.
	 * 
	 * @return A boolean that indicates if the maximum and minimum values have been calculated.
	 */
	public boolean areMaximumAndMinimumValuesCalculated() {
		return maximumValue != Double.MIN_VALUE && minimumValue != Double.MAX_VALUE;
	}

	/**
	 * Calculates plot frame based on start and end index: minimum and maximum values, start end end time.
	 */
	public void calculateFrame() {

		// Check that there is data to calculate the frame.
		if (isEmpty()) {
			throw new IllegalStateException();
		}

		int dataSize = get(0).size();
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		for (int i = startIndex; i < endIndex; i++) {
			if (i < 0 || i >= dataSize) {
				continue;
			}
			for (DataList dataList : dataLists) {
				DataType dataType = dataList.getDataInfo().getDataType();
				Data data = dataList.get(i);
				if (data == null || !data.isValid()) {
					continue;
				}
				double[] values = data.getData();
				if (dataType.equals(DataType.Price)) {
					// Price: check high and low.
					double high = values[OHLCV.Index.High.getIndex()];
					if (high > maxValue) {
						maxValue = high;
					}
					double low = values[OHLCV.Index.Low.getIndex()];
					if (low < minValue) {
						minValue = low;
					}
				} else if (dataType.equals(DataType.Volume)) {
					// Volume: min == 0, max = volume.
					double volume = values[OHLCV.Index.Volume.getIndex()];
					if (volume > maxValue) {
						maxValue = volume;
					}
					minValue = 0;
				} else {
					// Check max and min of the list of values.
					for (double value : values) {
						if (value > maxValue) {
							maxValue = value;
						}
						if (value < minValue) {
							minValue = value;
						}
					}
				}
			}
		}

		// Assign calculated min and max values.
		minimumValue = minValue;
		maximumValue = maxValue;
	}

	/**
	 * Scroll a certain number of periods or bars.
	 * <ul>
	 * <li>If the number of periods to scroll is negative, the <i>startIndex</i> and <i>endIndex</i> decrease, moving
	 * the plot to the right.</li>
	 * <li>If the number is positive, reverse the policy.</li>
	 * <li>The limit to scroll is to leave at least one visible bar. If <i>periods</i> is negative, <i>endIndex</i> has
	 * to be greater or equal to zero.</li>
	 * <li>If <i>periods</i> is positive, <i>startIndex</i> has to less than the data size.</li>
	 * </ul>
	 * 
	 * @param periods The number of periods to scroll.
	 */
	public void scroll(int periods) {
		if (isEmpty()) {
			return;
		}
		if (periods == 0) {
			return;
		}
		if (get(0).isEmpty()) {
			return;
		}
		int dataSize = get(0).size();
		int periodsToScroll;
		if (periods < 0) {
			periodsToScroll = (-1) * Math.min(endIndex, Math.abs(periods));
		} else {
			periodsToScroll = Math.min(periods, dataSize - startIndex - 1);
		}
		startIndex += periodsToScroll;
		endIndex += periodsToScroll;
	}

	/**
	 * Zoom a certain number of periods.
	 * <ul>
	 * <li>If the number of periods is negative, then zoom out increasing the number of bars shown by the number of
	 * periods.</li>
	 * <li>If the number of periods is positive, then zoom in decreasing the number of bars shown by the number of
	 * periods, leaving at least one bar visible, that is, <i>startIndex</i> and <i>endIndex</i> are the same and in the
	 * range of data.</li>
	 * <li>If zoom out and both <i>startIndex</i> and <i>endIndex</i> are in the range of data, then decrease
	 * <i>startIndex</i> and increase <i>endIndex</i> by the same number of periods.</li>
	 * <li>If zoom out and only one of the indexes is out of range, then move the othe one accordingly.</li>
	 * <li>If zoom out and both indexes are out of range, do not zoom.</li>
	 * </ul>
	 * 
	 * @param periods Thenumber of periods or bars to zoom.
	 */
	public void zoom(int periods) {
		if (isEmpty()) {
			return;
		}
		if (periods == 0) {
			return;
		}
		if (get(0).isEmpty()) {
			return;
		}
		int dataSize = get(0).size();
		boolean zoomOut = (periods < 0);
		boolean zoomIn = !zoomOut;
		periods = Math.abs(periods);

		// Zoom out
		if (zoomOut) {

			// If both indexes are out of range, do not zoom.
			if (startIndex < 0 && endIndex >= dataSize) {
				return;
			}

			// If both indexes are in the range of data, decrease the start index and increase the end index.
			if (startIndex >= 0 && endIndex < dataSize) {
				startIndex -= periods;
				endIndex += periods;
				return;
			}

			// If only startIndex is in the range...
			if (startIndex >= 0 && endIndex >= dataSize) {
				startIndex -= periods;
				return;
			}

			// If only endIndex is in the range...
			if (startIndex < 0 && endIndex < dataSize) {
				endIndex += periods;
				return;
			}
		}

		// Zoom in: always zoom, with the limit that indexes do not overlap and leaving at least one visible bar.
		if (zoomIn) {

			// If start and end indexes are the same, do nothing.
			if (startIndex == endIndex - 1) {
				return;
			}

			// If both indexes are out of range will zoom in the same way as if only the end index is out of range, that
			// is, zoom in the left of the chart maintaining as possible the right blank proportion.
			if (endIndex >= dataSize) {
				double startPeriods = endIndex - startIndex + 1;
				double blankPeriods = endIndex - dataSize;
				double blankFactor = blankPeriods / startPeriods;
				double endPeriods = startPeriods - periods;
				int endBlankPeriods = (int) (endPeriods * blankFactor);
				// Since there were blank periods, leave at list one.
				if (endBlankPeriods == 0) {
					endBlankPeriods = 1;
				}
				endIndex = dataSize + endBlankPeriods;
				startIndex = endIndex - (int) endPeriods + 1;
				checkIndexes();
				return;
			}

			// If both indexes are in the range zoom both sides
			if (startIndex >= 0 && endIndex < dataSize) {
				startIndex += periods;
				endIndex -= periods;
				checkIndexes();
				return;
			}

			// If only endIndex is in the range, zoom the right of the chart maintaining as possible the left blank
			// proportion.
			if (startIndex < 0 && endIndex < dataSize) {
				double startPeriods = endIndex - startIndex + 1;
				double blankPeriods = Math.abs(startIndex);
				double blankFactor = blankPeriods / startPeriods;
				double endPeriods = startPeriods - periods;
				int endBlankPeriods = (int) (endPeriods * blankFactor);
				// Since there were blank periods, leave at list one.
				if (endBlankPeriods == 0) {
					endBlankPeriods = 1;
				}
				startIndex = -endBlankPeriods;
				endIndex = startIndex + (int) endPeriods - 1;
				checkIndexes();
				return;
			}
		}
	}

	/**
	 * Check that indexes do not overlap.
	 */
	private void checkIndexes() {
		int dataSize = get(0).size();
		if (endIndex < startIndex) {
			endIndex = startIndex;
		}
		if (startIndex < 0 && endIndex < 0) {
			startIndex = endIndex = 0;
		}
		if (startIndex >= dataSize && endIndex >= dataSize) {
			startIndex = endIndex = dataSize - 1;
		}
		if (startIndex >= 0 && endIndex < dataSize) {
			if (endIndex == startIndex) {
				if (endIndex < dataSize - 1) {
					endIndex += 1;
				} else {
					startIndex -= 1;
				}
			}
		}
	}

	/**
	 * Check if this plot data has a data list of type <i>Price</i>.
	 * 
	 * @return A boolean.
	 */
	public boolean isPrice() {
		return isDataType(DataType.Price);
	}

	/**
	 * Check if this plot data has a data list of type <i>Indicator</i>.
	 * 
	 * @return A boolean.
	 */
	public boolean isIndicator() {
		return isDataType(DataType.Indicator);
	}

	/**
	 * Check if this plot data is made only of indicators.
	 * 
	 * @return A boolean that indicates if this plot data is made only of indicators.
	 */
	public boolean isIndicatorOnly() {
		return isIndicator() && !isPrice() && !isVolume();
	}

	/**
	 * Check if this plot data has a data list of type <i>Volume</i>.
	 * 
	 * @return A boolean.
	 */
	public boolean isVolume() {
		return isDataType(DataType.Volume);
	}

	/**
	 * Check if this plot data contains a data list of the argument data type.
	 * 
	 * @param dataType The required data type.
	 * @return A boolean that indicates if this plot data contains a data list of the argument data type.
	 */
	public boolean isDataType(DataType dataType) {
		for (DataList dataList : dataLists) {
			if (dataList.getDataInfo().getDataType().equals(dataType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called to notify changes in a data list.
	 * 
	 * @param e The data list event.
	 */
	public void dataListChanged(DataListEvent e) {
	}

	/**
	 * Check if a given period is odd.
	 * 
	 * @param index The index of the period.
	 * @return A boolean that indicates if the period is odd.
	 */
	public boolean isOdd(int index) {
		return odds.get(index);
	}

	/**
	 * Check if a given period is Even.
	 * 
	 * @param index The index of the period.
	 * @return A boolean that indicates if the period is Even.
	 */
	public boolean isEven(int index) {
		return odds.get(index);
	}

	/**
	 * Scans the data and sets the periods odds and evens.
	 */
	public void setOddsAndEvens() {
		if (isEmpty()) {
			return;
		}
		odds.clear();
		DataList dataList = get(0);
		for (int i = 0; i < dataList.size(); i++) {
			// First period is always odd (true).
			if (i == 0) {
				odds.add(true);
				continue;
			}
			// Compare current and previous times.
			boolean oddPrevious = odds.get(i - 1);
			long timePrevious = dataList.get(i - 1).getTime();
			long timeCurrent = dataList.get(i).getTime();
			Calendar calendarPrevious = new Calendar(timePrevious);
			Calendar calendarCurrent = new Calendar(timeCurrent);
			// Check unit.
			switch (getPeriod().getUnit()) {
			case Millisecond:
			case Second:
			case Minute:
			case Hour:
				if (calendarCurrent.getDay() != calendarPrevious.getDay() ||
					calendarCurrent.getWeek() != calendarPrevious.getWeek() ||
					calendarCurrent.getMonth() != calendarPrevious.getMonth() ||
					calendarCurrent.getYear() != calendarPrevious.getYear()) {
					odds.add(!oddPrevious);
				} else {
					odds.add(oddPrevious);
				}
				break;
			case Day:
				if (calendarCurrent.getWeek() != calendarPrevious.getWeek() ||
					calendarCurrent.getMonth() != calendarPrevious.getMonth() ||
					calendarCurrent.getYear() != calendarPrevious.getYear()) {
					odds.add(!oddPrevious);
				} else {
					odds.add(oddPrevious);
				}
				break;
			case Week:
				if (calendarCurrent.getMonth() != calendarPrevious.getMonth() ||
					calendarCurrent.getYear() != calendarPrevious.getYear()) {
					odds.add(!oddPrevious);
				} else {
					odds.add(oddPrevious);
				}
				break;
			case Month:
				if (calendarCurrent.getYear() != calendarPrevious.getYear()) {
					odds.add(!oddPrevious);
				} else {
					odds.add(oddPrevious);
				}
				break;
			default:
				odds.add(oddPrevious);
				break;
			}
		}
	}
}
