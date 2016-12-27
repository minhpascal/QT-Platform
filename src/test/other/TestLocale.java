package test.other;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class TestLocale {

	public static void main(String[] args) {
		Locale us = Locale.US;
		Locale[] locales = Locale.getAvailableLocales();
		Arrays.sort(locales, new LocaleComparator());
		for (Locale locale : locales) {
			if (locale.getLanguage().isEmpty()) {
				continue;
			}
			StringBuilder b = new StringBuilder();
			b.append(locale.getLanguage());
			if (!locale.getCountry().isEmpty()) {
				b.append("_" + locale.getCountry());
			} else {
				b.append("   ");
			}
			b.append(" - ");
			b.append(locale.getDisplayLanguage(us));
			if (!locale.getCountry().isEmpty()) {
				b.append(" - " + locale.getDisplayCountry(us));
			}
			System.out.println(b.toString());
		}
	}

	public static class LocaleComparator implements Comparator<Locale> {
		public int compare(Locale l1, Locale l2) {
			return getKey(l1).compareTo(getKey(l2));
		}

	}

	public static String getKey(Locale locale) {
		StringBuilder b = new StringBuilder();
		b.append(locale.getLanguage());
		if (!locale.getCountry().isEmpty()) {
			b.append("_" + locale.getCountry());
		}
		return b.toString();
	}
}
