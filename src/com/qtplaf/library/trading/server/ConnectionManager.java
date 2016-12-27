/**
 * 
 */
package com.qtplaf.library.trading.server;

/**
 * Interface responsible to manage connections.
 * 
 * @author Miquel Sas
 */
public interface ConnectionManager {

	/**
	 * Add a connection listener to receive connection events.
	 * 
	 * @param listener The connection listener.
	 */
	void addListener(ConnectionListener listener);

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
	void connect(String username, String password, ConnectionType connectionType) throws ServerException;

	/**
	 * Disconnect from the server.
	 * 
	 * @throws ServerException
	 */
	void disconnect() throws ServerException;

	/**
	 * Returns the connection type of the connection or null if not connected.
	 * 
	 * @return The connection type.
	 */
	ConnectionType getConnectionType();

	/**
	 * Returns a boolean indicating if the client is correctly connected to the server.
	 * 
	 * @return A boolean indicating if the client is correctly connected to the server.
	 */
	boolean isConnected();

	/**
	 * Tries to reconnect to the server using the current client information.
	 * 
	 * @throws ServerException
	 */
	void reconnect() throws ServerException;
}
