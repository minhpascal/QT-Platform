/**
 * 
 */
package test.com.msasc.jforex.feed;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;

/**
 * Test strategy on tick and o bar, and feed listener.
 * @author Miquel Sas
 */
public class TestFeed {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestFeed.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
		String userName = "msasc1EU";
		String password = "C1a2r3l4a5";

		System.out.println(LOGGER.getClass());
		IClient client = ClientFactory.getDefaultInstance();
		client.setSystemListener(new SystemListener());
		
		LOGGER.info("Connecting...");
		client.connect(jnlpUrl, userName, password);
		
		// Wait for connection.
		int i = 10; // wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			LOGGER.info("i=" + i);
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			LOGGER.error("Failed to connect Dukascopy servers");
			System.exit(1);
		}

		// Subscribe to the instruments
		Instrument[] instrArr = new Instrument[] { Instrument.EURUSD };
		Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(instrArr));

		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);
		
		LOGGER.info("Starting...");
		client.startStrategy(new Strategy());
		
	}
}
