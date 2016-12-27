package test.com.msasc.library.swing.formatters;

import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import test.com.msasc.library.swing.TestBox;

public class TestDefaultFormatter {

	public static void main(String[] args) {
		
		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(new Locale("es"));
		textField.setFormatterFactory(
			new DefaultFormatterFactory(new DefaultFormatter()));

		TestBox.show(textField);
		System.exit(0);
	}

}
