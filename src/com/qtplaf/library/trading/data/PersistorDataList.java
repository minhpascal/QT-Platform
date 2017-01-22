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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Condition;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.info.DataInfo;
import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A data list that retrieves its data from persistor. The contract for a persistor of data lists is that fields must be
 * defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory to start
 * at 1, not negative and indexed.</li>
 * <li>The second field is a long, the time of the ohlcv data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * With this structure, the <tt>PersistorDataList</tt> can handle not only <tt>OHLCV</tt> data, but any timed
 * <tt>Data</tt>.
 * 
 * @author Miquel Sas
 */
public class PersistorDataList extends DataList {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * A data item to be stored in the cached page.
	 */
	private class DataItem {

		private int index;
		private Data data;

		private DataItem(int index, Data data) {
			this.index = index;
			this.data = data;
		}

		private int getIndex() {
			return index;
		}

		private Data getData() {
			return data;
		}
	}

	/**
	 * The underlying persistor.
	 */
	private Persistor persistor;
	/**
	 * The page size to cache data, default is 5000.
	 */
	private int pageSize = 5000;
	/**
	 * The cached page.
	 */
	private Delist<DataItem> page = new ArrayDelist<>();
	/**
	 * First index in the table.
	 */
	private int firstIndex = -1;

	/**
	 * @param session
	 * @param dataInfo
	 */
	public PersistorDataList(Session session, DataInfo dataInfo, Persistor persistor) {
		super(session, dataInfo);
		this.persistor = persistor;
	}

	/**
	 * Set the page size.
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
		int first = getFirstIndex();
		int last = getLastIndex();
		return (last - first + 1);
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Add the data element to this list.
	 * 
	 * @param data The data element.
	 * @return A boolean indicating if the elementt was added.
	 */
	@Override
	public boolean add(Data data) {
		// TODO Pending to implement
		return false;
	}

	/**
	 * Returns the data element atthe given index.
	 * 
	 * @param index The index.
	 * @return The data element at the given index.
	 */
	@Override
	public Data get(int index) {

		// First check data in the current page.
		if (inPage(index)) {
			return getDataFromPage(index);
		}

		// The the page that should contain the data.
		loadPage(index);
		if (inPage(index)) {
			return getDataFromPage(index);
		}

		throw new IllegalStateException();
	}

	/**
	 * Check if the relative list index is in the loaded page.
	 * 
	 * @param index The index in the list.
	 * @return A boolean.
	 */
	private boolean inPage(int index) {

		// Page is empty, not in.
		if (page.isEmpty()) {
			return false;
		}

		// First persistor index in the page is greater than persistor index.
		if (page.getFirst().getIndex() > getIndex(index)) {
			return false;
		}

		// Last persistor index in the page is less than persistor index.
		if (page.getLast().getIndex() < getIndex(index)) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the data in the page given the list index, knowing that data is in the page.
	 * 
	 * @param index The list index.
	 * @return The data.
	 */
	private Data getDataFromPage(int index) {
		int pageIndex = getIndex(index) - page.getFirst().getIndex();
		return page.get(pageIndex).getData();
	}

	/**
	 * Returns the persistor index given the relative index in the list that starts at 0.
	 * 
	 * @param index The index in the list.
	 * @return The persistor index.
	 * @throws PersistorException
	 */
	private int getIndex(int index) {
		return getFirstIndex() - index;
	}

	/**
	 * Retrieves and returns the first index in the persistor.
	 * 
	 * @return The first index.
	 * @throws PersistorException
	 */
	private int getFirstIndex() {
		if (firstIndex == -1) {
			RecordIterator iter = null;
			try {
				Order order = new Order();
				order.add(persistor.getField(0), true);
				iter = persistor.iterator(null, order);
				if (iter.hasNext()) {
					Record record = iter.next();
					firstIndex = record.getValue(0).getInteger();
				}
			} catch (PersistorException exc) {
				logger.catching(exc);
			} finally {
				close(iter);
			}
		}
		return firstIndex;
	}

	/**
	 * Retrieves and returns the last index in the persistor.
	 * 
	 * @return The last index.
	 * @throws PersistorException
	 */
	private int getLastIndex() {
		int lastIndex = 0;
		RecordIterator iter = null;
		try {
			Order order = new Order();
			order.add(persistor.getField(0), false);
			iter = persistor.iterator(null, order);
			if (iter.hasNext()) {
				Record record = iter.next();
				lastIndex = record.getValue(0).getInteger();
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		} finally {
			close(iter);
		}
		return lastIndex;
	}

	/**
	 * Close the iterator.
	 * 
	 * @param iter The record iterator.
	 */
	private void close(RecordIterator iter) {
		try {
			if (iter != null) {
				iter.close();
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		}
	}

	/**
	 * Load a page that should contain the list index.
	 * 
	 * @param index The list index to be cached.
	 */
	private void loadPage(int index) {

		// First persistor index to scan.
		int persistorIndex = getIndex(index) - (pageSize / 2);
		if (persistorIndex < 0) {
			persistorIndex = 0;
		}

		Criteria criteria = new Criteria();
		criteria.add(Condition.fieldGE(persistor.getField(0), new Value(persistorIndex)));

		Order order = new Order();
		order.add(persistor.getField(0), true);

		RecordIterator iter = null;
		try {
			iter = persistor.iterator(criteria, order);
			page.clear();
			int size = 0;
			while (iter.hasNext()) {
				if (++size > pageSize) {
					break;
				}
				Record record = iter.next();
				page.addLast(getDataItem(record));
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		} finally {
			close(iter);
		}
	}

	/**
	 * Returns the data item given a record of the persistor.
	 * 
	 * @param record
	 * @return The data item.
	 */
	private DataItem getDataItem(Record record) {
		int index = record.getValue(0).getInteger();
		long time = record.getValue(1).getLong();
		List<Double> values = new ArrayList<>();
		for (int i = 2; i < record.getFieldCount(); i++) {
			if (record.getField(i).isPersistent()) {
				values.add(record.getValue(i).getDouble());
			}
		}
		Data data = new Data();
		data.setTime(time);
		data.setValues(ListUtils.toArray(values));
		return new DataItem(index, data);
	}
}
