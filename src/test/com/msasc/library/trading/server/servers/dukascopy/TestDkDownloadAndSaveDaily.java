/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.io.File;
import java.util.List;
import java.util.TimeZone;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.ConnectionType;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.HistoryManager;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.SystemUtils;

import incubator.persistence.Descriptor;
import incubator.persistence.OHLCVFile;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkDownloadAndSaveDaily {
	static String userName = "msasc1EU";
	static String password = "C1a2r3l4a5";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int error = 0;
		try {
			Instrument instrument = DkUtilities.fromDkInstrument(com.dukascopy.api.Instrument.EURUSD);
			Period period = DkUtilities.fromDkPeriod(com.dukascopy.api.Period.DAILY);
			
			File path = SystemUtils.getFileFromClassPathEntries("files/dukascopy");
			File dataFile = new File(path,OHLCVFile.getFileName(instrument, period));
			
			Descriptor descriptor = new Descriptor();
			descriptor.setInstrument(instrument);
			descriptor.setPeriod(period);
			descriptor.setOHLCV(true);
			
			OHLCVFile file = new OHLCVFile(descriptor, dataFile);
			
			DkServer server = new DkServer();
			server.getConnectionManager().addListener(new DkConnectionListener());
			server.getConnectionManager().connect(userName, password, ConnectionType.Demo);

			Filter filter = Filter.AllFlats;

			HistoryManager history = server.getHistoryManager();
			long first = history.getTimeOfFirstOHLCVData(instrument, period);
			Calendar calendar = new Calendar(2016,4,19,0,0,0,0);
			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			long last = calendar.getTimeInMillis();
			
			List<OHLCV> ohlcvList = history.getOHLCVData(instrument, period, filter, first, last);
			file.open();
			for (OHLCV ohlcv : ohlcvList) {
				file.add(ohlcv);
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
