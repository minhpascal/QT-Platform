package test.com.msasc.library.swing.formatters;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import com.qtplaf.library.database.Types;
import com.qtplaf.library.util.FormatUtils;

import test.com.msasc.library.swing.TestBox;

public class TestFormatterNumberDecimal {

	public static void main(String[] args) throws ParseException {
		Locale localeDefault = new Locale("es","ES");
		show(localeDefault);
		
		Locale[] locales = Locale.getAvailableLocales();
		TreeMap<String,Locale> map = new TreeMap<String,Locale>();
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i];
			map.put(locale.toString(),locale);
		}
		for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
			Locale locale = map.get(i.next());
			show(locale);
		}
		
		System.exit(0);
	}
	
	private static void show(Locale locale) throws ParseException {
		Locale.setDefault(locale);
		
		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(locale);
		textField.setFormatterFactory(
			new DefaultFormatterFactory(
				FormatUtils.getFormatterNumber(Types.Decimal,12,2,locale)));
		TestBox.show(textField);
	}

}
