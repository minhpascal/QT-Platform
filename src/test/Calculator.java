package test;

public class Calculator {
	
	interface IntegerMath {
		int operation(int a, int b);
	}
	
	public int operateBinary(int a, int b, IntegerMath op) {
		return op.operation(a, b);
	}

	public static void main(String[] args) {
		Calculator calc = new Calculator();
		IntegerMath add = (int a, int b) -> a + b;
		IntegerMath sub = (int a, int b) -> a - b;	
		System.out.println("40 + 2 = " + calc.operateBinary(40, 2, add));
		System.out.println("10 - 20 = " + calc.operateBinary(10, 20, sub));
	}

}
