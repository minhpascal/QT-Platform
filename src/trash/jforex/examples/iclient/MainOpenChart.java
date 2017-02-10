package trash.jforex.examples.iclient;

import static com.dukascopy.api.DataType.*;
import static com.dukascopy.api.Instrument.*;
import static com.dukascopy.api.PriceRange.*;
import static com.dukascopy.api.TickBarSize.*;
import static com.dukascopy.api.Period.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientChartPresentationManager;
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
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.TicksFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.system.ClientFactory;
import com.dukascopy.api.system.IClient;
import com.dukascopy.api.system.ISystemListener;
import com.dukascopy.api.chart.IChartTheme;
import com.dukascopy.api.chart.IChartTheme.ColoredElement;

import trash.jforex.learning.strategies.MyFirstStrategy;
import trash.jforex.strategies.CheckIndicatorInfo;

/**
 * Demonstrates the usage of Standalone Charts API (open charts / panels / control presentation view)<br/>
 * UI management and controls implemeneted by Client's methods
 */
public class MainOpenChart extends JFrame {

	private static final long serialVersionUID = 7287096355484721028L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MainOpenChart.class);

	private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	// user name
	private static String userName = "msasc1EU";
	// password
	private static String password = "C1a2r3l4a5";

	private final int frameWidth = 1000;
	private final int frameHeight = 600;

	private final int controlPanelHeight = 50;
	private final int controlPanelMaxHeight = 100;

	Map<IChart, ChartFrame> chartFrameMap = new HashMap<IChart, ChartFrame>();

	public static void main(String[] args) throws Exception {
		MainOpenChart mainGUI = new MainOpenChart();
		mainGUI.startStrategy();

	}

	private void startStrategy() throws Exception {
		// get the instance of the IClient interface
		final IClient client = ClientFactory.getDefaultInstance();
		// set the listener that will receive system events
		client.setSystemListener(new ISystemListener() {
			private int lightReconnects = 3;

			@Override
			public void onStart(long processId) {
				LOGGER.info("Strategy started: " + processId);
			}

			@Override
			public void onStop(long processId) {
				LOGGER.info("Strategy stopped: " + processId);
				if (client.getStartedStrategies().size() == 0) {
					System.exit(0);
				}
			}

			@Override
			public void onConnect() {
				LOGGER.info("Connected");
				lightReconnects = 3;
			}

			@Override
			public void onDisconnect() {
				LOGGER.warn("Disconnected");
				if (lightReconnects > 0) {
					client.reconnect();
					--lightReconnects;
				} else {
					try {
						// sleep for 10 seconds before attempting to reconnect
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// ignore
					}
					try {
						client.connect(jnlpUrl, userName, password);
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		});

		LOGGER.info("Connecting...");
		// connect to the server using jnlp, user name and password
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

		// subscribe to the instruments
		final Instrument[] instrArr = new Instrument[] { EURUSD, AUDCAD };
		Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(instrArr));

		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);

		// open charts
		for (Instrument instrument : instrArr) {
			IFeedDescriptor feedDescriptor = new TicksFeedDescriptor(instrument);
			feedDescriptor.setOfferSide(OfferSide.BID);// need to set due to platform requirements
			IChart chart = client.openChart(feedDescriptor);
			final IClientGUI clientGUI = client.getClientGUI(chart);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ChartFrame frame = new ChartFrame(clientGUI, client.getSubscribedInstruments());
					chartFrameMap.put(clientGUI.getChart(), frame);
					// Handle manual close - we need to call IClient.closeChart for strategy to know that the chart is no more there
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							LOGGER.info("Chart manually closed, removing the chart from the strategy context");
							client.closeChart(clientGUI.getChart());
							chartFrameMap.remove(clientGUI.getChart());
							if (chartFrameMap.isEmpty()) {
								LOGGER.info("All charts closed, stopping the program");
								System.exit(0);
							}
						}
					});
				}
			});
		}
		// a strategy that checks which charts are available from it
