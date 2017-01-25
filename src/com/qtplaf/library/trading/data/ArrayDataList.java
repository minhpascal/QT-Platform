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
import com.qtplaf.library.trading.data.info.DataInfo;

/**
 * A data list which underlying data is an in-memory list.
 * 
 * @author Miquel Sas
 */
public class ArrayDataList extends DataList {

	/**
	 * The list of data.
	 */
	private List<Data> dataList = new ArrayList<>();

	/**
	 * Constructor assigning the data type..
	 * 
	 * @param session The working session.
	 * @param dataType The data type.
	 */
	public ArrayDataList(Session session, DataInfo dataInfo) {
		super(session, dataInfo);
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 * @return A boolean indicating if the elementt was added.
	 */
	@Override
	public boolean add(Data data) {
		boolean added = dataList.add(data);
		if (added) {
			notifyChange(new DataListEvent(this, data, dataList.size() - 1, DataListEvent.Operation.Add));
		}
		return added;
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
	 * Set the data at the given in dex.
	 * 
	 * @param index The index.
	 * @param data The data.
	 */
	public void set(int index, Data data) {
		dataList.set(index, data);
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
}
