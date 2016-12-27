package trash.jforex.chart;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.dukascopy.api.IChartObject;
import com.dukascopy.api.drawings.IOhlcChartObject;

/**
 * The action listener for the OHLC button.
 * 
 * @author Miquel Sas
 */
class ActionListenerOHLCButton implements ActionListener {
	/** The chart manager. */
	private ChartManager chartManager;

	/**
	 * Constructor assigning the chart manager.
	 * 
	 * @param chartManager The chart manager.
	 */
	ActionListenerOHLCButton(ChartManager chartManager) {
		this.chartManager = chartManager;
	}

	/**
	 * Responds on action performed.
	 */
	public void actionPerformed(ActionEvent e) {
		List<IChartObject> objects = chartManager.getChart().getAll();
		for (IChartObject object : objects) {
			if (object instanceof IOhlcChartObject) {
				chartManager.getChart().remove(object);
				return;
			}
		}
		chartManager.getChartController().addOHLCInformer();
	}

}