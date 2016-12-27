/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qtplaf.library.trading.server.ConnectionListener;
import com.qtplaf.library.trading.server.ConnectionManager;
import com.qtplaf.library.trading.server.ConnectionType;
import com.qtplaf.library.trading.server.ServerException;

/**
 * Dukascopy connection manager implementation.
 * 
 * @author Miquel Sas
 */
public class DkConnectionManager implements ConnectionManager {
	
	/**
	 * Dukascopy uses this logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DkConnectionManager.class);

	/**
	 * Server reference.
	 */
	private DkServer server;
	/**
	 * The connection type.
	 */
	private ConnectionType connectionType;

	/**
	 * Constructor assigning the reference server.
	 * 
	 * @param server The Dukascopy server.
	 */
	public DkConnectionManager(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Add a connection listener to receive connection events.
	 * 
	 * @param listener The connection listener.
	 */
	public void addListener(ConnectionListener listener) {
		server.getSystemListener().addConnectionListener(listener);
	}

	/**
	 * Connect to the server, using the given string and password, for the given connection type.
	 * <p>
	 * It is the resposibility of the server implementation to ask for any additional information to connect, like for
	 * instance a PIN code.
	 * 
	 * @param username The user name.
	 * @param password The password.
	 * @param connectionType The type of connection.
	 * @throws ServerException
	 */
	public void connect(String username, String password, ConnectionType connectionType) throws ServerException {

		// Check if already connected.
		if (isConnected()) {
			throw new ServerException("Server is already connected, please disconnect first.");
		}

		// Remember the connection type.
		this.connectionType = connectionType;

		// Set the corresponding URL.
		String url = null;
		switch (connectionType) {
		case Demo:
			url = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
			break;
		case Live:
			break;
		case Test:
			break;
		default:
			break;
		}
		// TODO: Correctly implement live and test.

		// Do connect.
		try {
			server.getClient().connect(url, username, password);
			
			// Wait for connection.
			int i = 10; // wait max ten seconds
			while (i > 0 && !server.getClient().isConnected()) {
				LOGGER.info("i=" + i);
				Thread.sleep(1000);
				i--;
			}
			if (!server.getClient().isConnected()) {
				LOGGER.error("Failed to connect Dukascopy servers");
				System.exit(1);
			}
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
		
		// Install the strategy listener.
		server.getClient().startStrategy(server.getStrategyListener());
	}

	/**
	 * Disconnect from the server.
	 * 
	 * @throws ServerException
	 */
	public void disconnect() throws ServerException {
		server.getClient().disconnect();
	}

	/**
	 * Returns the connection type of the connection or null if not connected.
	 * 
	 * @return The connection type.
	 */
	public ConnectionType getConnectionType() {
			return connectionType;
	}

	/**
	 * Returns a boolean indicating if the client is correctly connected to the server.
	 * 
	 * @return A boolean indicating if the client is correctly connected to the server.
	 */
	public boolean isConnected() {
		return server.getClient().isConnected();
	}

	/**
	 * Tries to reconnect to the server using the current client information.
	 * 
	 * @throws ServerException
	 */
	public void reconnect() throws ServerException {
		try {
			server.getClient().reconnect();
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}
}
