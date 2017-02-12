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

package com.qtplaf.library.statistics;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.database.RecordSet;
import com.qtplaf.library.database.Table;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.trading.data.PlotData;

/**
 * Statistics descriptor.
 *
 * @author Miquel Sas
 */
public abstract class Statistics {

	/**
	 * An identifier.
	 */
	private String id;
	/**
	 * A title or short description.
	 */
	private String title;
	/**
	 * A description.
	 */
	private String description;
	/**
	 * List of outputs.
	 */
	private List<Output> outputs = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public Statistics() {
		super();
	}

	/**
	 * Add an output value definition.
	 * 
	 * @param output The output definition.
	 */
	public void add(Output output) {
		outputs.add(output);
	}

	/**
	 * Clear output values.
	 */
	public void clear() {
		outputs.clear();
	}

	/**
	 * Returns the output at the given index.
	 * 
	 * @param index The output index.
	 * @return The output.
	 */
	public Output get(int index) {
		return outputs.get(index);
	}

	/**
	 * Returns the number of output values.
	 * 
	 * @return The size or number of output values.
	 */
	public int size() {
		return outputs.size();
	}

	/**
	 * Returns the id.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id.
	 * 
	 * @param id The id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Return the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Return the title.
	 * 
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the task that calculates the statistic.
	 * 
	 * @return The calculator task.
	 */
	public abstract Task getTask();

	/**
	 * Returns the definition of the table where output results are stored or at least displayed in tabular form. It is
	 * expected to have at least fields to hold the output values.
	 * 
	 * @return The results table.
	 */
	public abstract Table getTable();

	/**
	 * Returns the recordset to browse the statistic results.
	 * 
	 * @return The recordset to browse the statistic results.
	 */
	public abstract RecordSet getRecordSet();

	/**
	 * Returns the list of plot datas to configure a chart and show the statistics results.
	 * 
	 * @return The list of plot datas.
	 */
	public abstract List<PlotData> getPlotDataList();
}
