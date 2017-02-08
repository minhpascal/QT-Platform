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
			if (!getIndexOHLCV().equals(dataList.getIndexOHLCV())) {
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
}
