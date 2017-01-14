/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.trading.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.swing.core.LineBorderSides;
import com.qtplaf.library.swing.core.SwingUtils;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.PlotData;

/**
 * A top panel aimed to contain all the panels involved in the display of a trading chart. From top to down the panels
 * are:
 * <ul>
 * <li>A chart panel to display prices and over-chart indicators, with its corrsponding right-vertical axis panel.</li>
 * <li>Several optional panels to display not over-chart indicators, with their corrsponding right-vertical axis
 * panels.</li>
 * <li>A optional panel to display volumes and over-volumes indicators, with its corrsponding right-vertical axis
 * panel.</li>
 * <li>A bottom horizontal axis panel.</li>
 * </ul>
 * 
 * @author Miquel Sas
 *
 */
public class JChart extends JPanel {

	/**
	 * The list with chart containers added to this chart.
	 */
	private List<JChartContainer> chartContainers = new ArrayList<>();
	/**
	 * The horizontal axis.
	 */
	private JChartHorizontalAxis horizontalAxis;
	/**
	 * Horizontal axis height.
	 */
	private int horizontalAxisHeight = 40;
	/**
	 * The default background color.
	 */
	private Color defaultBackgroundColor = Color.WHITE;
	/**
	 * The split panel divider size.
	 */
	private int splitPaneDividerSize = 2;
	/**
	 * Plot parameters.
	 */
	private PlotParameters plotParameters = new PlotParameters(this);
	/**
	 * The working session.
	 */
	private Session session;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public JChart(Session session) {
		super();
		this.session = session;

		// A small line border.
		setBorder(new LineBorderSides(Color.BLACK, 1, true, true, true, true));

		// Use a grid bag layout, as it is the most generic one.
		setLayout(new GridBagLayout());

	}

	/**
	 * Returns the working session.
	 * 
	 * @return The working session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Adds a plot data to this chart, configurating and adding the appropriate <i>JChartContainer</i>.
	 * 
	 * @param plotData The plot data.
	 */
	public void addPlotData(PlotData plotData) {
		JChartContainer chartContainer = new JChartContainer(this);
		chartContainer.setBackground(getDefaultBackgroundColor());
		chartContainer.getChartPlotter().setBackground(getDefaultBackgroundColor());
		chartContainer.getChartVerticalAxis().setBackground(getDefaultBackgroundColor());
		chartContainer.setPlotData(plotData);
		chartContainers.add(chartContainer);
		setOrPropagatePlotDataIndexes();
		layoutPanels();
	}

	/**
	 * On the first plot data set the start and end index, on subsequent propagates the indexes.
	 */
	private void setOrPropagatePlotDataIndexes() {
		if (chartContainers.isEmpty()) {
			return;
		}
		if (chartContainers.size() == 1) {
			int width = getSize().width - chartContainers.get(0).getChartVerticalAxis().getSize().width;
			JChartContainer chartContainer = chartContainers.get(0);
			// Give a minimum of 4 pixels per bar.
			int periods = width / 4;
			PlotData plotData = chartContainer.getPlotData();
			if (!plotData.isEmpty()) {
				int size = plotData.get(0).size();
				int endIndex = size - 1;
				int startIndex = endIndex - periods + 1;
				if (startIndex < 0) {
					startIndex = 0;
				}
				plotData.setStartIndex(startIndex);
				plotData.setEndIndex(endIndex);
				plotData.calculateFrame();
			}
		} else {
			// Propagate.
			JChartContainer chartContainer = chartContainers.get(0);
			PlotData plotData = chartContainer.getPlotData();
			int startIndex = plotData.getStartIndex();
			int endIndex = plotData.getEndIndex();
			for (int i = 1; i < chartContainers.size(); i++) {
				chartContainers.get(i).getPlotData().setStartIndex(startIndex);
				chartContainers.get(i).getPlotData().setEndIndex(endIndex);
				chartContainers.get(i).getPlotData().calculateFrame();
			}
		}
	}

	/**
	 * Returns the plot parameters.
	 * 
	 * @return The plot parameters.
	 */
	public PlotParameters getPlotParameters() {
		return plotParameters;
	}

