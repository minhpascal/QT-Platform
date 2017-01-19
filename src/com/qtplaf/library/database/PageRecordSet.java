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
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.database.Condition.Operator;
import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

/**
 * A <tt>PageRecordSet</tt> is a <tt>RecordSet</tt> that retrieves records per pages from the underlinying
 * <tt>Persistor</tt> and <tt>Criteria</tt>. It is aimed to be used when the underlying view is very huge. Note that
 * they can not be greater that <tt>Integer.MAX_VALUE</tt>.
 * <p>
 * Additionally, this <tt>PageRecordSet</tt> can not be sorted since it strictly uses the underlying order of the
 * persistor.
 * 
 * @author Miquel Sas
 */
public class PageRecordSet extends RecordSet {

	/** Logger instance to log exceptions. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Page information.
	 */
	private class Page {
		/** Index of first record. */
		private int firstIndex;
		/** Key of first record. */
		private OrderKey firstKey;
		/** Index of last record. */
		private int lastIndex;
		/** Key of last record. */
		private OrderKey lastKey;

		/**
		 * Constructor.
		 * 
		 * @param firstIndex First index.
		 * @param firstKey First key.
		 * @param lastIndex Last index.
		 * @param lastKey Last key.
		 */
		private Page(int firstIndex, OrderKey firstKey, int lastIndex, OrderKey lastKey) {
			super();
			this.firstIndex = firstIndex;
			this.firstKey = firstKey;
			this.lastIndex = lastIndex;
			this.lastKey = lastKey;
		}

		/**
		 * Returns the first index.
		 * 
		 * @return The first index.
		 */
		private int getFirstIndex() {
			return firstIndex;
		}

		/**
		 * Set the first index.
		 * 
		 * @param firstIndex The first index.
		 */
		private void setFirstIndex(int firstIndex) {
			this.firstIndex = firstIndex;
		}

		/**
		 * Returns the first key.
		 * 
		 * @return The first key.
		 */
		private OrderKey getFirstKey() {
			return firstKey;
		}

		/**
		 * Set the first key.
		 * 
		 * @param firstKey The first key.
		 */
		private void setFirstKey(OrderKey firstKey) {
			this.firstKey = firstKey;
		}

		/**
		 * Returns the last index.
		 * 
		 * @return The last index.
		 */
		private int getLastIndex() {
			return lastIndex;
		}

		/**
		 * Set the last index.
		 * 
		 * @param lastIndex The last index.
		 */
		private void setLastIndex(int lastIndex) {
			this.lastIndex = lastIndex;
		}

		/**
		 * Returns the last key.
		 * 
		 * @return The last key.
		 */
		private OrderKey getLastKey() {
			return lastKey;
		}

		/**
		 * Set the last key.
		 * 
		 * @param lastKey The last key.
		 */
		private void setLastKey(OrderKey lastKey) {
			this.lastKey = lastKey;
		}

		/**
		 * Check equal.
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof Page) {
				Page p = (Page) o;
				return getFirstIndex() == p.getFirstIndex();
			}
			return false;
		}

		/**
		 * Returns the string representation.
		 */
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("[");
			b.append(Integer.toString(firstIndex));
			b.append(", ");
			b.append(Integer.toString(lastIndex));
			b.append("]");
			b.append(", ");
			b.append("[");
			b.append(firstKey);
			b.append("]");
			b.append(", ");
			b.append("[");
			b.append(lastKey);
			b.append("]");
			return b.toString();
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
	 * The stack of pages. The page in the recordset will always be the last.
	 */
	private Delist<Page> pages = new ArrayDelist<>();
	/**
	 * The size of the recordset.
	 */
	private int size = -1;
	/**
	 * The page size, default is 100.
	 */
	private int pageSize = 100;

	/**
	 * Constructor.
	 */
	public PageRecordSet() {
		super();
	}

