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

package com.qtplaf.library.trading.chart.plotter.drawing;

import java.awt.Graphics2D;

import com.qtplaf.library.trading.chart.drawings.Line;

/**
 * A generic line plotter.
 *
 * @author Miquel Sas
 */
public class LinePlotter extends DrawingPlotter {

	/** The line. */
	private Line line;

	/**
	 * Constructor.
	 */
	public LinePlotter() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param line The line to plot.
	 */
	public LinePlotter(Line line) {
		super();
		this.line = line;
	}

	/**
	 * Returns the line to plot.
	 * 
	 * @return The line to plot.
	 */
	public Line getLine() {
		return line;
	}

	/**
	 * Set the line to plot.
	 * 
	 * @param line The line to plot.
	 */
	public void setLine(Line line) {
		this.line = line;
	}

	/**
	 * Plot the underlying drawing.
	 * 
	 * @param g2 The graphics object.
	 */
	@Override
	public void plot(Graphics2D g2) {
		
		// Check line set.
		if (getLine() == null) {
			throw new IllegalStateException();
		}
		
		// Check that line is in the drawing area.
		int startIndex = getContext().getPlotData().getStartIndex();
		int endIndex = getContext().getPlotData().getEndIndex();
		
		// If both indexes are out of range in the same side, do not plot.
		if (getLine().getIndex1() < startIndex && getLine().getIndex2() < startIndex) {
			return;
		}
		if (getLine().getIndex1() < startIndex && getLine().getIndex2() < startIndex) {
			return;
		}

	}
}