	/**
	 * Returns the split pane divider size.
	 * 
	 * @return The split pane divider size.
	 */
	public int getSplitPaneDividerSize() {
		return splitPaneDividerSize;
	}

	/**
	 * Sets the split pane divider size.
	 * 
	 * @param splitPaneDividerSize The split pane divider size.
	 */
	public void setSplitPaneDividerSize(int splitPaneDividerSize) {
		this.splitPaneDividerSize = splitPaneDividerSize;
	}

	/**
	 * Returns the horizontal axis height.
	 * 
	 * @return The horizontal axis height.
	 */
	public int getHorizontalAxisHeight() {
		return horizontalAxisHeight;
	}

	/**
	 * Sets the horizontal axis height.
	 * 
	 * @param horizontalAxisHeight The horizontal axis height.
	 */
	public void setHorizontalAxisHeight(int horizontalAxisHeight) {
		this.horizontalAxisHeight = horizontalAxisHeight;
	}

	/**
	 * Returns the default background color.
	 * 
	 * @return The default background color.
	 */
	public Color getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	/**
	 * Set the default background color.
	 * 
	 * @param defaultBackgroundColor The default background color.
	 */
	public void setDefaultBackgroundColor(Color defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
	}

	/**
	 * Returns the default split panel used to separate chart panels.
	 * 
	 * @return The split panel.
	 */
	private JSplitPane getSplitPane() {
		JSplitPane splitPane = new JSplitPane();
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerSize(getSplitPaneDividerSize());
		splitPane.setResizeWeight(0.5);
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	/**
	 * Returns the list with all split panels in the added order.
	 * 
	 * @return The list with all split panels.
	 */
	private List<JSplitPane> getAllSplitPanes() {
		List<Component> components = SwingUtils.getAllComponents(this);
		List<JSplitPane> splitPanes = new ArrayList<>();
		for (Component component : components) {
			if (JSplitPane.class.isInstance(component)) {
				splitPanes.add((JSplitPane) component);
			}
		}
		return splitPanes;
	}

	/**
	 * Returns the horizontal axis or null if not already set.
	 * 
	 * @return The horizontal axis.
	 */
	public JChartHorizontalAxis getChartHorizontalAxis() {
		return horizontalAxis;
	}

	/**
	 * Returns the list of chart containers of the given type.
	 * 
	 * @param chartType The chart type.
	 * @return The list of chart containers of the given type.
	 */
	public List<JChartContainer> getChartContainers(DataType chartType) {
		List<JChartContainer> containers = new ArrayList<>();
		for (JChartContainer chartContainer : chartContainers) {
			PlotData plotData = chartContainer.getPlotData();
			if (plotData == null) {
				continue;
			}
			if (plotData.isDataType(chartType)) {
				containers.add(chartContainer);
			}
		}
		return containers;
	}

	/**
	 * Returns the <i>JChartContainer</i> at the given <i>index</i> position.
	 * 
	 * @param index The index of the chart.
	 * @return The <i>JChartContainer</i>.
	 */
	public JChartContainer getChartContainer(int index) {
		return chartContainers.get(index);
	}

	/**
	 * Returns the number of chart containers.
	 * 
	 * @return The number of chart containers.
	 */
	public int getChartCount() {
		return chartContainers.size();
	}

	/**
	 * Removes the argument chart container.
	 * 
	 * @param chartContainer The chart container to remove.
	 */
	public void removeChartContainer(JChartContainer chartContainer) {
		chartContainers.remove(chartContainer);
		layoutPanels();
	}

	/**
	 * Layout the panels in the list.
	 */
	private void layoutPanels() {

		// Configure and set the horizontal axis panel.
		if (horizontalAxis == null) {
			horizontalAxis = new JChartHorizontalAxis(this);
		}

		// Remove all from this conainer, mainly charts and split panels.
		removeAll();

		// If no panels...
		if (chartContainers.isEmpty()) {
			revalidate();
			repaint();
			return;
		}

		// Constraints top panel.
		GridBagConstraints constraintsTopPanel = new GridBagConstraints();
		constraintsTopPanel.anchor = GridBagConstraints.NORTH;
		constraintsTopPanel.fill = GridBagConstraints.BOTH;
		constraintsTopPanel.gridheight = 1;
		constraintsTopPanel.gridwidth = 1;
		constraintsTopPanel.weightx = 1;
		constraintsTopPanel.weighty = 1;
		constraintsTopPanel.gridx = 0;
		constraintsTopPanel.gridy = 0;
		constraintsTopPanel.insets = new Insets(0, 0, 0, 0);

		// Constraints bottom panel.
		GridBagConstraints constraintsBottomPanel = new GridBagConstraints();
		constraintsBottomPanel.anchor = GridBagConstraints.SOUTH;
		constraintsBottomPanel.fill = GridBagConstraints.HORIZONTAL;
		constraintsBottomPanel.gridheight = 1;
		constraintsBottomPanel.gridwidth = 1;
		constraintsBottomPanel.weightx = 1;
		constraintsBottomPanel.weighty = 0;
		constraintsBottomPanel.gridx = 0;
		constraintsBottomPanel.gridy = 1;
		constraintsBottomPanel.insets = new Insets(0, 0, 0, 0);

		// Add panels in the order of the list.
		int splitPanels = chartContainers.size() - 1;
		double height = getSize().getHeight() - getHorizontalAxisHeight();
		double panelHeight = height * 0.15;
		int index = chartContainers.size() - 1;
		for (int i = 0; i < splitPanels; i++) {

			// Create the split panel.
			JSplitPane splitPane = getSplitPane();
			height -= panelHeight;
			int location = (int) (height);
			splitPane.setDividerLocation(location);

			// Set the bottom the chart pointed by index.
			splitPane.setBottomComponent(chartContainers.get(index));

			// Cecrease the index because we add charts in inverse order.
			index--;

			// If there are no split panels added, then simply add this one, otherwise this one will be the top
			// component of the last one added.
			if (getAllSplitPanes().isEmpty()) {
				add(splitPane, constraintsTopPanel);
			} else {
				getAllSplitPanes().get(getAllSplitPanes().size() - 1).setTopComponent(splitPane);
			}

		}

		// If there is only one chart, there are no split panels and we add this chart directly, otherwise we set it as
		// the top component of the last split panel added.
		if (chartContainers.size() == 1) {
			add(chartContainers.get(index), constraintsTopPanel);
		} else {
			getAllSplitPanes().get(getAllSplitPanes().size() - 1).setTopComponent(chartContainers.get(index));
		}

		// Add the horizontal axis panel.
		add(getChartHorizontalAxis(), constraintsBottomPanel);

		// Set the sizes of all vertical axis to the maximum.
		int maxWidth = 0;
		for (JChartContainer chartContainer : chartContainers) {
			maxWidth = Math.max(maxWidth, chartContainer.getChartVerticalAxis().getMinimumSize().width);
		}
		Dimension verticalAxisSize = new Dimension(maxWidth, 0);
		for (JChartContainer chartContainer : chartContainers) {
			chartContainer.getChartVerticalAxis().setMinimumSize(verticalAxisSize);
			chartContainer.getChartVerticalAxis().setMaximumSize(verticalAxisSize);
			chartContainer.getChartVerticalAxis().setPreferredSize(verticalAxisSize);
		}

		// Validate and repaint.
		revalidate();
		repaint();
	}

	/**
	 * Propagates the frame changes in the argument plot data to the rest of plot datas.
	 * 
	 * @param plotData The source plot data.
	 */
	public void propagateFrameChanges(PlotData plotData) {
		for (JChartContainer chartContainer : chartContainers) {
			if (chartContainer.getPlotData().equals(plotData)) {
				continue;
			}
			chartContainer.getPlotData().setStartIndex(plotData.getStartIndex());
			chartContainer.getPlotData().setEndIndex(plotData.getEndIndex());
			chartContainer.getPlotData().calculateFrame();
		}
		repaint();
	}

}
