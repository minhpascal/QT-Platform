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

/**
 * A comparator useful to compare records and sort them within a record set or list.
 *
 * @author Miquel Sas
 */
public class RecordComparator implements Comparator<Record> {

	/**
	 * The key pointers.
	 */
	private final List<KeyPointer> keyPointers;

	/**
	 * Constructor assigning the list of key pointers.
	 * 
	 * @param keyPointers The list of key pointers.
	 */
	public RecordComparator(List<KeyPointer> keyPointers) {
		super();
		this.keyPointers = keyPointers;
	}

	/**
	 * Constructor using a master record and an order to get the key pointers.
	 * 
	 * @param masterRecord The master record.
	 * @param order The order.
	 */
	public RecordComparator(Record masterRecord, Order order) {
		this(order.getKeyPointers(masterRecord.getFieldList().getFields()));
	}

	/**
	 * Compares two records based on the order key.
	 *
	 * @param r1
	 * @param r2
	 * @return -1 if r1 is less than r2, 0 if equal, and 1 if greater than.
	 */
	@Override
	public int compare(Record r1, Record r2) {
		OrderKey k1 = r1.getIndexKey(keyPointers);
		OrderKey k2 = r2.getIndexKey(keyPointers);
		return k1.compareTo(k2);
	}

	/**
	 * Returns the corresponding order.
	 * 
	 * @param record The base record.
	 * @return An order that corresponds to the key pointers.
	 */
	public Order getOrder(Record record) {
		Order order = new Order();
		for (KeyPointer keyPointer : keyPointers) {
			order.add(record.getField(keyPointer.getIndex()), keyPointer.isAsc());
		}
		return order;
	}
}
