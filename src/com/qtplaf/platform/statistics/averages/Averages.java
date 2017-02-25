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

package com.qtplaf.platform.statistics.averages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.ai.rlearning.function.Normalizer;
import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Index;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.statistics.Statistics;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Server;
import com.qtplaf.platform.database.Names;
import com.qtplaf.platform.database.formatters.DataValue;
import com.qtplaf.platform.database.formatters.PipValue;
import com.qtplaf.platform.database.formatters.TimeFmtValue;
import com.qtplaf.platform.statistics.Manager;
import com.qtplaf.platform.statistics.averages.configuration.Average;
import com.qtplaf.platform.statistics.averages.configuration.Configuration;
import com.qtplaf.platform.statistics.averages.configuration.Speed;
import com.qtplaf.platform.statistics.averages.configuration.Spread;
import com.qtplaf.platform.util.DomainUtils;
import com.qtplaf.platform.util.FieldUtils;
import com.qtplaf.platform.util.PersistorUtils;

/**
 * Root class for states statistics based on averages.
 *
 * @author Miquel Sas
 */
public abstract class Averages extends Statistics {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/** The server. */
	private Server server;
	/** The instrument. */
	private Instrument instrument;
	/** The period. */
	private Period period;

	/** The configuration. */
	private Configuration configuration;

	/** Pip value formatter. */
	private PipValue pipFormatter;
	/** Time formatter and calculator. */
	private TimeFmtValue timeFormatter;
	/** Value formatter for raw spread and speed values. */
	private DataValue valueFormatterRaw;
	/** Value formatter for normalized continuous spread and speed values. */
	private DataValue valueFormatterNormCont;
	/** Value formatter for normalized discrete spread and speed values. */
	private DataValue valueFormatterNormDisc;

