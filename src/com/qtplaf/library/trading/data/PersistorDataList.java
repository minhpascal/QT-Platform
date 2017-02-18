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
import java.util.Map;
import java.util.TreeMap;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.trading.data.info.DataInfo;

/**
 * A data list that retrieves its data from persistor. The contract for a persistor of data lists is that fields must be
 * defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory to start
 * at 1, not negative and indexed.</li>
 * <li>The second field is a long, the time of the timed data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * With this structure, the <tt>PersistorDataList</tt> can handle not only <tt>Data</tt> data, but any timed
 * <tt>Data</tt>.
 * 
 * @author Miquel Sas
 */
public class PersistorDataList extends DataList {

	/**
	 * The underlying persistor.
	 */
	private DataPersistor dataPersistor;
	/**
	 * A map to cache retrieved records by relative index.
	 */
	private Map<Integer, Record> map = new TreeMap<>();
	/**
	 * The cache size, default 1000.
	 */
	private int cacheSize = 1000;
	/**
	 * The page size to read chunks.
	 */
	private int pageSize = 100;

	/**
	 * Constructor.
	 * 
	 * @param session
	 * @param dataInfo
	 */
	public PersistorDataList(Session session, DataInfo dataInfo, Persistor persistor) {
		super(session, dataInfo);
		this.dataPersistor = new DataPersistor(persistor);
	}

	/**
	 * Returns the data persistor.
	 * 
	 * @return The data persistor.
	 */
	public DataPersistor getDataPersistor() {
		return dataPersistor;
	}

	/**
	 * Returns the index in the data item given the index in the record.
	 * 
	 * @param recordIndex The index in the record.
	 * @return The index in the data.
	 */
	public int getDataIndex(int recordIndex) {
		return dataPersistor.getDataIndex(recordIndex);
	}

	/**
	 * Returns the data index given the field alias.
	 * 
	 * @param alias The field alias.
	 * @return The index in the data.
	 */
	public int getDataIndex(String alias) {
		return dataPersistor.getDataIndex(alias);
	}

	/**
	 * Returns the index in the record given the index in the data item.
	 * 
	 * @param dataIndex The index in the data item.
	 * @return The index in the record.
	 */
	public int getRecordIndex(int dataIndex) {
		return dataPersistor.getRecordIndex(dataIndex);
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
	 * Retuns the page size used to read chunks.
	 * 
	 * @return The page size.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size to read chunks.
	 * 
	 * @param pageSize The page size.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		return dataPersistor.size().intValue();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return dataPersistor.isEmpty();
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

		RecordSet recordSet = dataPersistor.getPage(Long.valueOf(index), getPageSize());
		Record record = recordSet.get(0);
		for (int i = 0; i < recordSet.size(); i++) {
			Record rc = recordSet.get(i);
			int rcIndex = dataPersistor.getIndex(rc).intValue();
			addToCache(rcIndex, recordSet.get(i));
		}
		return dataPersistor.getData(record);
	}

	/**
	 * Add the record to the cache.
	 * 
	 * @param index The index relative to 0.
	 * @param record The record.
	 */
	private void addToCache(int index, Record record) {
		if (map.size() == cacheSize) {
			// Remove 1/5 or the cache.
			List<Integer> indexes = getCachedIndexes(cacheSize / 5);
			for (Integer i : indexes) {
				map.remove(i);
			}
		}
		map.put(index, record);
	}

	/**
	 * Returns the first count cached indexes.
	 * 
	 * @param count The number of indexes to retrieve.
	 * @return The list of indexes.
	 */
	private List<Integer> getCachedIndexes(int count) {
		Iterator<Integer> iterator = map.keySet().iterator();
		List<Integer> indexes = new ArrayList<>();
		while (indexes.size() < count && iterator.hasNext()) {
			indexes.add(iterator.next());
		}
		return indexes;
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
			return dataPersistor.getData(record);
		}
		return null;
	}

	/**
	 * Removes a data element from the cache.
	 * 
	 * @param index The index of the data to remove.
	 * @return The removed data or null.
	 */
	public Data remove(int index) {
		Record record = map.remove(index);
		if (record == null) {
			return null;
		}
		return dataPersistor.getData(record);
	}
}
