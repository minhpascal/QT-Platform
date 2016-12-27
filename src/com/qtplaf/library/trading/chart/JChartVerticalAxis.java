/**
 * 
 */
package com.qtplaf.library.trading.chart;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.VerticalAxisPlotter;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.util.FormatUtils;

/**
 * A panel that contains the vertical axis in a chart view.
 *
 * @author Miquel Sas
 */
public class JChartVerticalAxis extends JPanel {

	/**
	 * The parent chart container.
	 */
	private JChartContainer chartContainer;
	/**
	 * The cursor (mouse) point in the chart panel..
	 */
	private Point mousePoint;

	/**
	 * Constructor assigning the parent chart container.
	 * 
	 * @param cartContainer The parent chart container.
	 */
	public JChartVerticalAxis(JChartContainer chartContainer) {
		super();
		this.chartContainer = chartContainer;
	}

	/**
	 * Returns the parent container.
	 * 
	 * @return The parent container.
	 */
	public JChartContainer getChartContainer() {
		return chartContainer;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return getChartContainer().getSession();
	}

	/**
	 * Sets the maximum, minimum and preferred sizes based on the data and the insets.
	 */
	public void setMaximumMinimumAndPreferredSizes() {

		// Retrieve the plot data and calculate frema if necessary.
		PlotData plotData = chartContainer.getPlotData();
		if (!plotData.areMaximumAndMinimumValuesCalculated()) {
			plotData.calculateFrame();
		}
		double maximumValue = plotData.getMaximumValue();
		double minimumValue = plotData.getMinimumValue();
		int tickScale = plotData.getTickScale();
		String smaximumValue = FormatUtils.unformattedFromDouble(maximumValue, tickScale);
		String sminimumValue = FormatUtils.unformattedFromDouble(minimumValue, tickScale);

		// Plot parameters.
		PlotParameters plotParameters = chartContainer.getChart().getPlotParameters();

		// A grafics object and textFont metrics necessary to calculate the text width.
		Graphics g = chartContainer.getChart().getGraphics();
		FontMetrics fm = g.getFontMetrics(plotParameters.getVerticalAxisTextFont());
		int textWidth = Math.max(fm.stringWidth(smaximumValue), fm.stringWidth(sminimumValue));

		// Calculate the total width.
		int width =
			plotParameters.getVerticalAxisLineLength()
				+
				plotParameters.getVerticalAxisTextInsets().left
				+
				textWidth
				+
				plotParameters.getVerticalAxisTextInsets().right;

		// Set the sizes.
		setMinimumSize(new Dimension(width, 0));
		setMaximumSize(new Dimension(width, 0));
		setPreferredSize(new Dimension(width, 0));

		// Repaint the container.
		chartContainer.repaint();
	}

	/**
	 * Set the mouse point and repaint.
	 * 
	 * @param mousePoint The mouse point.
	 * @param repaint A boolean that indicates if the panel sould be repainted.
	 */
	public void setMousePoint(Point mousePoint, boolean repaint) {
		this.mousePoint = mousePoint;
		if (repaint) {
			repaint();
		}
	}

	/**
	 * Clear the mouse point, so it does not show custom cursors.
	 */
	public void clearMousePoint(boolean repaint) {
		mousePoint = null;
		if (repaint) {
			repaint();
		}
	}

	/**
	 * Paint this chart.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// 2D graphics.
		Graphics2D g2 = (Graphics2D) g;

		// The plotter.
		VerticalAxisPlotter plotter = getVerticalAxisPlotter();

		// Plot the scale.
		plotter.plotScale(g2);

		// Plot the vursor value if the mouse pointer have been set.
		if (mousePoint != null) {
			plotter.plotCursorValue(g2, mousePoint.y);
		}
	}

	/**
	 * Returns the vertical axis plotter.
	 * 
	 * @return The vertical axis plotter.
	 */
	private VerticalAxisPlotter getVerticalAxisPlotter() {
		Dimension chartSize = chartContainer.getChartPlotter().getSize();
		PlotData plotData = chartContainer.getPlotData();
		PlotParameters plotParameters = chartContainer.getChart().getPlotParameters();
		return new VerticalAxisPlotter(getSession(), plotData, chartSize, plotParameters);
	}

}
