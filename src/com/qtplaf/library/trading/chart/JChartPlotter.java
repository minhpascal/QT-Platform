/**
 * 
 */
package com.qtplaf.library.trading.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.plotter.BarPlotter;
import com.qtplaf.library.trading.chart.plotter.CandlestickPlotter;
import com.qtplaf.library.trading.chart.plotter.CrossCursorPlotter;
import com.qtplaf.library.trading.chart.plotter.DataPlotter;
import com.qtplaf.library.trading.chart.plotter.LinePlotter;
import com.qtplaf.library.trading.chart.plotter.Plotter;
import com.qtplaf.library.trading.chart.plotter.drawings.CrossCursor;
import com.qtplaf.library.trading.data.DataList;
import com.qtplaf.library.trading.data.PlotData;
import com.qtplaf.library.trading.data.PlotProperties;

/**
 * The chart panel that effectively plots charts. The types of charts are <i>line</i>, <i>bar</i>, <i>candlestick</i>
 * and <i>histogram</i>.
 * 
 * @author Miquel Sas
 */
public class JChartPlotter extends JPanel {

	/**
	 * The parent chart container.
	 */
	private JChartContainer chartContainer;
	/**
	 * Current mouse point.
	 */
	private Point currentMousePoint;
	/**
	 * Previous mouse point.
	 */
	private Point previousMousePoint;
	/**
	 * The cursor to use.
	 */
	private CursorType cursorType = CursorType.Custom;

