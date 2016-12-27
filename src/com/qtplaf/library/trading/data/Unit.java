/**
 * 
 */
package com.qtplaf.library.trading.data;

import java.util.Locale;

import com.qtplaf.library.util.TextServer;

/**
 * Units used to define periods of aggregate incoming quotes.
 * 
 * @author Miquel Sas
 */
public enum Unit {
	Millisecond("MS"),
	Second("SC"),
	Minute("MN"),
	Hour("HR"),
	Day("DY"),
	Week("WK"),
	Month("MT"),
	Year("YR");

	/** 2 char id. */
	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id Two char id.
	 */
	private Unit(String id) {
		this.id = id;
	}

	/**
	 * Returns the two char id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the short description.
	 * 
	 * @return The short description.
	 */
	public String getShortDescription() {
		switch (this) {
		case Millisecond:
			return TextServer.getString("tradeUnitMillisecondDescription", Locale.UK);
		case Second:
			return TextServer.getString("tradeUnitSecondDescription", Locale.UK);
		case Minute:
			return TextServer.getString("tradeUnitMinuteDescription", Locale.UK);
		case Hour:
			return TextServer.getString("tradeUnitHourDescription", Locale.UK);
		case Day:
			return TextServer.getString("tradeUnitDayDescription", Locale.UK);
		case Week:
			return TextServer.getString("tradeUnitWeekDescription", Locale.UK);
		case Month:
			return TextServer.getString("tradeUnitMonthDescription", Locale.UK);
		case Year:
			return TextServer.getString("tradeUnitYearDescription", Locale.UK);
		}
		return null;
	}
}
