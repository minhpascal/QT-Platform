/**
 * 
 */
package com.qtplaf.library.trading.server.database;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.Field;
import com.qtplaf.library.database.Record;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.database.Types;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.server.Filter;
import com.qtplaf.library.trading.server.OfferSide;

/**
 * A table to store OHLCV elements.
 * 
 * @author Miquel Sas
 */
public class OHLCVTable extends Table {

	/**
	 * Returns a list of values given an OHLCV data element.
	 * 
	 * @param ohlcv The OHLCV data element.
	 * @return The list of values.
	 */
	public static List<Value> getValues(OHLCV ohlcv) {
		List<Value> values = new ArrayList<>();
		values.add(new Value(ohlcv.getTime()));
		values.add(new Value(ohlcv.getOpen()));
		values.add(new Value(ohlcv.getHigh()));
		values.add(new Value(ohlcv.getLow()));
		values.add(new Value(ohlcv.getClose()));
		values.add(new Value(ohlcv.getVolume()));
		return values;
	}

	/**
	 * Returns an OHLCV data element given the corresponding list of values.
	 * 
	 * @param values The list of values.
	 * @return The OHLCV data element
	 */
	public static OHLCV getOHLCV(List<Value> values) {
		if (values.size() != 6) {
			throw new IllegalArgumentException("Invalid list of values: size must be 6");
		}
		if (!values.get(0).getType().equals(Types.Long)) {
			throw new IllegalArgumentException("Invalid list of values: type of element 0  must be Long");
		}
		for (int i = 1; i < values.size(); i++) {
			if (!values.get(i).getType().equals(Types.Double)) {
				throw new IllegalArgumentException("Invalid list of values: type of first elements 1 to 5 must be Double");
			}
		}
		OHLCV ohlcv = new OHLCV();
		ohlcv.setTime(values.get(0).getLong());
		ohlcv.setOpen(values.get(1).getDouble());
		ohlcv.setHigh(values.get(2).getDouble());
		ohlcv.setLow(values.get(3).getDouble());
		ohlcv.setClose(values.get(4).getDouble());
		ohlcv.setVolume(values.get(5).getDouble());
		return ohlcv;
	}

	/**
	 * Instrument.
	 */
	private Instrument instrument;
	/**
	 * Period.
	 */
	private Period period;
	/**
	 * Filter.
	 */
	private Filter filter;
	/**
	 * Offer side.
	 */
	private OfferSide offerSide;

	/**
	 * Constructor using the fields.
	 * 
	 * @param instrument
	 * @param period
	 */
	public OHLCVTable(Instrument instrument, Period period) {
		this(instrument, period, null, null);
	}

	/**
	 * Constructor using the fields.
	 * 
	 * @param instrument
	 * @param period
	 * @param offerSide
	 */
	public OHLCVTable(Instrument instrument, Period period, OfferSide offerSide) {
		this(instrument, period, null, offerSide);
	}

	/**
	 * Constructor.
	 * 
	 * @param instrument
	 * @param period
	 * @param filter
	 * @param offerSide
	 */
	public OHLCVTable(Instrument instrument, Period period, Filter filter, OfferSide offerSide) {
		super();

		// Register the parameters.
		this.instrument = instrument;
		this.period = period;
		this.filter = filter;
		this.offerSide = offerSide;

		// Set the standard name base on the parameters.
		setName(Names.getOHLCVName(instrument, period, filter, offerSide));

		// Configure the segments.

		// OHLCV time.
		Field time = new Field();
		time.setName("TIME");
		time.setAlias("TIME");
		time.setTitle("Time");
		time.setLabel("Time");
		time.setHeader("Time");
		time.setType(Types.Long);
		time.setPrimaryKey(true);
		addField(time);

		// OHLCV open.
		Field open = new Field();
		open.setName("OPEN");
		open.setAlias("OPEN");
		open.setTitle("Open");
		open.setLabel("Open");
		open.setHeader("Open");
		open.setType(Types.Double);
		addField(open);

		// OHLCV high.
		Field high = new Field();
		high.setName("HIGH");
		high.setAlias("HIGH");
		high.setTitle("High");
		high.setLabel("High");
		high.setHeader("High");
		high.setType(Types.Double);
		addField(high);

		// OHLCV low.
		Field low = new Field();
		low.setName("LOW");
		low.setAlias("LOW");
		low.setTitle("Low");
		low.setLabel("Low");
		low.setHeader("Low");
		low.setType(Types.Double);
		addField(low);

		// OHLCV close.
		Field close = new Field();
		close.setName("CLOSE");
		close.setAlias("CLOSE");
		close.setTitle("Close");
		close.setLabel("Close");
		close.setHeader("Close");
		close.setType(Types.Double);
		addField(close);

		// OHLCV volume.
		Field volume = new Field();
		volume.setName("VOLUME");
		volume.setAlias("VOLUME");
		volume.setTitle("Volume");
		volume.setLabel("Volume");
		volume.setHeader("Volume");
		volume.setType(Types.Double);
		addField(volume);
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
	 * Returns the filter.
	 * 
	 * @return The filter.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Returns the offer side.
	 * 
	 * @return The offer side.
	 */
	public OfferSide getOfferSide() {
		return offerSide;
	}

	/**
	 * Returns a record given an OHLCV data element.
	 * 
	 * @param ohlcv The OHLCV data element.
	 * @return The corresponding record.
	 */
	public Record getRecord(OHLCV ohlcv) {
		Record record = getFieldList().getRecord(getValues(ohlcv));
		record.setPersistor(getPersistor());
		return record;
	}
}
