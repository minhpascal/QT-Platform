package test.com.msasc.library.swing.formatters;

import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import test.com.msasc.library.swing.TestBox;

public class TestFormatterMask {

	public static void main(String[] args) throws Exception {
		
		String mask = "UUUUUUUUUU";
		MaskFormatter formatter = new MaskFormatter(mask);
//		formatter.setPlaceholderCharacter(' ');
//		formatter.se
		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(new Locale("es"));
		textField.setFormatterFactory(
			new DefaultFormatterFactory(formatter));
		
		TestBox.show(textField);
		System.exit(0);

	}

}
