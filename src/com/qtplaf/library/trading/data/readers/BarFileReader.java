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
package com.qtplaf.library.trading.data.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.MapDataList;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.OHLCV;
import com.qtplaf.library.trading.data.Period;
import com.qtplaf.library.trading.data.info.PriceInfo;
import com.qtplaf.library.util.Calendar;
import com.qtplaf.library.util.StringUtils;

/**
 * A reader for txt files extracted from ViasualChart.
 * 
 * @author Miquel Sas
 */
public class BarFileReader {
	public static DataList read(File file, Instrument instrument, Period period, String instrumentId, int maxBars)
		throws IOException {

		Session session = new Session(Locale.UK);
		DataList dataList = new MapDataList(session, new PriceInfo(session, instrument, period));
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		int currentBar = -1;
		while (true) {
			line = br.readLine();
			if (line == null) {
				break;
			}
			if (!line.startsWith(instrumentId)) {
				continue;
			}
			currentBar++;
			if (maxBars >= 0 && currentBar >= maxBars) {
				break;
			}

			String[] tokens = StringUtils.parseCommaSeparatedStrings(line);
			String sdate = tokens[2];
			String stime = tokens[3];
			String sopen = tokens[4];
			String shigh = tokens[5];
			String slow = tokens[6];
			String sclose = tokens[7];
			String svolume = tokens[8];

			int year = Integer.parseInt(sdate.substring(0, 4));
			int month = Integer.parseInt(sdate.substring(4, 6));
			int day = Integer.parseInt(sdate.substring(6, 8));
			int hour = 0;
			int minute = 0;
			int second = 0;
			if (Integer.parseInt(stime) > 0) {
				hour = Integer.parseInt(stime.substring(0, 2));
				minute = Integer.parseInt(stime.substring(2, 4));
				second = Integer.parseInt(stime.substring(4, 6));
			}

			Calendar calendar = new Calendar(year, month, day, hour, minute, second);
			long time = calendar.getTimeInMillis();

			double open = Double.parseDouble(sopen);
			double high = Double.parseDouble(shigh);
			double low = Double.parseDouble(slow);
			double close = Double.parseDouble(sclose);
			double volume = Double.parseDouble(svolume);

			OHLCV candle = new OHLCV(time, open, high, low, close, volume);
			dataList.add(candle);
		}
		br.close();
		fr.close();

		return dataList;
	}
}
