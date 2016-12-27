package test.com.msasc.library.math;

import com.qtplaf.library.math.Calculator;
import com.qtplaf.library.util.NumberUtils;

public class TestNormalize {

	public static void main(String[] args) {
		System.out.println(NumberUtils.round(Calculator.normalize(100, 150, 50),2));
		System.out.println(NumberUtils.round(Calculator.normalize(-100, -50, -150),2));
		System.out.println(NumberUtils.round(Calculator.normalize(150, 200, -200),2));
		System.out.println(NumberUtils.round(Calculator.normalize(-150, 200, -200),2));
	}

}
