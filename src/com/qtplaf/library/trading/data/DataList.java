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
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.DataPlotter;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.trading.data.info.VolumeInfo;

/**
 * A list of data objects.
 * 
 * @author Miquel Sas
 */
public class DataList {

	/**
	 * The data info.
	 */
	private DataInfo dataInfo;
	/**
	 * The list of data.
	 */
	private List<Data> dataList = new ArrayList<>();
	/**
	 * The plot type to apply to this data.
	 */
	private PlotType plotType = PlotType.Line;
	/**
	 * The OHLCV index case of price and line plot.
	 */
	private OHLCV.Index indexOHLCV = OHLCV.Index.Close;
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
	 * The data plotter used to plot this data list, set by the chart container prior to plot tha data.
	 */
	private transient DataPlotter dataPlotter;
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
		;
		this.dataInfo = dataInfo;
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
	 * Directly set the data list. This method is aimed to be used by the persistent layer and should be used with care.
	 * 
	 * @param dataList The data list to set.
	 */
	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
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
	public int size() {
		return dataList.size();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	public boolean isEmpty() {
		return dataList.isEmpty();
	}

	/**
	 * Add the dara element to this list.
	 * 
	 * @param data
	 * @return A boolean indicating if the elementt was added.
	 */
	public boolean add(Data data) {
		boolean added = dataList.add(data);
		if (added) {
			notifyChange(new DataListEvent(this, data, dataList.size() - 1, DataListEvent.Operation.Add));
		}
		return added;
	}

	/**
	 * Clear the list.
	 */
	public void clear() {
		dataList.clear();
		notifyChange(new DataListEvent(this, null, -1, DataListEvent.Operation.Clear));
	}

	/**
	 * Returns the data element atthe given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	public Data get(int index) {
		return dataList.get(index);
	}

	/**
	 * Returns the sub list of data items.
	 * 
	 * @param fromIndex Start index.
	 * @param toIndex End index.
	 * @return The sub list.
	 */
	public List<Data> subList(int fromIndex, int toIndex) {
		return dataList.subList(fromIndex, toIndex);
	}

	/**
	 * Removes the data at the given index.
	 * 
	 * @param index The data index.
	 * @return The removed data.
	 */
	public Data remove(int index) {
		Data removedData = dataList.remove(index);
		notifyChange(new DataListEvent(this, removedData, index, DataListEvent.Operation.Remove));
		return removedData;
	}

	/**
	 * Sets the element at the given index.
	 * 
	 * @param index The index.
	 * @param data The data element.
	 * @return The data element that was previously at the index location.
	 */
	public Data set(int index, Data data) {
		Data previousData = dataList.set(index, data);
		notifyChange(new DataListEvent(this, previousData, index, DataListEvent.Operation.Set));
		return previousData;
	}

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
	 * Returns the OHLCV index, in case of price chart and line plot.
	 * 
	 * @return The OHLCV index.
	 */
	public OHLCV.Index getIndexOHLCV() {
		return indexOHLCV;
	}

	/**
	 * Sets the OHLCV index, in case of price chart and line plot.
	 * 
	 * @param indexOHLCV The OHLCV index.
	 */
	public void setIndexOHLCV(OHLCV.Index plotValue) {
		this.indexOHLCV = plotValue;
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
			int count = get(0).size();
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
	private void notifyChange(DataListEvent e) {
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
		DataList volumeDataList = new DataList(getSession(), volumeDataInfo);
		volumeDataList.dataList = this.dataList;
		volumeDataList.setPlotType(PlotType.Histogram);
		volumeDataList.initializePlotProperties();
		return volumeDataList;
	}

	/**
	 * Returns this data list suitable data plotter.
	 * 
	 * @return This data list suitable data plotter.
	 */
	public DataPlotter getDataPlotter() {
		return dataPlotter;
	}

	/**
	 * Sets this data list suitable data plotter.
	 * 
	 * @param dataPlotter This data list suitable data plotter.
	 */
	public void setDataPlotter(DataPlotter dataPlotter) {
		this.dataPlotter = dataPlotter;
	}

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
	public Data union(int fromIndex, int toIndex) {
		List<Data> subList = subList(fromIndex, toIndex);
		if (subList.isEmpty()) {
			throw new IllegalStateException("The sub list can not be empty.");
		}
		Data data = subList.get(0);
		if (!OHLCV.class.isInstance(data)) {
			return subList.get(subList.size() - 1);
		}
		double open = 0;
		double high = 0;
		double low = 0;
		double close = 0;
		double volume = 0;
		long time = 0;
		for (int i = 0; i < subList.size(); i++) {
			OHLCV ohlcv = (OHLCV) subList.get(i);
			if (i == 0) {
				open = ohlcv.getOpen();
				time = ohlcv.getTime();
			}
			high = Math.max(high, ohlcv.getHigh());
			low = Math.min(low, ohlcv.getLow());
			if (i == subList.size() - 1) {
				close = ohlcv.getClose();
			}
			volume += ohlcv.getVolume();
		}
		return new OHLCV(time, open, high, low, close, volume);
	}

	/**
	 * Returns a data list with this list data and the argument info, mostly for tricky calculations in indicators.
	 * 
	 * @param dataInfo The data info.
	 * @return The new data list.
	 */
	public DataList getDataListFromInfo(DataInfo dataInfo) {
		DataList resultDataList = new DataList(getSession(), dataInfo);
		resultDataList.dataList = this.dataList;
		return resultDataList;
	}

}