	/** Map of fields. */
	private Map<String, Field> mapFields = new HashMap<>();
	/** Map of field lists. */
	private Map<String, List<Field>> mapFieldLists = new HashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 */
	public Averages(Session session) {
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
	 * Returns the pip value formatter.
	 * 
	 * @return The pip value formatter.
	 */
	protected PipValue getPipFormatter() {
		if (pipFormatter == null) {
			pipFormatter = new PipValue(getSession(), getInstrument());
		}
		return pipFormatter;
	}

	/**
	 * Returns the time formatter and calculator.
	 * 
	 * @return The time formatter and calculator.
	 */
	protected TimeFmtValue getTimeFormatter() {
		if (timeFormatter == null) {
			timeFormatter = new TimeFmtValue(getPeriod().getUnit());
		}
		return timeFormatter;
	}

	/**
	 * Returns the value formatter for raw spread and speed values.
	 * 
	 * @return The value formatter for raw spread and speed values.
	 */
	protected DataValue getValueFormatterRaw() {
		if (valueFormatterRaw == null) {
			valueFormatterRaw = new DataValue(getSession(), 10);
		}
		return valueFormatterRaw;
	}

	/**
	 * Returns the value formatter for normalized continuous spread and speed values.
	 * 
	 * @return The value formatter for normalized continuous spread and speed values.
	 */
	protected DataValue getValueFormatterNormCont() {
		if (valueFormatterNormCont == null) {
			valueFormatterNormCont = new DataValue(getSession(), 4);
		}
		return valueFormatterNormCont;
	}

	/**
	 * Returns the value formatter for normalized discrete spread and speed values.
	 * 
	 * @return The value formatter for normalized discrete spread and speed values.
	 */
	protected DataValue getValueFormatterNormDisc() {
		if (valueFormatterNormDisc == null) {
			valueFormatterNormDisc = new DataValue(getSession(), 4);
		}
		return valueFormatterNormDisc;
	}

	/**
	 * Returns the list of average fields.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListAverages() {
		List<Field> fields = mapFieldLists.get("averages");
		if (fields == null) {
			fields = new ArrayList<>();
			for (Average average : getConfiguration().getAverages()) {
				getFieldDefAverage(average).setFormatter(getPipFormatter());
				fields.add(getFieldDefAverage(average));
			}
			mapFieldLists.put("averages", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields, raw values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsRaw() {
		List<Field> fields = getFieldListSpreads("raw");
		for (Field field : fields) {
			field.setFormatter(getValueFormatterRaw());
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields, normalized continuous values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsNormalizedContinuous() {
		List<Field> fields = getFieldListSpreads("nrm");
		for (Field field : fields) {
			field.setFormatter(getValueFormatterNormCont());
		}
		return fields;
	}

	/**
	 * Returns the list of spread fields, normalized discrete values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpreadsNormalizedDiscrete() {
		List<Field> fields = getFieldListSpreads("dsc");
		for (Field field : fields) {
			field.setFormatter(getValueFormatterNormDisc());
		}
		return fields;
	}

	/**
	 * Returns the list of delta fields, raw values.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListDeltasRaw() {
		List<Field> fields = mapFieldLists.get("deltas_raw");
		if (fields == null) {
			fields = getFieldListDeltas("raw");
			for (Field field : fields) {
				field.setFormatter(getValueFormatterRaw());
			}
			mapFieldLists.put("deltas_raw", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of delta fields, normalized continuous.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListDeltasNormalizedContinuous() {
		List<Field> fields = mapFieldLists.get("deltas_nrm");
		if (fields == null) {
			fields = getFieldListDeltas("nrm");
			for (Field field : fields) {
				field.setFormatter(getValueFormatterRaw());
			}
			mapFieldLists.put("deltas_nrm", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of delta fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	private List<Field> getFieldListDeltas(String suffix) {
		List<Field> fields = new ArrayList<>();
		fields.add(getFieldDefDelta(getFieldDefHigh(), suffix));
		fields.add(getFieldDefDelta(getFieldDefLow(), suffix));
		fields.add(getFieldDefDelta(getFieldDefClose(), suffix));
		return fields;
	}

	/**
	 * Returns the list of spread fields.
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	private List<Field> getFieldListSpreads(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (Spread spread : getConfiguration().getSpreads()) {
			fields.add(getFieldDefSpread(spread, suffix));
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, raw values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsRaw() {
		List<Field> fields = mapFieldLists.get("speeds_raw");
		if (fields == null) {
			fields = getFieldListSpeeds("raw");
			for (Field field : fields) {
				field.setFormatter(getValueFormatterRaw());
			}
			mapFieldLists.put("speeds_raw", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, normalized continuous values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsNormalizedContinuous() {
		List<Field> fields = mapFieldLists.get("speeds_nrm");
		if (fields == null) {
			fields = getFieldListSpeeds("nrm");
			for (Field field : fields) {
				field.setFormatter(getValueFormatterNormCont());
			}
			mapFieldLists.put("speeds_nrm", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, normalized discrete values
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListSpeedsNormalizedDiscrete() {
		List<Field> fields = mapFieldLists.get("speeds_dsc");
		if (fields == null) {
			fields = getFieldListSpeeds("dsc");
			for (Field field : fields) {
				field.setFormatter(getValueFormatterNormDisc());
			}
			mapFieldLists.put("speeds_dsc", fields);
		}
		return fields;
	}

	/**
	 * Returns the list of speed fields, raw values
	 * 
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The list of fields.
	 */
	private List<Field> getFieldListSpeeds(String suffix) {
		List<Field> fields = new ArrayList<>();
		for (Speed speed : getConfiguration().getSpeeds()) {
			fields.add(getFieldDefSpeed(speed, suffix));
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
			Spread spread = getPropertySpread(field);
			if (spread.isStateKey()) {
				keyFields.add(field);
			}
		}
		for (Field field : speedFields) {
			Speed speed = getPropertySpeed(field);
			if (speed.isStateKey()) {
				keyFields.add(field);
			}
		}
		return keyFields;
	}

	/**
	 * Returns the list of fields to calculate ranges raw.
	 * 
	 * @return The list of fields.
	 */
	public List<Field> getFieldListToCalculateRangesRaw() {
		List<Field> fields = new ArrayList<>();
		fields.addAll(getFieldListDeltasRaw());
		fields.addAll(getFieldListSpreadsRaw());
		fields.addAll(getFieldListSpeedsRaw());
		return fields;
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

		table.addField(getFieldDefName());
		table.addField(getFieldDefMinMax());
		table.addField(getFieldDefPeriod());
		table.addField(getFieldDefValue());
		table.addField(getFieldDefIndex());
		table.addField(getFieldDefTime());

		// Non unique index on name, minmax, period.
		Index index = new Index();
		index.add(getFieldDefName());
		index.add(getFieldDefMinMax());
		index.add(getFieldDefPeriod());
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
		table.addField(getFieldDefIndex());
		table.addField(getFieldDefTime());

		// Time formatted.
		table.addField(getFieldDefTimeFmt());

		// Open, high, low, close.
		table.addField(getFieldDefOpen());
		table.addField(getFieldDefHigh());
		table.addField(getFieldDefLow());
		table.addField(getFieldDefClose());

		// Averages fields.
		table.addFields(getFieldListAverages());
		
		// Deltas high, low, close, raw values.
		table.addFields(getFieldListDeltasRaw());

		// Spreads between averages, raw values.
		table.addFields(getFieldListSpreadsRaw());

		// Speed (tangent) of averages, raw values
		table.addFields(getFieldListSpeedsRaw());
		
		// Deltas high, low, close, normalized values continuous.
		table.addFields(getFieldListDeltasNormalizedContinuous());

		// Spreads between averages, normalized values continuous.
		table.addFields(getFieldListSpreadsNormalizedContinuous());

		// Speed (tangent) of averages, normalized values continuous.
		table.addFields(getFieldListSpeedsNormalizedContinuous());

		// Spreads between averages, normalized values discrete.
		table.addFields(getFieldListSpreadsNormalizedDiscrete());

		// Speed (tangent) of averages, normalized values discrete.
		table.addFields(getFieldListSpeedsNormalizedDiscrete());

		// The state key.
		table.addField(getFieldDefState());

		// Primary key on Time.
		getFieldDefIndex().setPrimaryKey(true);

		// Unique index on Index.
		Index indexOnIndex = new Index();
		indexOnIndex.add(getFieldDefIndex());
		indexOnIndex.setUnique(true);
		table.addIndex(indexOnIndex);

		// Non unique index on the state key.
		Index indexOnKeyState = new Index();
		indexOnKeyState.add(getFieldDefState());
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
		table.addField(getFieldDefStateInput());
		table.addField(getFieldDefStateOutput());

		// Input and output indexes of source states.
		table.addField(getFieldDefIndexInput());
		table.addField(getFieldDefIndexOutput());

		// Index group (groups consecutive transitions of the same state).
		table.addField(getFieldDefIndexGroup());

		// Estimaded function value high, low and close.
		table.addField(getFieldDefTransitionValueHigh());
		table.addField(getFieldDefTransitionValueLow());
		table.addField(getFieldDefTransitionValueClose());

		// Primary key.
		getFieldDefStateInput().setPrimaryKey(true);
		getFieldDefStateOutput().setPrimaryKey(true);
		getFieldDefIndexInput().setPrimaryKey(true);
		getFieldDefIndexOutput().setPrimaryKey(true);

		table.setPersistor(PersistorUtils.getPersistor(table.getSimpleView()));
		return table;
	}

	/**
	 * Returns the field for the average.
	 * 
	 * @param average The average.
	 * @return The field.
	 */
	public Field getFieldDefAverage(Average average) {
		String name = average.getName();
		Field field = mapFields.get(name);
		if (field == null) {
			String header = average.getHeader();
			String label = average.getLabel();
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the close field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefClose() {
		String name = "close";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getClose(getSession(), name);
			field.setFormatter(getPipFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate count field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefCount() {
		String name = "count";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getInteger(getSession(), "count", "Count", "Count records for the same value");
			field.setFunction("count(*)");
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate maximum field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMaximum() {
		String name = "maximum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "maximum", "Maximum", "Maximum value");
			field.setFunction("max(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate minimum field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMinimum() {
		String name = "minimum";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "minimum", "Minimum", "Minimum value");
			field.setFunction("min(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate average field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAverage() {
		String name = "average";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "average", "Average", "Average value");
			field.setFunction("avg(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAvgStd1() {
		String name = "avgstd1";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "avgstd_1", "AvgStd_1", "Avg/1 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(getSession(), 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the avg/stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefAvgStd2() {
		String name = "avgstd2";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "avgstd_2", "AvgStd_2", "Avg/2 Stddev value");
			field.setPersistent(false);
			field.setFormatter(new DataValue(getSession(), 2));
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the aggregate stddev field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStdDev() {
		String name = "stddev";
		Field field = mapFields.get(name);
		if (field == null) {
			field = DomainUtils.getDouble(getSession(), "stddev", "Std Dev", "Standard deviation value");
			field.setFunction("stddev(value)");
			field.setFormatter(getValueFormatterRaw());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the high field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefHigh() {
		String name = "high";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getHigh(getSession(), name);
			field.setFormatter(getPipFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndex() {
		String name = "index";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index";
			String label = "Index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index group field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexGroup() {
		String name = "index_group";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Index group";
			String label = "Index group";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexInput() {
		String name = "index_in";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Input index";
			String label = "Input index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the index output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefIndexOutput() {
		String name = "index_out";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Output index";
			String label = "Output index";
			field = FieldUtils.getIndex(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key input field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStateInput() {
		String name = "state_in";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Input state key";
			String label = "Input state key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key output field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefStateOutput() {
		String name = "state_out";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "Output state key";
			String label = "Output state key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the key state field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefState() {
		String name = "state";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 100;
			String header = "State key";
			String label = "State key";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			field.setNullable(true);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the low field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefLow() {
		String name = "low";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getLow(getSession(), name);
			field.setFormatter(getPipFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the min_max field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefMinMax() {
		String name = "min_max";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 3;
			String header = "Min/Max";
			String label = "Minimum/Maximum calculation";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns field definition for name field, a 40 chars string used as name of fields.
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefName() {
		String name = "name";
		Field field = mapFields.get(name);
		if (field == null) {
			int length = 40;
			String header = "Name";
			String label = "Value name";
			field = DomainUtils.getString(getSession(), name, length, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the period field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefPeriod() {
		String name = "period";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Period";
			String label = "Period";
			field = DomainUtils.getInteger(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the open field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefOpen() {
		String name = "open";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Open";
			String label = "Open price";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			field.setFormatter(getPipFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the speed field.
	 * 
	 * @param speed The speed definition.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldDefSpeed(Speed speed, String suffix) {
		Average average = speed.getAverage();
		String name = "speed_" + average.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Speed-" + average.getPeriod() + "-" + suffix;
			String label = "Speed " + average.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertySpeed(field, speed);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field.
	 * 
	 * @param spread Spread.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldDefSpread(Spread spread, String suffix) {
		Average averageFast = spread.getFastAverage();
		Average averageSlow = spread.getSlowAverage();
		String name = "spread_" + averageFast.getPeriod() + "_" + averageSlow.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + averageFast.getPeriod() + "-" + averageSlow.getPeriod() + "-" + suffix;
			String label = "Spread " + averageFast.getPeriod() + " - " + averageSlow.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertySpread(field, spread);
			mapFields.put(name, field);
		}
		return field;
	}
	
	/**
	 * Returns the field definition for the delta high (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaHight() {
		return getFieldDefDelta(getFieldDefHigh(), "nrm");
	}

	/**
	 * Returns the field definition for the delta low (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaLow() {
		return getFieldDefDelta(getFieldDefLow(), "nrm");
	}

	/**
	 * Returns the field definition for the delta close (normalized continuous).
	 * 
	 * @return The field definition.
	 */
	public Field getFieldDefDeltaClose() {
		return getFieldDefDelta(getFieldDefClose(), "nrm");
	}

	/**
	 * Returns the field definition for a delta field.
	 * 
	 * @param source The source field.
	 * @param suffix The suffix (raw, nrm)
	 * @return The field definition.
	 */
	public Field getFieldDefDelta(Field source, String suffix) {
		String name = "delta_" + source.getName() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Delta-" + source.getName() + "-" + suffix;
			String label = "Delta " + source.getName() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertySourceField(field, source);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the spread field between a source field (high, low, close) and an average (normally the fast one).
	 * 
	 * @param source Source field.
	 * @param average The average.
	 * @param suffix The suffix to differentiate from raw, normalized continuous and discrete.
	 * @return The field.
	 */
	public Field getFieldDefSpread(Field source, Average average, String suffix) {
		String name = "spread_" + source.getName() + "_" + average.getPeriod() + "_" + suffix;
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Spread-" + source.getName() + "-" + average.getPeriod() + "-" + suffix;
			String label = "Spread " + source.getName() + " - " + average.getPeriod() + " - " + suffix;
			field = DomainUtils.getDouble(getSession(), name, header, label);
			setPropertySourceField(field, source);
			setPropertyAverage(field, average);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTime() {
		String name = "time";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTime(getSession(), name);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the time field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTimeFmt() {
		String name = "time_fmt";
		Field field = mapFields.get(name);
		if (field == null) {
			field = FieldUtils.getTimeFmt(getSession(), name);
			field.setFormatter(getTimeFormatter());
			field.setCalculator(getTimeFormatter());
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueClose() {
		String name = "value_close";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value close";
			String label = "Transition value close";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.v
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueHigh() {
		String name = "value_high";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value high";
			String label = "Transition value high";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the transition value field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefTransitionValueLow() {
		String name = "value_low";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value low";
			String label = "Transition value low";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Returns the value field.
	 * 
	 * @return The field.
	 */
	public Field getFieldDefValue() {
		String name = "value";
		Field field = mapFields.get(name);
		if (field == null) {
			String header = "Value";
			String label = "Value";
			field = DomainUtils.getDouble(getSession(), name, header, label);
			mapFields.put(name, field);
		}
		return field;
	}

	/**
	 * Validate that server, instrument, period and configuration are set.
	 */
	private void validateState() {
		if (getServer() == null || getInstrument() == null || getPeriod() == null || getConfiguration() == null) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the average property of the field.
	 * 
	 * @param field The source field.
	 * @return The average.
	 */
	public Average getPropertyAverage(Field field) {
		return (Average) field.getProperty("average");
	}

	/**
	 * Sets the average property to the field.
	 * 
	 * @param field The field.
	 * @param average The average.
	 */
	private void setPropertyAverage(Field field, Average average) {
		field.setProperty("average", average);
	}

	/**
	 * Returns the spread property of the field.
	 * 
	 * @param field The source field.
	 * @return The spread.
	 */
	public Spread getPropertySpread(Field field) {
		return (Spread) field.getProperty("spread");
	}

	/**
	 * Sets the spread property to the field.
	 * 
	 * @param field The field.
	 * @param spread The spread.
	 */
	private void setPropertySpread(Field field, Spread spread) {
		field.setProperty("spread", spread);
	}

	/**
	 * Returns the speed property of the field.
	 * 
	 * @param field The source field.
	 * @return The speed.
	 */
	public Speed getPropertySpeed(Field field) {
		return (Speed) field.getProperty("speed");
	}

	/**
	 * Sets the speed property to the field.
	 * 
	 * @param field The field.
	 * @param speed The speed.
	 */
	private void setPropertySpeed(Field field, Speed speed) {
		field.setProperty("speed", speed);
	}

	/**
	 * Return the source field property.
	 * 
	 * @param field The field.
	 * @return The source field.
	 */
	public Field getPropertySourceField(Field field) {
		return (Field) field.getProperty("source-field");
	}

	/**
	 * Sets the source field property for the field.
	 * 
	 * @param field The field.
	 * @param source The source field.
	 */
	private void setPropertySourceField(Field field, Field source) {
		field.setProperty("source-field", source);
	}

	/**
	 * Returns the map of normalizers for continuous normalization.
	 * 
	 * @return The map of normalizers.
	 */
	public Map<String, Normalizer> getMapNormalizers() {
		try {

			// Ranges statistics.
			Manager manager = new Manager(getSession());
			Ranges ranges = manager.getRanges(getServer(), getInstrument(), getPeriod(), getConfiguration());

			// The map to fill.
			Map<String, Normalizer> map = new HashMap<>();

			double stddevs = 2;
			RecordSet recordSet = ranges.getRecordSet(false);
			for (int i = 0; i < recordSet.size(); i++) {
				Record record = recordSet.get(i);
				String fieldName = record.getValue(ranges.getFieldDefName().getName()).getString();
				String minMax = record.getValue(ranges.getFieldDefMinMax().getName()).getString();
				double average = record.getValue(ranges.getFieldDefAverage().getName()).getDouble();
				double stddev = record.getValue(ranges.getFieldDefStdDev().getName()).getDouble();
				Normalizer normalizer = map.get(fieldName);
				if (normalizer == null) {
					normalizer = new Normalizer();
					normalizer.setMaximum(0);
					normalizer.setMinimum(0);
					map.put(fieldName, normalizer);
				}
				if (minMax.equals("min")) {
					normalizer.setMinimum(average - (stddev * stddevs));
				} else {
					normalizer.setMaximum(average + (stddev * stddevs));
				}
			}

			return map;
		} catch (Exception exc) {
			logger.catching(exc);
		}
		return null;
	}
}
