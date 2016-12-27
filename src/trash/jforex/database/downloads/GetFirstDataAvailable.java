/**
 * 
 */
package trash.jforex.database.downloads;

import java.util.ArrayList;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IDataService;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;

/**
 * Before starting the strategy, the list of required pairs of Instrument/Period must be set. Then the strategy started
 * and when the request job has finished and the strategy has been stopped, the data can be retrieved.
 * 
 * @author Miquel Sas
 */
public class GetFirstDataAvailable implements IStrategy {

	/**
	 * The list of items to process.
	 */
	private ArrayList<DownloadItem> items = new ArrayList<>();
	/**
	 * A flag that indicates if it is still processing.
	 */
	private boolean processingItems = false;

	/**
	 * Default constructor.
	 */
	public GetFirstDataAvailable(ArrayList<DownloadItem> items) {
		super();
		this.items = items;
		setProcessingItems(true);
	}

	/**
	 * Set processing
	 * 
	 * @param processingItems
	 */
	private synchronized void setProcessingItems(boolean processingItems) {
		this.processingItems = processingItems;
	}

	/**
	 * Check if processing.
	 * 
	 * @return
	 */
	public synchronized boolean isProcessingItems() {
		return processingItems;
	}

	/**
	 * Get the first time of data for every item
	 */
	public void onStart(IContext context) throws JFException {
		IDataService dataService = context.getDataService();
		try {
			for (DownloadItem item : items) {
				Instrument instrument = item.getInstrument();
				Period period = item.getPeriod();
				item.setTimeStart(dataService.getTimeOfFirstCandle(instrument, period));
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			setProcessingItems(false);
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onStop() throws JFException {
	}

}
