package test.com.msasc.library.swing;

import java.util.Locale;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.JOptionDialog;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.util.TextServer;

public class TestJOptionDialog {

	public static void main(String[] args) {
		TextServer.addBaseResource("SysString.xml");
		Session session = new Session(Locale.UK);

		JOptionDialog dlg = new JOptionDialog(session);
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			if (i > 0) {
				b.append("\n");
			}
			b.append("Esto es una linea de texto");
		}
		String text =
			"kdjahs dkjash dkjash dkasjh dkasjdh kasjdh kasjdh kasjdh askjdh askjdh askjdh askjdh askjdh kasjdh kasjdh kasjdh kasjdh kasjh dkasjhd kasjh dksajh dkjsah dkjash dkasjh dksajdh kasjdh kasjdh askjd kasjdh kasjdh kasjdh ksajh dkjsah dkjash dkjash dkjash dkjash dkjsah kdjh asksjdh askjdh askjsdh akjdh akjdh askjdh askjdh";
		dlg.setMessage(b.toString());
//		dlg.setMessage(text, 60, true);
//		dlg.setMessage("Hello dialog", 40, true);
		dlg.addOption("Accept");
//		dlg.addOption("Option 2");
//		dlg.addOption("Option 3");
		
		Icon icon = UIManager.getIcon("OptionPane.questionIcon");
		dlg.setIcon(icon);
		
		MessageBox.question(session, b.toString());

		String option = dlg.showDialog();
		System.out.println(option);

		System.exit(0);

	}

}
