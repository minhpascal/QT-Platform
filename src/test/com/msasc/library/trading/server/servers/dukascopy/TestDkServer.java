/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.qtplaf.library.trading.server.ConnectionType;
import com.qtplaf.library.trading.server.servers.dukascopy.DkServer;

/**
 * Test the Dukascopy server implementation.
 * 
 * @author Miquel Sas
 */
public class TestDkServer {
	static String userName = "DEMO2PuduL";
	static String password = "Pudu";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		DkServer server = new DkServer();
		server.getConnectionManager().addListener(new DkConnectionListener());
		server.getConnectionManager().connect(userName, password, ConnectionType.Demo);
		
		Instrument dkInstrument = Instrument.EURUSD;
		Period dkPeriod = Period.TEN_SECS;
		OfferSide dkOfferSide = OfferSide.ASK;
		
		DkFeedListenerTest listener = new DkFeedListenerTest();
		listener.addCurrentOHLCVSubscription(dkInstrument, dkPeriod, dkOfferSide);
		listener.addOHLCVSubscription(dkInstrument, dkPeriod, dkOfferSide);
		listener.addTickSubscription(dkInstrument);
		server.getFeedManager().addFeedListener(listener);
		
		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < 60000) {
			Thread.sleep(10);
		}
		System.out.println("Removing listener...");
		server.getFeedManager().removeFeedListener(listener);
	}

}
