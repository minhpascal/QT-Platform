/**
 * 
 */
package trash.jforex.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import com.qtplaf.library.database.rdbms.DBEngine;
import com.qtplaf.library.database.rdbms.DBEngineAdapter;
import com.qtplaf.library.database.rdbms.adapters.DerbyAdapterEmbedded;
import com.qtplaf.library.database.rdbms.connection.ConnectionInfo;
import com.qtplaf.library.util.SystemUtils;

/**
 * Factory of used <i>DBEngine</i>'s.
 * 
 * @author Miquel Sas
 */
public class DBEngineFactory {
	
	/**
	 * Forex <i>DBEngine</i> unique instance.
	 */
	private static DBEngine forexDBEngine;
	
	/**
	 * Returns the <i>DBEngine</i> to access the local Forex download database.
	 * 
	 * @return The <i>DBEngine</i>
	 * @throws FileNotFoundException 
	 * @throws SQLException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static DBEngine getDBEngineForex() 
		throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		if (forexDBEngine == null) {
			DBEngineAdapter databaseAdapter = new DerbyAdapterEmbedded();
			ConnectionInfo connectionInfo = new ConnectionInfo();
			connectionInfo.setId("FOREX");
			connectionInfo.setDescription("Forex download database");
			connectionInfo.setDriver("jdbc:derby:");
			File databases = SystemUtils.getFileFromClassPathEntries("databases");
			File forex = new File(databases,"forex");
			System.out.println(forex.getAbsolutePath());
			connectionInfo.setDatabase(forex.getPath());
			forexDBEngine = new DBEngine(databaseAdapter, connectionInfo);
		}
		return forexDBEngine;
	}
}
