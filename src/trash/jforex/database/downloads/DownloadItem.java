/**
 * 
 */
package trash.jforex.database.downloads;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.qtplaf.library.util.Timestamp;

/**
 * A structure that couples an intrument and a period.
 * 
 * @author Miquel Sas
 */
public class DownloadItem {

	private Instrument instrument;
	private Period period;
	private long timeStart;

	/**
	 * Default constructor.
	 */
	public DownloadItem() {
		super();
	}

	/**
	 * @param instrument
	 * @param period
	 */
	public DownloadItem(Instrument instrument, Period period) {
		super();
		this.instrument = instrument;
		this.period = period;
	}

	/**
	 * @return the instrument
	 */
	public Instrument getInstrument() {
		return instrument;
	}

	/**
	 * @param instrument the instrument to set
	 */
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	/**
	 * @return the period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * @return the timeStart
	 */
	public long getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}
	
	/**
	 * Returns a string representation.
	 */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(instrument.toString());
		b.append(" ----- ");
		b.append(period.name());
		b.append(" ----- ");
		b.append(new Timestamp(timeStart));
		return b.toString();
	}
}
