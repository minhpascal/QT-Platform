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

package com.qtplaf.platform.task;

import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.TaskRunner;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.ServerConnector;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Tables;

/**
 * Task to synchronize server instruments.
 * 
 * @author Miquel Sas
 */
public class SynchronizeServerInstruments extends TaskRunner {

	/** Label trace. */
	private static final String Trace = "Trace";

	/** The server to synchronize. */
	private Server server;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public SynchronizeServerInstruments(Session session, Server server) {
		super(session);
		this.server = server;
		addAdditionalLabel(Trace);
	}

	/**
	 * If the task supports pre-counting steps, a call to this method forces counting (and storing) the number of steps.
	 * This task does not support count steps.
	 * 
	 * @return The number of steps.
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public long countSteps() throws Exception {
		return 0;
	}

	/**
	 * Executes the underlying task processing.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {
		notifyLabel(Trace, "Connecting to server " + server.getName());
		ServerConnector.connect(server);

		notifyLabel(Trace, "Retrieving available instruments");
		List<Instrument> instruments = server.getAvailableInstruments();

		notifyLabel(Trace, "Deleting registered instruments");
		Table table = Tables.getTableInstruments(getSession());
		Persistor persistor = table.getPersistor();
		persistor.delete((Criteria) null);
		
		notifyLabel(Trace, "Inserting availablñe instruments");
		for (Instrument instrument : instruments) {
			Record record = table.getDefaultRecord();
			record.setValue(Fields.ServerId, server.getId());
			record.setValue(Fields.InstrumentId, instrument.getId());
			record.setValue(Fields.InstrumentDesc, instrument.getDescription());
			record.setValue(Fields.InstrumentPipValue, instrument.getPipValue());
			record.setValue(Fields.InstrumentPipScale, instrument.getPipScale());
			record.setValue(Fields.InstrumentTickValue, instrument.getTickValue());
			record.setValue(Fields.InstrumentTickScale, instrument.getTickScale());
			record.setValue(Fields.InstrumentVolumeScale, instrument.getVolumeScale());
			record.setValue(Fields.InstrumentPrimaryCurrency, instrument.getPrimaryCurrency().toString());
			record.setValue(Fields.InstrumentSecondaryCurrency, instrument.getSecondaryCurrency().toString());
			persistor.insert(record);
		}
		
		notifyLabel(Trace, "Disconnecting from server " + server.getName());
		ServerConnector.disconnect(server);
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests. This task does not support cancel.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return false;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * This task does not support counting steps.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return false;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * This task is indeterminate.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return true;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests. This task does not support
	 * pause.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return false;
	}
}
