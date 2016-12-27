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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A RecordSet packs a list of records.
 *
 * @author Miquel Sas
 */
public class RecordSet implements Iterable<Record> {

	/**
	 * The list of records.
	 */
	private List<Record> records = new ArrayList<>();
	/**
	 * The list of fields.
	 */
	private FieldList fields;
	/**
	 * The list of order by key pointers.
	 */
	private List<KeyPointer> orderByKeyPointers;

	/**
	 * Default constructor.
	 */
	public RecordSet() {
		super();
	}

	/**
	 * Constructor assigning the list of fields.
	 * 
	 * @param fields The list of fields.
	 */
	public RecordSet(FieldList fields) {
		super();
		setFieldList(fields);
	}

	/**
	 * Returns the list of order by key pointers.
	 *
	 * @return The list of order by key pointers.
	 */
	public List<KeyPointer> getOrderByKeyPointers() {
		if (orderByKeyPointers == null) {
			orderByKeyPointers = getFieldList().getPrimaryKeyPointers();
		}
		return orderByKeyPointers;
	}

	/**
	 * Sets the list of order by key pointers.
	 *
	 * @param orderByKeyPointers The list of order by key pointers.
	 */
	public void setOrderByKeyPointers(List<KeyPointer> orderByKeyPointers) {
		this.orderByKeyPointers = orderByKeyPointers;
	}

	/**
	 * Sets the field list.
	 *
	 * @param fields The field list.
	 */
	public void setFieldList(FieldList fields) {
		this.fields = fields;
	}

	/**
	 * Returns a copy of this record set.
	 * 
	 * @return A copy.
	 */
	public RecordSet getCopy() {
		RecordSet recordSet = new RecordSet();
		recordSet.setFieldList(getFieldList());
		recordSet.setOrderByKeyPointers(getOrderByKeyPointers());
		recordSet.records.addAll(records);
		return recordSet;
	}

	/**
	 * Returns the number of fields.
	 *
	 * @return The number of fields.
	 */
	public int getFieldCount() {
		return fields.size();
	}

	/**
	 * Returns the fields, for use in the friend class Cursor.
	 *
	 * @return The field list.
	 */
	public FieldList getFieldList() {
		return fields;
	}

	/**
	 * Get the field at the given index.
	 *
	 * @param index The index of the field.
	 * @return The field.
	 */
	public Field getField(int index) {
		return fields.getField(index);
	}

	/**
	 * Get a field by alias.
	 *
	 * @param alias The field alias.
	 * @return The field or null if not found.
	 */
	public Field getField(String alias) {
		return fields.getField(alias);
	}

	/**
	 * Clear the list of records.
	 */
	public void clear() {
		records.clear();
	}

	/**
	 * Returns this record set size.
	 *
	 * @return The size.
	 */
	public int size() {
		return records.size();
	}

	/**
	 * Add a record to the list.
	 *
	 * @param record The record to add
	 * @return A boolean indicating if the record has been added.
	 */
	public boolean add(Record record) {
		if (record.getFieldList() == null) {
			record.setFieldList(fields);
		}
		if (fields == null) {
			fields = record.getFieldList();
		}
		return records.add(record);
	}

	/**
	 * Inserts a record at a given index.
	 *
	 * @param index The index.
	 * @param record The record to insert.
	 */
	public void add(int index, Record record) {
		if (record.getFieldList() == null) {
			record.setFieldList(fields);
		}
		if (fields == null) {
			fields = record.getFieldList();
		}
		records.add(index, record);
	}

	/**
	 * Gets the insert index using the order key.
	 *
	 * @param record The record.
	 * @return The insert index.
	 */
	public int getInsertIndex(Record record) {
		int index;
		OrderKey key = record.getIndexKey(getOrderByKeyPointers());
		for (index = 0; index < records.size(); index++) {
			Record scanRecord = records.get(index);
			OrderKey scanKey = scanRecord.getIndexKey(getOrderByKeyPointers());
			if (key.compareTo(scanKey) <= 0) {
				break;
			}
		}
		return index;
	}

	/**
	 * Get a record given its index in the record list.
	 *
	 * @return The Record.
	 * @param index The index in the record list.
	 */
	public Record get(int index) {
		if (index < 0 || index >= size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return records.get(index);
	}

	/**
	 * Find the index of the given record.
	 *
	 * @param record The record to find its index.
	 * @return The index of the given record.
	 */
	public int indexOf(Record record) {
		return records.indexOf(record);
	}

	/**
	 * @return If the record set is empty.
	 * @see java.util.ArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return records.isEmpty();
	}

	/**
	 * Remove a record given its index in the list.
	 *
	 * @return The removed record.
	 * @param index The index in the list of records.
	 */
	public Record remove(int index) {
		if (index < 0 || index >= size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		Record record = get(index);
		records.remove(index);
		return record;
	}

	/**
	 * Sets a record given its index in the record list.
	 *
	 * @param index The index in the record list.
	 * @param record The record.
	 */
	public void set(int index, Record record) {
		records.set(index, record);
	}

	/**
	 * Sort this list of records based on the order by key pointers, or in its default the primary key pointers.
	 */
	public void sort() {
		if (size() == 0) {
			return;
		}
		sort(getOrderByKeyPointers());
	}

	/**
	 * Sort this list of records based on the order based on a list of key pointers.
	 *
	 * @param keyPointers The list of key pointers
	 */
	public void sort(List<KeyPointer> keyPointers) {
		sort(new RecordComparator(keyPointers));
	}

	/**
	 * Sort this list of records based on the order stated by the argument order.
	 *
	 * @param order The <code>Order</code> to use in the sort.
	 */
	public void sort(Order order) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getFieldList().getFields());
		sort(order.getKeyPointers(fields));
	}

	/**
	 * Sort this list of records based on a comparator.
	 *
	 * @param comparator
	 */
	public void sort(Comparator<Record> comparator) {
		Record[] recordArray = toArray();
		Arrays.sort(recordArray, comparator);
		records.clear();
		for (Record record : recordArray) {
			records.add((Record) record);
		}
	}

	/**
	 * Returns an array containing all the records.
	 *
	 * @return The array of records.
	 */
	public Record[] toArray() {
		return records.toArray(new Record[records.size()]);
	}

	/**
	 * Returns the record iterator.
	 */
	public Iterator<Record> iterator() {
		return records.iterator();
	}

	/**
	 * Returns a recordset based on this recordset that meets the argument criteria.
	 * 
	 * @param criteria The criteria to meet.
	 * @return The result recordset.
	 */
	public RecordSet getRecordSet(Criteria criteria) {
		RecordSet recordSet = new RecordSet(fields);
		for (Record record : records) {
			if (criteria.check(record)) {
				recordSet.add(record);
			}
		}
		return recordSet;
	}
}
