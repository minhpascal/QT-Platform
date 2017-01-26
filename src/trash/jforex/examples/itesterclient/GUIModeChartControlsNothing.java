/*
 * Copyright (c) 2009 Dukascopy (Suisse) SA. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Dukascopy (Suisse) SA or the names of contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS
 * AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. DUKASCOPY (SUISSE) SA ("DUKASCOPY") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN
 * NO EVENT WILL DUKASCOPY OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF DUKASCOPY HAS BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGES.
 */
package trash.jforex.examples.itesterclient;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientGUI;
import com.dukascopy.api.IClientGUIListener;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.system.ISystemListener;
import com.dukascopy.api.system.ITesterClient;
import com.dukascopy.api.system.TesterFactory;
import com.dukascopy.api.system.tester.ITesterChartController;
import com.dukascopy.api.system.tester.ITesterExecution;
import com.dukascopy.api.system.tester.ITesterExecutionControl;
import com.dukascopy.api.system.tester.ITesterGui;
import com.dukascopy.api.system.tester.ITesterIndicatorsParameters;
import com.dukascopy.api.system.tester.ITesterUserInterface;
import com.dukascopy.api.system.tester.ITesterVisualModeParameters;

import trash.jforex.examples.singlejartest.MA_Play;
import trash.jforex.learning.strategies.MyFirstStrategy;
import trash.jforex.strategies.NoStrategy;

/**
 * This small program demonstrates how to initialize Dukascopy tester and start a strategy in GUI mode
 */
@SuppressWarnings("serial")
public class GUIModeChartControlsNothing extends JFrame implements ITesterUserInterface, ITesterExecution {
	private static final Logger LOGGER = LoggerFactory.getLogger(GUIModeChartControlsNothing.class);

	private final int frameWidth = 1000;
	private final int frameHeight = 600;
	private final int controlPanelHeight = 100;
	private final int controlPanelMaxHeight = 150;

	private JPanel currentChartPanel = null;
	private ITesterExecutionControl executionControl = null;

	private JPanel controlPanel = null;
	private JButton startStrategyButton = null;
	private JButton pauseButton = null;
	private JButton continueButton = null;
	private JButton cancelButton = null;
	private JPeriodComboBox jPeriodComboBox = null;

	private Map<IChart, ITesterGui> chartPanels = null;

	// url of the DEMO jnlp
	private static String jnlpUrl = "https://www.dukascopy.com/client/demo/jclient/jforex.jnlp";
	// user name
	private static String userName = "msasc1EU";
	// password
	private static String password = "C1a2r3l4a5";

