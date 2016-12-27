/**
 * 
 */
package trash.jforex.database;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientGUI;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;

import trash.jforex.chart.ChartManager;
import trash.jforex.chart.JFrameChart;
import trash.jforex.connection.ConnectorDemoClient;
import trash.jforex.indicators.PeriodStrength;

/**
 * Simple strategy tester.
 * 
 * @author Miquel Sas
 */
public class StrategyTester {

	private IStrategy strategy;

	/**
	 * Constructor.
	 */
	public StrategyTester(IStrategy strategy) {
		super();
		this.strategy = strategy;
	}

	/**
	 * Connect and start the strategy
	 */
	public void start() throws Exception {
		
		ConnectorDemoClient connector = new ConnectorDemoClient();
		connector.connect();
		
		// subscribe to the instruments
		Instrument[] instrArr = new Instrument[] { Instrument.EURUSD };
		Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(instrArr));

		System.out.println("Subscribing instruments...");
		connector.getClient().setSubscribedInstruments(instruments);

		connector.getClient().startStrategy(strategy);
	}
}
