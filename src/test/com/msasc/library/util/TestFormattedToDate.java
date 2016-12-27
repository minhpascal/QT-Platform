package test.com.msasc.library.util;

import java.text.ParseException;
import java.util.Locale;

import com.qtplaf.library.util.FormatUtils;

public class TestFormattedToDate {

	public static void main(String[] args) throws ParseException {
		String s = " 2/  12/16";
		System.out.println(FormatUtils.formattedToDate(s, Locale.UK));
	}

}
