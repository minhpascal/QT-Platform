package test.com.msasc.library.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.FieldList;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.swing.core.JLookupRecords;
import com.qtplaf.library.util.TextServer;

public class TestJLookupRecords {

	public static void main(String[] args) {
		TextServer.addBaseResource("StringsLibrary.xml");

		FieldList fieldList = Util.getFieldList();
		Record masterRecord = new Record(fieldList);

		int size = 500;
		RecordSet recordSet = new RecordSet(fieldList);
		for (int r = 0; r < size; r++) {
			List<Value> values = fieldList.getDefaultValues();
			for (int i = 0; i < values.size(); i++) {
				Field field = fieldList.getField(i);
				if (field.isPossibleValues()) {
					values.set(i, Util.getRandomPossibleValues(field));
					continue;
				} else if (field.isString()) {
					if (field.isPrimaryKey()) {
						values.get(i).setString(Util.getRandomCode(field, 4));
					} else {
						values.get(i).setString(Util.getRandomText(field));
					}
					continue;
				}
				if (field.isDecimal()) {
					values.get(i).setBigDecimal(Util.getRandomDecimal(field));
					continue;
				}
				if (field.isBoolean()) {
					values.get(i).setBoolean(Util.getRandomBoolean());
				}
				if (field.isDate()) {
					values.get(i).setDate(Util.getRandomDate());
				}
			}
			Record record = new Record();
			record.setFieldListAndValues(fieldList, values);
			recordSet.add(record);
		}

		recordSet.sort();

		List<Record> selectedRecords = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			selectedRecords.add(recordSet.get(i));
		}
		double qsales = 0;
		for (Record record : recordSet) {
			qsales += record.getValue("QSALES").getDouble();
		}
		Record total = new Record(fieldList);
		for (int i = 0; i < total.getFieldCount(); i++) {
			total.getValue(i).setNull();
			total.getValue(i).setBackgroundColor(Color.LIGHT_GRAY);
		}
		total.getValue("QSALES").setDouble(qsales);
		total.setProperty(Record.KeyTotal, Boolean.TRUE);
		recordSet.add(total);

		JLookupRecords lookup = new JLookupRecords(new Session(Locale.UK), masterRecord);
		lookup.addColumn("CARTICLE");
		lookup.addColumn("DARTICLE");
		lookup.addColumn("CBUSINESS");
		lookup.addColumn("TCREATED");
		lookup.addColumn("QSALES");
		lookup.addColumn("ICHECKED");
		lookup.addColumn("IREQUIRED");
		lookup.addColumn("ISTATUS");

		lookup.setSelectedRecords(selectedRecords);
		List<Record> selected = lookup.lookupRecords(recordSet);
		for (Record r : selected) {
			System.out.println(r.toString());
		}

		System.exit(0);
	}
}
