package test.com.msasc.library.swing.formatters;

import java.util.Date;
import java.util.Locale;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.FormatUtils;

import test.com.msasc.library.swing.TestBox;

public class TestSpinnerDateEditor {

	public static void main(String[] args) {
		Locale locale = new Locale("es");
		String pattern = FormatUtils.getNormalizedDatePattern(locale);
		SpinnerDateModel model = new SpinnerDateModel(
			new Date(), 
			new Date(0),
			new Date(Long.MAX_VALUE),
			Calendar.DATE);
		JSpinner spinner = new JSpinner(model);
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner,pattern);
		editor.getTextField().setColumns(pattern.length());
		TestBox.show(spinner);
		System.exit(0);
	}

}
