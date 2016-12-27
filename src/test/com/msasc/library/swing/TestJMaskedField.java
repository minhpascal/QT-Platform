package test.com.msasc.library.swing;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.swing.MessageBox;
import com.qtplaf.library.util.TextServer;

public class TestJMaskedField {

	static class ActionX extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			MessageBox.warning(new Session(Locale.UK), "Lookup");
		}

	}

	public static void main(String[] args) {

		TextServer.addBaseResource("SysString.xml");

		FieldList fieldList = Util.getFieldList();
		Record record = new Record(fieldList);

		EditContext editContext = new EditContext(new Session(Locale.UK));
		editContext.setRecord(record);
		editContext.setAlias("CARTICLE");
		editContext.setActionLookup(new ActionX());

		TestBox.show(editContext.getEditField());
		System.out.println(editContext.getEditField().getValue());
		System.exit(0);
	}
}
