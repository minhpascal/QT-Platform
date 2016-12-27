/**
 * 
 */
package com.qtplaf.library.trading.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.LineBorderSides;
import com.qtplaf.library.trading.chart.plotter.HorizontalAxisPlotter;
import com.qtplaf.library.trading.data.PlotData;

/**
 * A panel that contains the horizontal axis in a chart view. The horizontal axis contans one or two lines and shows
 * time information depending on the period shown.
 * 
 * @author Miquel Sas
 */
public class JChartHorizontalAxis extends JPanel {

	/**
	 * The parent chart object.
	 */
	private JChart chart;

	/**
	 * Constructor assigning the parent chart.
	 * 
	 * @param chart The parent chart.
	 */
	public JChartHorizontalAxis(JChart chart) {
		super();
		this.chart = chart;

		setBackground(chart.getDefaultBackgroundColor());
		setBorder(new LineBorderSides(Color.BLACK, 1, true, false, false, false));

		// A grafics object and textFont metrics necessary to calculate the text width.
		Graphics g = chart.getGraphics();
		FontMetrics fm = g.getFontMetrics(chart.getPlotParameters().getHorizontalAxisTextFont());

		// Text insets and axis height.
		Insets insets = chart.getPlotParameters().getHorizontalAxisTextInsets();
		int height = insets.top + fm.getAscent() + fm.getDescent() + insets.bottom;

		// Set the sizes.
		setMinimumSize(new Dimension(0, height));
		setMaximumSize(new Dimension(0, height));
		setPreferredSize(new Dimension(0, height));
	}

	/**
	 * Returns the parent chart.
	 * 
	 * @return The parent chart.
	 */
	public JChart getChart() {
		return chart;
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return chart.getSession();
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
		HorizontalAxisPlotter plotter = getHorizontalAxisPlotter();

		// Do plot.
		plotter.plotAxis(g2);

	}

	/**
	 * Returns the horizontal axis plotter.
	 * 
	 * @return The horizontal axis plotter.
	 */
	private HorizontalAxisPlotter getHorizontalAxisPlotter() {
		Dimension chartSize = chart.getChartContainer(0).getChartPlotter().getSize();
		PlotData plotData = chart.getChartContainer(0).getPlotData();
		PlotParameters plotParameters = chart.getChartContainer(0).getChart().getPlotParameters();
		return new HorizontalAxisPlotter(getSession(), plotData, chartSize, plotParameters, getSize());
	}
}
