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
package com.qtplaf.library.trading.server;

/**
 * Enumerates the three possible types of connections.
 * 
 * @author Miquel Sas
 */
public enum ConnectionType {
	/**
	 * Normal live connection, supported by all servers (brokers).
	 */
	Live,
	/**
	 * Normal demo connection, also supported by the majority of servers.
	 */
	Demo,
	/**
	 * Test connection. This kind of connection is implemented by this system to provide a way to reproduce the history
	 * in order to test strategies and studies.
	 */
	Test;
}
