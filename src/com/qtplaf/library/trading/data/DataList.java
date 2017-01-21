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
public abstract class DataList {

	/**
	 * The data info.
	 */
	private DataInfo dataInfo;
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
	 * @return A boolean indicating if the elementt was added.
	 */
	public abstract boolean add(Data data);

	/**
	 * Returns the data element atthe given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	public abstract Data get(int index);

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
}
