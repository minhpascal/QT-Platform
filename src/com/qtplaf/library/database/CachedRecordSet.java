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

package com.qtplaf.library.database;

import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

/**
 * A <tt>CachedRecordSet</tt> is a <tt>RecordSet</tt> that does not retrieve all the records from the underlinying
 * <tt>Persistor</tt> and <tt>Criteria</tt>. It is aimed to be used when the underlying view is very huge. Note that
 * they can not be greater that <tt>Integer.MAX_VALUE</tt>.
 * <p>
 * Additionally, this <tt>CachedRecordSet</tt> can not be sorted since it strictly uses the underlying order of the
 * persistor.
 * 
 * @author Miquel Sas
 */
public class CachedRecordSet extends RecordSet {

	/** Logger instance to log exceptions. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * An order key that stores the index of the record in the view.
	 */
	class Key extends OrderKey {
		private int index;

		Key(int index, OrderKey orderKey) {
			super();
			this.index = index;
			for (int i = 0; i < orderKey.size(); i++) {
				OrderKey.Segment segment = orderKey.get(i);
				add(segment.getValue(), segment.isAsc());
			}
		}
	}

	/**
	 * The persistor that will provide the rows.
	 */
	private Persistor persistor;
	/**
	 * The criteria used to retrieve the rows.
	 */
	private Criteria criteria;
	/**
	 * The stack of order keys.
	 */
	private Delist<Key> keys = new ArrayDelist<>();
	/**
	 * The size of the recordset.
	 */
	private int size = -1;
	/**
	 * The page size, default is 100.
	 */
	private int pageSize = 100;

	/**
	 * The underlying index in the view of the first record in the loaded list.
	 */
	private int firstIndex = 0;
	/**
	 * The underlying index in the view of the last record in the loaded list.
	 */
	private int lastIndex = 0;

	/**
	 * Constructor.
	 */
	public CachedRecordSet() {
		super();
	}

	/**
	 * Constructor assigning the field list.
	 * 
	 * @param fields
	 */
	public CachedRecordSet(FieldList fields) {
		super(fields);
	}

	/**
	 * Set the persistor.
	 * 
	 * @param persistor The persistor.
	 */
	public void setPersistor(Persistor persistor) {
		this.persistor = persistor;
	}

	/**
	 * Sets the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Set the page size, default is 100.
	 * 
	 * @param pageSize The page size.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	public int size() {
		if (size < 0) {
			try {
				size = (int) persistor.count(criteria);
			} catch (PersistorException exc) {
				logger.catching(exc);
			}
		}
		return 0;
	}

	/**
	 * Returns the view order.
	 * 
	 * @return The view order.
	 */
	private Order getOrder() {
		return persistor.getView().getOrderBy();
	}

	/**
	 * Returns the additional criteria to retrieve records GE the key.
	 * 
	 * @param key The order key.
	 * @return The additional criteria to retrieve records GE the key.
	 */
	private Criteria getCriteriaGE(OrderKey key) {
		if (getOrder().size() != key.size()) {
			throw new IllegalArgumentException();
		}
		Criteria criteria = new Criteria();
		for (int i = 0; i < key.size(); i++) {
			Field field = getOrder().get(i).getField();
			Value value = key.get(i).getValue();
			criteria.add(Condition.fieldGE(field, value));
		}
		return criteria;
	}

	/**
	 * Not supported.
	 */
	@Override
	public void setOrderByKeyPointers(List<KeyPointer> orderByKeyPointers) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(List<KeyPointer> keyPointers) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Order order) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported.
	 */
	@Override
	public void sort(Comparator<Record> comparator) {
		throw new UnsupportedOperationException();
	}

}
