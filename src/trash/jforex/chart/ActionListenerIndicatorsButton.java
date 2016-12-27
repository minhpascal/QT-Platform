package trash.jforex.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The action listener for the indicators button.
 * 
 * @author Miquel Sas
 */
class ActionListenerIndicatorsButton implements ActionListener {
	/** The chart manager. */
	private ChartManager chartManager;
	/** The cursor flag control. */
	private boolean cursor = false;

	/**
	 * Constructor assigning the chart manager.
	 * 
	 * @param chartManager The chart manager.
	 */
	ActionListenerIndicatorsButton(ChartManager chartManager) {
		this.chartManager = chartManager;
	}

	/**
	 * Responds on action performed.
	 */
	public void actionPerformed(ActionEvent e) {
		cursor = !cursor;
		chartManager.getChartController().addIndicators();
	}

}