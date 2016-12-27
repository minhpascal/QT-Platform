package test.other;

import java.util.Arrays;

import com.qtplaf.library.math.Calculator;

public class TestKernel {

	public static void main(String[] args) {
		int length = 1000;
		double va = 100;
		double vb = 50;
		double[] a = new double[length];
		double[] b = new double[length];
		Arrays.fill(a, va);
		Arrays.fill(b, vb);
		double ed = Calculator.euclideanDistance(a, b);
		System.out.println(ed);
		System.out.println(ed/length);
	}

}
