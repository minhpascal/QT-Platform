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

package com.qtplaf.library.trading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.info.DataInfo;

/**
 * A data list that retireves its data persistor. The contract of a persistor of data lists is that fields must be
 * defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory.</li>
 * <li>The second field is a long, the time of the ohlcv data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class PersistorDataList extends DataList {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * The underlying persistor.
	 */
	private Persistor persistor;
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
	 * Returns the number of elements in this list.
	 *
	 * @return The number of elements in this list.
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Retrieves and returns the first index in the persistor.
	 * 
	 * @return The first index.
	 * @throws PersistorException
	 */
	private int getFirstIndex() throws PersistorException {
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
			} finally {
				if (iter != null) {
					iter.close();
				}
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
	private int getLastIndex() throws PersistorException {
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
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
		return lastIndex;
	}
}
