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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * A delegating statement for poolable connections.
 *
 * @author Miquel Sas
 */
public class DelegatingStatement implements Statement {

	/**
	 * The underlying statement.
	 */
	private Statement st = null;
	/**
	 * The poolable connection that holds this statement.
	 */
	private PoolableConnection cn = null;

	/**
	 * Constructor.
	 *
	 * @param cn The poolable connection that holds this statement.
	 * @param st The underlying statement.
	 */
	public DelegatingStatement(PoolableConnection cn, Statement st) {
		this.cn = cn;
		this.st = st;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		st.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		st.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		st.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		st.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		cn.removeDelegatingStatement(this);
		st.close();
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return st.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return st.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return st.execute(sql, columnNames);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return st.execute(sql);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return st.executeBatch();
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return st.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return st.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return st.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return st.executeUpdate(sql, columnNames);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return st.executeUpdate(sql);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return st.getConnection();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return st.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return st.getFetchSize();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return st.getGeneratedKeys();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return st.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return st.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return st.getMoreResults();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return st.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return st.getQueryTimeout();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return st.getResultSet();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return st.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return st.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return st.getResultSetType();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return st.getUpdateCount();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return st.getWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		st.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		st.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		st.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		st.setFetchSize(rows);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		st.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		st.setMaxRows(max);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		st.setQueryTimeout(seconds);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return st.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return st.isWrapperFor(iface);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return st.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		st.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return st.isPoolable();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		st.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return st.isCloseOnCompletion();
	}

}
