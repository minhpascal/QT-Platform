package test.com.msasc.library.database;

import java.io.File;

import com.qtplaf.library.database.Database;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.providers.XMLDatabaseProvider;
import com.qtplaf.library.util.SystemUtils;

public class TestDatabase {

	public static void main(String[] args) {
		try {
			File databases_metadata = SystemUtils.getFileFromClassPathEntries("databases_metadata");
			File parentDirectory = new File(databases_metadata, "test_parser");
			
			XMLDatabaseProvider provider = new XMLDatabaseProvider(parentDirectory);
			provider.setCatalogs(false);
			provider.setSchemas(false);
			Database database = new Database(provider);
			
			Field stringField = database.getField("TEST_STRING_FIELD");
			System.out.println(stringField);
			
			Table table = database.getTable("FULL_TABLE");
			System.out.println(table.getName());
			System.out.println(table.getField("STRING_FIELD"));
		
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
