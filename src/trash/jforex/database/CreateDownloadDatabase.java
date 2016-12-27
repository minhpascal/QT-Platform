package trash.jforex.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.qtplaf.library.util.SystemUtils;

public class CreateDownloadDatabase {

	public static void main(String[] args) {
		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver);
			File databases = SystemUtils.getFileFromClassPathEntries("databases");
			File forex = new File(databases,"forex");
			System.out.println(forex.getAbsolutePath());
			String connectionURL = "jdbc:derby:" + forex.getPath() + ";create=true";
			Connection cn = DriverManager.getConnection(connectionURL);
			cn.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
