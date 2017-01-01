/**
 * 
 */
package test.com.msasc.library.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.SwingUtils;
import com.qtplaf.library.swing.action.DefaultActionAccept;

/**
 * Option pane to test a text field.
 * 
 * @author Miquel Sas
 */
public class TestBox {
	public static void show(Object component) {
		
		JOptionPane pane = new JOptionPane();
		pane.setMessage(component);
		pane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		pane.setLocale(Locale.UK);

		JButton button = new JButton();
		AbstractAction action = new ActionAccept();
		ActionUtils.setButton(action, button);
		button.setAction(action);
		pane.setOptions(new Object[] { button });

		JDialog dialog = pane.createDialog("Test edit component");
		dialog.setResizable(true);
//		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		SwingUtils.setMnemonics(button);
//		SwingUtils.installAcceleratorKeyListener(dialog);
		dialog.pack();
		dialog.setVisible(true);

		if (component instanceof JFormattedTextField) {
			System.out.println(((JFormattedTextField) component).getText());
		}
	}

	static class ActionAccept extends DefaultActionAccept {
		ActionAccept() {
			super(new Session(Locale.UK));
		}
		public void actionPerformed(ActionEvent e) {
			JButton button = ActionUtils.getButton(this);
			Component component = SwingUtils.getFirstParentFrameOrDialog(button);
			if (component instanceof JDialog) {
				JDialog dlg = (JDialog) component;
				dlg.setVisible(false);
				dlg.dispose();
			}
		}
	}
}
