/**
 * 
 */
package test.com.msasc.jforex.feed;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IBarFeedListener;

/**
 * Bar listener.
 * 
 * @author Miquel Sas
 */
public class BarListener implements IBarFeedListener {
	int index;
	public BarListener(int index) {
		this.index = index;
	}
	public void onBar(Instrument instrument, Period period, OfferSide offerSide, IBar bar) {
		if (bar != null) {
			Util.print(period, bar, "LST-"+index);
		}
	}
}
