/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.io.File;
import java.util.TimeZone;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.AccountType;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.HistoryManager;
import com.qtplaf.library.trading.server.OHLCVIterator;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.SystemUtils;

import incubator.persistence.Descriptor;
import incubator.persistence.OHLCVFile;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkOHLCVIterator {
	/** Logger configuration. */
	static {
		System.setProperty("log4j.configurationFile", "LoggerQTPlatform.xml");
	}
	static String userName = "msasc2EU";
	static String password = "C1a2r3l4a5";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int error = 0;
		try {
			DkServer server = new DkServer();
			Instrument instrument = server.getDkConverter().fromDkInstrument(com.dukascopy.api.Instrument.EURUSD);
			Period period = server.getDkConverter().fromDkPeriod(com.dukascopy.api.Period.ONE_MIN);
			
			server.getConnectionManager().addListener(new DkConnectionListener());
			server.getConnectionManager().connect(userName, password, AccountType.Demo);

			Filter filter = Filter.AllFlats;

			HistoryManager history = server.getHistoryManager();
			long from = history.getTimeOfFirstOHLCVData(instrument, period);
			Calendar calendar = new Calendar(2016,4,19,0,0,0,0);
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			long to = calendar.getTimeInMillis();

			Descriptor descriptor = new Descriptor();
			descriptor.setInstrument(instrument);
			descriptor.setPeriod(period);
			descriptor.setOHLCV(true);
			
			File path = SystemUtils.getFileFromClassPathEntries("files/dukascopy");
			File dataFile = new File(path,OHLCVFile.getFileName(instrument, period));
			
			OHLCVFile file = new OHLCVFile(descriptor, dataFile);
			file.open();
			file.truncate();
			
			OHLCVIterator iter = history.getOHLCVIterator(instrument, period, filter, from, to);
			int count = 1;
			while (iter.hasNext()) {
				OHLCV ohlcv = iter.next();
				file.add(ohlcv);
				count++;
				if (count % 10000 == 0) {
					System.out.println(count+" - "+ohlcv);
				}
			}
			file.close();

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
