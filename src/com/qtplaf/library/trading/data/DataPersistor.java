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

import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Order;
import com.qtplaf.library.database.OrderKey;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.PersistorDDL;
import com.qtplaf.library.database.PersistorException;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordIterator;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.database.ValueMap;
import com.qtplaf.library.database.View;
import com.qtplaf.library.util.list.ListUtils;

/**
 * A persistor for elements of timed <tt>Data</tt>. The general contract for a persistor of timed <tt>Data</tt> is that
 * fields must be defined as follows:
 * <ul>
 * <li>The first field is always the index, an auto-increment field starting preferably at 1, but not mandatory to start
 * at 1, not negative, increased by 1, and indexed.</li>
 * <li>The second field is a long, the time of the timed data.</li>
 * <li>All subsequent <b>persistent</b> fields are of type double and are considered data.</li>
 * </ul>
 * 
 * @author Miquel Sas
 */
public class DataPersistor implements Persistor {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * The uderlying persistor.
	 */
	private Persistor persistor;
	/**
	 * First index in the underlying table.
	 */
	private Long firstIndex = Long.valueOf(-1);

	/**
	 * Constructor.
	 * 
	 * @param persistor The underlying persistor.
	 */
	public DataPersistor(Persistor persistor) {
		super();
		validate(persistor);
		this.persistor = persistor;
	}

	/**
	 * Validates that the argument persistor conforms to the general contract of <tt>Data</tt> persistors.
	 * 
	 * @param persistor The persistor to validate.
	 */
	private void validate(Persistor persistor) {

		// First field must be of type <tt>AutoIncrement</tt>.
		if (!persistor.getField(0).isAutoIncrement()) {
			throw new IllegalArgumentException();
		}

		// The second fied must be of type <tt>Long</tt>.
		if (!persistor.getField(1).isLong()) {
			throw new IllegalArgumentException();
		}

		// Any othe persisten field must be of type <tt>Double</tt>.
		for (int i = 2; i < persistor.getFieldCount(); i++) {
			if (persistor.getField(i).isPersistent()) {
				if (!persistor.getField(i).isDouble()) {
					throw new IllegalArgumentException();
				}
			}
		}
	}

	/**
	 * Returns the <tt>Data</tt> in the record.
	 * 
	 * @param record The source record.
	 * @return The <tt>Data</tt>.
	 */
	public Data getData(Record record) {
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
		return data;
	}

	/**
	 * Returns the underlying index in the record.
	 * 
	 * @param record The source record.
	 * @return The index.
	 */
	public Long getIndex(Record record) {
		return record.getValue(0).getLong();
	}

	/**
	 * Returns the persistor index given the relative index in that starts at 0.
	 * 
	 * @param index The index in the list.
	 * @return The persistor index.
	 * @throws PersistorException
	 */
	public Long getIndex(Long index) {
		return getFirstIndex() + index;
	}

