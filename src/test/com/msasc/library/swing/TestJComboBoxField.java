package test.com.msasc.library.swing;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.EditContext;
import com.qtplaf.library.util.TextServer;

public class TestJComboBoxField {
	
	public static void main(String[] args) {
		
		Locale.setDefault(new Locale("en","GB"));
		
		TextServer.addBaseResource("SysString.xml");
		
		FieldList fieldList = Util.getFieldList();
		Record record = new Record(fieldList);

		EditContext editContext = new EditContext(new Session(Locale.UK));
		editContext.setRecord(record);
		editContext.setAlias("ISTATUS");
		
		TestBox.show(editContext.getEditField());
		System.out.print(editContext.getEditField().getValue());
		System.out.print(" - ");
		System.out.println(editContext.getEditField().getValue().getLabel());
		System.exit(0);
	}
}
