/**
 * 
 */
package trash.jforex.database;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.sql.Select;

import trash.jforex.database.tables.Instruments;

/**
 * @author Miquel Sas
 *
 */
public class CreateTableInstruments {
	public static void main(String[] args) throws Exception {

		try {

			DBEngine dbEngine = DBEngineFactory.getDBEngineForex();
			Instruments instruments = new Instruments();

			if (dbEngine.existsTable(instruments)) {
				dbEngine.executeDropTable(instruments);
			}
			dbEngine.executeCreateTable(instruments);
			dbEngine.executeAddPrimaryKey(instruments);

			Record record = instruments.getDefaultRecord();
			record.setValue("CODE", "EURUSD");
			record.setValue("DESCRIPTION", "EUR/USD Forex exchange");
			record.setValue("PIP_VALUE", 0.0001);
			record.setValue("PIP_SCALE", 4);
			dbEngine.executeInsert(instruments, record);

			Select select = new Select();
			select.setView(instruments.getSimpleView(null));
			RecordSet rs = dbEngine.executeSelectRecordSet(select);
			for (Record rc : rs) {
				System.out.println(
					rc.getValue("CODE") + " - " +
						rc.getValue("DESCRIPTION") + " - " +
						rc.getValue("PIP_VALUE") + " - " +
						rc.getValue("PIP_SCALE"));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		System.exit(0);
	}
}
