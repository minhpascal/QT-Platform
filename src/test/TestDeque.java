package test;

import java.util.ArrayDeque;
import java.util.Deque;

public class TestDeque {

	public static void main(String[] args) {
		Deque<String> deque = new ArrayDeque<>();
		deque.addFirst("A");
		deque.addFirst("B");
		System.out.println(deque);
		deque.addLast("A");
		deque.addLast("B");
		System.out.println(deque);
		System.out.println(deque.getFirst());
		System.out.println(deque.getLast());
	}

}
