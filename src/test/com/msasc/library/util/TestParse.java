package test.com.msasc.library.util;

import java.text.ParseException;
import java.util.Locale;

import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.FormatUtils;

public class TestParse {

	public static void main(String[] args) throws ParseException {
		Locale locale =new Locale("es","ES");
		String str = "21/08/2015";
		java.util.Date date = FormatUtils.getNormalizedDateFormat(locale).parse(str);
		System.out.println(date);
		System.out.println(new Date(date.getTime()));

	}

}
