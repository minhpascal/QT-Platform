package test.com.msasc.library.math;

import com.qtplaf.library.util.MArray;

public class TestMArray {

	public static void main(String[] args) {
//		Object a = Array.newInstance(Double.class, 3, 3);
//		double value = 0;
//		for (int i = 0; i < 3; i++) {
//			Object b = Array.get(a, i);
//			for (int j = 0; j < 3; j++) {
//				Array.set(b, j, value++);
//			}
//		}
//		for (int i = 0; i < 3; i++) {
//			Object b = Array.get(a, i);
//			for (int j = 0; j < 3; j++) {
//				System.out.println(Array.get(b, j));
//			}
//		}
		
		MArray<Double> ma = new MArray<>(Double.class, 3, 3);
		double value = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				ma.set(value++, i, j);
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.println(ma.get(i, j));
			}
		}
		ma.init(1.1);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.println(ma.get(i, j));
			}
		}
	}

}
