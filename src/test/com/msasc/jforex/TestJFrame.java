package test.com.msasc.jforex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientGUI;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
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
import trash.jforex.indicators.PeriodStrength;

public class TestJFrame {
	/** Logger configuration. */
//	static {
//		System.setProperty("log4j.configurationFile", "LoggerQTPlatform.xml");
//	}
	private static final Logger LOGGER = LoggerFactory.getLogger(TestJFrame.class);

	public static void main(String[] args) throws Exception {
		String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
		String userName = "msasc2EU";
		String password = "C1a2r3l4a5";

		System.out.println(LOGGER.getClass());
		IClient client = ClientFactory.getDefaultInstance();
		// set the listener that will receive system events
		client.setSystemListener(new ISystemListener() {
			public void onStart(long processId) {
				LOGGER.info("Strategy started: " + processId);
			}

			public void onStop(long processId) {
				LOGGER.info("Strategy stopped: " + processId);
			}

			public void onConnect() {
				LOGGER.info("Connected");
			}

			public void onDisconnect() {
				LOGGER.warn("Disconnected");
			}
		});

		LOGGER.info("Connecting...");
		client.connect(jnlpUrl, userName, password);

		// wait for it to connect
		int i = 10; // wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			LOGGER.info("i=" + i);
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			LOGGER.error("Failed to connect Dukascopy servers");
			System.exit(1);
		}
		
		// Available instruments.
		Set<Instrument> avInstrs = client.getAvailableInstruments();
		for (Instrument instr : avInstrs) {
			System.out.println(instr.toString());
		}

		// subscribe to the instruments
		Instrument[] instrArr = new Instrument[] { Instrument.EURUSD, Instrument.EURJPY };
		Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(instrArr));

		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);

		// open charts
		JFrameChart frame = new JFrameChart();
		for (Instrument instrument : instrArr) {
			IFeedDescriptor feedDescriptor =
				new TimePeriodAggregationFeedDescriptor(instrument, Period.FIVE_MINS, OfferSide.BID, Filter.ALL_FLATS);
			IChart chart = client.openChart(feedDescriptor);
			LOGGER.info("Historical tester chart: " + chart.isHistoricalTesterChart());
			IClientGUI clientGUI = client.getClientGUI(chart);
			ChartManager chartManager = new ChartManager();
			chartManager.setChart(chart);
			chartManager.setChartPanel(clientGUI.getChartPanel());
			chartManager.setChartPresentationManager(clientGUI.getChartPresentationManager());
			chartManager.setChartController(clientGUI.getClientChartController());
			chartManager.setFeedDescriptor(feedDescriptor);
			frame.addChartManager(chartManager);
		}
		// a strategy that checks which charts are available from it
		client.startStrategy(new IStrategy() {
			public void onStart(IContext context) throws JFException {
//				IHistory history = context.getHistory();
//
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//				Date dateFrom, dateTo;
//				 try { 
//				     dateFrom = dateFormat.parse("2012/01/01 00:00:00"); 
//				     dateTo = dateFormat.parse("2015/05/31 00:00:00");
//				 } catch (ParseException e) {
//				     e.printStackTrace();
//				     return;
//				 }				
//				List<IBar> bars =
//					history.getBars(
//						Instrument.EURUSD,
//						Period.ONE_HOUR,
//						OfferSide.ASK,
//						dateFrom.getTime(),
//						dateTo.getTime());
//
//				for (IBar bar : bars) {
//					System.out.println(bar);
//				}
				IIndicators indicators = context.getIndicators();
				indicators.registerCustomIndicator(PeriodStrength.class);
				
				Set<IChart> charts = context.getCharts();
				for (IChart chart : charts) {
					IIndicator indicator = indicators.getIndicator("PERIODSTRENGTH");
//					chart.add(indicator);
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
		});

	}

}
