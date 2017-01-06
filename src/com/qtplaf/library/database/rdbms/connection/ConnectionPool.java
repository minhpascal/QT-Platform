/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A connection pool.
 *
 * @author Miquel Sas
 */
public class ConnectionPool {

	/**
	 * The TimerTask that closes the connections.
	 */
	class Closer extends TimerTask {

		/**
		 * Default constructor.
		 */
		public Closer() {
			super();
		}

		/**
		 * Closes the connections.
		 */
		@Override
		public void run() {
			try {
				closeConnections();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * The list of connections in the pool.
	 */
	private final List<PoolableConnection> connections = new ArrayList<>();
	/**
	 * Lock to scan connections.
	 */
	private final Object lock = "LOCK";
	/**
	 * Timeout to close free or user closed connections, by default one minute.
	 */
	private long timeoutClosed = 60000;
	/**
	 * Connection info.
	 */
	private ConnectionInfo connectionInfo = null;
	/**
	 * The closer timer.
	 */
	private final Timer closerTimer;
	/**
	 * The closer task.
	 */
	private Closer closer;

	/**
	 * Constructor with one parameter.
	 *
	 * @param connectionInfo The connection information.
	 */
	public ConnectionPool(ConnectionInfo connectionInfo) {
		super();
		this.connectionInfo = connectionInfo;

		String closerName = "ConnectionPool.Closer." + connectionInfo.getId();
		this.closerTimer = new Timer(closerName);
		this.closer = new Closer();
		this.closerTimer.schedule(this.closer, timeoutClosed, timeoutClosed);
	}

	/**
	 * Scan connections to close.
	 *
	 * @throws SQLException
	 */
	private void closeConnections() throws SQLException {
		long time = System.currentTimeMillis();
		synchronized (lock) {
			for (int i = 0; i < connections.size(); i++) {
				PoolableConnection connection = connections.get(i);
				if (connection.isClosed()) {
					if (time - connection.getTimeLastUsed() >= timeoutClosed) {
						connection.reallyClose();
						connections.remove(connection);
					}
				}
			}
		}
	}

	/**
	 * Returns the a connection.
	 *
	 * @return A connection.
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		synchronized (lock) {
			for (PoolableConnection connection : connections) {
				if (connection.isClosed()) {
					connection.setClosed(false);
					connection.setAutoCommit(true);
					return connection;
				}
			}
			String url = connectionInfo.getURL();
			String user = connectionInfo.getUser();
			String password = connectionInfo.getPassword();
			Connection underlyingConnection = DriverManager.getConnection(url,
					user, password);
			PoolableConnection connection = new PoolableConnection(
					underlyingConnection);
			connection.setClosed(false);
			connection.setAutoCommit(true);
			connections.add(connection);
			return connection;
		}
	}

	/**
	 * Close all connections.
	 *
	 * @throws SQLException
	 */
	private void reallyCloseAll() throws SQLException {
		for (PoolableConnection connection : connections) {
			connection.reallyClose();
		}
		connections.clear();
	}

	/**
	 *
	 * @return The timeoutClosed.
	 */
	public long getTimeoutClosed() {
		return timeoutClosed;
	}

	/**
	 * @param timeoutClosed The timeoutClosed to set.
	 */
	public void setTimeoutClosed(long timeoutClosed) {
		this.timeoutClosed = timeoutClosed;
		closer.cancel();
		closerTimer.purge();
		closer = new Closer();
		this.closerTimer.schedule(this.closer, timeoutClosed, timeoutClosed);
	}

	/**
	 * Closes and disposes the connection pool object
	 */
	public void close() {
		try {
			reallyCloseAll();
		} catch (SQLException e) {
		}
		closer.cancel();
		closerTimer.cancel();
		connections.clear();
		connectionInfo = null;
	}

}
