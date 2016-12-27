package test.com.msasc.library.swing.formatters;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.qtplaf.library.util.FormatUtils;

import test.com.msasc.library.swing.TestBox;

public class TestFormatterDateJava {

	public static void main(String[] args) throws Exception {

		Locale locale = new Locale("es");

		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(locale);

		String pattern = FormatUtils.getNormalizedDatePattern(locale);
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		DateFormatter formatter = new DateFormatter(format);
		textField.setFormatterFactory(
			new DefaultFormatterFactory(formatter));

		TestBox.show(textField);
		System.exit(0);

	}

}
