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

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.info.IndicatorInfo;

/**
 * A data list that retrieves its data from an indicator, thus calculating the data each time it is retrieved through
 * the <tt>get</tt> method.
 * 
 * @author Miquel Sas
 */
public class IndicatorDataList extends DataList {

	/**
	 * The indicator that calculates the data list.
	 */
	private Indicator indicator;
	/**
	 * The list of indicator sources that the indicator will use to calculate data.
	 */
	private List<IndicatorSource> indicatorSources;
	/**
	 * A data list to cache this indicator calculated data.
	 */
	private ArrayDataList indicatorData;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 * @param indicator The indicator.
	 * @param dataInfo The data info.
	 * @param indicatorSources The list of indicator sources.
	 */
	public IndicatorDataList(
		Session session,
		Indicator indicator,
		IndicatorInfo dataInfo,
		List<IndicatorSource> indicatorSources) {
		super(session, dataInfo);
		this.indicator = indicator;
		this.indicatorSources = indicatorSources;
		this.indicator.start(indicatorSources);

		this.indicatorData = new ArrayDataList(session, dataInfo);

		initializePlotProperties();
	}

	/**
	 * Returns the underlying indicator of this indicator data list.
	 * 
	 * @return The indicator.
	 */
	public Indicator getIndicator() {
		return indicator;
	}

	/**
	 * Returns the indicator data size already calculated.
	 * 
	 * @return The indicator data size already calculated.
	 */
	public int getIndicatorDataSize() {
		return indicatorData.size();
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return indicatorSources.get(0).getDataList().size();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 * @return A boolean indicating if the elementt was added.
	 */
	@Override
	public boolean add(Data data) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Returns the data element at the given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {
		Data data = indicator.calculate(index, indicatorSources, indicatorData);
		setIndicatorData(index, data);
		return data;
	}

	/**
	 * Sets the indicator data when this indicator is reentrant.
	 * 
	 * @param index The index of the data.
	 * @param data The data.
	 */
	private void setIndicatorData(int index, Data data) {

		// Shoud never happen.
		if (index < 0) {
			throw new IllegalArgumentException();
		}

		// Index is in the range of already cached data.
		if (index < indicatorData.size()) {
			indicatorData.set(index, data);
			return;
		}

		// Index is exactly a new incoming data.
		if (index == indicatorData.size()) {
			indicatorData.add(data);
			return;
		}

		// Index is far from the last element.
		while (indicatorData.size() < index) {
			Data invalidData = new Data();
			invalidData.setValid(false);
			indicatorData.add(invalidData);
		}
		indicatorData.add(data);
	}

}
