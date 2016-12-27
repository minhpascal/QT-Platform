package trash.jforex.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JTabbedPane;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * The action listener for the period list box.
 * 
 * @author Miquel Sas
 */
public class ActionListenerComboBoxPeriod implements ActionListener {
	/** The chart manager. */
	private ChartManager chartManager;
	/** The combo box period. */
	private JComboBox<Period> comboBoxPeriod;

	/**
	 * Constructor assigning the chart manager and the combo box.
	 * 
	 * @param chartManager The chart manager.
	 * @param comboBoxPeriod The period combo box.
	 */
	public ActionListenerComboBoxPeriod(ChartManager chartManager, JComboBox<Period> comboBoxPeriod) {
		this.chartManager = chartManager;
		this.comboBoxPeriod = comboBoxPeriod;
	}

	/**
	 * Responds on action performed.
	 */
	public void actionPerformed(ActionEvent e) {
		JTabbedPane tabbedPaneCharts = ChartUtilities.getParentTabbedPane(e);
		if (tabbedPaneCharts == null) {
			return;
		}

		Period selectedPeriod = (Period) comboBoxPeriod.getSelectedItem();
		IFeedDescriptor feedDescriptor = chartManager.getFeedDescriptor();
		if (selectedPeriod == null) {
			return;
		}
		if (selectedPeriod.equals(Period.TICK)) {
			feedDescriptor.setDataType(DataType.TICKS);
		} else {
			feedDescriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
		}
		feedDescriptor.setPeriod(selectedPeriod);
		chartManager.setFeedDescriptor(feedDescriptor);
		int selectedIndex = tabbedPaneCharts.getSelectedIndex();
		TabTitle tabTitleSelected = (TabTitle) tabbedPaneCharts.getTabComponentAt(selectedIndex);
		if (tabTitleSelected != null) {
			tabTitleSelected.setTitle(chartManager.getTitle());
		}
	}

}