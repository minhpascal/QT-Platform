package test.com.msasc.library.swing.formatters;

import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;

import com.qtplaf.library.database.Types;
import com.qtplaf.library.util.FormatUtils;

import test.com.msasc.library.swing.TestBox;

public class TestFormatterNumberDoubleWithoutDecimals {

	public static void main(String[] args) throws ParseException {
		
		JFormattedTextField textField = new JFormattedTextField();
		textField.setLocale(new Locale("es"));
		textField.setFormatterFactory(
			new DefaultFormatterFactory(FormatUtils.getFormatterNumber(Types.Double,-1,-1,textField.getLocale())));
		textField.setText("0,05");

		TestBox.show(textField);
		System.exit(0);

	}

}
