/**
 * 
 */
package trash.jforex.chart;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.dukascopy.api.Period;
import com.dukascopy.api.Unit;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * Base frame to contain JForex-SDK charts. The frame can contain several charts in a tab pane.
 * 
 * @author Miquel Sas
 */
public class JFrameChart extends JFrame {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -9207656018848658244L;
	
	/**
	 * The list of periods.
	 */
	private static Period[] periods = new Period[] {
		Period.TICK,
		Period.ONE_SEC,
		Period.TWO_SECS,
		Period.TEN_SECS,
		Period.TWENTY_SECS,
		Period.THIRTY_SECS,
		Period.ONE_MIN,
		Period.createCustomPeriod(Unit.Minute, 3),
		Period.FIVE_MINS,
		Period.FIFTEEN_MINS,
		Period.TWENTY_MINS,
		Period.THIRTY_MINS,
		Period.ONE_HOUR,
		Period.FOUR_HOURS,
		Period.DAILY,
		Period.WEEKLY,
		Period.MONTHLY
	};

	/**
	 * The tab pane that contains the charts.
	 */
	private JTabbedPane tabbedPaneCharts;
	/**
	 * The list with chart managers, one gor each chart pane.
	 */
	private List<ChartManager> chartManagers = new ArrayList<>();

	/**
	 * Default constructor. No other constructor is provided, title and other properties should be set through a call to
	 * the appropriate methods.
	 * 
	 * @throws HeadlessException
	 */
	public JFrameChart() throws HeadlessException {
		super();
		setupFrame();
	}

	/**
	 * Setup the frame by adding its components.
	 */
	private void setupFrame() {
		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints constraintsMenuBar = new GridBagConstraints();
		constraintsMenuBar.anchor = GridBagConstraints.NORTH;
		constraintsMenuBar.fill = GridBagConstraints.HORIZONTAL;
		constraintsMenuBar.gridheight = 1;
		constraintsMenuBar.gridwidth = 1;
		constraintsMenuBar.weightx = 1;
		constraintsMenuBar.weighty = 0;
		constraintsMenuBar.gridx = 0;
		constraintsMenuBar.gridy = 0;
		constraintsMenuBar.insets = new Insets(1, 1, 1, 1);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(0, 20));
		menuBar.setMinimumSize(new Dimension(0, 20));
		JMenu menuFile = new JMenu("File");
		JMenuItem itemFileSave = new JMenuItem("Save");
		menuFile.add(itemFileSave);
		menuBar.add(menuFile);

		getContentPane().add(menuBar, constraintsMenuBar);

		GridBagConstraints constraintsTabbedPaneCharts = new GridBagConstraints();
		constraintsTabbedPaneCharts.anchor = GridBagConstraints.NORTH;
		constraintsTabbedPaneCharts.fill = GridBagConstraints.BOTH;
		constraintsTabbedPaneCharts.gridheight = 1;
		constraintsTabbedPaneCharts.gridwidth = 1;
		constraintsTabbedPaneCharts.weightx = 1;
		constraintsTabbedPaneCharts.weighty = 1;
		constraintsTabbedPaneCharts.gridx = 0;
		constraintsTabbedPaneCharts.gridy = 1;
		constraintsTabbedPaneCharts.insets = new Insets(1, 1, 1, 1);

