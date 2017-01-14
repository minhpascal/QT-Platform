/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.Tick;
import com.qtplaf.library.trading.server.AccountType;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.HistoryManager;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;
import com.qtplaf.library.trading.server.servers.dukascopy.DkUtilities;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.Timestamp;

/**
 * Test download from history.
 * 
 * @author Miquel Sas
 */
public class TestDkDownloadBars {
	static String userName = "msasc2EU";
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
			Period period = DkUtilities.fromDkPeriod(com.dukascopy.api.Period.ONE_HOUR);
			Filter filter = Filter.AllFlats;

			HistoryManager history = server.getHistoryManager();
			long first = history.getTimeOfFirstOHLCVData(instrument, period);
			Timestamp timestamp = new Timestamp(first);
			System.out.println("First candle: " + timestamp);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//			Date dateTo = dateFormat.parse("14/04/2016 00:00:00");
			
			Tick lastTick = history.getLastTick(instrument);
			Calendar tickCalendar = new Calendar(lastTick.getTime());
			int year = tickCalendar.getYear();
			int month = tickCalendar.getMonth();
			int day = tickCalendar.getDay();
//			int hour = tickCalendar.getHour();
			
			Calendar calendar = Calendar.getGTMCalendar(year,month,day,0,0,0,0);
//			calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
//			calendar.setYear(2016);
//			calendar.setMonth(4);
//			calendar.setDay(14);
//			calendar.setHour(0);
//			calendar.setMinute(0);
//			calendar.setSecond(0);
//			calendar.setMilliSecond(0);
//			long last = dateTo.getTime();
			
			OHLCV ohlcvCurrent = history.getOHLCV(instrument, period, 0);
			long last = ohlcvCurrent.getTime();
			
			long lastCal = calendar.getTimeInMillis();
			List<OHLCV> ohlcvList = history.getOHLCVData(instrument, period, filter, first, last);
			for (OHLCV ohlcv : ohlcvList) {
				System.out.println(ohlcv);
			}
			server.getConnectionManager().disconnect();
			error = 0;
		} catch (Exception exc) {
			error = 1;
			exc.printStackTrace();
		} finally {
			System.exit(error);
		}
	}

}
