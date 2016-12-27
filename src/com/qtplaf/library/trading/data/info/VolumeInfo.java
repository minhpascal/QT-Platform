/**
 * 
 */
package com.qtplaf.library.trading.data.info;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.data.Period;

/**
 * Data info for volume.
 * 
 * @author Miquel Sas
 */
public class VolumeInfo extends PriceInfo {

	/**
	 * Constructor assigning instrument and period.
	 * 
	 * @param session The working session.
	 * @param instrument The instrument.
	 * @param period The period.
	 */
	public VolumeInfo(Session session, Instrument instrument, Period period) {
		super(session, instrument, period);
		setDataType(DataType.Volume);
	}
}
