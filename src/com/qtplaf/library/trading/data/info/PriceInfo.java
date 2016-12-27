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
package com.qtplaf.library.trading.data.info;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;

/**
 * Data information for prices.
 * 
 * @author Miquel Sas
 */
public class PriceInfo extends DataInfo {

	/**
	 * Constructor assigning instrument and period.
	 * 
	 * @param session The working session.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public PriceInfo(Session session, Instrument instrument, Period period) {
		super(session);
		setDataType(DataType.Price);
		setInstrument(instrument);
		setPeriod(period);
		addOutput("Open", "O", 0, "Open OHLCV value");
		addOutput("High", "H", 1, "High OHLCV value");
		addOutput("Low", "L", 2, "Low OHLCV value");
		addOutput("Close", "C", 3, "Close OHLCV value");
		addOutput("Volume", "V", 4, "Volume OHLCV value");
	}
}
