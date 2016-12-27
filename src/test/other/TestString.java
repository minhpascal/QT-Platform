package test.other;

public class TestString {

	public static void main(String[] args) {
		String s = "a.b.c.d\ntururut";
		String[] a = s.split(".\n");
		for (String x : a) {
			System.out.println(x);
		}
	}
}