	/**
	 * Constructor assigning the parent chart container.
	 * 
	 * @param cartContainer The parent chart container.
	 */
	public JChartPlotter(JChartContainer chartContainer) {
		super();
		this.chartContainer = chartContainer;

		// All panels above the container must be transparent.
		setOpaque(false);

		// Setup mouse listeners.
		JChartPlotterListener listener = new JChartPlotterListener(this);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		addMouseWheelListener(listener);

		// Set the cursor type from default plot parameters.
		setCursorType(chartContainer.getChart().getPlotParameters().getChartCursorType());
	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return chartContainer.getSession();
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
	 * Returns the cursor type.
	 * 
	 * @return The cursor type.
	 */
	public CursorType getCursorType() {
		return cursorType;
	}

	/**
	 * Sets the cursor type.
	 * 
	 * @param cursorType The cursor type.
	 */
	public void setCursorType(CursorType cursorType) {
		this.cursorType = cursorType;
	}

	/**
	 * Set the mouse point and repaint if required. If repaint is required a the clips for the cross cursor are added.
	 * 
	 * @param currentMousePoint The mouse point.
	 * @param repaint A boolean that indicates if the panel sould be repainted.
	 */
	public void setMousePoint(Point mousePoint, boolean repaint) {

		// Register the mouse points.
		previousMousePoint = currentMousePoint;
		currentMousePoint = mousePoint;

		// The need to repaint is only applicable if the cursor type is the chart cross cursor.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (repaint) {
				Rectangle currentBounds = getCrossCursor(mousePoint).getShape(getCrossCursorPlotter()).getBounds();
				currentBounds = Plotter.getIntersectionBounds(currentBounds);
				if (previousMousePoint != null) {
					Rectangle previousBounds =
						getCrossCursor(previousMousePoint).getShape(getCrossCursorPlotter()).getBounds();
					previousBounds = Plotter.getIntersectionBounds(previousBounds);
					currentBounds = currentBounds.union(previousBounds);
				}
				paintImmediately(currentBounds);
			}
		}
	}

	/**
	 * Clear the mouse point, so it does not show custom cursors.
	 */
	public void clearMousePoint(boolean repaint) {
		if (currentMousePoint == null) {
			return;
		}
		// The need to repaint is only applicable if the cursor type is the chart cross cursor.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (repaint) {
				Rectangle bounds = getCrossCursor(currentMousePoint).getShape(getCrossCursorPlotter()).getBounds();
				repaint(bounds);
			}
		}
		currentMousePoint = null;
		previousMousePoint = null;
	}

	/**
	 * Paint this chart.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// The graphics object.
		Graphics2D g2 = (Graphics2D) g;

		// Set plotters.
		setDataPlotters();

		// Plot chart data.
		plotChartData(g2, chartContainer.getPlotData());

		// Plot the cross cursor if required.
		if (cursorType.equals(CursorType.ChartCross)) {
			if (currentMousePoint != null) {
				CrossCursorPlotter crossCursorPlotter = getCrossCursorPlotter();
				crossCursorPlotter.plot(g2, getCrossCursor(currentMousePoint));
			}
		}
	}

	/**
	 * Plot the chart data.
	 * 
	 * @param g2 The graphics object.
	 */
	private void plotChartData(Graphics2D g2, PlotData plotData) {

		// Retrieve a plotter to calculate start and end indexes depending on the clip bounds.
		Plotter plotter = plotData.get(0).getDataPlotter();
		int x1 = g2.getClipBounds().x;
		int x2 = x1 + g2.getClipBounds().width;
		int startIndexCheck = plotter.getDataIndex(x1);
		int endIndexCheck = plotter.getDataIndex(x2);

		// Start and end indexes from plot data.
		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();
		int startIndexClip = startIndex;
		int endIndexClip = endIndex;

		// Set start and end indexes applying clip bounds with a margin.
		int margin = Math.max((endIndex - startIndex + 1) / 50, 2);
		if (startIndexCheck > startIndex + margin) {
			startIndexClip = startIndexCheck - margin;
		}
		if (endIndexCheck < endIndex - margin) {
			endIndexClip = endIndexCheck + margin;
		}

		// Do plot.
		for (int i = 0; i < plotData.size(); i++) {
			DataList dataList = plotData.get(i);
			if (isPlotFromScratch(dataList)) {
				for (int index = startIndex; index < endIndex; index++) {
					plotChartData(g2, dataList, index);
				}
			} else {
				for (int index = startIndexClip; index < endIndexClip; index++) {
					plotChartData(g2, dataList, index);
				}
			}
		}

		// Terminate plots.
		for (int i = 0; i < plotData.size(); i++) {
			DataList dataList = plotData.get(i);
			dataList.getDataPlotter().endPlot(g2, dataList);
		}
	}

	/**
	 * Plot an index of a data list.
	 * 
	 * @param g2 The graphics object.
	 * @param index The index to plot.
	 */
	private void plotChartData(Graphics2D g2, DataList dataList, int index) {

		// Data size.
		int size = dataList.size();

		// If the index is out of range, do nothing.
		if (index < 0 || index >= size) {
			return;
		}

		// If the current data is not valid, skip it.
		if (!dataList.get(index).isValid()) {
			return;
		}

		// Do plot.
		dataList.getDataPlotter().plotDataIndex(g2, dataList, index);
	}

	/**
	 * Check if a data list has to be plotted from scratch, mainly because it plots lines with dashes.
	 * 
	 * @param dataList The data list.
	 * @return A boolean that indicates if the data list has to be plotte from scratch.
	 */
	private boolean isPlotFromScratch(DataList dataList) {
		int count = dataList.getPlotPropertiesCount();
		for (int i = 0; i < count; i++) {
			PlotProperties plotProperties = dataList.getPlotProperties(i);
			BasicStroke stroke = (BasicStroke) plotProperties.getStroke();
			if (stroke.getDashArray() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets a suitable plotter to each data list in the plot data.
	 */
	private void setDataPlotters() {
		Dimension chartSize = getSize();
		PlotData plotData = chartContainer.getPlotData();
		PlotParameters plotParameters = chartContainer.getChart().getPlotParameters();
		for (int i = 0; i < plotData.size(); i++) {
			DataList dataList = plotData.get(i);
			DataPlotter dataPlotter;
			switch (dataList.getPlotType()) {
			case Bar:
				dataPlotter = new BarPlotter(getSession(), plotData, chartSize, plotParameters);
				break;
			case Candlestick:
				dataPlotter = new CandlestickPlotter(getSession(), plotData, chartSize, plotParameters);
				break;
			case Histogram:
				// TODO: implement histogram plotter.
				dataPlotter = new LinePlotter(getSession(), plotData, chartSize, plotParameters);
				break;
			case Line:
				dataPlotter = new LinePlotter(getSession(), plotData, chartSize, plotParameters);
				break;
			default:
				dataPlotter = new LinePlotter(getSession(), plotData, chartSize, plotParameters);
				break;
			}
			dataList.setDataPlotter(dataPlotter);
		}
	}

	/**
	 * Returns the cross cursor plotter.
	 * 
	 * @return The cross cursor plotter.
	 */
	private CrossCursorPlotter getCrossCursorPlotter() {
		Dimension chartSize = getSize();
		PlotData plotData = chartContainer.getPlotData();
		PlotParameters plotParameters = chartContainer.getChart().getPlotParameters();
		return new CrossCursorPlotter(getSession(), plotData, chartSize, plotParameters);
	}

	/**
	 * Returns the cross cursor given the point.
	 * 
	 * @param point The cursor point.
	 * @return The cross cursor.
	 */
	protected CrossCursor getCrossCursor(Point point) {
		PlotParameters plotParameters = chartContainer.getChart().getPlotParameters();
		CrossCursor cursor = new CrossCursor(point);
		cursor.setWidth(plotParameters.getChartCrossCursorWidth());
		cursor.setHeight(plotParameters.getChartCrossCursorHeight());
		cursor.setStroke(plotParameters.getChartCrossCursorStroke());
		cursor.setColor(plotParameters.getChartCrossCursorColor());
		cursor.setRadius(plotParameters.getChartCrossCursorCircleRadius());
		return cursor;
	}

	/**
	 * Returns a transparent cursor
	 * 
	 * @return The transparent cursor.
	 */
	protected Cursor getTransparentCursor() {
		Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(0, 0);
		BufferedImage cursorImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Transparent cursor");
	}

	/**
	 * Returns the custom cross hair cursor, thinest that the Java one.
	 * 
	 * @return The custom cross hair cursor.
	 */
	protected Cursor getCustomCursor() {
		Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(0, 0);
		int width = size.width;
		int height = size.height;
		int x = width / 2;
		int y = height / 2;
		BufferedImage cursorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = cursorImage.getGraphics();
		g.setColor(Color.GRAY);
		g.drawLine(0, y, width, y);
		g.drawLine(x, 0, x, height);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Transparent cursor");
	}

	/**
	 * Sets the appropriate cursor.
	 */
	void setCursor() {
		switch (cursorType) {
		case Predefined:
			setCursor(Cursor.getPredefinedCursor(
				getChartContainer().getChart().getPlotParameters().getChartCursorTypePredefined()));
			break;
		case Custom:
			setCursor(getCustomCursor());
			break;
		case ChartCross:
			// Set the transparent cursor.
			// setCursor(getTransparentCursor());
			setCursor(Cursor.getPredefinedCursor(
				getChartContainer().getChart().getPlotParameters().getChartCursorTypePredefined()));
			break;
		default:
			setCursor(Cursor.getPredefinedCursor(
				getChartContainer().getChart().getPlotParameters().getChartCursorTypePredefined()));
			break;
		}
	}

}
