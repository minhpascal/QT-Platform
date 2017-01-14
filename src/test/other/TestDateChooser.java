package test.other;

import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.util.Date;
import com.qtplaf.library.util.FormatUtils;
import com.toedter.calendar.JDateChooser;

public class TestDateChooser {

	public static void main(String[] args) {
		
		Locale locale = new Locale("es","ES");
		
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setLocale(locale);
		dateChooser.setDate(new Date());
		dateChooser.setDateFormatString(FormatUtils.getNormalizedDatePattern(locale));
		
		JOptionPane pane = new JOptionPane();
		pane.setMessage(dateChooser);
		pane.setMessageType(JOptionPane.OK_CANCEL_OPTION);
		pane.setOptionType(0);
	
		JDialog dialog = pane.createDialog("Ja");
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		SwingUtils.setMnemonics(SwingUtils.getAllButtons(dialog));
		dialog.pack();
		dialog.setVisible(true);

		System.out.println(dateChooser.getDate());
		System.exit(0);
	}

}
