package test.com.msasc.library.math;

import com.qtplaf.library.math.Calculator;

public class TestOrthoNormal {

	public static void main(String[] args) {
		double[] v = new double[]{ 0.0, 1.0, 2.0, 3.0, 4.0 };
		double[] n = Calculator.orthoNormal(v);
		System.out.println(Calculator.total(n));
		System.out.println(Calculator.euclideanNorm(n));
	}

}
