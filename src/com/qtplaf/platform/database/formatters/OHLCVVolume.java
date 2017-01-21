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

package com.qtplaf.platform.database.formatters;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.database.Value;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.util.NumberUtils;

/**
 * A formatter for OHLCV/Bar volume value that adapts to the volume scale.
 * 
 * @author Miquel Sas
 */
public class OHLCVVolume extends AbstractFormatter {
	
	/** Session. */
	private Session session;
	/** Volume scale. */
	private int volumeScale;
	
	/**
	 * Constructor.
	 * 
	 * @param session Working session.
	 * @param period The period.
	 */
	public OHLCVVolume(Session session, Instrument instrument) {
		super();
		this.session = session;
		this.volumeScale = instrument.getVolumeScale();
	}


	@Override
	public Object stringToValue(String text) throws ParseException {
		return null;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value instanceof Value) {
			Value vol = (Value) value;
			Value fmt = new Value(NumberUtils.getBigDecimal(vol.getDouble(), volumeScale));
			return fmt.toStringFormatted(session.getLocale());
		}
		return value.toString();
	}
}
