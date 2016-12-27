package test.com.msasc.library.database;

import java.io.File;

import com.qtplaf.library.util.SystemUtils;

public class TestDB2 {

	public static void main(String[] args) {
		try {
			File databases = SystemUtils.getFileFromClassPathEntries("databases");
			File test = new File(databases, "test");
			File db = new File(test, "db");
			System.out.println(databases.getAbsolutePath());
			System.out.println(db.exists());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
