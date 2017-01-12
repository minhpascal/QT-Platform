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

/**
 * Interface that should implement back end persistence engines that operate with records.
 * 
 * @author Miquel Sas
 */
public interface Persistor {

	/**
	 * Returns the underlying view of the persistor.
	 * 
	 * @return The view.
	 */
	View getView();

	/**
	 * Returns the default record of the underlying view.
	 * 
	 * @return The default record of the underlying view.
	 */
	Record getDefaultRecord();

	/**
	 * Returns the number of fields this persistor manages.
	 * 
	 * @return The number of fields.
	 */
	int getFieldCount();

	/**
	 * Returns a field by index.
	 * 
	 * @param index The field index.
	 * @return The field or null.
	 */
	Field getField(int index);

	/**
	 * Returns a field by alias.
	 * 
	 * @param alias The field alias.
	 * @return The field or null.
	 */
	Field getField(String alias);

	/**
	 * Count the number of records that agree with the criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @return The number of records that agree with the criteria.
	 * @throws PersistorException
	 */
	long count(Criteria criteria) throws PersistorException;

	/**
	 * Deletes records based on a selection criteria.
	 * 
	 * @param criteria The criteria to select the entities to delete.
	 * @return The number of deleted records.
	 * @throws PersistorException
	 */
	int delete(Criteria criteria) throws PersistorException;

	/**
	 * Delete a record.
	 * 
	 * @param record The record to delete.
	 * @return The number deleted records (one or zero).
	 * @throws PersistorException
	 */
	int delete(Record record) throws PersistorException;

	/**
	 * Check if the record exists within its persistent layer.
	 * 
	 * @param record The record.
	 * @return A boolean stating if the record exists within its persistent layer.
	 * @throws PersistorException
	 */
	boolean exists(Record record) throws PersistorException;

	/**
	 * Insert a record.
	 * 
	 * @param record The record to insert.
	 * @return The number of already inserted records (one or zero).
	 * @throws PersistorException
	 */
	int insert(Record record) throws PersistorException;

	/**
	 * Returns a record iterator to scan the records that agree with the criteria.
	 * 
	 * @param criteria Filter criteria.
	 * @return The record iterator.
	 * @throws PersistorException
	 */
	RecordIterator iterator(Criteria criteria) throws PersistorException;

	/**
	 * Returns a record iterator to scan the records that agree with the criteria, in the given order.
	 * 
	 * @param criteria Filter criteria.
	 * @param order Order.
	 * @return The record iterator.
	 * @throws PersistorException
	 */
	RecordIterator iterator(Criteria criteria, Order order) throws PersistorException;

	/**
	 * Returns the maximum values of the argument field list with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The maximum values.
	 * @throws PersistorException
	 */
	ValueMap max(Criteria criteria, int... indexes) throws PersistorException;

	/**
	 * Returns the maximum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field alias.
	 * @return The maximum values.
	 * @throws PersistorException
	 */
	ValueMap max(Criteria criteria, String... aliases) throws PersistorException;

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The field indexes.
	 * @return The minimum values.
	 * @throws PersistorException
	 */
	ValueMap min(Criteria criteria, int... indexes) throws PersistorException;

	/**
	 * Returns the minimum values of the argument field with the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The field aliases.
	 * @return The minimum values.
	 * @throws PersistorException
	 */
	ValueMap min(Criteria criteria, String... aliases) throws PersistorException;

	/**
	 * Saves the record, inserting if it does not exists and updating if it does.
	 * 
	 * @param record The record to save.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException
	 */
	int save(Record record) throws PersistorException;

	/**
	 * Select a list of records based on a selection criteria.
	 *
	 * @param criteria The selection criteria.
	 * @return The list of records.
	 * @throws PersistorException
	 */
	RecordSet select(Criteria criteria) throws PersistorException;

	/**
	 * Select a list of records based on a selection criteria, returning the list with the given order.
	 *
	 * @param criteria The selection criteria.
	 * @param order The selection order.
	 * @return The list of records.
	 * @throws PersistorException
	 */
	RecordSet select(Criteria criteria, Order order) throws PersistorException;

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param indexes The list of indexes.
	 * @return The list of values in a value map keyed by index.
	 * @throws PersistorException
	 */
	ValueMap sum(Criteria criteria, int... indexes) throws PersistorException;

	/**
	 * Returns the list of values that are the sum of the numeric argument fields applying the filter criteria.
	 * 
	 * @param criteria The filter criteria.
	 * @param aliases The list of field aliases.
	 * @return The list of values in a value map keyed by alias.
	 * @throws PersistorException
	 */
	ValueMap sum(Criteria criteria, String... aliases) throws PersistorException;

	/**
	 * Update a record.
	 * 
	 * @param record The record to update.
	 * @return The number of updated records (one or zero).
	 * @throws PersistorException
	 */
	int update(Record record) throws PersistorException;

	/**
	 * Update a set of fields with given values for the firlter criteria. The map of values can be keyed either by
	 * index, alias or field.
	 * 
	 * @param criteria The filter criteria.
	 * @param map The map of field-values.
	 * @return The number of updated records.
	 * @throws PersistorException
	 */
	int update(Criteria criteria, ValueMap map) throws PersistorException;
}
