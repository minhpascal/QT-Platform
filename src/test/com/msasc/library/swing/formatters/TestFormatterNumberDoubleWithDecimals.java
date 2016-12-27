package test.com.msasc.library.swing.formatters;

import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import com.qtplaf.library.database.Types;
import com.qtplaf.library.util.FormatUtils;

import test.com.msasc.library.swing.TestBox;

public class TestFormatterNumberDoubleWithDecimals {

	public static void main(String[] args) throws ParseException {
		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(new Locale("es"));
		textField.setFormatterFactory(
			new DefaultFormatterFactory(
				FormatUtils.getFormatterNumber(Types.Double,8,2,textField.getLocale())));

		TestBox.show(textField);
		System.exit(0);

	}

}
