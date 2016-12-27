package test.other;

import com.qtplaf.library.util.NumberUtils;

public class TestModulus {

	public static void main(String[] args) {
		System.out.println(12 % 10);
		System.out.println(NumberUtils.remainder(12, 10));
		System.out.println(NumberUtils.remainder(12, 7));
		System.out.println(12 % 7);
		System.out.println(NumberUtils.isLeap(100));
		System.out.println(NumberUtils.isOdd(103));
		System.out.println(NumberUtils.round(3d * 0.75, 0));
	}

}
