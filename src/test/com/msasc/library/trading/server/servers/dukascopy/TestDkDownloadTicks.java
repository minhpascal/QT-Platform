/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.util.List;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.server.AccountType;
import com.qtplaf.library.trading.server.HistoryManager;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;
import com.qtplaf.library.util.Calendar;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkDownloadTicks {
	static String userName = "msasc1EU";
	static String password = "C1a2r3l4a5";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int error = 0;
		try {
			DkServer server = new DkServer();
			server.getConnectionManager().addListener(new DkConnectionListener());
			server.getConnectionManager().connect(userName, password, AccountType.Demo);

			Instrument instrument = DkUtilities.fromDkInstrument(com.dukascopy.api.Instrument.EURUSD);

			HistoryManager history = server.getHistoryManager();
			// long first = history.getTimeOfFirstTick(instrument);
			// Timestamp timestamp = new Timestamp(first);
			long first = new Calendar(2016, 4, 13, 9, 0).getTimeInMillis();
//			System.out.println("First candle: " + timestamp);
			Calendar calendar = new Calendar(2016, 4, 14, 9, 0);
			long last = calendar.getTimeInMillis();
			List<Tick> tickList = history.getTickData(instrument, first, last);
			for (Tick tick : tickList) {
				System.out.println(tick);
			}
			server.getConnectionManager().disconnect();
			error = 0;
		} catch (Exception exc) {
			error = 1;
			throw exc;
		} finally {
			System.exit(error);
		}
	}

}
