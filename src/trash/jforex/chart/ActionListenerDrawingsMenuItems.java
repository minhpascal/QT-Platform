package trash.jforex.chart;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The action listener for the drawings menu items.
 * 
 * @author Miquel Sas
 */
public class ActionListenerDrawingsMenuItems implements ActionListener {
	/** The chart manager. */
	private ChartManager chartManager;

	/**
	 * Constructor assigning the chart manager.
	 */
	public ActionListenerDrawingsMenuItems(ChartManager chartManager) {
		this.chartManager = chartManager;
	}

	/**
	 * Responds on action performed.
	 */
	public void actionPerformed(ActionEvent e) {
		Component component = (Component) e.getSource();
		if (component.getName().equals("ShortLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.ShortLine);
		}
		if (component.getName().equals("LongLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.LongLine);
		}
		if (component.getName().equals("PolyLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.PolyLine);
		}
		if (component.getName().equals("RayLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.RayLine);
		}
		if (component.getName().equals("HorizontalLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.HorizontalLine);
		}
		if (component.getName().equals("VerticalLine")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.VerticalLine);
		}
		if (component.getName().equals("Rectangle")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.Rectangle);
		}
		if (component.getName().equals("Ellipse")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.Ellipse);
		}
		if (component.getName().equals("Triangle")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.Triangle);
		}
		if (component.getName().equals("FiboRetracements")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.FibonacciRetracements);
		}
		if (component.getName().equals("FiboExpansion")) {
			chartManager.getMouseListenerChartPanel().setChartObject(ChartObject.FibonacciExpansion);
		}
	}

}