package test.com.msasc.library.util;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.list.ListUtils;

public class TestShuffle {

	public static void main(String[] args) {
		List<Integer> origin = new ArrayList<>();
		origin.add(0);
		origin.add(1);
		origin.add(2);
		origin.add(3);
		origin.add(4);
		origin.add(5);
		origin.add(6);
		origin.add(7);
		origin.add(8);
		origin.add(9);
		origin.add(10);
		origin.add(11);
		origin.add(12);
		origin.add(13);
		origin.add(14);
		origin.add(15);
		origin.add(16);
		origin.add(17);
		origin.add(18);
		origin.add(19);
		System.out.println(ListUtils.shuffle(origin));
	}

}
