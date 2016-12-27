/**
 * 
 */
package com.qtplaf.library.trading.chart.plotter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.chart.PlotParameters;
import com.qtplaf.library.trading.chart.plotter.drawings.CrossCursor;
import com.qtplaf.library.trading.data.PlotData;

/**
 * A cursor plotter.
 * 
 * @author Miquel Sas
 */
public class CrossCursorPlotter extends Plotter {

	/**
	 * Constructor assinging the necessary values.
	 * 
	 * @param session The working session.
	 * @param plotData The plot data.
	 * @param chartSize The chart plotter size.
	 * @param plotParameters The plot parameters
	 */
	public CrossCursorPlotter(Session session, PlotData plotData, Dimension chartSize, PlotParameters plotParameters) {
		super(session, plotData, chartSize, plotParameters);
	}

	/**
	 * Plot a cross cursor.
	 * 
	 * @param g2 The graphics context.
	 * @param crossCursor The cross cursor.
	 */
	public void plot(Graphics2D g2, CrossCursor crossCursor) {

		// The shape.
		Shape shape = crossCursor.getShape(this);

		// Check intersection with clip bounds.
		if (!getIntersectionBounds(shape.getBounds()).intersects(g2.getClipBounds())) {
			return;
		}

		// Save color and stroke.
		Color saveColor = g2.getColor();
		Stroke saveStroke = g2.getStroke();

		// Set color and stroke.
		g2.setColor(crossCursor.getColor());
		g2.setStroke(crossCursor.getStroke());

		// Draw it.
		g2.draw(shape);

		// Restore color and stroke.
		g2.setColor(saveColor);
		g2.setStroke(saveStroke);
	}
}
