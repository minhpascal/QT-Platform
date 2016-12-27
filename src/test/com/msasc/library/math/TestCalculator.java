package test.com.msasc.library.math;

import com.qtplaf.library.math.Calculator;

public class TestCalculator {
	public static void main(String[] args) {
		testMeanSquaredMinimum();
	}
	
	public static void test1() {
		double[] values = new double[]{ 5.0, -5.0, 10.0, -10.0, 3.0, -3.0 };
		double mean = Calculator.mean(values);
		double stddev = Calculator.stddev(values, mean);
		double[] norm = Calculator.normalize(values);
		double[] normMeanStdDev = Calculator.normalize(values,mean,stddev);
		System.out.println(mean);
		System.out.println(stddev);
		System.out.println(normMeanStdDev);
	}
	
	public static void test2() {
		double[] a = new double[]{ 4, 4, 4, 4, 4 };
		double[] b = new double[]{ 1, 1, 1, 1, 1 };
		double dist = Calculator.euclideanDistance(a, b);
		System.out.println(dist);
		System.out.println(dist/a.length);
		System.out.println(2*dist/a.length);
	}
	
	public static void testMeanSquaredMinimum() {
		double[] output = new double[]{ 10, 15, 20, 25, 30, 35 };
		double[] input = new double[]{ 2, -3, 4, -5, 6, -7 };
		double[] result = Calculator.meanSquaredMinimum(output, input, 0.01, 0.00000000001, 1000000);
		print(output);
		print(input);
		print(result);
	}
	
	public static void print(double[] values) {
		StringBuilder b = new StringBuilder();
		boolean comma = false;
		for (double value : values) {
			if (comma) {
				b.append(", ");
			}
			b.append(value);
			comma = true;
		}
		System.out.println(b);
	}
}
