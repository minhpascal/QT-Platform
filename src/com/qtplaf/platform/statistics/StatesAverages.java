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
	 * @param server The server.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public StatesAverages(Session session, Server server, Instrument instrument, Period period) {
		super(session);
		this.server = server;
		this.instrument = instrument;
		this.period = period;
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
	 * Returns the instrument.
	 * 
	 * @return The instrument.
	 */
	public Instrument getInstrument() {
		return instrument;
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
	 * Returns the table definition for states values.
	 * 
	 * @return The table definition.
	 */
	protected Table getTableStates() {

		Table table = new Table();

		table.setName(Names.getName(getInstrument(), getPeriod(), getId().toLowerCase()));
		table.setSchema(Names.getSchema(getServer()));

		// Index and time.
		Field index = getConfiguration().getFieldIndex();
		table.addField(index);
		Field time = getConfiguration().getFieldTime();
		table.addField(time);

		// Time formatted.
		Field timeFmt = getConfiguration().getFieldTimeFmt();
		timeFmt.setFormatter(new TimeFmtValue(getPeriod().getUnit()));
		timeFmt.setCalculator(new TimeFmtValue(getPeriod().getUnit()));
		table.addField(timeFmt);

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

		// Primary key on Time.
		time.setPrimaryKey(true);

		// Unique index on Index.
		Index indexOnIndex = new Index();
		indexOnIndex.add(index);
		indexOnIndex.setUnique(true);
		table.addIndex(indexOnIndex);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

}
