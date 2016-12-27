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
package com.qtplaf.library.swing.formatters;

import java.sql.Time;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.MaskFormatter;

import com.qtplaf.library.util.FormatUtils;

/**
 * A time formatter that acts as expected.
 * 
 * @author Miquel Sas
 */
public class TimeFormatter extends MaskFormatter {

	/** The locale. */
	private Locale locale = null;

	/**
	 * Constructor assigning the locale.
	 * 
	 * @param locale The locale to use.
	 * @param editSeconds A boolean that indicates if seconds should be edited.
	 * @throws ParseException
	 */
	public TimeFormatter(Locale locale, boolean editSeconds) throws ParseException {
		super();
		this.locale = locale;
		String mask = getTimeMask(FormatUtils.getNormalizedTimePattern(locale));
		if (!editSeconds) {
			mask = mask.substring(0, mask.length() - 3);
		}
		setMask(mask);
	}

	/**
	 * Convert the string to a value.
	 * 
	 * @param value The string to convert.
	 * @return The converted value.
	 */
	public Object stringToValue(String value) throws ParseException {
		return FormatUtils.formattedToTime(value, locale);
	}

	/**
	 * Convert the value to a string.
	 * 
	 * @param value The value to convert.
	 * @return The converted value.
	 */
	public String valueToString(Object value) throws ParseException {
		String str = FormatUtils.formattedFromTime((Time) value, locale);
		if (str.trim().length() == 0) {
			return super.valueToString(value);
		}
		return str;
	}

	/**
	 * Returns the mask that this mask formatter must use.
	 * 
	 * @param datePattern The date pattern.
	 * @return The mask.
	 */
	private String getTimeMask(String datePattern) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < datePattern.length(); i++) {
			char c = datePattern.charAt(i);
			switch (c) {
			case 'd':
			case 'M':
			case 'y':
			case 'H':
			case 'm':
			case 's':
				b.append("#");
				break;
			default:
				b.append(c);
				break;
			}
		}
		return b.toString();
	}
}
