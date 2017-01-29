package test.other;

import java.math.BigDecimal;
import java.util.List;

import com.qtplaf.library.util.NumberUtils;

public class TestIncreases {

	public static void main(String[] args) {
		List<BigDecimal> increases = NumberUtils.getIncreases(2, 1, 1);
		for (BigDecimal increase : increases) {
			System.out.println(increase.intValue());
		}
	}

}
