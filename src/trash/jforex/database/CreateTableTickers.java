/**
 * 
 */
package trash.jforex.database;

import com.qtplaf.library.database.rdbms.DBEngine;

import trash.jforex.database.tables.Tickers;

/**
 * @author Miquel Sas
 */
public class CreateTableTickers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			DBEngine dbEngine = DBEngineFactory.getDBEngineForex();
			Tickers tickers = new Tickers();

			if (dbEngine.existsTable(tickers)) {
				dbEngine.executeDropTable(tickers);
			}
			dbEngine.executeCreateTable(tickers);
			dbEngine.executeAddPrimaryKey(tickers);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		System.exit(0);
	}

}
