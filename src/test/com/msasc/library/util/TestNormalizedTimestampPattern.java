package test.com.msasc.library.util;

import java.util.Locale;

import com.qtplaf.library.util.FormatUtils;

public class TestNormalizedTimestampPattern {

	public static void main(String[] args) {
		System.out.println(FormatUtils.getNormalizedTimestampPattern(Locale.UK));
	}

}
