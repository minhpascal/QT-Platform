package test.com.msasc.library.swing.formatters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import com.qtplaf.library.util.FormatUtils;

public class TestPattern {

	static class LocaleComparator implements Comparator<Locale> {
		public int compare(Locale o1, Locale o2) {
			return o1.toString().compareTo(o2.toString());
		}

	}

	public static void main(String[] args) {
		Locale[] locales = Locale.getAvailableLocales();
		Arrays.sort(locales, new LocaleComparator());
		for (Locale locale : locales) {
			String date = FormatUtils.getNormalizedDatePattern(locale);
			String time = FormatUtils.getNormalizedTimePattern(locale);
			String timestamp = FormatUtils.getNormalizedTimestampPattern(locale);
			StringBuilder b = new StringBuilder();
			b.append(locale);
			b.append(": ");
			b.append(date);
			b.append(" -- ");
			b.append(time);
			b.append(" -- ");
			b.append(timestamp);
			System.out.println(b);
		}
	}

}
