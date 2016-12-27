/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import com.qtplaf.library.trading.server.feed.OHLCVEvent;
import com.qtplaf.library.trading.server.feed.TickEvent;
import com.qtplaf.library.trading.server.servers.dukascopy.listeners.DkFeedListener;

/**
 * @author Miquel Sas
 */
public class DkFeedListenerTest extends DkFeedListener {
	public DkFeedListenerTest() {
	}
	public void onCurrentOHLCV(OHLCVEvent ohlcvEvent) {
		System.out.println("Current: "+ohlcvEvent);
	}
	public void onOHLCV(OHLCVEvent ohlcvEvent) {
		System.out.println("Completed: "+ohlcvEvent);
	}
	public void onTick(TickEvent tickEvent) {
		System.out.println("Tick: "+tickEvent);
	}
}
