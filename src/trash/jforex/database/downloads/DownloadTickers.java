/**
 * 
 */
package trash.jforex.database.downloads;

import java.util.ArrayList;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.api.system.IClient;
import com.qtplaf.library.util.TextServer;

import trash.jforex.connection.ConnectorDemoClient;

/**
 * Download a list of tickers from the origin to to the en data.
 * 
 * @author Miquel Sas
 */
public class DownloadTickers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DownloadTickers download = new DownloadTickers();
		download.addItem(Instrument.EURUSD, Period.DAILY);
		download.addItem(Instrument.EURUSD, Period.FOUR_HOURS);
		download.addItem(Instrument.EURUSD, Period.ONE_HOUR);
		download.addItem(Instrument.EURUSD, Period.THIRTY_MINS);
		download.addItem(Instrument.EURUSD, Period.FIFTEEN_MINS);
		download.addItem(Instrument.EURUSD, Period.FIVE_MINS);
		download.addItem(Instrument.EURUSD, Period.ONE_MIN);
		

		try {
			download.processDownload();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * List of items to download.
	 */
	private ArrayList<DownloadItem> items = new ArrayList<>();
	
	/**
	 * Default constructor.
	 */
	public DownloadTickers() {
		super();
	}
	
	/**
	 * Add a new instrument to download.
	 * @param instrument
	 * @param period
	 */
	public void addItem(Instrument instrument, Period period) {
		items.add(new DownloadItem(instrument,period));
	}

	public void processDownload() throws Exception {
		
		// Initialize text server 
		TextServer.addBaseResource("StringsLibrary.xml");
		
		// Retrieve the database engine that will be used overall
//		DBEngine dbEngine = DBEngineFactory.getDBEngineForex();
		
		// Connect to Dukascopy servers
		ConnectorDemoClient connector = new ConnectorDemoClient();
		connector.connect();
		IClient client = connector.getClient();
		
		// Start the strategy to retrieve the first time of data for each pair.
		GetFirstDataAvailable firstDataStrategy = new GetFirstDataAvailable(items);
		long id = client.startStrategy(firstDataStrategy);
		
		// Wait to end job
		while (firstDataStrategy.isProcessingItems()) {
			Thread.sleep(10);
		}
		
		// Stop the strategy
		client.stopStrategy(id);
		
		// Print items time
		for (DownloadItem item : items) {
			System.out.println(item);
		}
		
		// Exit
		System.exit(0);
		
	}
	
}
