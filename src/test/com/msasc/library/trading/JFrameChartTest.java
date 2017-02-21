/**
 * 
 */
package test.com.msasc.library.trading;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.JFrame;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.trading.chart.JChart;

/**
 * A frame to test the chart panel functionality.
 * 
 * @author Miquel Sas
 */
public class JFrameChartTest extends JFrame {
	
	/**
	 * The chart panel.
	 */
	private JChart chart;
	
	/**
	 * @throws HeadlessException if GraphicsEnvironment.isHeadless()
	 */
	public JFrameChartTest() throws HeadlessException {
		super();
		getContentPane().setLayout(new GridBagLayout());
		
		GridBagConstraints constraintsChartPanel = new GridBagConstraints();
		constraintsChartPanel.anchor = GridBagConstraints.NORTH;
		constraintsChartPanel.fill = GridBagConstraints.BOTH;
		constraintsChartPanel.gridheight = 1;
		constraintsChartPanel.gridwidth = 1;
		constraintsChartPanel.weightx = 1;
		constraintsChartPanel.weighty = 1;
		constraintsChartPanel.gridx = 0;
		constraintsChartPanel.gridy = 1;
		constraintsChartPanel.insets = new Insets(1, 1, 1, 1);
		
		chart = new JChart(new Session(Locale.UK));
		getContentPane().add(chart, constraintsChartPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		SwingUtils.setSizeAndCenterOnSreen(this, 0.8, 0.8);
		setVisible(true);
	}

	public JChart getChart() {
		return chart;
	}
}
