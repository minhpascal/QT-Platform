/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.io.File;
import java.util.TimeZone;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.SystemUtils;

import incubator.persistence.ChunkFile;
import incubator.persistence.OHLCVFile;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkOHLCVIteratorReadDaily {
	static String userName = "msasc1EU";
	static String password = "C1a2r3l4a5";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int error = 0;
		try {
			Instrument instrument = DkUtilities.fromDkInstrument(com.dukascopy.api.Instrument.EURUSD);
			Period period = DkUtilities.fromDkPeriod(com.dukascopy.api.Period.ONE_HOUR);

			File path = SystemUtils.getFileFromClassPathEntries("files/dukascopy");
			String dataFileName = OHLCVFile.getFileName(instrument, period);
			String descriptorFileName = ChunkFile.getDescriptorFileName(dataFileName);

			File dataFile = new File(path, dataFileName);
			File descriptorFile = new File(path, descriptorFileName);

			OHLCVFile file = new OHLCVFile(descriptorFile, dataFile);
			file.open();
//			OHLCV ohlcv;
//			int count = 1;
//			while ((ohlcv = file.readOHLCV()) != null) {
//				if (count % 5000 == 0) {
//					System.out.println(count + " - " + new Timestamp(ohlcv.getTime()));
//				}
//				count++;
//				System.out.println(ohlcv);
//			}
			OHLCV first = file.first();
			System.out.println(first);
			OHLCV last = file.last();
			System.out.println(last);
			
			Calendar cal = new Calendar(last.getTime());
			cal.setTimeZone(TimeZone.getTimeZone("GTM"));
			cal.addHours(-1);
			
			OHLCV seek = file.seek(cal.getTimeInMillis());
			System.out.println(seek);
			
			System.out.println(file.read());
			
			OHLCV seekFirst = file.seek(first.getTime());
			System.out.println(seekFirst);
			
			file.close();

			error = 0;
		} catch (Exception exc) {
			error = 1;
			throw exc;
		} finally {
			System.exit(error);
		}
	}

}
