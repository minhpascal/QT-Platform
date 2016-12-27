/**
 * 
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
