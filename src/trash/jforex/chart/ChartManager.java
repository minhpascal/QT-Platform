/**
 * 
 */
package trash.jforex.chart;

import java.util.List;

import javax.swing.JPanel;

import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientChartController;
import com.dukascopy.api.IClientChartPresentationManager;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.api.chart.mouse.IChartPanelMouseListener;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * A chart manager that is created either from anormal or a tester environment.
 * 
 * @author Miquel Sas
 */
public class ChartManager {

	/**
	 * The reference to the chart.
	 */
	private IChart chart;
	/**
	 * The reference to the panel that contains the chart.
	 */
	private JPanel chartPanel;
	/**
	 * The chart presentation manager.
	 */
	private IClientChartPresentationManager chartPresentationManager;
	/**
	 * The chart controller.
	 */
	private IClientChartController chartController;

	/**
	 * Default constructor.
	 */
	public ChartManager() {
		super();
	}

	/**
	 * Returns the chart controller.
	 * 
	 * @return The chart controller.
	 */
	public IClientChartController getChartController() {
		return chartController;
	}

	/**
	 * Sets the chart controller.
	 * 
	 * @param chartController The chart ¡controller.
	 */
	public void setChartController(IClientChartController chartController) {
		this.chartController = chartController;
	}

	/**
	 * Returns the IChart interface.
	 * 
	 * @return The IChart interface.
	 */
	public IChart getChart() {
		return chart;
	}

	/**
	 * Set the IChart interface.
	 * 
	 * @param chart The IChart interface.
	 */
	public void setChart(IChart chart) {
		this.chart = chart;
		chart.addMouseListener(true, new MouseListenerChartPanel(this));
	}

	/**
	 * Returns the chart panel mouse listener installed by this chart manager.
	 * 
	 * @return The MouseListenerChartPanel
	 */
	public MouseListenerChartPanel getMouseListenerChartPanel() {
		List<IChartPanelMouseListener> mouseListeners = chart.getMouseListeners();
		for (IChartPanelMouseListener mouseListener : mouseListeners) {
			if (mouseListener instanceof MouseListenerChartPanel) {
				return (MouseListenerChartPanel) mouseListener;
			}
		}
		return null;
	}

	/**
	 * Returns the chart panel.
	 * 
	 * @return The chart panel.
	 */
	public JPanel getChartPanel() {
		return chartPanel;
	}

	/**
	 * Sets the chart panel.
	 * 
	 * @param chartPanel The JPanel that contains the charts.
	 */
	public void setChartPanel(JPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	/**
	 * Returns the chart presentation manager.
	 * 
	 * @return The chart presentation manager.
	 */
	public IClientChartPresentationManager getChartPresentationManager() {
		return chartPresentationManager;
	}

	/**
	 * Sets the chart presentation manager.
	 * 
	 * @param chartPresentationManager The chart presentation manager.
	 */
	public void setChartPresentationManager(IClientChartPresentationManager chartPresentationManager) {
		this.chartPresentationManager = chartPresentationManager;
	}

	/**
	 * Returns the chart feed descriptor.
	 * 
	 * @return The feed descriptor.
	 */
	public IFeedDescriptor getFeedDescriptor() {
		return getChart().getFeedDescriptor();
	}

	/**
	 * Sets the feed descriptor and updates the chart.
	 * 
	 * @param feedDescriptor The feed descriptor.
	 */
	public void setFeedDescriptor(IFeedDescriptor feedDescriptor) {
		getChartPresentationManager().setFeedDescriptor(feedDescriptor);
	}

	/**
	 * Returns the chart object factory.
	 * 
	 * @return The chart object factory.
	 */
	public IChartObjectFactory getChartObjectFactory() {
		return getChart().getChartObjectFactory();
	}

	/**
	 * Returns a suitable title of this chart.
	 * 
	 * @return The title.
	 */
	public String getTitle() {

		IFeedDescriptor feedDescriptor = getFeedDescriptor();
		Instrument instrument = feedDescriptor.getInstrument();
		Period period = feedDescriptor.getPeriod();

		StringBuilder title = new StringBuilder();
		title.append(instrument.toString());
		title.append(", ");
		title.append(period.toString());

		return title.toString();
	}

}
