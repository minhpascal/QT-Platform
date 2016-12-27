package test.com.msasc.library.swing;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.JPanelFormFields;
import com.qtplaf.library.util.TextServer;

public class TestJPanelFormFields {

	public static void main(String[] args) {

		TextServer.addBaseResource("SysString.xml");

		FieldList fieldList = Util.getFieldList();
		Record record = new Record(fieldList);

		JPanelFormFields panel = new JPanelFormFields(new Session(Locale.UK));
		panel.setRecord(record);
		panel.addField("CARTICLE", 0, 0);
		panel.addField("DARTICLE", 0, 0);
		panel.addField("DARTICLE", 0, 0);
		panel.addField("CBUSINESS", 0, 0);
		panel.addField("IREQUIRED", 0, 0);
		panel.addField("ISTATUS", 0, 0);
		panel.addField("ICHECKED", 0, 0);
		panel.layoutFields();
		

		TestBox.show(panel);
		System.exit(0);
	}

}
