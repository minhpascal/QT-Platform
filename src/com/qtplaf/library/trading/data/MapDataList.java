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

import java.util.Map;
import java.util.TreeMap;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.info.DataInfo;

/**
 * A data list which underlying data is an in-memory sorted map.
 * 
 * @author Miquel Sas
 */
public class MapDataList extends DataList {

	/**
	 * The list of data.
	 */
	private Map<Integer, Data> dataList = new TreeMap<>();

	/**
	 * Constructor assigning the data type..
	 * 
	 * @param session The working session.
	 * @param dataType The data type.
	 */
	public MapDataList(Session session, DataInfo dataInfo) {
		super(session, dataInfo);
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 * @return A boolean indicating if the elementt was added.
	 */
	@Override
	public void add(Data data) {
		dataList.put(size(), data);
		notifyChange(new DataListEvent(this, data, dataList.size() - 1, DataListEvent.Operation.Add));
	}

	/**
	 * Returns the data element atthe given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {
		return dataList.get(index);
	}

	/**
	 * Put the data at the given in dex.
	 * 
	 * @param index The index.
	 * @param data The data.
	 */
	public void put(int index, Data data) {
		dataList.put(index, data);
	}

	/**
	 * Remove data at the given index.
	 * 
	 * @param index The index.
	 * @return The removed data.
	 */
	public Data remove(int index) {
		return dataList.remove(index);
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return dataList.isEmpty();
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return dataList.size();
	}

	/**
	 * Returns a boolean indicating if the mapped list contains the index.
	 * 
	 * @param index The index to check.
	 * @return A boolean.
	 */
	public boolean contains(int index) {
		return dataList.containsKey(index);
	}
}
