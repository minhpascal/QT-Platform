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

package com.qtplaf.platform.statistics;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class StatesAverages extends Statistics {

	/** The server. */
	private Server server;
	/** The instrument. */
	private Instrument instrument;
	/** The period. */
	private Period period;

	/** The configuration. */
	private Configuration configuration;

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public StatesAverages(Session session) {
		super(session);
	}

	/**
	 * Returns the server.
	 * 
	 * @return The server.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Set the server.
	 * 
	 * @param server The server
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * Set the instrument.
	 * 
	 * @param instrument The instrument.
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	/**
	 * Returns the period.
	 * 
	 * @return The period.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Set the period.
	 * 
	 * @param period The period.
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Returns the configuration.
	 * 
	 * @return The configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the configuration.
	 * 
	 * @param configuration The configuration.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the list of average fields.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListAverages() {
		List<Field> fields = new ArrayList<>();
		for (Average average : getConfiguration().getAverages()) {
			fields.add(getConfiguration().getFieldAverage(average));
		}
		return fields;
	}

	/**
	 * Returns the list of fields for spreads between the field and the fast average, raw values.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsAverageRaw() {
		return getFieldListSpreadsAverage("raw");
	}

	/**
	 * Returns the list of fields for spreads between the field and the fast average, normalized continuous values.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsAverageNormalizedContinuous() {
		return getFieldListSpreadsAverage("nrm");
	}

	/**
	 * Returns the list of fields for spreads between the field and the fast average, normalized discrete values.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsAverageNormalizedDiscrete() {
		return getFieldListSpreadsAverage("dsc");
	}

	/**
	 * Returns the list of fields for spreads between the field and the fast average, raw values.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	private List<Field> getFieldListSpreadsAverage(String suffix) {
		List<Field> fields = new ArrayList<>();
		Average averageFast = getConfiguration().getAverages().get(0);
		Field high = getConfiguration().getFieldHigh();
		Field low = getConfiguration().getFieldLow();
		Field close = getConfiguration().getFieldClose();
		fields.add(getConfiguration().getFieldSpread(high, averageFast, suffix));
		fields.add(getConfiguration().getFieldSpread(low, averageFast, suffix));
		fields.add(getConfiguration().getFieldSpread(close, averageFast, suffix));
		return fields;
	}

	/**
	 * Returns the list of spread fields, raw values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsRaw() {
		return getFieldListSpreads("raw");
	}

	/**
	 * Returns the list of spread fields, normalized continuous values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsNormalizedContinuous() {
		return getFieldListSpreads("nrm");
	}

	/**
	 * Returns the list of spread fields, normalized discrete values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsNormalizedDiscrete() {
		return getFieldListSpreads("dsc");
	}

	/**
	 * Returns the list of spread fields, raw values
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	private List<Field> getFieldListSpreads(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (Spread spread : getConfiguration().getSpreads()) {
			fields.add(getConfiguration().getFieldSpread(spread, suffix));
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, raw values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsRaw() {
		return getFieldListSpeeds("raw");
	}

	/**
	 * Returns the list of speed fields, normalized continuous values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsNormalizedContinuous() {
		return getFieldListSpeeds("nrm");
	}

	/**
	 * Returns the list of speed fields, normalized discrete values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsNormalizedDiscrete() {
		return getFieldListSpeeds("dsc");
	}

	/**
	 * Returns the list of speed fields, raw values
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeeds(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (Speed speed : getConfiguration().getSpeeds()) {
			fields.add(getConfiguration().getFieldSpeed(speed, suffix));
		}
		return fields;
	}

	/**
	 * Returns the list of fields to calculate the state key.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListStateKey() {
		List<Field> spreadFields = new ArrayList<>();
		spreadFields.addAll(getFieldListSpreadsNormalizedDiscrete());
		List<Field> speedFields = new ArrayList<>();
		speedFields.addAll(getFieldListSpeedsNormalizedDiscrete());
		List<Field> keyFields = new ArrayList<>();
		for (Field field : spreadFields) {
			Spread spread = getConfiguration().getPropertySpread(field);
			if (spread.isStateKey()) {
				keyFields.add(field);
			}
		}
		for (Field field : speedFields) {
			Speed speed = getConfiguration().getPropertySpeed(field);
			if (speed.isStateKey()) {
				keyFields.add(field);
			}
		}
		return keyFields;
	}

	/**
	 * Returns the table definition to calculate ranges for minimums and maximums.
	 * 
	 * @return The table definition.
	 */
	protected Table getTableRanges() {
		validateState();

		Table table = new Table();

		table.setName(Names.getName(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Names.getSchema(getServer()));

		table.addField(getConfiguration().getFieldName());
		table.addField(getConfiguration().getFieldMinMax());
		table.addField(getConfiguration().getFieldPeriod());
		table.addField(getConfiguration().getFieldValue());
		table.addField(getConfiguration().getFieldIndex());
		table.addField(getConfiguration().getFieldTime());

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(getConfiguration().getFieldName());
		index.add(getConfiguration().getFieldMinMax());
		index.add(getConfiguration().getFieldPeriod());
		index.setUnique(false);
		table.addIndex(index);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Returns the table definition for states values.
	 * 
	 * @return The table definition.
	 */
	protected Table getTableStates() {
		validateState();

		Table table = new Table();

		table.setName(Names.getName(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Names.getSchema(getServer()));

		// Index and time.
		table.addField(getConfiguration().getFieldIndex());
		table.addField(getConfiguration().getFieldIndex());

		// Time formatted.
		getConfiguration().getFieldTimeFmt().setFormatter(new TimeFmtValue(getPeriod().getUnit()));
		getConfiguration().getFieldTimeFmt().setCalculator(new TimeFmtValue(getPeriod().getUnit()));
		table.addField(getConfiguration().getFieldTimeFmt());

		// Open, high, low, close.
		table.addField(getConfiguration().getFieldOpen());
		table.addField(getConfiguration().getFieldHigh());
		table.addField(getConfiguration().getFieldLow());
		table.addField(getConfiguration().getFieldClose());

		// Averages fields.
		table.addFields(getFieldListAverages());

		// Price spreads over the first (fastest) average, raw values.
		table.addFields(getFieldListSpreadsAverageRaw());

		// Spreads between averages, raw values.
		table.addFields(getFieldListSpreadsRaw());

		// Speed (tangent) of averages, raw values
		table.addFields(getFieldListSpeedsRaw());

		// Price spreads over the first (fastest) average, normalized values continuous.
		table.addFields(getFieldListSpreadsAverageNormalizedContinuous());

		// Spreads between averages, normalized values continuous.
		table.addFields(getFieldListSpreadsNormalizedContinuous());

		// Speed (tangent) of averages, normalized values continuous.
		table.addFields(getFieldListSpeedsNormalizedContinuous());

		// Price spreads over the first (fastest) average, normalized values discrete.
		table.addFields(getFieldListSpreadsAverageNormalizedDiscrete());

		// Spreads between averages, normalized values discrete.
		table.addFields(getFieldListSpreadsNormalizedDiscrete());

		// Speed (tangent) of averages, normalized values discrete.
		table.addFields(getFieldListSpeedsNormalizedDiscrete());

		// The state key.
		table.addField(getConfiguration().getFieldKeyState());

		// Primary key on Time.
		getConfiguration().getFieldIndex().setPrimaryKey(true);

		// Unique index on Index.
		Index indexOnIndex = new Index();
		indexOnIndex.add(getConfiguration().getFieldIndex());
		indexOnIndex.setUnique(true);
		table.addIndex(indexOnIndex);

		// Non unique index on the state key.
		Index indexOnKeyState = new Index();
		indexOnKeyState.add(getConfiguration().getFieldKeyState());
		indexOnKeyState.setUnique(false);
		table.addIndex(indexOnKeyState);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Returns the table definition for states transitions.
	 * 
	 * @return The table definition.
	 */
	protected Table getTableTransitions() {
		validateState();

		Table table = new Table();

		table.setName(Names.getName(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Names.getSchema(getServer()));

		// Input and output states (keys)
		table.addField(getConfiguration().getFieldKeyInput());
		table.addField(getConfiguration().getFieldKeyOutput());

		// Input and output indexes of source states.
		table.addField(getConfiguration().getFieldIndexInput());
		table.addField(getConfiguration().getFieldIndexOutput());

		// Index group (groups consecutive transitions of the same state).
		table.addField(getConfiguration().getFieldIndexGroup());

		// Estimaded function value high, low and close.
		table.addField(getConfiguration().getFieldTransitionValueHigh());
		table.addField(getConfiguration().getFieldTransitionValueLow());
		table.addField(getConfiguration().getFieldTransitionValueClose());

		// Primary key.
		getConfiguration().getFieldKeyInput().setPrimaryKey(true);
		getConfiguration().getFieldKeyOutput().setPrimaryKey(true);
		getConfiguration().getFieldIndexInput().setPrimaryKey(true);
		getConfiguration().getFieldIndexOutput().setPrimaryKey(true);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Validate that server, instrument, period and configuration are set.
	 */
	private void validateState() {
		if (getServer() == null || getInstrument() == null || getPeriod() == null || getConfiguration() == null) {
			throw new IllegalStateException();
		}
	}
}
