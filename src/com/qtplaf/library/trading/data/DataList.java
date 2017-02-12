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

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.BarPlotter;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.DataPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.chart.plotter.PlotterContext;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.VolumeInfo;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.NumberUtils;

/**
 * A list of data objects.
 * 
 * @author Miquel Sas
 */
public abstract class DataList {

	/**
	 * Returns the list of first level indicator data lists, given a list of data lists.
	 * 
	 * @param dataLists The source list of data lists.
	 * @return The list of first level indicator data lists.
	 */
	public static List<IndicatorDataList> getIndicatorDataLists(List<DataList> dataLists) {
		List<IndicatorDataList> indicatorDataLists = new ArrayList<>();
		for (int i = 0; i < dataLists.size(); i++) {
			DataList dataList = dataLists.get(i);
			if (dataList instanceof IndicatorDataList) {
				IndicatorDataList indicatorDataList = (IndicatorDataList) dataList;
				indicatorDataLists.add(indicatorDataList);
			}
		}
		return indicatorDataLists;
	}

	/**
	 * Returns the list of indicator data lists, in the order of precendence they are to be calculated. If an indicator
	 * uses the data of another indicator, the last one must be calculated first.
	 * 
	 * @param dataLists The source list of data lists.
	 * @return The list of indicator data lists,
	 */
	public static List<IndicatorDataList> getIndicatorDataListsToCalculate(List<DataList> dataLists) {
		List<IndicatorDataList> firstLevelIndicatorDataLists = getIndicatorDataLists(dataLists);
		List<IndicatorDataList> indicatorDataLists = new ArrayList<>();
		fillIndicatorDataLists(indicatorDataLists, firstLevelIndicatorDataLists);
		return indicatorDataLists;
	}

	/**
	 * Fill the result indicator data list in the appropriate calculation order.
	 * 
	 * @param results The result lists.
	 * @param parents The parent lists.
	 */
	private static void fillIndicatorDataLists(List<IndicatorDataList> results, List<IndicatorDataList> parents) {
		// Process required first.
		for (IndicatorDataList parent : parents) {
			List<IndicatorDataList> required = getIndicatorDataListsRequired(parent);
			fillIndicatorDataLists(results, required);
		}
		// Process current level.
		for (IndicatorDataList parent : parents) {
			if (!results.contains(parent)) {
				results.add(parent);
			}
		}
	}

