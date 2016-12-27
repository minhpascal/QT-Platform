package test.com.msasc.library.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.qtplaf.library.util.SystemUtils;

public class TestDB {

	public static void main(String[] args) {
		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver);
			File databases = SystemUtils.getFileFromClassPathEntries("databases");
			File test = new File(databases,"test");
			File db = new File(test,"db");
			System.out.println(databases.getName());
			System.out.println(databases.getPath());
			System.out.println(databases.getAbsolutePath());
			System.out.println(test.getName());
			System.out.println(test.getPath());
			System.out.println(test.getAbsolutePath());
			String connectionURL = "jdbc:derby:" + db.getPath() + ";create=true";
			Connection cn = DriverManager.getConnection(connectionURL);
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
