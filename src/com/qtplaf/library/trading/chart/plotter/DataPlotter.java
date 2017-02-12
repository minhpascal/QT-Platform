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
package com.qtplaf.library.trading.chart.plotter;

import java.awt.Graphics2D;

import com.qtplaf.library.trading.data.Data;
import com.qtplaf.library.trading.data.DataList;

/**
 * Base class for data plotters of timed data.
 * 
 * @author Miquel Sas
 */
public abstract class DataPlotter extends Plotter {

	/**
	 * Indexes within the data element used by the plotter.
	 */
	private int[] indexes;

	/**
	 * Constructor.
	 */
	public DataPlotter() {
		super();
	}

	/**
	 * Returns the indexes within the data element used by the plotter.
	 * 
	 * @return The indexes within the data element used by the plotter.
	 */
	public int[] getIndexes() {
		return indexes;
	}

	/**
	 * Returns the indexes to apply to the data item. By default, all data values.
	 * 
	 * @param data The data item.
	 * @return The indexes.
	 */
	public int[] getIndexes(Data data) {
		if (indexes == null) {
			indexes = new int[data.size()];
			for (int i = 0; i < data.size(); i++) {
				indexes[i] = i;
			}
		}
		return indexes;
	}

	/**
	 * Returns the list of values given the data element.
	 * 
	 * @param data The data item.
	 * @return The list of values.
	 */
	public double[] getValues(Data data) {
		int[] indexes = getIndexes(data);
		double[] values = new double[indexes.length];
		for (int i = 0; i < indexes.length; i++) {
			values[i] = data.getValue(indexes[i]);
		}
		return values;
	}

	/**
	 * Set the indexes within the data element used by the plotter.
	 * 
	 * @param indexes The indexes within the data element used by the plotter.
	 */
	public void setIndexes(int[] indexes) {
		this.indexes = indexes;
	}

	/**
	 * Plot a data list item. If the number of data items per plot unit is not -1, the union is plotted.
	 * 
	 * @param g2 The graphics object.
	 * @param dataList The data list to plot.
	 * @param index The index to plot.
	 */
	public abstract void plotDataIndex(Graphics2D g2, DataList dataList, int index);

	/**
	 * Termination method to end the plot and clear or close resources. Overwrite if necessary.
	 * 
	 * @param g2 The graphics context.
	 */
	public void endPlot(Graphics2D g2) {
	}

}