//		client.startStrategy(new IStrategy() {
//
//			private IContext context;
//
//			@Override
//			public void onStart(IContext context) throws JFException {
//				this.context = context;
//				
//				CheckIndicatorInfo info = new CheckIndicatorInfo();
//				info.onStart(context);
//			}
//
//			@Override
//			public void onTick(Instrument instrument, ITick tick) throws JFException {
//				IChart chart = context.getChart(instrument);
//				if (chart != null) {
//					System.out.println(chart.getFeedDescriptor() + " " + tick);
//				}
//			}
//			public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {			}
//			public void onMessage(IMessage message) throws JFException {			}
//			public void onAccount(IAccount account) throws JFException {			}
//			public void onStop() throws JFException {			}
//		});
		client.startStrategy(new MyFirstStrategy());
	}
	
	private interface IFeedChangeListener{
		void onFeedChange(IFeedDescriptor feedDescriptor);
	}

	@SuppressWarnings("serial")
	private class ChartFrame extends JFrame {

		private final IClientGUI clientGUI;
		private final Set<Instrument> availableInstruments;
		private IFeedChangeListener feedChangeListener;

		private ChartFrame(IClientGUI clientGUI, Set<Instrument> availableInstruments) {
			super(clientGUI.getChart().getFeedDescriptor().getInstrument().toString() + " chart controller");
			this.clientGUI = clientGUI;
			this.availableInstruments = availableInstruments;
			feedChangeListener = new IFeedChangeListener(){
				@Override
				public void onFeedChange(IFeedDescriptor feedDescriptor) {
					ChartFrame.this.setTitle(feedDescriptor.getInstrument() + " " + feedDescriptor.getDataType());
				}				
			};
			createFrame();
			addControlPanel();
			addChartPanel(clientGUI.getChartPanel());
			pack();
			setVisible(true);
		}

		private void createFrame() {
			getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
			requestFocusInWindow();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension screenSize = tk.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			// move every next chart by 20px down
			int openedChartCount = chartFrameMap.size();
			setLocation(screenWidth / 4 - openedChartCount * 20, screenHeight / 4 - openedChartCount * 20);
		}

		/**
		 * Add chart controls panel
		 */
		private void addControlPanel() {
			IFeedDescriptor feedDescriptor = clientGUI.getChart().getFeedDescriptor();
			IClientChartPresentationManager chartPresentationManager = clientGUI.getChartPresentationManager();

			setTitle(feedDescriptor.getInstrument().toString());

			JPanel controlPanel = new JPanel();
			FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
			controlPanel.setLayout(flowLayout);
			controlPanel.setPreferredSize(new Dimension(frameWidth, controlPanelHeight));
			controlPanel.setMinimumSize(new Dimension(frameWidth, controlPanelHeight));
			controlPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, controlPanelMaxHeight));

			controlPanel.add(new FeedDescriptorPanel(feedDescriptor, chartPresentationManager, availableInstruments, feedChangeListener));
			getContentPane().add(controlPanel);
		}

		/**
		 * Add chart panel to the frame
		 * 
		 * @param panel
		 */
		private void addChartPanel(JPanel chartPanel) {
			chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight - controlPanelHeight));
			chartPanel.setMinimumSize(new Dimension(frameWidth, 200));
			chartPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			getContentPane().add(chartPanel);
		}
	}

	@SuppressWarnings("serial")
	private class FeedDescriptorPanel extends JPanel {

		private JComboBox cmbDataType;
		private JComboBox cmbInstrument;
		private JComboBox cmbOfferSide;
		private JComboBox cmbFilter;
		private JComboBox cmbPeriod;
		private JComboBox cmbPriceRange;
		private JComboBox cmbReversalAmount;
		private JComboBox cmbTickBarSize;
		private JComboBox cmbDataRepresentationType;
		private JComboBox cmbChartTheme;

		final IFeedDescriptor feedDescriptor;
		final IClientChartPresentationManager chartPresentationManager;
		final IFeedChangeListener feedChangeListener;

		public FeedDescriptorPanel(
				IFeedDescriptor feedDescriptor, 
				IClientChartPresentationManager chartPresentationManager, 
				Set<Instrument> availableInstruments, 
				IFeedChangeListener feedChangeListener
			) {

			this.feedDescriptor = feedDescriptor;
			this.chartPresentationManager = chartPresentationManager;
			this.feedChangeListener = feedChangeListener;
			this.setLayout(new FlowLayout(FlowLayout.LEFT));

			cmbDataType = setupComboBox(DataType.values(), "Data type", feedDescriptor.getDataType());
			cmbInstrument = setupComboBox(availableInstruments.toArray(), "Instrument", feedDescriptor.getInstrument());
			cmbOfferSide = setupComboBox(OfferSide.values(), "Offer Side", feedDescriptor.getOfferSide());
			cmbFilter = setupComboBox(Filter.values(), "Filter", feedDescriptor.getFilter());
			// Note that for most of there we put only some of the available values
			cmbPeriod = setupComboBox(new Period[] { TEN_SECS, ONE_MIN, TEN_MINS, ONE_HOUR, FOUR_HOURS, DAILY }, "Period",
					feedDescriptor.getPeriod());
			cmbPriceRange = setupComboBox(new PriceRange[] { ONE_PIP, TWO_PIPS, THREE_PIPS, FOUR_PIPS, FIVE_PIPS, SIX_PIPS },
					"Price Range", feedDescriptor.getPriceRange());
			cmbReversalAmount = setupComboBox(new ReversalAmount[] { ReversalAmount.ONE, ReversalAmount.TWO, ReversalAmount.THREE },
					"Reversal Amount", feedDescriptor.getReversalAmount());
			cmbTickBarSize = setupComboBox(new TickBarSize[] { TWO, THREE, FOUR, FIVE }, "Tick data Size",
					feedDescriptor.getTickBarSize());
			cmbDataRepresentationType = new JComboBox(feedDescriptor.getDataType().getSupportedPresentationTypes().toArray());
			cmbDataRepresentationType.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbDataRepresentationType.getSelectedItem() != null) {
						FeedDescriptorPanel.this.chartPresentationManager
								.setDataPresentationType((DataPresentationType) cmbDataRepresentationType.getSelectedItem());
					}
				}
			});   
			cmbChartTheme = new JComboBox(new IChartTheme[]{
					chartPresentationManager.getPredefinedTheme(IChartTheme.Predefined.getDefault())
						.setName("Default theme: " + IChartTheme.Predefined.getDefault().getName()),
					chartPresentationManager.getPredefinedTheme(IChartTheme.Predefined.BLUE_BLACK_ON_GRAY)
						.setName("Custom tick/candle color theme")
						.setColor(ColoredElement.CANDLE_BEAR, Color.CYAN)
						.setColor(ColoredElement.CANDLE_BULL, Color.ORANGE)
						.setColor(ColoredElement.ASK, Color.YELLOW.darker())
						.setColor(ColoredElement.BID, Color.PINK.darker()),
					chartPresentationManager.getPredefinedTheme(IChartTheme.Predefined.getDefault())
						.setName("Custom background theme")
						.setColor(ColoredElement.BACKGROUND, new Color(150, 243, 241))
						.setColor(ColoredElement.AXIS_PANEL_BACKGROUND, new Color(150, 243, 151))
			});
			cmbChartTheme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cmbChartTheme.getSelectedItem() != null) {
						FeedDescriptorPanel.this.chartPresentationManager.setTheme((IChartTheme)cmbChartTheme.getSelectedItem());
					}
				}
			}); 
						
			add(cmbDataType);
			add(cmbDataRepresentationType);
			add(cmbInstrument);
			add(cmbPeriod);
			add(cmbOfferSide);
			add(cmbFilter);
			add(cmbPriceRange);
			add(cmbReversalAmount);
			add(cmbTickBarSize);
			add(cmbChartTheme);
			
			onSelectionChange();
		}

		private void resetDataRepresentationTypeCombobox() {
			cmbDataRepresentationType.removeAllItems();
			for (Object o : feedDescriptor.getDataType().getSupportedPresentationTypes()) {
				cmbDataRepresentationType.addItem(o);
			}
		}

		private JComboBox setupComboBox(final Object items[], final String name, Object defaultValue) {
			JComboBox comboBox = new JComboBox(items);
			comboBox.setSelectedItem(defaultValue == null ? items[0] : defaultValue);
			comboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onSelectionChange();
					if (e.getSource() == cmbDataType) {
						resetDataRepresentationTypeCombobox();
					}
				}

			});
			comboBox.setToolTipText(name);
			return comboBox;
		}
		
		private void onSelectionChange(){
			updateFeedDesciptor();
			updateComboBoxes();
		}

		private void updateComboBoxes() {

			DataType dataType = (DataType) cmbDataType.getSelectedItem();

			// visibility conditions according to IFeedDescription interface documentation
			cmbDataType.setVisible(true);
			cmbInstrument.setVisible(true);
			cmbOfferSide.setVisible(dataType != TICKS);
			cmbFilter.setVisible(dataType == TIME_PERIOD_AGGREGATION);
			cmbPeriod.setVisible(dataType == TIME_PERIOD_AGGREGATION);
			cmbPriceRange.setVisible(dataType == PRICE_RANGE_AGGREGATION || dataType == POINT_AND_FIGURE || dataType == RENKO);
			cmbReversalAmount.setVisible(dataType == POINT_AND_FIGURE);
			cmbTickBarSize.setVisible(dataType == TICK_BAR);
		}

		private void updateFeedDesciptor() {

			feedDescriptor.setDataType((DataType) cmbDataType.getSelectedItem());
			feedDescriptor.setInstrument((Instrument) cmbInstrument.getSelectedItem());
			feedDescriptor.setPeriod((Period) cmbPeriod.getSelectedItem());
			feedDescriptor.setOfferSide((OfferSide) cmbOfferSide.getSelectedItem());
			feedDescriptor.setFilter((Filter) cmbFilter.getSelectedItem());
			feedDescriptor.setPriceRange((PriceRange) cmbPriceRange.getSelectedItem());
			feedDescriptor.setReversalAmount((ReversalAmount) cmbReversalAmount.getSelectedItem());
			feedDescriptor.setTickBarSize((TickBarSize) cmbTickBarSize.getSelectedItem());
			chartPresentationManager.setFeedDescriptor(feedDescriptor);
			
			feedChangeListener.onFeedChange(feedDescriptor);
		}

	}
}