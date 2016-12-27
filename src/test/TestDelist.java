package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.qtplaf.library.util.list.ArrayDelist;
import com.qtplaf.library.util.list.Delist;

public class TestDelist {

	public static void main(String[] args) {
		Delist<String> delist = new ArrayDelist<>();
		delist.addFirst("B");
		delist.addFirst("A");
		System.out.println(delist);
		System.out.println();
		delist.addLast("C");
		delist.addLast("D");
		System.out.println(delist);
		System.out.println();
		System.out.println(delist.getFirst());
		System.out.println(delist.getLast());
		System.out.println();

		List<String> list = new ArrayList<>();
		list.add("X");
		list.add("Y");
		list.add("Z");
		
		delist.addFirst(list);
		delist.addLast(list);
		System.out.println(delist);
		System.out.println();
		delist.addLast("1");
		delist.addLast("2");
		delist.addLast("3");
		
		Iterator<String> di = delist.descendingIterator();
		while (di.hasNext()) {
			System.out.println(di.next());
		}
		System.out.println();
		
		Consumer<String> cs = (String s) -> System.out.println(s);
		delist.forEachAscending(cs);
		System.out.println();
		delist.forEachDescending(cs);
		System.out.println();
		
		StringBuilder b = new StringBuilder();
		while (!delist.isEmpty()) {
			b.append(delist.removeLast());
		}
		System.out.println(b);
		System.out.println(delist.isEmpty());
	}

}
