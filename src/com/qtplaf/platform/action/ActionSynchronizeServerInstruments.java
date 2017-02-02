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

package com.qtplaf.platform.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Criteria;
import com.qtplaf.library.database.Persistor;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.swing.ActionUtils;
import com.qtplaf.library.swing.core.StatusBar;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.LaunchArgs;
import com.qtplaf.platform.ServerConnector;
import com.qtplaf.platform.database.tables.Instruments;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Synchronize server available instruments.
 * 
 * @author Miquel Sas
 */
public class ActionSynchronizeServerInstruments extends AbstractAction {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Runnable to perform synchronizing.
	 */
	class Synchronizer implements Runnable {
		public void run() {
			try {
				Session session = ActionUtils.getSession(ActionSynchronizeServerInstruments.this);
				Server server = LaunchArgs.getServer(ActionSynchronizeServerInstruments.this);
				StatusBar statusBar = ActionUtils.getStatusBar(ActionSynchronizeServerInstruments.this);

				statusBar.setStatus("Connecting to server " + server.getName(), 1, 5);
				ServerConnector.connect(server);

				statusBar.setStatus("Retrieving available instruments", 2, 5);
				List<Instrument> instruments = server.getAvailableInstruments();

				statusBar.setStatus("Deleting registered instruments", 3, 5);
				Persistor persistor = PersistorUtils.getPersistorInstruments(session);
				persistor.delete((Criteria) null);

				statusBar.setStatus("Inserting available instruments", 4, 5);
				for (Instrument instrument : instruments) {
					Record record = persistor.getDefaultRecord();
					record.setValue(Instruments.Fields.ServerId, server.getId());
					record.setValue(Instruments.Fields.InstrumentId, instrument.getId());
					record.setValue(Instruments.Fields.InstrumentDesc, instrument.getDescription());
					record.setValue(Instruments.Fields.InstrumentPipValue, instrument.getPipValue());
					record.setValue(Instruments.Fields.InstrumentPipScale, instrument.getPipScale());
					record.setValue(Instruments.Fields.InstrumentTickValue, instrument.getTickValue());
					record.setValue(Instruments.Fields.InstrumentTickScale, instrument.getTickScale());
					record.setValue(Instruments.Fields.InstrumentVolumeScale, instrument.getVolumeScale());
					record.setValue(
						Instruments.Fields.InstrumentPrimaryCurrency,
						instrument.getPrimaryCurrency().toString());
					record.setValue(
						Instruments.Fields.InstrumentSecondaryCurrency,
						instrument.getSecondaryCurrency().toString());
					persistor.insert(record);
				}

				statusBar.setStatus("Disconnecting from server " + server.getName(), 5, 5);
				ServerConnector.disconnect(server);

				statusBar.clearStatus();

			} catch (Exception exc) {
				logger.catching(exc);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public ActionSynchronizeServerInstruments() {
		super();
	}

	/**
	 * Perform the action.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new Synchronizer()).start();
	}
}
