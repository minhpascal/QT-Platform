/**
 * 
 */
package test.com.msasc.library.trading.server.servers.dukascopy;

import com.qtplaf.library.trading.server.ConnectionEvent;
import com.qtplaf.library.trading.server.ConnectionListener;

/**
 * @author Miquel Sas
 */
public class DkConnectionListener implements ConnectionListener {

	/**
	 * Constructor.
	 */
	public DkConnectionListener() {
	}

	public void status(ConnectionEvent e) {
		System.out.println(e.getMessage());
	}
}
