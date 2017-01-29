package test;

import java.util.Arrays;

import com.qtplaf.library.ai.rlearning.NormalizedStateValueDescriptor;

public class TestNormalizedValue {

	public static void main(String[] args) {
		NormalizedStateValueDescriptor nv = new NormalizedStateValueDescriptor();
		nv.setMaximum(1.0);
		nv.setMinimum(-1.0);
		nv.setScale(1);

		System.out.println(nv.getValue(0.24));

		NormalizedStateValueDescriptor nvSeg = new NormalizedStateValueDescriptor();
		nvSeg.setMaximum(1.00);
		nvSeg.setMinimum(-1.00);
		nvSeg.setScale(2);
		nvSeg.setSegments(4);

		System.out.println(Arrays.toString(nvSeg.getPositives()));
		System.out.println(Arrays.toString(nvSeg.getNegatives()));
		System.out.println(nvSeg.getValue(0.3));
		System.out.println(nvSeg.getValue(-0.8));
		
		System.out.println();
		println(10, 4, 5);
	}

	private static void println(int num, int... nums) {
		StringBuilder b = new StringBuilder();
		b.append(num);
		if (nums != null) {
			for (int n : nums) {
				b.append(", " + n);
			}
		}
		System.out.println(b);
	}
}
