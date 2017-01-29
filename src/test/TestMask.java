package test;

public class TestMask {

	public static void main(String[] args) {
		int x = 1 | 2 | 3;
		System.out.println(x);
		System.out.println((x & 1) == 1);
		System.out.println((x & 2) == 2);
		System.out.println((x & 4) == 4);
	}

}
