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

package com.qtplaf.platform.database.util;

import com.qtplaf.library.database.Record;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.platform.database.tables.Tickers;

/**
 * Centralizes period operations.
 *
 * @author Miquel Sas
 */
public class PeriodUtils {

	/**
	 * Returns the period from the tickers record.
	 * 
	 * @param record The tickers record.
	 * @return The period.
	 */
	public static Period getPeriodFromRecordTickers(Record record) {
		String periodId = record.getValue(Tickers.Fields.PeriodId).getString();
		return Period.parseId(periodId);
	}
}