	/**
	 * Constructor assigning the field list.
	 * 
	 * @param fields
	 */
	public PageRecordSet(FieldList fields) {
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
	 * Get a record given its index in the record list.
	 *
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	@Override
	public Record get(int index) {

		try {

			// Current loaded page.
			Page page = pages.getLast();

			// First check if the required record is in the current loaded page.
			if (inPage(index, page)) {
				return get(index, page);
			}

			// Index is in previous pages.
			if (index < page.getFirstIndex()) {
				Iterator<Page> i = pages.descendingIterator();
				while (i.hasNext()) {
					page = i.next();
					if (inPage(index, page)) {
						loadPage(page);
						return get(index, page);
					}
				}
			}

			// Index is in next pages.
			if (index > page.getLastIndex()) {
				int pageIndex = pages.indexOf(page);
				while (pageIndex < pages.size()) {
					page = pages.get(pageIndex);
					if (inPage(index, page)) {
						loadPage(page);
						return get(index, page);
					}
					pageIndex++;
				}
				while (true) {
					loadNextPage(page);
					page = pages.getLast();
					if (inPage(index, page)) {
						return get(index, page);
					}
				}
			}

		} catch (PersistorException exc) {
			logger.catching(exc);
		}

		return null;
	}

	/**
	 * Check whether the index is in the page.
	 * 
	 * @param index The index.
	 * @param page The page.
	 * @return A boolean.
	 */
	private boolean inPage(int index, Page page) {
		return index >= page.getFirstIndex() && index <= page.getLastIndex();
	}

	/**
	 * Get the record at the view index.
	 * 
	 * @param index The index of the record in the view.
	 * @param page The current loaded page.
	 * @return The record.
	 */
	private Record get(int index, Page page) {
		if (!inPage(index, page)) {
			throw new IllegalArgumentException();
		}
		return super.get(index - page.getFirstIndex());
	}

	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	@Override
	public int size() {
		if (size < 0) {
			try {
				size = (int) persistor.count(criteria);
				loadFirstPage();
			} catch (PersistorException exc) {
				logger.catching(exc);
			}
		}
		return size;
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
	 * Load the page and leaves it as the last loaded page.
	 * 
	 * @param page The page to load.
	 * @throws PersistorException
	 */
	private void loadPage(Page page) throws PersistorException {
		RecordIterator iter = null;
		try {
			clear();

			int firstIndex = page.getFirstIndex();
			OrderKey firstKey = page.getFirstKey();
			int lastIndex = 0;
			OrderKey lastKey = null;

			Criteria criteria = new Criteria();
			if (this.criteria != null) {
				criteria.add(this.criteria);
			}
			criteria.add(getCriteria(firstKey, Operator.FIELD_GE));

			Order order = getOrder();
			Record record = null;

			iter = persistor.iterator(criteria, order);
			while (iter.hasNext()) {
				record = iter.next();
				add(record);
				if (super.size() >= pageSize) {
					break;
				}
			}
			if (record != null) {
				lastIndex += firstIndex + super.size() - 1;
				lastKey = record.getOrderKey(order);
			}
			page.setFirstIndex(firstIndex);
			page.setFirstKey(firstKey);
			page.setLastIndex(lastIndex);
			page.setLastKey(lastKey);

			// Remove subsequent pages.
			// while (!pages.getLast().equals(page)) {
			// pages.removeLast();
			// }

		} finally {
			if (iter != null) {
				iter.close();
			}
		}
	}

	/**
	 * Loads next page and sets it as the last page loaded.
	 * 
	 * @param page
	 * @throws PersistorException
	 */
	private void loadNextPage(Page page) throws PersistorException {
		RecordIterator iter = null;
		try {
			clear();

			int firstIndex = page.getLastIndex() + 1;
			OrderKey firstKey = null;
			int lastIndex = 0;
			OrderKey lastKey = null;

			Criteria criteria = new Criteria();
			if (this.criteria != null) {
				criteria.add(this.criteria);
			}
			criteria.add(getCriteria(page.getLastKey(), Operator.FIELD_GT));

			Order order = getOrder();
			Record record = null;
			boolean first = true;

			iter = persistor.iterator(criteria, order);
			while (iter.hasNext()) {
				record = iter.next();
				if (first) {
					firstKey = record.getOrderKey(order);
					first = false;
				}
				add(record);
				if (super.size() >= pageSize) {
					break;
				}
			}
			if (record != null) {
				lastIndex += firstIndex + super.size() - 1;
				lastKey = record.getOrderKey(order);
			}
			Page nextPage = new Page(firstIndex, firstKey, lastIndex, lastKey);
			// while (!pages.getLast().equals(page)) {
			// pages.removeLast();
			// }
			pages.addLast(nextPage);

		} finally {
			if (iter != null) {
				iter.close();
			}
		}
	}

	/**
	 * Load the first page.
	 * 
	 * @throws PersistorException
	 */
	private void loadFirstPage() throws PersistorException {
		RecordIterator iter = null;
		try {
			clear();
			pages.clear();

			int firstIndex = 0;
			OrderKey firstKey = null;
			int lastIndex = 0;
			OrderKey lastKey = null;

			Order order = getOrder();
			Record record = null;
			boolean first = true;

			iter = persistor.iterator(criteria, order);
			while (iter.hasNext()) {
				record = iter.next();
				if (first) {
					firstKey = record.getOrderKey(order);
					first = false;
				}
				add(record);
				if (super.size() >= pageSize) {
					break;
				}
			}
			if (record != null) {
				lastIndex += firstIndex + super.size() - 1;
				lastKey = record.getOrderKey(order);
			}
			Page page = new Page(firstIndex, firstKey, lastIndex, lastKey);
			pages.addLast(page);
		} finally {
			if (iter != null) {
				iter.close();
			}
		}
	}

	/**
	 * Returns the additional criteria to retrieve records GE/GT the key.
	 * 
	 * @param key The order key.
	 * @param operator The condition operator (FIELD_GE or FIELD_GT).
	 * @return The additional criteria to retrieve records GE/GT the key.
	 */
	private Criteria getCriteria(OrderKey key, Condition.Operator operator) {
		if (getOrder().size() != key.size()) {
			throw new IllegalArgumentException();
		}
		if (operator != Operator.FIELD_GE && operator != Operator.FIELD_GT) {
			throw new IllegalArgumentException();
		}
		Criteria criteria = new Criteria();
		for (int i = 0; i < key.size(); i++) {
			Field field = getOrder().get(i).getField();
			Value value = key.get(i).getValue();
			criteria.add(new Condition(field, operator, value));
		}
		return criteria;
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
