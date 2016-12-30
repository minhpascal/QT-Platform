package trash.jforex.chart;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.qtplaf.library.util.Icons;
import com.qtplaf.library.util.ImageIconUtils;

/**
 * The listener for the changes in the tab pane.
 * 
 * @author Miquel Sas
 */
public class ChangeListenerTab implements ChangeListener {

	/**
	 * Default constructor.
	 */
	public ChangeListenerTab() {
		super();
	}

	/**
	 * Respond on state changed.
	 */
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabbedPaneCharts = ChartUtilities.getParentTabbedPane(e);
		if (tabbedPaneCharts == null) {
			return;
		}

		// Set the appropriate tab icon depending if the tab is active or not.
		try {
			int selectedIndex = tabbedPaneCharts.getSelectedIndex();
			TabTitle tabTitleSelected = (TabTitle) tabbedPaneCharts.getTabComponentAt(selectedIndex);
			if (tabTitleSelected != null) {
				tabTitleSelected.setIconChart(
					ImageIconUtils.getImageIcon(Icons.chart_16x16_titlebar_chart_active));
			}
			for (int i = 0; i < tabbedPaneCharts.getTabCount(); i++) {
				if (i != selectedIndex) {
					TabTitle tabTitleNotSelected = (TabTitle) tabbedPaneCharts.getTabComponentAt(i);
					if (tabTitleNotSelected != null) {
						tabTitleNotSelected.setIconChart(
							ImageIconUtils.getImageIcon(Icons.chart_16x16_titlebar_chart_inactive));
					}
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}