	/**
	 * Retrieves and returns the first index in the persistor.
	 * 
	 * @return The first index.
	 */
	public Long getFirstIndex() {
		if (firstIndex == -1) {
			RecordIterator iter = null;
			try {
				iter = persistor.iterator(null, getIndexOrder(true));
				if (iter.hasNext()) {
					Record record = iter.next();
					firstIndex = getIndex(record);
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
	public Long getLastIndex() {
		Long lastIndex = Long.valueOf(0);
		RecordIterator iter = null;
		try {
			iter = persistor.iterator(null, getIndexOrder(false));
			if (iter.hasNext()) {
				Record record = iter.next();
				lastIndex = getIndex(record);
			}
		} catch (PersistorException exc) {
			logger.catching(exc);
		} finally {
			close(iter);
		}
		return lastIndex;
	}

	/**
	 * Returns the size or number of record in the persistor.
	 * 
	 * @return The size.
	 */
	public Long size() {
		long first = getFirstIndex();
		long last = getLastIndex();
		return (last - first + 1);
	}

	/**
	 * Returns the order on the index field.
	 * 
	 * @param asc A boolean that indicates ascending/descending order.
	 * @return The order.
	 */
	public Order getIndexOrder(boolean asc) {
		Order order = new Order();
		order.add(persistor.getField(0), asc);
		return order;
	}

	/**
	 * Check if the table is empty.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns a suitable DDL.
	 * 
	 * @return The DDL.
	 */
	public PersistorDDL getDDL() {
		return persistor.getDDL();
	}

	/**
	 * Returns the underlying view of the persistor.
	 * 
	 * @return The view.
	 */
	public View getView() {
		return persistor.getView();
	}

	/**
	 * Returns the default record of the underlying view.
	 * 
	 * @return The default record of the underlying view.
	 */
	public Record getDefaultRecord() {
		return persistor.getDefaultRecord();
	}

	/**
	 * Returns the record given the primary key.
	 * 
	 * @param primaryKey The primary key.
	 * @return The record or null.
	 */
	public Record getRecord(OrderKey primaryKey) throws PersistorException {
		return persistor.getRecord(primaryKey);
	}

	/**
	 * Returns the record given the list of primnary key values.
	 * 
	 * @param primaryKeyValues The list of primnary key values.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public Record getRecord(List<Value> primaryKeyValues) throws PersistorException {
		return persistor.getRecord(primaryKeyValues);
	}

	/**
	 * Returns the record given the list of primnary key values.
	 * 
	 * @param primaryKeyValues The list of primnary key values.
	 * @return The record or null.
	 * @throws PersistorException
	 */
	public Record getRecord(Value... primaryKeyValues) throws PersistorException {
		return persistor.getRecord(primaryKeyValues);
	}

	/**
	 * Returns the number of fields this persistor manages.
	 * 
	 * @return The number of fields.
	 */
	public int getFieldCount() {
		return persistor.getFieldCount();
	}

	/**
	 * Returns a field by index.
	 * 
	 * @param index The field index.
	 * @return The field or null.
	 */
	public Field getField(int index) {
		return persistor.getField(index);
	}

	/**
	 * Returns a field by alias.
	 * 
	 * @param alias The field alias.
	 * @return The field or null.
	 */
	public Field getField(String alias) {
		return persistor.getField(alias);
	}

	/**
	 * Count the number of records that agree with the criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @return The number of records that agree with the criteria.
	 * @throws PersistorException
	 */
	public long count(Criteria criteria) throws PersistorException {
		return persistor.count(criteria);
	}

	/**
	 * Deletes records based on a selection criteria.
	 * 
	 * @param criteria The criteria to select the entities to delete.
	 * @return The number of deleted records.
	 * @throws PersistorException
	 */
	public int delete(Criteria criteria) throws PersistorException {
		return persistor.delete(criteria);
	}

	/**
	 * Delete a record.
	 * 
	 * @param record The record to delete.
	 * @return The number deleted records (one or zero).
	 * @throws PersistorException
	 */
	public int delete(Record record) throws PersistorException {
		return persistor.delete(record);
	}

	/**
	 * Check if the record exists within its persistent layer.
	 * 
	 * @param record The record.
	 * @return A boolean stating if the record exists within its persistent layer.
	 * @throws PersistorException
	 */
	public boolean exists(Record record) throws PersistorException {
		return persistor.exists(record);
	}

	/**
	 * Returns true if the record has successfully refreshed.
	 * 
	 * @param record The source record that must have set at least the primary key
	 * @return A boolean indicating whether the record has successfully refreshed.
	 * @throws PersistorException
	 */
	public boolean refresh(Record record) throws PersistorException {
		return persistor.refresh(record);
	}

	/**
	 * Insert a record.
	 * 
	 * @param record The record to insert.
	 * @return The number of already inserted records (one or zero).
	 * @throws PersistorException
	 */
	public int insert(Record record) throws PersistorException {
		return persistor.insert(record);
	}

	/**
	 * Returns a record iterator to scan the records that agree with the criteria.
	 * 
	 * @param criteria Filter criteria.
	 * @return The record iterator.
	 * @throws PersistorException
	 */
	public RecordIterator iterator(Criteria criteria) throws PersistorException {
		return persistor.iterator(criteria);
	}

	/**
	 * Returns a record iterator to scan the records that agree with the criteria, in the given order.
	 * 
	 * @param criteria Filter criteria.
	 * @param order Order.
	 * @return The record iterator.
	 * @throws PersistorException
	 */
	public RecordIterator iterator(Criteria criteria, Order order) throws PersistorException {
		return persistor.iterator(criteria, order);
	}

	/**
	 * Returns the maximum values of the argument field list with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The maximum values.
	 * @throws PersistorException
	 */
	public ValueMap max(Criteria criteria, int... indexes) throws PersistorException {
		return persistor.max(criteria, indexes);
	}

	/**
	 * Returns the maximum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field alias.
	 * @return The maximum values.
	 * @throws PersistorException
	 */
	public ValueMap max(Criteria criteria, String... aliases) throws PersistorException {
		return persistor.max(criteria, aliases);
	}

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The minimum values.
	 * @throws PersistorException
	 */
	public ValueMap min(Criteria criteria, int... indexes) throws PersistorException {
		return persistor.min(criteria, indexes);
	}

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field aliases.
	 * @return The minimum values.
	 * @throws PersistorException
	 */
	public ValueMap min(Criteria criteria, String... aliases) throws PersistorException {
		return persistor.min(criteria, aliases);
	}

	/**
	 * Saves the record, inserting if it does not exists and updating if it does.
	 * 
	 * @param record The record to save.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException
	 */
	public int save(Record record) throws PersistorException {
		return persistor.save(record);
	}

	/**
	 * Select a list of records based on a selection criteria.
	 *
	 * @param criteria The selection criteria.
	 * @return The list of records.
	 * @throws PersistorException
	 */
	public RecordSet select(Criteria criteria) throws PersistorException {
		return persistor.select(criteria);
	}

	/**
	 * Select a list of records based on a selection criteria, returning the list with the given order.
	 *
	 * @param criteria The selection criteria.
	 * @param order The selection order.
	 * @return The list of records.
	 * @throws PersistorException
	 */
	public RecordSet select(Criteria criteria, Order order) throws PersistorException {
		return persistor.select(criteria, order);
	}

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The list of indexes.
	 * @return The list of values in a value map keyed by index.
	 * @throws PersistorException
	 */
	public ValueMap sum(Criteria criteria, int... indexes) throws PersistorException {
		return persistor.sum(criteria, indexes);
	}

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The list of field aliases.
	 * @return The list of values in a value map keyed by alias.
	 * @throws PersistorException
	 */
	public ValueMap sum(Criteria criteria, String... aliases) throws PersistorException {
		return persistor.sum(criteria, aliases);
	}

	/**
	 * Update a record.
	 * 
	 * @param record The record to update.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException
	 */
	public int update(Record record) throws PersistorException {
		return persistor.update(record);
	}

	/**
	 * Update a set of fields with given values for the firlter criteria. The map of values can be keyed either by
	 * index, alias or field.
	 * 
	 * @param criteria The filter criteria.
	 * @param map The map of field-values.
	 * @return The number of updated records.
	 * @throws PersistorException
	 */
	public int update(Criteria criteria, ValueMap map) throws PersistorException {
		return persistor.update(criteria, map);
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
}
