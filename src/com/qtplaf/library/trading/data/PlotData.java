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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

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
	 * Returns the list of indicator data lists in this plot data, in the order of precendence they are to be
	 * calculated. If an indicator uses the data of another indicator, the last one must be calculated first.
	 * 
	 * @return The list of indicator data lists,
	 */
	public List<IndicatorDataList> getIndicatorDataLists() {
		List<IndicatorDataList> indicatorDataLists = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			DataList dataList = get(i);
			if (dataList instanceof IndicatorDataList) {
				IndicatorDataList indicatorDataList = (IndicatorDataList) dataList;
			}
		}
		return indicatorDataLists;
	}

	/**
	 * Returns the list of first level indicator data lists.
	 * 
	 * @return The list of first level indicator data lists.
	 */
	private List<IndicatorDataList> getFirstLevelIndicatorDataLists() {
		List<IndicatorDataList> indicatorDataLists = new ArrayList<>();
		for (int i = 0; i < size(); i++) {
			DataList dataList = get(i);
			if (dataList instanceof IndicatorDataList) {
				IndicatorDataList indicatorDataList = (IndicatorDataList) dataList;
				indicatorDataLists.add(indicatorDataList);
			}
		}
		return indicatorDataLists;
	}

	/**
	 * Returns the list of indicator data lists that the argument indicator data list requires as indicator sources.
	 * 
	 * @param parent The parent indicator data list.
	 * @return The list of chilkdren required indicator data lists.
	 */
	private List<IndicatorDataList> getIndicatorDataLists(IndicatorDataList parent) {
		List<IndicatorDataList> children = new ArrayList<>();
		List<IndicatorSource> sources = parent.getIndicatorSources();
		for (IndicatorSource source : sources) {
			if (source.getDataList() instanceof IndicatorDataList) {
				IndicatorDataList child = (IndicatorDataList) source.getDataList();
				children.add(child);
			}
		}
		return children;
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
	 * Returns the data of the data list.
	 * 
	 * @param dataListIndex Tha data list index.
	 * @param dataIndex The index of the data in the data list.
	 * @return
	 */
	public Data getData(int dataListIndex, int dataIndex) {
		return get(dataListIndex).get(dataIndex);
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
	 * Ensure that indicators are calculated from look backward up to the start index minus one.
	 * 
	 * @param plotData The plot data.
	 * @param startIndex The index to ensure calculation of indicators.
	 */
	private void ensureIndicatorsCalculated() {
		// Index 0, no need to do nothing.
		if (startIndex == 0) {
			return;
		}

		// If no indicator data lists...
		List<IndicatorDataList> indicatorDataLists = getIndicatorDataLists();
		if (indicatorDataLists.isEmpty()) {
			return;
		}

		// Get the maximum look backward.
		int lookBackward = 0;
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			Indicator indicator = indicatorDataList.getIndicator();
			lookBackward = Math.max(lookBackward, indicator.getIndicatorInfo().getLookBackward());
		}

		// Remove calculated from start to end, and calculate again.
		int start = Math.max(0, startIndex - lookBackward + 1);
		int end = startIndex - 1;
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			for (int index = start; index <= end; index++) {
				indicatorDataList.remove(index);
			}
		}
		for (IndicatorDataList indicatorDataList : indicatorDataLists) {
			for (int index = start; index <= end; index++) {
				indicatorDataList.calculate(index);
			}
		}
	}

	/**
	 * Calculates plot frame based on start and end index: minimum and maximum values, start end end time.
	 */
	public void calculateFrame() {

		// Check that there is data to calculate the frame.
		if (isEmpty()) {
			throw new IllegalStateException();
		}

		// Ensure that indicators are calculated up to the start index minus one.
		ensureIndicatorsCalculated();

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
		if (isEmpty()) {
			return false;
		}
		if (get(0).isEmpty()) {
			return false;
		}
		if (get(0).size() <= index) {
			return false;
		}
		return (getOddCode(get(0).get(index)) == 1);
	}

	/**
	 * Check if a given period is Even.
	 * 
	 * @param index The index of the period.
	 * @return A boolean that indicates if the period is Even.
	 */
	public boolean isEven(int index) {
		if (isEmpty()) {
			return false;
		}
		if (get(0).isEmpty()) {
			return false;
		}
		if (get(0).size() <= index) {
			return false;
		}
		return (getOddCode(get(0).get(index)) == 2);
	}

	/**
	 * Returns the odd code, 1 odd, 2 even, 0 none.
	 * 
	 * @param data The data item.
	 * @return The odd code.
	 */
	public int getOddCode(Data data) {
		if (data == null) {
			return 0;
		}
		Calendar calendar = Calendar.getGTMCalendar(data.getTime());
		switch (getPeriod().getUnit()) {
		case Millisecond:
		case Second:
		case Minute:
		case Hour:
			if (NumberUtils.isOdd(calendar.getDay())) {
				return 1;
			}
			return 2;
		case Day:
			if (NumberUtils.isOdd(calendar.getWeek())) {
				return 1;
			}
			return 2;
		case Week:
			if (NumberUtils.isOdd(calendar.getMonth())) {
				return 1;
			}
			return 2;
		case Month:
			if (NumberUtils.isOdd(calendar.getYear())) {
				return 1;
			}
			return 2;
		default:
			return 0;
		}
	}
}
