package test.other;

import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

public class Locales {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Locale[] locales = Locale.getAvailableLocales();
		TreeMap<String,Locale> map = new TreeMap<String,Locale>();
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i];
//			StringBuffer b = new StringBuffer();
//			b.append(locale.toString());
//			b.append(" - ");
//			b.append(locale.getDisplayLanguage(Locale.ENGLISH));
//			b.append(" - ");
//			b.append(locale.getDisplayCountry(Locale.ENGLISH));
//			System.out.println(b);
			map.put(locale.toString(),locale);
		}
		Locale localeDefault = new Locale("");
		map.put(localeDefault.toString(),localeDefault);
		for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
			Locale locale = map.get(i.next());
			StringBuffer b = new StringBuffer();
			b.append(locale.toString());
			b.append(" - ");
			b.append(locale.getLanguage().length());
			b.append(" - ");
			b.append(locale.getCountry().length());
			System.out.println(b);
//			StringBuffer b = new StringBuffer();
//			b.append("<xs:enumeration value=\""+locale.toString()+"\">\n");
//			b.append("  <xs:annotation>\n");
//			b.append("    <xs:documentation>\n");
//			b.append("      "+locale.getDisplayName(Locale.ENGLISH)+"\n");
//			b.append("    </xs:documentation>\n");
//			b.append("  </xs:annotation>\n");
//			b.append("</xs:enumeration>\n");
//			System.out.print(b.toString());
		}
	}

}
