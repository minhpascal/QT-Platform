package trash.jforex.chart;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * The tab title panel
 * 
 * @author Miquel Sas
 */
public class TabTitle extends JPanel {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -8763537088183257580L;
	
	/** The button with the chart icon and the text. */
	private JButton buttonChart;
	/** The button with the close icon. */
	private JButton buttonClose;

	/**
	 * Constructor assigning the flow layout.
	 */
	public TabTitle(String title, String tooltipText) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 2));
		setOpaque(false);
		try {
			// Add the button with icon title bar chart
			buttonChart = new JButton(title, 
				ImageIconUtils.getImageIcon(Icons.chart_16x16_titlebar_chart_inactive));
			buttonChart.setName("ButtonChart");
			buttonChart.setContentAreaFilled(false);
			buttonChart.setFocusable(false);
			buttonChart.setBorder(BorderFactory.createEmptyBorder());
			buttonChart.setBorderPainted(false);
			buttonChart.setToolTipText(tooltipText);
			add(buttonChart);

			JButton buttonSep = new JButton();
			buttonSep.setContentAreaFilled(false);
			buttonSep.setFocusable(false);
			buttonSep.setBorder(BorderFactory.createEmptyBorder());
			buttonSep.setBorderPainted(false);
			buttonSep.setPreferredSize(new Dimension(10, 0));
			add(buttonSep);

			// Add the the button with the close icon
			buttonClose = new JButton(
				ImageIconUtils.getImageIcon(Icons.chart_16x16_titlebar_close_tab));
			buttonClose.setName("ButtonClose");
			buttonClose.setContentAreaFilled(false);
			buttonClose.setFocusable(false);
			buttonClose.setBorder(BorderFactory.createEmptyBorder());
			buttonClose.setBorderPainted(false);
			buttonClose.setToolTipText("Close the current chart");
			add(buttonClose);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		MouseAdapter mouseAdapter = new MouseListenerTabTitle(this);
		buttonChart.addMouseListener(mouseAdapter);
		buttonClose.addMouseListener(mouseAdapter);

	}

	/**
	 * Sets the tab text title.
	 * 
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		buttonChart.setText(title);
	}

	/**
	 * Sets the chart icon.
	 * 
	 * @param icon The chart icon.
	 */
	public void setIconChart(ImageIcon icon) {
		buttonChart.setIcon(icon);
	}

}