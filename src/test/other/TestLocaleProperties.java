package test.other;

import java.util.Locale;
import java.util.Set;

public class TestLocaleProperties {

	public static void main(String[] args) {
		Locale locale = Locale.ENGLISH;
		System.out.println(locale.getLanguage()+" - "+locale.getCountry());
		System.out.println(locale.getDisplayLanguage(new Locale("pl","PL"))+" - "+locale.getDisplayCountry(new Locale("pl","PL")));
		Set<String> keys = locale.getUnicodeLocaleKeys();
		for (String key : keys) {
			System.out.println(key);
		}
		Set<String> attributes = locale.getUnicodeLocaleAttributes();
		for (String attribute : attributes) {
			System.out.println(attribute);
		}
	}

}
