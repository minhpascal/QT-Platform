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

import java.util.HashMap;
import java.util.Map;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

/**
 * A data list that retrieves its data from persistor. The contract for a persistor of data lists is that fields must be
 * defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory to start
 * at 1, not negative and indexed.</li>
 * <li>The second field is a long, the time of the timed data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * With this structure, the <tt>PersistorDataList</tt> can handle not only <tt>OHLCV</tt> data, but any timed
 * <tt>Data</tt>.
 * 
 * @author Miquel Sas
 */
public class PersistorDataList extends DataList {

	/**
	 * The underlying persistor.
	 */
	private DataPersistor persistor;
	/**
	 * A map to cache retrieved records by relative index.
	 */
	private Map<Integer, Record> map = new HashMap<>();
	/**
	 * The stack to get the FIFO order.
	 */
	private Delist<Record> list = new ArrayDelist<>();
	/**
	 * The cache size, default 1000.
	 */
	private int cacheSize = 1000;

	/**
	 * @param session
	 * @param dataInfo
	 */
	public PersistorDataList(Session session, DataInfo dataInfo, Persistor persistor) {
		super(session, dataInfo);
		this.persistor = new DataPersistor(persistor);
	}

	/**
	 * Returns the cache size.
	 * 
	 * @return The cache size.
	 */
	public int getCacheSize() {
		return cacheSize;
	}

	/**
	 * Sets the cache size. Setting the cache size to -1 has the effect to cache all data.
	 * 
	 * @param cacheSize The cache size.
	 */
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return persistor.size().intValue();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return persistor.isEmpty();
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 * @return A boolean indicating if the elementt was added.
	 */
	@Override
	public void add(Data data) {
		// TODO Pending to implement
	}

	/**
	 * Returns the data element at the given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {

		Data data = getFromCache(index);
		if (data != null) {
			return data;
		}

		Record record = persistor.getRecord(Long.valueOf(index));
		addToCache(index, record);
		return persistor.getData(record);
	}

	/**
	 * Add the record to the cache.
	 * 
	 * @param index The index relative to 0.
	 * @param record The record.
	 */
	private void addToCache(int index, Record record) {
		if (list.size() == cacheSize) {
			// Remove 1/5 or the cache.
			int countRemove = cacheSize / 5;
			while (countRemove > 0) {
				Record remove = list.removeFirst();
				int key = persistor.getIndex(remove).intValue() - 1;
				map.remove(key);
				countRemove--;
			}
		}
		map.put(index, record);
		list.addLast(record);
	}

	/**
	 * Returns the data from the cache.
	 * 
	 * @param index The relative index starting at 0.
	 * @return The data or null if not present in the cache.
	 */
	private Data getFromCache(int index) {
		Record record = map.get(index);
		if (record != null) {
			return persistor.getData(record);
		}
		return null;
	}
}
