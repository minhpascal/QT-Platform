/**
 * 
 */
package trash.jforex.database;

/**
 * Test the CheckFirstData strategy
 * 
 * @author Miquel Sas
 */
public class TestCheckFirstData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CheckFirstData check = new CheckFirstData();
			StrategyTester tester = new StrategyTester(check);
			tester.start();
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

}
