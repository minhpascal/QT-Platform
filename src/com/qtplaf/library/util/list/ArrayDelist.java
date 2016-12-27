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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Delist implementation, internally supported by an <tt>ArrayList</tt>.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 * 
 * @author Miquel Sas
 */
public class ArrayDelist<E> implements Delist<E> {

	/**
	 * Ascending iterator.
	 */
	private class AscendingIterator implements Iterator<E> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			return index < list.size();
		}

		@Override
		public E next() {
			return list.get(index++);
		}

		@Override
		public void remove() {
			list.remove(index - 1);
		}

	}

	/**
	 * Descending iterator.
	 */
	private class DescendingIterator implements Iterator<E> {

		private int index = list.size() - 1;

		@Override
		public boolean hasNext() {
			return index >= 0;
		}

		@Override
		public E next() {
			return list.get(index--);
		}

		@Override
		public void remove() {
			list.remove(index + 1);
		}

	}

	/**
	 * Internal list.
	 */
	private List<E> list;

	/**
	 * Default constructor.
	 */
	public ArrayDelist() {
		super();
		list = new ArrayList<>();
	}

	/**
	 * Constructor assigning the capacity.
	 * 
	 * @param capacity Initial capacity.
	 */
	public ArrayDelist(int capacity) {
		super();
		list = new ArrayList<>(capacity);
	}

	/**
	 * Returns the number of elements in this delist.
	 *
	 * @return The number of elements.
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Returns <tt>true</tt> if this delist contains no elements.
	 *
	 * @return <tt>true</tt> if this delist contains no elements
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this delist contains the specified element.
	 *
	 * @param o Element whose presence in this list is to be tested.
	 * @return <tt>true</tt> If this delist contains the specified element.
	 * @throws ClassCastException if the type of the specified element is incompatible with this list
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	public boolean contains(Object o) {
		return list.contains(o);
	}

	/**
	 * Removes all of the elements from this list (optional operation). The list will be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException if the <tt>clear</tt> operation is not supported by this list
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * Returns an iterator over the elements in this list in ascending order, from first to last.
	 *
	 * @return An iterator in ascending order.
	 */
	public Iterator<E> ascendingIterator() {
		return new AscendingIterator();
	}

	/**
	 * Returns an iterator over the elements in this list in descending order, from last to first.
	 *
	 * @return An iterator in descending order.
	 */
	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}

	/**
	 * Compares the specified object with this list for equality. Returns <tt>true</tt> if and only if the specified
	 * object is also a list, both lists have the same size, and all corresponding pairs of elements in the two lists
	 * are <i>equal</i>. (Two elements <tt>e1</tt> and <tt>e2</tt> are <i>equal</i> if <tt>(e1==null ? e2==null :
	 * e1.equals(e2))</tt>.) In other words, two lists are defined to be equal if they contain the same elements in the
	 * same order. This definition ensures that the equals method works properly across different implementations of the
	 * <tt>List</tt> interface.
	 *
	 * @param o the object to be compared for equality with this list
	 * @return <tt>true</tt> if the specified object is equal to this list
	 */
	public boolean equals(Object o) {
		return list.equals(o);
	}

	/**
	 * Returns the hash code value for this list. The hash code of a list is defined to be the result of the following
	 * calculation:
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	int hashCode = 1;
	 * 	for (E e : list)
	 * 		hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
	 * }
	 * </pre>
	 * 
	 * This ensures that <tt>list1.equals(list2)</tt> implies that <tt>list1.hashCode()==list2.hashCode()</tt> for any
	 * two lists, <tt>list1</tt> and <tt>list2</tt>, as required by the general contract of {@link Object#hashCode}.
	 *
	 * @return the hash code value for this list
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	public int hashCode() {
		return list.hashCode();
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public E get(int index) {
		return list.get(index);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element (optional operation).
	 *
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the <tt>set</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this list
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 *         list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public E set(int index, E element) {
		return list.set(index, element);
	}

	/**
	 * Inserts the specified element at the specified position in this list (optional operation). Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this list
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 *         list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	public void add(int index, E element) {
		list.add(index, element);
	}

	/**
	 * Inserts all of the elements in the specified collection into this list at the specified position (optional
	 * operation). Shifts the element currently at that position (if any) and any subsequent elements to the right
	 * (increases their indices). The new elements will appear in this list in the order that they are returned by the
	 * specified collection's iterator. The behavior of this operation is undefined if the specified collection is
	 * modified while the operation is in progress. (Note that this will occur if the specified collection is this list,
	 * and it's nonempty.)
	 *
	 * @param index index at which to insert the first element from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of the specified collection prevents it from being added to
	 *         this list
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 *         not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the specified collection prevents it from
	 *         being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		return list.addAll(index, c);
	}

	/**
	 * Removes the element at the specified position in this list (optional operation). Shifts any subsequent elements
	 * to the left (subtracts one from their indices). Returns the element that was removed from the list.
	 *
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	public E remove(int index) {
		return list.remove(index);
	}

	/**
	 * Removes from this list all of its elements that are contained in the specified collection (optional operation).
	 *
	 * @param c collection containing elements to be removed from this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of this list is incompatible with the specified collection
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this list contains a null element and the specified collection does not permit
	 *         null elements (<a href="Collection.html#optional-restrictions">optional</a>), or if the specified
	 *         collection is null
	 */
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>, or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 *         contain the element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>, or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 *         contain the element
	 * @throws ClassCastException if the type of the specified element is incompatible with this list
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this list does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	/**
	 * Inserts the specified element at the front of this deque if it is possible to do so immediately without violating
	 * capacity restrictions, throwing an {@code IllegalStateException} if no space is currently available. When using a
	 * capacity-restricted deque, it is generally preferable to use method {@link #offerFirst}.
	 *
	 * @param element the element to add
	 * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 *         deque
	 */
	public void addFirst(E element) {
		list.add(0, element);
	}

	/**
	 * Inserts all of the elements in the specified collection into this list at the origin. Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (increases their indices). The new
	 * elements will appear in this list in the order that they are returned by the specified collection's iterator. The
	 * behavior of this operation is undefined if the specified collection is modified while the operation is in
	 * progress. (Note that this will occur if the specified collection is this list, and it's nonempty.)
	 *
	 * @param index index at which to insert the first element from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of the specified collection prevents it from being added to
	 *         this list
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 *         not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the specified collection prevents it from
	 *         being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	public void addFirst(Collection<? extends E> c) {
		list.addAll(0, c);
	}

	/**
	 * Inserts the specified element at the end of this deque if it is possible to do so immediately without violating
	 * capacity restrictions, throwing an {@code IllegalStateException} if no space is currently available. When using a
	 * capacity-restricted deque, it is generally preferable to use method {@link #offerLast}.
	 *
	 * <p>
	 * This method is equivalent to {@link #add}.
	 *
	 * @param element the element to add
	 * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions
	 * @throws ClassCastException if the class of the specified element prevents it from being added to this deque
	 * @throws NullPointerException if the specified element is null and this deque does not permit null elements
	 * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this
	 *         deque
	 */
	public void addLast(E element) {
		list.add(list.size(), element);
	}

	/**
	 * Inserts all of the elements in the specified collection into this list at the end.
	 *
	 * @param index index at which to insert the first element from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this list
	 * @throws ClassCastException if the class of an element of the specified collection prevents it from being added to
	 *         this list
	 * @throws NullPointerException if the specified collection contains one or more null elements and this list does
	 *         not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the specified collection prevents it from
	 *         being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
	 */
	public void addLast(Collection<? extends E> c) {
		list.addAll(list.size(), c);
	}

	/**
	 * Retrieves, but does not remove, the first element of this deque.
	 *
	 * This method differs from {@link #peekFirst peekFirst} only in that it throws an exception if this deque is empty.
	 *
	 * @return the head of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public E getFirst() {
		return list.get(0);
	}

	/**
	 * Retrieves, but does not remove, the last element of this deque. This method differs from {@link #peekLast
	 * peekLast} only in that it throws an exception if this deque is empty.
	 *
	 * @return the tail of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public E getLast() {
		return list.get(list.size() - 1);
	}

	/**
	 * Retrieves and removes the first element of this deque. This method differs from {@link #pollFirst pollFirst} only
	 * in that it throws an exception if this deque is empty.
	 *
	 * @return the head of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public E removeFirst() {
		if (list.isEmpty()) {
			throw new NoSuchElementException();
		}
		return list.remove(0);
	}

	/**
	 * Retrieves and removes the last element of this deque. This method differs from {@link #pollLast pollLast} only in
	 * that it throws an exception if this deque is empty.
	 *
	 * @return the tail of this deque
	 * @throws NoSuchElementException if this deque is empty
	 */
	public E removeLast() {
		if (list.isEmpty()) {
			throw new NoSuchElementException();
		}
		return list.remove(list.size() - 1);
	}

	/**
	 * Performs the given action for each element in ascending order.
	 *
	 * @param action The action to be performed for each element
	 * @throws NullPointerException if the specified action is null
	 */
	public void forEachAscending(Consumer<? super E> action) {
		if (action == null) {
			throw new NullPointerException();
		}
		Iterator<E> i = ascendingIterator();
		while (i.hasNext()) {
			action.accept(i.next());
		}
	}

	/**
	 * Performs the given action for each element in descending order.
	 *
	 * @param action The action to be performed for each element
	 * @throws NullPointerException if the specified action is null
	 */
	public void forEachDescending(Consumer<? super E> action) {
		if (action == null) {
			throw new NullPointerException();
		}
		Iterator<E> i = descendingIterator();
		while (i.hasNext()) {
			action.accept(i.next());
		}
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
	 * <p>
	 * The returned array will be "safe" in that no references to it are maintained by this list. (In other words, this
	 * method must allocate a new array). The caller is thus free to modify the returned array.
	 *
	 * @return an array containing all of the elements in this list in proper sequence
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element); the
	 * runtime type of the returned array is that of the specified array. If the list fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the runtime type of the specified array and the size
	 * of this list.
	 *
	 * @param a the array into which the elements of the list are to be stored, if it is big enough; otherwise, a new
	 *        array of the same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of
	 *         every element in this list
	 * @throws NullPointerException if the specified array is null
	 */
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	/**
	 * Returns a string representation.
	 */
	@Override
	public String toString() {
		return list.toString();
	}

}
