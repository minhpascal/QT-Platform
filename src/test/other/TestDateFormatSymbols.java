package test.other;

import java.text.DateFormatSymbols;
import java.util.Locale;

public class TestDateFormatSymbols {

	public static void main(String[] args) {
		DateFormatSymbols symbols = DateFormatSymbols.getInstance(new Locale("it"));
		showSymbols(symbols);
	}
	
	private static void showSymbols(DateFormatSymbols symbols) {
		StringBuilder b = new StringBuilder();
		b.append("AM/PM: "+format(symbols.getAmPmStrings()));
		b.append("Eras: "+format(symbols.getEras()));
		b.append("Pattern chars: "+format(symbols.getLocalPatternChars()));
		b.append("Months: "+format(symbols.getMonths()));
		b.append("Short months: "+format(symbols.getShortMonths()));
		b.append("Short week days: "+format(symbols.getShortWeekdays()));
		b.append("Week days: "+format(symbols.getWeekdays()));
		
		
		System.out.println(b.toString());
	}
	
	private static String format(String... strings) {
		StringBuilder b = new StringBuilder();
		for (String string : strings) {
			if (string.isEmpty()) {
				continue;
			}
			b.append("[");
			b.append(string);
			b.append("]");
		}
		b.append("\n");
		return b.toString();
	}

}
