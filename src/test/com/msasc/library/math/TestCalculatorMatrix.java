package test.com.msasc.library.math;

import com.qtplaf.library.math.Calculator;

class TestCalculatorMatrix {

	public static void main(String[] args) {
		double[][] a = new double[][]{
			{ 2, 4, 6 },
			{ 1, 3, 5 }
		};
		double[][] b = new double[][]{
			{ 1, 2 },
			{ 3, 4 },
			{ 5, 6 }
		};

		System.out.println(Calculator.toString(Calculator.transpose(a)));
		System.out.println();
		System.out.println(Calculator.toString(Calculator.multiply(a, b)));
		
		double[][] v = new double[][]{
			{ 1, 3, 5 }
		};
		
		System.out.println(Calculator.toString(Calculator.multiply(v, b)));
		System.out.println();
		
		double[] k = new double[]{ 1, 3, 5 };
		
		System.out.println(Calculator.toString(Calculator.diagonalMatrix(k)));
		System.out.println();
		System.out.println(Calculator.toString(Calculator.identity(4,4)));
	}

}