	public GUIModeChartControlsNothing() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	}

	@Override
	public void setChartPanels(Map<IChart, ITesterGui> chartPanels) {
		this.chartPanels = chartPanels;
		this.jPeriodComboBox.setChartPanels(chartPanels);

		if (chartPanels != null && chartPanels.size() > 0) {

			IChart chart = chartPanels.keySet().iterator().next();
			LOGGER.info("Historical tester chart: " + chart.isHistoricalTesterChart());
			Instrument instrument = chart.getInstrument();
			setTitle(instrument.toString() + " " + chart.getSelectedOfferSide() + " " + chart.getSelectedPeriod());

			JPanel chartPanel = chartPanels.get(chart).getChartPanel();
			addChartPanel(chartPanel);
		}
	}

	@Override
	public void setExecutionControl(ITesterExecutionControl executionControl) {
		this.executionControl = executionControl;
	}

	public void startStrategy() throws Exception {
		// get the instance of the IClient interface
		final ITesterClient client = TesterFactory.getDefaultInstance();
		// set the listener that will receive system events
		client.setSystemListener(new ISystemListener() {
			@Override
			public void onStart(long processId) {
				LOGGER.info("Strategy started: " + processId);
				updateButtons();
			}

			@Override
			public void onStop(long processId) {
				LOGGER.info("Strategy stopped: " + processId);
				resetButtons();

				File reportFile = new File("C:\\report.html");
				try {
					client.createReport(processId, reportFile);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
				if (client.getStartedStrategies().size() == 0) {
					// Do nothing
				}
			}

			@Override
			public void onConnect() {
				LOGGER.info("Connected");
			}

			@Override
			public void onDisconnect() {
				// tester doesn't disconnect
			}
		});

		LOGGER.info("Connecting...");
		// connect to the server using jnlp, user name and password
		// connection is needed for data downloading
		client.connect(jnlpUrl, userName, password);

		// wait for it to connect
		int i = 10; // wait max ten seconds
		while (i > 0 && !client.isConnected()) {
			Thread.sleep(1000);
			i--;
		}
		if (!client.isConnected()) {
			LOGGER.error("Failed to connect Dukascopy servers");
			System.exit(1);
		}

		// set instruments that will be used in testing
		final Set<Instrument> instruments = new HashSet<Instrument>();
		instruments.add(Instrument.EURUSD);

		LOGGER.info("Subscribing instruments...");
		client.setSubscribedInstruments(instruments);

		client.addClientGUIListener(new IClientGUIListener() {

			@Override
			public void onOpenChart(final IClientGUI clientGUI) {
				LOGGER.info("Chart opened from a startegy " + clientGUI.getChart().getFeedDescriptor());
			}

			@Override
			public void onCloseChart(IChart chart) {
				LOGGER.info("Chart closed from a startegy " + chart.getFeedDescriptor());
			}

			private void updateOnClose(IChart chart) {
			}
		});

		// setting initial deposit
		client.setInitialDeposit(Instrument.EURUSD.getSecondaryCurrency(), 50000);
		// load data
		LOGGER.info("Downloading data");
		Future<?> future = client.downloadData(null);
		// wait for downloading to complete
		future.get();
		// start the strategy
		LOGGER.info("Starting strategy");

		// Implementation of IndicatorParameterBean
		final class IndicatorParameterBean implements ITesterIndicatorsParameters {
			@Override
			public boolean isEquityIndicatorEnabled() {
				return true;
			}

			@Override
			public boolean isProfitLossIndicatorEnabled() {
				return true;
			}

			@Override
			public boolean isBalanceIndicatorEnabled() {
				return true;
			}
		}
		// Implementation of TesterVisualModeParametersBean
		final class TesterVisualModeParametersBean implements ITesterVisualModeParameters {
			@Override
			public Map<Instrument, ITesterIndicatorsParameters> getTesterIndicatorsParameters() {
				Map<Instrument, ITesterIndicatorsParameters> indicatorParameters = new HashMap<Instrument, ITesterIndicatorsParameters>();
				IndicatorParameterBean indicatorParameterBean = new IndicatorParameterBean();
				indicatorParameters.put(Instrument.EURUSD, indicatorParameterBean);
				return indicatorParameters;
			}
		}
		// Create TesterVisualModeParametersBean
		TesterVisualModeParametersBean visualModeParametersBean = new TesterVisualModeParametersBean();

		// Start strategy
		client.startStrategy(
			new NoStrategy(),
			new LoadingProgressListener() {
				@Override
				public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
					LOGGER.info(information);
				}

				@Override
				public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime) {
				}

				@Override
				public boolean stopJob() {
					return false;
				}
			},
			visualModeParametersBean,
			this,
			this
			);
		// now it's running
	}

	/**
	 * Center a frame on the screen
	 */
	private void centerFrame() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);
	}

	/**
	 * Add chart panel to the frame
	 * 
	 * @param panel
	 */
	private void addChartPanel(JPanel chartPanel) {
		removecurrentChartPanel();

		this.currentChartPanel = chartPanel;
		chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight - controlPanelHeight));
		chartPanel.setMinimumSize(new Dimension(frameWidth, 200));
		chartPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		getContentPane().add(chartPanel);
		this.validate();
		chartPanel.repaint();
	}

	private ITesterChartController getChartController() {
		if (chartPanels == null || chartPanels.size() == 0) {
			return null;
		}
		IChart chart = chartPanels.keySet().iterator().next();
		ITesterGui gui = chartPanels.get(chart);
		ITesterChartController chartController = gui.getTesterChartController();
		return chartController;
	}

	/**
	 * Add buttons to start/pause/continue/cancel actions and other buttons
	 */
	private void addControlPanel() {

		controlPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		controlPanel.setLayout(flowLayout);
		controlPanel.setPreferredSize(new Dimension(frameWidth, controlPanelHeight));
		controlPanel.setMinimumSize(new Dimension(frameWidth, controlPanelHeight));
		controlPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, controlPanelMaxHeight));

		startStrategyButton = new JButton("Start strategy");
		startStrategyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startStrategyButton.setEnabled(false);
				Runnable r = new Runnable() {
					public void run() {
						try {
							startStrategy();
						} catch (Exception e2) {
							LOGGER.error(e2.getMessage(), e2);
							e2.printStackTrace();
							resetButtons();
						}
					}
				};
				Thread t = new Thread(r);
				t.start();
			}
		});

		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executionControl != null) {
					executionControl.pauseExecution();
					updateButtons();
				}
			}
		});

		continueButton = new JButton("Continue");
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executionControl != null) {
					executionControl.continueExecution();
					updateButtons();
				}
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executionControl != null) {
					executionControl.cancelExecution();
					updateButtons();
				}
			}
		});

		jPeriodComboBox = new JPeriodComboBox(this);

		List<JButton> chartControlButtons = new ArrayList<JButton>();

		chartControlButtons.add(new JButton("Add IndicatorUtils") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().addIndicators();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Price Marker") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activatePriceMarker();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Time Marker") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateTimeMarker();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Chart Auto Shift") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().setChartAutoShift();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Percent Lines") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activatePercentLines();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Channel Lines") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateChannelLines();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Poly Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activatePolyLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Short Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateShortLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Long Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateLongLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Ray Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateRayLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Horizontal Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateHorizontalLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Vertical Line") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateVerticalLine();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Add Text") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().activateTextMode();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Zoom In") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().zoomIn();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Zoom Out") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().zoomOut();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("add OHLC Index") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().addOHLCInformer();
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Bid") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().switchOfferSide(OfferSide.BID);
					}
				});
			}
		});

		chartControlButtons.add(new JButton("Ask") {
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getChartController().switchOfferSide(OfferSide.ASK);
					}
				});
			}
		});

		controlPanel.add(startStrategyButton);
		controlPanel.add(pauseButton);
		controlPanel.add(continueButton);
		controlPanel.add(cancelButton);

		controlPanel.add(jPeriodComboBox);

		for (JButton btn : chartControlButtons) {
			controlPanel.add(btn);
		}

		getContentPane().add(controlPanel);

		pauseButton.setEnabled(false);
		continueButton.setEnabled(false);
		cancelButton.setEnabled(false);

	}

	private void updateButtons() {
		if (executionControl != null) {
			startStrategyButton.setEnabled(executionControl.isExecutionCanceled());
			pauseButton.setEnabled(!executionControl.isExecutionPaused() && !executionControl.isExecutionCanceled());
			cancelButton.setEnabled(!executionControl.isExecutionCanceled());
			continueButton.setEnabled(executionControl.isExecutionPaused());

		}
	}

	private void resetButtons() {
		startStrategyButton.setEnabled(true);
		pauseButton.setEnabled(false);
		continueButton.setEnabled(false);
		cancelButton.setEnabled(false);
	}

	private void removecurrentChartPanel() {
		if (this.currentChartPanel != null) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						GUIModeChartControlsNothing.this.getContentPane().remove(GUIModeChartControlsNothing.this.currentChartPanel);
						GUIModeChartControlsNothing.this.getContentPane().repaint();
					}
				});
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public void showChartFrame() {
		setSize(frameWidth, frameHeight);
		centerFrame();
		addControlPanel();
		setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		GUIModeChartControlsNothing testerMainGUI = new GUIModeChartControlsNothing();
		testerMainGUI.showChartFrame();
	}
	@SuppressWarnings("serial")
	class JPeriodComboBox extends JComboBox implements ItemListener {
		private JFrame mainFrame = null;
		private Map<IChart, ITesterGui> chartPanels = null;
		private Map<Period, DataType> periods = new LinkedHashMap<Period, DataType>();

		public void setChartPanels(Map<IChart, ITesterGui> chartPanels) {
			this.chartPanels = chartPanels;

			IChart chart = chartPanels.keySet().iterator().next();
			this.setSelectedItem(chart.getSelectedPeriod());
		}

		public JPeriodComboBox(JFrame mainFrame) {
			this.mainFrame = mainFrame;
			this.addItemListener(this);

			periods.put(Period.THIRTY_SECS, DataType.TIME_PERIOD_AGGREGATION);
			periods.put(Period.FIVE_MINS, DataType.TIME_PERIOD_AGGREGATION);
			periods.put(Period.TEN_MINS, DataType.TIME_PERIOD_AGGREGATION);
			periods.put(Period.THIRTY_MINS, DataType.TIME_PERIOD_AGGREGATION);

			for (Period period : periods.keySet()) {
				this.addItem(period);
			}
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (chartPanels != null && chartPanels.size() > 0) {
					IChart chart = chartPanels.keySet().iterator().next();
					ITesterGui gui = chartPanels.get(chart);
					ITesterChartController chartController = gui.getTesterChartController();

					Period period = (Period) e.getItem();
					DataType dataType = periods.get(period);

					chartController.changePeriod(dataType, period);
					mainFrame.setTitle(chart.getInstrument().toString() + " " + chart.getSelectedOfferSide() + " " + chart.getSelectedPeriod());
				}
			}
		}
	}
}

