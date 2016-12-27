/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.io.File;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;
import com.qtplaf.library.util.SystemUtils;

import incubator.persistence.ChunkFile;
import incubator.persistence.OHLCVFile;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkDownloadAndSaveDailyRead {
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
			String dataFileName = OHLCVFile.getFileName(instrument, period);
			String descriptorFileName = ChunkFile.getDescriptorFileName(dataFileName);

			File dataFile = new File(path, dataFileName);
			File descriptorFile = new File(path, descriptorFileName);

			OHLCVFile file = new OHLCVFile(descriptorFile, dataFile);
			file.open();
			OHLCV ohlcv;
			while ((ohlcv = file.read()) != null) {
				System.out.println(ohlcv);
			}
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
