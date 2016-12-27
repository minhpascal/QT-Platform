package com.qtplaf.library.swing.formatters;

import java.sql.Date;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.text.MaskFormatter;

import com.qtplaf.library.util.FormatUtils;

/**
 * A date formatter that acts as expected.
 * 
 * @author Miquel Sas
 */
public class DateFormatter extends MaskFormatter {

	/**
	 * The locale.
	 */
	private Locale locale = null;
	/**
	 * The original date mask used.
	 */
	private String mask;

	/**
	 * Constructor assigning the locale.
	 * 
	 * @param locale The locale to use.
	 * @throws ParseException
	 */
	public DateFormatter(Locale locale) throws ParseException {
		super();
		this.locale = locale;
		this.mask = FormatUtils.getNormalizedDatePattern(locale);
		// Set the appropriate mask to the MaskFormatter.
		setMask(getDateMask(mask));
	}

	/**
	 * Convert the string to a value.
	 */
	public Object stringToValue(String value) throws ParseException {
		return FormatUtils.formattedToDate(value, locale);
	}

	/**
	 * Convert the value to a string.
	 */
	public String valueToString(Object value) throws ParseException {
		String str = FormatUtils.formattedFromDate((Date) value, locale);
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
	private String getDateMask(String datePattern) {
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