		tabbedPaneCharts = new JTabbedPane();
		tabbedPaneCharts.addChangeListener(new ChangeListenerTab());
		getContentPane().add(tabbedPaneCharts, constraintsTabbedPaneCharts);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		SwingUtils.setSizeAndCenterOnSreen(this, 0.8, 0.8);
		setVisible(true);
	}

	/**
	 * Adds a chart manager, and it's correspondent chart, to this chart frame.
	 * 
	 * @param charManager The chart manager to add.
	 */
	public void addChartManager(ChartManager chartManager) {

		try {
			// Configure the panel to add to the tab pane.
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			// Store the chart manager in the client property of the panel to get them associated.
			panel.putClientProperty("ChartManager", chartManager);

			// Add the tool bar
			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

			// The period combo box
			JComboBox<Period> comboBoxPeriod = new JComboBox<>();
			for (Period period : periods) {
				comboBoxPeriod.addItem(period);
			}
			comboBoxPeriod.setSelectedItem(chartManager.getFeedDescriptor().getPeriod());
			comboBoxPeriod.addActionListener(new ActionListenerComboBoxPeriod(chartManager, comboBoxPeriod));
			toolBar.add(comboBoxPeriod);

			// Define buttons of the tool bar

			JButton buttonDrawings = new JButton();
			buttonDrawings.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			buttonDrawings.setIcon(ImageIconUtils.getImageIcon(Icons.chart_16x16_toolbar_drawings));
			buttonDrawings.setToolTipText("Drawings");
			buttonDrawings.setMargin(new Insets(1, 1, 1, 1));
			buttonDrawings.addActionListener(new ActionListenerDrawingsButton(chartManager));
			toolBar.add(buttonDrawings);

			JButton buttonCursor = new JButton();
			buttonCursor.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			buttonCursor.setIcon(ImageIconUtils.getImageIcon(Icons.chart_16x16_toolbar_cursor));
			buttonCursor.setToolTipText("Set cursor pointer");
			buttonCursor.setMargin(new Insets(1, 1, 1, 1));
			buttonCursor.addActionListener(new ActionListenerCursorButton(chartManager));
			toolBar.add(buttonCursor);

			JButton buttonIndicators = new JButton();
			buttonIndicators.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			buttonIndicators.setIcon(
				ImageIconUtils.getImageIcon(Icons.chart_16x16_toolbar_indicators));
			buttonIndicators.setToolTipText("Indicators");
			buttonIndicators.setMargin(new Insets(1, 1, 1, 1));
			buttonIndicators.addActionListener(new ActionListenerIndicatorsButton(chartManager));
			toolBar.add(buttonIndicators);

			JButton buttonOHLC = new JButton();
			buttonOHLC.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			buttonOHLC.setIcon(ImageIconUtils.getImageIcon(Icons.chart_16x16_toolbar_ohlc));
			buttonOHLC.setToolTipText("OHLC");
			buttonOHLC.setMargin(new Insets(1, 1, 1, 1));
			buttonOHLC.addActionListener(new ActionListenerOHLCButton(chartManager));
			toolBar.add(buttonOHLC);

			// Add the tool bar
			GridBagConstraints constraintsToolBar = new GridBagConstraints();
			constraintsToolBar.anchor = GridBagConstraints.NORTH;
			constraintsToolBar.fill = GridBagConstraints.HORIZONTAL;
			constraintsToolBar.gridheight = 1;
			constraintsToolBar.gridwidth = 1;
			constraintsToolBar.weightx = 1;
			constraintsToolBar.weighty = 0;
			constraintsToolBar.gridx = 0;
			constraintsToolBar.gridy = 0;
			constraintsToolBar.insets = new Insets(1, 1, 1, 1);
			panel.add(toolBar, constraintsToolBar);

			// Add the chart
			GridBagConstraints constraintsChart = new GridBagConstraints();
			constraintsChart.anchor = GridBagConstraints.NORTH;
			constraintsChart.fill = GridBagConstraints.BOTH;
			constraintsChart.gridheight = 1;
			constraintsChart.gridwidth = 1;
			constraintsChart.weightx = 1;
			constraintsChart.weighty = 1;
			constraintsChart.gridx = 0;
			constraintsChart.gridy = 1;
			constraintsChart.insets = new Insets(1, 1, 1, 1);
			panel.add(chartManager.getChartPanel(), constraintsChart);

			// Add the panel to the tab pane
			tabbedPaneCharts.addTab(chartManager.getTitle(), panel);
			int tabCount = tabbedPaneCharts.getTabCount();
			tabbedPaneCharts.setTabComponentAt(tabCount - 1, new TabTitle(chartManager.getTitle(), "tab number"));

			// Add the chart manager to the list of chart managers
			chartManagers.add(chartManager);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void addTab(String title) {
		try {
			tabbedPaneCharts.addTab(title, 
				ImageIconUtils.getImageIcon(Icons.chart_16x16_titlebar_chart_inactive), new JPanel());
			int tabCount = tabbedPaneCharts.getTabCount();
			tabbedPaneCharts.setTabComponentAt(tabCount - 1, new TabTitle(title, ""));
			// tabbedPaneCharts.setTabComponentAt(tabCount-1, new ButtonTabComponent(tabbedPaneCharts));
			tabbedPaneCharts.setSelectedIndex(tabCount - 1);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
