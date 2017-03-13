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

package com.qtplaf.platform.database.tables;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Fields;
import com.qtplaf.platform.database.Schemas;
import com.qtplaf.platform.database.Tables;
import com.qtplaf.platform.database.fields.FieldClose;
import com.qtplaf.platform.database.fields.FieldHigh;
import com.qtplaf.platform.database.fields.FieldIndex;
import com.qtplaf.platform.database.fields.FieldLow;
import com.qtplaf.platform.database.fields.FieldOpen;
import com.qtplaf.platform.database.fields.FieldState;
import com.qtplaf.platform.database.fields.FieldTime;
import com.qtplaf.platform.database.fields.FieldTimeFmt;
import com.qtplaf.platform.statistics.averages.States;
import com.qtplaf.platform.statistics.averages.Suffix;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * States table, based on states averages.
 *
 * @author Miquel Sas
 */
public class TableStates extends Table {

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param states The states statistics.
	 */
	public TableStates(Session session, States states) {
		super(session);

		Server server = states.getServer();
		Instrument instrument = states.getInstrument();
		Period period = states.getPeriod();
		String id = states.getId().toLowerCase();

		setName(Tables.ticker(instrument, period, id));
		setSchema(Schemas.server(server));

		// Index and time.
		addField(new FieldIndex(getSession(), Fields.Index));
		addField(new FieldTime(getSession(), Fields.Time));

		// Time formatted.
		addField(new FieldTimeFmt(getSession(), Fields.TimeFmt));

		// Open, high, low, close.
		addField(new FieldOpen(getSession(), instrument, Fields.Open));
		addField(new FieldHigh(getSession(), instrument, Fields.High));
		addField(new FieldLow(getSession(), instrument, Fields.Low));
		addField(new FieldClose(getSession(), instrument, Fields.Close));

		// Averages fields.
		addFields(states.getFieldListAverages());

		// Spreads between averages, raw values.
		addFields(states.getFieldListSpreads(Suffix.raw));

		// Speed (tangent) of averages, raw values
		addFields(states.getFieldListSpeeds(Suffix.raw));

		// Sum of spreads and sum of speeds, raw values.
		addFields(states.getFieldListCalculations(Suffix.raw));

		// Spreads between averages, normalized values continuous.
		addFields(states.getFieldListSpreads(Suffix.nrm));

		// Speed (tangent) of averages, normalized values continuous.
		addFields(states.getFieldListSpeeds(Suffix.nrm));

		// Sum of spreads and sum of speeds, normalizes continuous.
		addFields(states.getFieldListCalculations(Suffix.nrm));

		// Spreads between averages, normalized values discrete.
		addFields(states.getFieldListSpreads(Suffix.dsc));

		// Speed (tangent) of averages, normalized values discrete.
		addFields(states.getFieldListSpeeds(Suffix.dsc));

		// Sum of spreads and sum of speeds, normalizes continuous.
		addFields(states.getFieldListCalculations(Suffix.dsc));

		// The state key.
		addField(new FieldState(getSession(), Fields.State));

		// Primary key on Time.
		getField(Fields.Index).setPrimaryKey(true);

		// Unique index on Index.
		Index indexOnIndex = new Index();
		indexOnIndex.add(getField(Fields.Index));
		indexOnIndex.setUnique(true);
		addIndex(indexOnIndex);

		// Non unique index on the state key.
		Index indexOnKeyState = new Index();
		indexOnKeyState.add(getField(Fields.State));
		indexOnKeyState.setUnique(false);
		addIndex(indexOnKeyState);

		setPersistor(PersistorUtils.getPersistor(getSimpleView()));
	}

}
