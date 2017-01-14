package test.com.msasc.library.swing;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.swing.core.JOptionDialog;
import com.qtplaf.library.util.TextServer;

public class TestJOptionDialogAction {
	
	static class ActionDo extends AbstractAction {
		ActionDo() {
			ActionUtils.setSourceName(this, "Close");
		}
		public void actionPerformed(ActionEvent e) {
			Session session = ActionUtils.getSession(this);
			JOptionDialog dialog = (JOptionDialog) ActionUtils.getUserObject(this);
			MessageBox.warning(session, dialog.getMessage());
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");
		Session session = new Session(Locale.UK);
		JOptionDialog dialog = new JOptionDialog(session);
		dialog.setTitle("Dialog title");
		dialog.setMessage("This is the message");
		dialog.addOption(new ActionDo());
		dialog.showDialog();
		System.exit(0);
	}

}
