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
package com.qtplaf.library.util.list;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * ArrayList utility functions.
 *
 * @author Miquel Sas
 */
public class ListUtils {

	/**
	 * Private constructor. No instances have sense.
	 */
	private ListUtils() {
	}

	/**
	 * Returns the array of double values given the list.
	 * 
	 * @param list The list of doubles.
	 * @return The array.
	 */
	public static double[] toArray(List<Double> list) {
		double[] values = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		return values;
	}

	/**
	 * Returns a list given the argument array.
	 * 
	 * @param array The array.
	 * @return The list.
	 */
	public static List<Integer> asList(int... array) {
		List<Integer> list = new ArrayList<>();
		for (int element : array) {
			list.add(element);
		}
		return list;
	}

	/**
	 * Returns a list given the argument array.
	 * 
	 * @param array The array.
	 * @return The list.
	 */
	@SafeVarargs
	public static <T> List<T> asList(T... array) {
		List<T> list = new ArrayList<>();
		for (T element : array) {
			list.add(element);
		}
		return list;
	}

	/**
	 * Add the element to the first position of a list.
	 *
	 * @param e The element to add.
	 * @param list The list.
	 */
	public static <T> void addFirst(T e, List<T> list) {
		list.add(0, e);
	}

	/**
	 * Add the element to the last position of a list.
	 *
	 * @param e The element to add.
	 * @param list The list.
	 */
	public static <T> void addLast(T e, List<T> list) {
		list.add(e);
	}

	/**
	 * Returns the first element of a list.
	 * 
	 * @param list The list.
	 * @return The first element.
	 */
	public static <T> T getFirst(List<T> list) {
		return list.get(0);
	}

	/**
	 * Returns the last element of a list.
	 * 
	 * @param list The list.
	 * @return The last element.
	 */
	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}

	/**
	 * Remove the first element in the list.
	 * 
	 * @param list The list.
	 * @return The removed element.
	 */
	public static <T> T removeFirst(List<T> list) {
		return list.remove(0);
	}

	/**
	 * Remove the last element in the list.
	 * 
	 * @param list The list.
	 * @return The removed element.
	 */
	public static <T> T removeLast(List<T> list) {
		return list.remove(list.size() - 1);
	}

	/**
	 * Check whether two lists are equal.
	 * 
	 * @param l1 List 1.
	 * @param l2 List 2.
	 * @return A boolean.
	 */
	public static boolean equals(List<?> l1, List<?> l2) {
		if (l1.size() != l2.size()) {
			return false;
		}
		for (int i = 0; i < l1.size(); i++) {
			if (l1.get(i) == null && l2.get(i) != null) {
				return false;
			}
			if (l1.get(i) != null && l2.get(i) == null) {
				return false;
			}
			if (l1.get(i) == null && l2.get(i) == null) {
				continue;
			}
			if (!l1.get(i).equals(l2.get(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares an array list of comparable objects to the argument object. Returns a negative integer, zero, or a
	 * positive integer as this list is less than, equal to, or greater than the specified argument list. Throws an
	 * UnsupportedOperationException if the argument is not an
	 *
	 * @param arrayList The source array list of comparable objects.
	 * @param o The object to compare.
	 * @return The comparison integer.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int compareTo(ArrayList arrayList, Object o) {
		ArrayList<Comparable> comparable = null;
		try {
			comparable = ((ArrayList) o);
		} catch (ClassCastException exc) {
			throw new UnsupportedOperationException(
				MessageFormat.format("Not comparable type: {0}", o.getClass().getName()));
		}
		if (arrayList.isEmpty() && comparable.size() > 0) {
			return -1;
		}
		if (arrayList.size() > 0 && comparable.isEmpty()) {
			return 1;
		}
		for (int i = 0; i < arrayList.size(); i++) {
			Comparable c1 = (Comparable) arrayList.get(i);
			Comparable c2 = (Comparable) comparable.get(i);
			int compare = c1.compareTo(c2);
			if (compare == 0) {
				if (i == arrayList.size() - 1 && i < comparable.size() - 1) {
					return -1;
				}
				if (i < arrayList.size() - 1 && i == comparable.size() - 1) {
					return 1;
				}
				continue;
			}
			return compare;
		}
		return 0;
	}

	/**
	 * Shuffles an origin list of data to process each iteration over all the origin data in a different order,
	 * preserving the origin data order in the origin list.
	 * 
	 * @param originData The origin learning data.
	 * @return The shuffled learning data.
	 */
	public static <T> List<T> shuffle(List<T> originData) {
		List<T> toShuffle = new ArrayList<>();
		toShuffle.addAll(originData);
		Random random = new Random();
		List<T> shuffled = new ArrayList<>();
		while (!toShuffle.isEmpty()) {
			int index = random.nextInt(toShuffle.size());
			T data = toShuffle.remove(index);
			shuffled.add(data);
		}
		return shuffled;
	}

	@SuppressWarnings("rawtypes")
	public static void sort(List list) {
		sort(list, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void sort(List list, Comparator comparator) {
		Object[] objects = list.toArray(new Object[list.size()]);
		list.clear();
		if (comparator != null) {
			Arrays.sort(objects, comparator);
		} else {
			Arrays.sort(objects);
		}
		for (Object object : objects) {
			list.add(object);
		}
	}
}
