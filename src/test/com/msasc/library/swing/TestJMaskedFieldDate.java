package test.com.msasc.library.swing;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.util.TextServer;

public class TestJMaskedFieldDate {

	public static void main(String[] args) {

		Locale.setDefault(new Locale("en", "GB"));

		TextServer.addBaseResource("StringsLibrary.xml");

		FieldList fieldList = Util.getFieldList();
		Record record = new Record(fieldList);

		EditContext editContext = new EditContext(new Session(Locale.UK));
		editContext.setRecord(record);
		editContext.setAlias("TCREATED");

		TestBox.show(editContext.getEditField());
		System.out.println(editContext.getEditField().getValue());
		System.exit(0);
	}
}
