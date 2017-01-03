package test.com.msasc.library.swing;

import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.JFormRecord;
import com.qtplaf.library.util.TextServer;

public class TestFormRecord {

	public static void main(String[] args) {

		TextServer.addBaseResource("StringsLibrary.xml");

		FieldList fieldList = Util.getFieldList();
		Record record = new Record(fieldList);
		for (int i = 0; i < record.getFieldCount(); i++) {
			record.getField(i).setFieldGroup(null);
		}

		record.setValue("CARTICLE", "A525345000");

		JFormRecord form = new JFormRecord(new Session(Locale.UK));
		form.setTitle("Testing form record");
		form.setRecord(record);
		// form.setScrollGroupPanels(true);
		// form.setEditMode(EditMode.ReadOnly);

		form.addField("CARTICLE", 0, 0);
		// form.addField("DARTICLE", 0, 0);
		form.addField("CBUSINESS", 0, 0);
		form.addField("IREQUIRED", 0, 0);
		form.addField("ISTATUS", 0, 1);
		form.addField("ICHECKED", 0, 1);
		form.addField("TCREATED", 0, 1);
		form.addField("TCREATED", 0, 1);
		form.addField("TCREATED", 0, 1);

		form.addField("CARTICLE", 1, 0);
		// form.addField("DARTICLE", 1, 0);
		form.addField("CBUSINESS", 1, 0);
		form.addField("IREQUIRED", 1, 0);
		form.addField("IREQUIRED", 1, 0);
		form.addField("IREQUIRED", 1, 0);
		// form.addField("ISTATUS", 1, 1);
		form.addField("ICHECKED", 1, 1);
		form.addField("ICHECKED", 1, 1);
		form.addField("ICHECKED", 1, 1);
		form.addField("ICHECKED", 1, 1);
		form.addField("TCREATED", 1, 1);

		form.addField("CARTICLE", 2, 0);
		// form.addField("DARTICLE", 2, 0);
		form.addField("CBUSINESS", 2, 0);
		form.addField("IREQUIRED", 2, 0);
		form.addField("ISTATUS", 2, 1);
		form.addField("ICHECKED", 2, 1);
		form.addField("TCREATED", 2, 1);

		// form.addField(fieldList.getField("CARTICLE"), 0, 0);
		// form.addField(fieldList.getField("DARTICLE"), 0, 0);
		// form.addField(fieldList.getField("CBUSINESS"), 0, 0);
		// form.addField(fieldList.getField("IREQUIRED"), 0, 0);
		// form.addField(fieldList.getField("ISTATUS"), 1, 0);
		// form.addField(fieldList.getField("ICHECKED"), 1, 0);
		// form.addField(fieldList.getField("TCREATED"), 1, 0);
		// form.addField(fieldList.getField("DARTICLE"), 1, 0);

		// form.addField(fieldList.getField("IREQUIRED"), 1, 0);
		// form.addField(fieldList.getField("ISTATUS"), 1, 0);
		// form.addField(fieldList.getField("ICHECKED"), 1, 0);
		// form.addField(fieldList.getField("TCREATED"), 1, 0);

		if (form.edit()) {
			System.out.println(form.getRecord());
		}
		System.exit(0);

	}

}
