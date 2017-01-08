/*
 * Copyright (C) 2015 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.database.rdbms.connection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A JDBC connection that can be pooled.
 *
 * @author Miquel Sas
 */
public class PoolableConnection implements Connection {

	/**
	 * Error message when the pooled connection is closed.
	 */
	private static final String CLOSED = "PooledConnection is closed";
	/**
	 * Close control flag (to not reuse the underlying connection.
	 */
	private boolean closed = false;
	/**
	 * The underlying connection.
	 */
	private final Connection cn;
	/**
	 * The time in milliseconds when this connection was last used.
	 */
	private long timeLastUsed = 0;
	/**
	 * The list of delegating callable statements.
	 */
	private final ArrayList<DelegatingCallableStatement> callableStatements = new ArrayList<>();
	/**
	 * The list of delegating prepared statements.
	 */
	private final ArrayList<DelegatingPreparedStatement> preparedStatements = new ArrayList<>();
	/**
	 * The list of delegating statements
	 */
	private final ArrayList<DelegatingStatement> statements = new ArrayList<>();

	/**
	 * Package private constructor.
	 */
	PoolableConnection(Connection cn) {
		super();
		this.cn = cn;
	}

	void removeDelegatingCallableStatement(DelegatingCallableStatement cs) {
		callableStatements.remove(cs);
	}

	void removeDelegatingPreparedStatement(DelegatingPreparedStatement ps) {
		preparedStatements.remove(ps);
	}

	void removeDelegatingStatement(DelegatingStatement st) {
		statements.remove(st);
	}

	private DelegatingStatement createDelegatingStatement(Statement st) {
		DelegatingStatement dst = new DelegatingStatement(this, st);
		statements.add(dst);
		return dst;
	}

	private DelegatingCallableStatement createDelegatingCallableStatement(CallableStatement cs) {
		DelegatingCallableStatement dcs = new DelegatingCallableStatement(this, cs);
		callableStatements.add(dcs);
		return dcs;
	}

	private DelegatingPreparedStatement createDelegatingPreparedStatement(PreparedStatement ps) {
		DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, ps);
		preparedStatements.add(dps);
		return dps;
	}

	@Override
	public void clearWarnings() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		closeStatements();
		setClosed(true);
		timeLastUsed = System.currentTimeMillis();
	}

	private void closeStatements() throws SQLException {
		while (!callableStatements.isEmpty()) {
			if (!callableStatements.get(0).isClosed()) {
				callableStatements.get(0).close();
			}
		}
		while (!preparedStatements.isEmpty()) {
			if (!preparedStatements.get(0).isClosed()) {
				preparedStatements.get(0).close();
			}
		}
		while (!statements.isEmpty()) {
			if (!statements.get(0).isClosed()) {
				statements.get(0).close();
			}
		}
	}

	/**
	 * Set the closed flag.
	 *
	 * @param closed
	 */
	void setClosed(boolean closed) {
		this.closed = closed;
		timeLastUsed = System.currentTimeMillis();
	}

	/**
	 * Really close the underlying connection.
	 * 
	 * @throws SQLException
	 */
	void reallyClose() throws SQLException {
		cn.close();
		closed = true;
		timeLastUsed = System.currentTimeMillis();
	}

	@Override
	public void commit() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.commit();
	}

	@Override
	public Statement createStatement() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingStatement(cn.createStatement());
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingStatement(cn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingStatement(cn.createStatement(resultSetType, resultSetConcurrency));
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getCatalog();
	}

	@Override
	public int getHoldability() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getMetaData();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.isReadOnly();
	}

	/**
	 * Check if the underlying connection is closed.
	 *
	 * @return A boolean
	 * @throws SQLException
	 */
	public boolean isReallyClosed() throws SQLException {
		return cn.isClosed();
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(
		String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingCallableStatement(cn.prepareCall(
			sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingCallableStatement(cn.prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingCallableStatement(cn.prepareCall(sql));
	}

	@Override
	public PreparedStatement prepareStatement(
		String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
		throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(
			sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
		throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(sql, autoGeneratedKeys));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(sql, columnIndexes));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(sql, columnNames));
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return createDelegatingPreparedStatement(cn.prepareStatement(sql));
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.rollback();
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.rollback(savepoint);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setAutoCommit(autoCommit);
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setCatalog(catalog);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setHoldability(holdability);
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		return cn.setSavepoint(name);
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		if (closed) {
			throw new SQLException(CLOSED);
		}
		timeLastUsed = System.currentTimeMillis();
		cn.setTypeMap(map);
	}

	/**
	 * @return The last time this connection was used.
	 */
	public long getTimeLastUsed() {
		return timeLastUsed;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.unwrap(iface);
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		cn.abort(executor);
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
		throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createSQLXML();
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
		throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.createStruct(typeName, attributes);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.getClientInfo(name);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.getNetworkTimeout();
	}

	@Override
	public String getSchema() throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.getSchema();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		return cn.isValid(timeout);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		timeLastUsed = System.currentTimeMillis();
		cn.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		timeLastUsed = System.currentTimeMillis();
		cn.setClientInfo(name, value);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		cn.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		timeLastUsed = System.currentTimeMillis();
		cn.setSchema(schema);
	}

}
