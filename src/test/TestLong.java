package test;

public class TestLong {

	public static void main(String[] args) {
		System.out.println(Long.MAX_VALUE);
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Long.MAX_VALUE >> 32);
		System.out.println((int)(Long.MAX_VALUE >> 32));
		System.out.println((long)Integer.MAX_VALUE);

		long value = Integer.MAX_VALUE;
		System.out.println((int)value);
	}

}