	/**
	 * Returns the list of indicator data lists that the argument indicator data list requires as indicator sources.
	 * 
	 * @param parent The parent indicator data list.
	 * @return The list of chilkdren required indicator data lists.
	 */
	private static List<IndicatorDataList> getIndicatorDataListsRequired(IndicatorDataList parent) {
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
	 * Returns all the unique data lists involved.
	 * 
	 * @param parent The top list.
	 * @return All the unique data lists involved.
	 */
	public static List<DataList> getDataLists(DataList parent) {
		List<DataList> allDataLists = getAllDataLists(parent);
		List<DataList> dataLists = new ArrayList<>();
		for (DataList dataList : allDataLists) {
			if (!dataLists.contains(dataList)) {
				dataLists.add(dataList);
			}
		}
		return dataLists;
	}

	/**
	 * Returns all the data lists, even repeating.
	 * 
	 * @param parent The top list.
	 * @return A list with all lists involved.
	 */
	private static List<DataList> getAllDataLists(DataList parent) {
		List<DataList> children = new ArrayList<>();
		if (parent instanceof IndicatorDataList) {
			IndicatorDataList indicator = (IndicatorDataList) parent;
			List<IndicatorSource> sources = indicator.getIndicatorSources();
			for (IndicatorSource source : sources) {
				children.addAll(getAllDataLists(source.getDataList()));
			}
		}
		children.add(parent);
		return children;
	}

	/**
	 * The data info.
	 */
	private DataInfo dataInfo;
	/**
	 * The plot type to apply to this data.
	 */
	private PlotType plotType = PlotType.Line;
	/**
	 * The price index case of price and line plot.
	 */
	private int indexPrice = Data.IndexClose;
	/**
	 * A list of plot properties, one to plot prices, either lines, bars or candles, one for volumes, and one for each
	 * data value in indicators.
	 */
	private List<PlotProperties> plotPropertiesList = new ArrayList<>();
	/**
	 * A list of data list listeners.
	 */
	private List<DataListListener> listeners = new ArrayList<>();
	/**
	 * List of data plotters.
	 */
	private List<DataPlotter> dataPlotters = new ArrayList<>();
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor assigning the data type..
	 * 
	 * @param session The working session.
	 * @param dataType The data type.
	 */
	public DataList(Session session, DataInfo dataInfo) {
		super();
		this.session = session;
		this.dataInfo = dataInfo;
	}

	/**
	 * Add a user data plotter for the list.
	 * 
	 * @param dataPlotter The data plotter.
	 */
	public void addDataPlotter(DataPlotter dataPlotter) {
		dataPlotters.add(dataPlotter);
	}

	/**
	 * Check whether the argument object is equal to this data list.
	 * 
	 * A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataList) {
			DataList dataList = (DataList) obj;
			if (!getDataInfo().equals(dataList.getDataInfo())) {
				return false;
			}
			if (!getPlotType().equals(dataList.getPlotType())) {
				return false;
			}
			if (getIndexPrice() != dataList.getIndexPrice()) {
				return false;
			}
			int count = getPlotPropertiesCount();
			if (count != dataList.getPlotPropertiesCount()) {
				return false;
			}
			for (int i = 0; i < count; i++) {
				if (!getPlotProperties(i).equals(dataList.getPlotProperties(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Returns this data list data info.
	 * 
	 * @return The data info.
	 */
	public DataInfo getDataInfo() {
		return dataInfo;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	public abstract int size();

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	public abstract boolean isEmpty();

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 */
	public abstract void add(Data data);

	/**
	 * Returns the data element at the given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	public abstract Data get(int index);

	/**
	 * Remove and return the data at the given index.
	 * 
	 * @param index The index.
	 * @return The removed data or null.
	 */
	public abstract Data remove(int index);

	/**
	 * Returns the type of plot.
	 * 
	 * @return The type of plot.
	 */
	public PlotType getPlotType() {
		return plotType;
	}

	/**
	 * Sets the type of plot.
	 * 
	 * @param plotType The type of plot.
	 */
	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

	/**
	 * Returns the price index, in case of price chart and line plot.
	 * 
	 * @return The price index.
	 */
	public int getIndexPrice() {
		return indexPrice;
	}

	/**
	 * Sets the price index to plot, in case of price chart and line plot.
	 * 
	 * @param indexPrice The price index.
	 */
	public void setIndexPrice(int indexPrice) {
		this.indexPrice = indexPrice;
	}

	/**
	 * Returns the number of plot properties.
	 * 
	 * @return The number of plot properties.
	 */
	public int getPlotPropertiesCount() {
		return plotPropertiesList.size();
	}

	/**
	 * Returns the list of plot properties.
	 * 
	 * @return The list of plot properties.
	 */
	List<PlotProperties> getPlotPropertiesList() {
		return plotPropertiesList;
	}

	/**
	 * Sets the list of plot properties.
	 * 
	 * @param plotPropertiesList The list of plot properties.
	 */
	void setPlotPropertiesList(List<PlotProperties> plotPropertiesList) {
		this.plotPropertiesList = plotPropertiesList;
	}

	/**
	 * Returns the plot properties at the givenindex.
	 * 
	 * @param index The index.
	 * @return The plot properties.
	 */
	public PlotProperties getPlotProperties(int index) {
		return plotPropertiesList.get(index);
	}

	/**
	 * Initializes the plot properties depending on the data type.
	 */
	public void initializePlotProperties() {
		if (isEmpty()) {
			return;
		}
		plotPropertiesList.clear();
		switch (getDataInfo().getDataType()) {
		case Price:
			plotPropertiesList.add(new PlotProperties());
			break;
		case Volume:
			plotPropertiesList.add(new PlotProperties());
			break;
		case Indicator:
			int count = getDataInfo().getOutputCount();
			for (int i = 0; i < count; i++) {
				plotPropertiesList.add(new PlotProperties());
			}
			break;
		default:
			plotPropertiesList.add(new PlotProperties());
			break;
		}
	}

	/**
	 * Add a data list listener.
	 * 
	 * @param listener The listener.
	 */
	public void addListener(DataListListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remooves the argument listener.
	 * 
	 * @param listener he listener to remove.
	 */
	public void removeListener(DataListListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Clear listeners.
	 */
	public void clearListeners() {
		listeners.clear();
	}

	/**
	 * Notify listeners a data list change.
	 * 
	 * @param e The data list event.
	 */
	protected void notifyChange(DataListEvent e) {
		for (DataListListener listener : listeners) {
			listener.dataListChanged(e);
		}
	}

	/**
	 * Returns the volume data list of this price data list.
	 * 
	 * @return The volume data list.
	 * @throws IllegalStateException If this data list is not a price data list.
	 */
	public DataList getVolumeDataList() throws IllegalStateException {
		if (!getDataInfo().getDataType().equals(DataType.Price)) {
			throw new IllegalStateException("Data type must be price.");
		}
		DataInfo volumeDataInfo =
			new VolumeInfo(getSession(), getDataInfo().getInstrument(), getDataInfo().getPeriod());
		DataList volumeDataList = new DelegateDataList(getSession(), volumeDataInfo, this);
		volumeDataList.setPlotType(PlotType.Histogram);
		volumeDataList.initializePlotProperties();
		return volumeDataList;
	}

	/**
	 * Returns the list of data plotters.
	 * 
	 * @return The list of data plotters.
	 */
	public List<DataPlotter> getDataPlotters() {
		if (dataPlotters.isEmpty()) {
			DataPlotter dataPlotter;
			switch (getPlotType()) {
			case Bar:
				dataPlotter = new BarPlotter();
				break;
			case Candlestick:
				dataPlotter = new CandlestickPlotter();
				break;
			case Histogram:
				// TODO: implement histogram plotter.
				dataPlotter = new LinePlotter();
				break;
			case Line:
				dataPlotter = new LinePlotter();
				break;
			default:
				dataPlotter = new LinePlotter();
				break;
			}
			dataPlotters.add(dataPlotter);
		}
		return dataPlotters;
	}

	/**
	 * Set the plotter context to data plotters.
	 * 
	 * @param context The plotter context.
	 */
	public void setPlotterContext(PlotterContext context) {
		List<DataPlotter> dataPlotters = getDataPlotters();
		for (DataPlotter dataPlotter : dataPlotters) {
			dataPlotter.setContext(context);
		}
	}

	/**
	 * Check if a data list has to be plotted from scratch, mainly because it plots lines with dashes.
	 * 
	 * @param dataList The data list.
	 * @return A boolean that indicates if the data list has to be plotte from scratch.
	 */
	public boolean isPlotFromScratch() {
		int count = getPlotPropertiesCount();
		for (int i = 0; i < count; i++) {
			PlotProperties plotProperties = getPlotProperties(i);
			BasicStroke stroke = (BasicStroke) plotProperties.getStroke();
			if (stroke.getDashArray() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a string representation.
	 * 
	 * @return A readable string representation.
	 */
	@Override
	public String toString() {
		return getDataInfo().toString();
	}

	/**
	 * Returns a boolean indicating whether the value at value index, of the data at data index, is a maximum for the
	 * argument period.
	 * 
	 * @param dataIndex The index of the data element.
	 * @param valueIndex The index of the value within the data.
	 * @param period The period, number of data elements to check before and after.
	 * @return A boolean indicating whether the value is a maximum.
	 */
	public boolean isMaximum(int dataIndex, int valueIndex, int period) {
		return isMinimumMaximum(dataIndex, valueIndex, period, false);
	}

	/**
	 * Returns a boolean indicating whether the value at value index, of the data at data index, is a minimum for the
	 * argument period.
	 * 
	 * @param dataIndex The index of the data element.
	 * @param valueIndex The index of the value within the data.
	 * @param period The period, number of data elements to check before and after.
	 * @return A boolean indicating whether the value is a minimum.
	 */
	public boolean isMinimum(int dataIndex, int valueIndex, int period) {
		return isMinimumMaximum(dataIndex, valueIndex, period, true);
	}

	/**
	 * Returns a boolean indicating whether the value at value index, of the data at data index, is a minimum/maximum
	 * for the argument period.
	 * 
	 * @param dataIndex The index of the data element.
	 * @param valueIndex The index of the value within the data.
	 * @param period The period, number of data elements to check before and after.
	 * @param minimum A boolean that indicates whether to ckeck minimum or maximum.
	 * @return A boolean indicating whether the value is a minimum/maximum.
	 */
	private boolean isMinimumMaximum(int dataIndex, int valueIndex, int period, boolean minimum) {
		if (dataIndex < period) {
			return false;
		}
		if (dataIndex > size() - 1 - period) {
			return false;
		}
		double value = get(dataIndex).getValue(valueIndex);
		int startBackward = Math.max(0, dataIndex - period);
		for (int i = dataIndex - 1; i >= startBackward; i--) {
			if (minimum) {
				if (get(i).getValue(valueIndex) < value) {
					return false;
				}
			} else {
				if (get(i).getValue(valueIndex) > value) {
					return false;
				}
			}
		}
		int endForward = Math.min(dataIndex + period, size() - 1);
		for (int i = dataIndex + 1; i <= endForward; i++) {
			if (minimum) {
				if (get(i).getValue(valueIndex) < value) {
					return false;
				}
			} else {
				if (get(i).getValue(valueIndex) > value) {
					return false;
				}
			}
		}
		return true;
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
		if (isEmpty()) {
			return false;
		}
		if (get(0).size() <= index) {
			return false;
		}
		return (getOddCode(get(index)) == 1);
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
		if (isEmpty()) {
			return false;
		}
		if (get(0).size() <= index) {
			return false;
		}
		return (getOddCode(get(index)) == 2);
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
		switch (getDataInfo().getPeriod().getUnit()) {
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
