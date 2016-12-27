package trash.jforex.chart;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;

/**
 * The mouse adapter for the tab title component.
 * 
 * @author Miquel Sas
 */
public class MouseListenerTabTitle extends MouseAdapter {
	
	/** The reference to the JTabTitle pane. */
	private TabTitle tabTitle;

	/**
	 * Constructor.
	 */
	public MouseListenerTabTitle(TabTitle tabTitle) {
		super();
		this.tabTitle = tabTitle;
	}

	/**
	 * Deal with the mouse pressed event.
	 */
	public void mousePressed(MouseEvent e) {
		JTabbedPane tabbedPaneCharts = ChartUtilities.getParentTabbedPane(e);
		if (tabbedPaneCharts == null) {
			return;
		}
		int index = tabbedPaneCharts.indexOfTabComponent(tabTitle);
		tabbedPaneCharts.setSelectedIndex(index);
	}

}