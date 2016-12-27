package test.other;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qtplaf.library.util.FormatUtils;
import com.qtplaf.library.util.NumberUtils;

public class TestAny {

	public static void main(String[] args) throws ParseException {
		BigDecimal b1 =  new BigDecimal("0.05");
		System.out.println(b1);
		System.out.println(b1.scale());		
		BigDecimal b2 =  new BigDecimal("0.05");
		System.out.println(b1.multiply(b2));
		System.out.println(b1.multiply(b2).scale());
		System.out.println(b1.divide(b2));
		System.out.println(b1.divide(b2).scale());
		
		Locale locale = new Locale("es");
		BigDecimal b3 = FormatUtils.formattedToBigDecimal("0,05", locale);
		System.out.println(b3);
		System.out.println(b3.scale());
	}
	
	public static void testSome() {
		int pipScale = 4;
		List<BigDecimal> increases = getIncreases(0, pipScale, 1, 2, 5);
		double quote = 0.9328;
		List<BigDecimal> values = new ArrayList<>();
		for (BigDecimal increase : increases) {
			int scale = increase.scale() - 1;
			double pow = quote * Math.pow(10, scale);
			double floor = Math.floor(pow);
			double value = floor / Math.pow(10, scale);
			double next = NumberUtils.round(value + increase.doubleValue(), pipScale);
			values.add(new BigDecimal(next).setScale(pipScale, BigDecimal.ROUND_HALF_UP));
		}
		boolean comma = false;
		StringBuilder b = new StringBuilder();
		for (BigDecimal value : values) {
			if (comma) {
				b.append(", ");
			}
			b.append(value.toPlainString());
			comma = true;
		}
		System.out.println(b);
	}
	
	public static void testIncreases() {
		List<BigDecimal> increases = getIncreases(0, 4, 1, 2, 5);
		StringBuilder b = new StringBuilder();
		boolean comma = false;
		for (BigDecimal increase : increases) {
			if (comma) {
				b.append(", ");
			}
			b.append(increase.toPlainString());
			comma = true;
		}
		System.out.println(b);
	}

	public static List<BigDecimal> getIncreases(int integerDigits, int decimalDigits, int... multipliers) {
		List<BigDecimal> increaments = new ArrayList<>();
		int upperScale = decimalDigits;
		int lowerScale = (integerDigits - 1) * (-1);
		for (int scale = upperScale; scale >= lowerScale; scale--) {
			for (int multiplier : multipliers) {
				BigDecimal value = NumberUtils.getBigDecimal(Math.pow(10, -scale), scale);
				BigDecimal multiplicand = new BigDecimal(multiplier).setScale(0, BigDecimal.ROUND_HALF_UP);
				increaments.add(value.multiply(multiplicand));
			}
		}
		return increaments;
	}
	
	public static void testFloat() {
		double d = 101.125315;
		float f = new Float(d).floatValue();
		System.out.println(d);
		System.out.println(f);
	}
}
