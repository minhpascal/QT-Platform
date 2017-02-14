package test;

import java.math.BigDecimal;

import com.qtplaf.library.util.NumberUtils;
import com.qtplaf.library.util.StringUtils;

public class TestPow {

	public static void main(String[] args) {
		System.out.println(Math.pow(4.0, 0.5));
		
		BigDecimal b = NumberUtils.getBigDecimal(0.2, 1);
		System.out.println(b);
		System.out.println(b.toPlainString());
		System.out.println(StringUtils.remove(b.toPlainString(),'.'));
	}

}
