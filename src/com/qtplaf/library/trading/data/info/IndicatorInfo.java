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
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.trading.data.DataType;
import com.qtplaf.library.trading.data.Indicator;

/**
 * Pack the information that describes an indicator.
 * 
 * @author Miquel Sas
 */
public class IndicatorInfo extends DataInfo {

	/**
	 * The indicator that this info is attached at.
	 */
	private Indicator indicator;
	/**
	 * A boolean that indicates if this indicator has to be plotted in its own chart container, mainly because the
	 * values range is different than the values ranges of the data sources.
	 */
	private boolean ownChartContainer;
	/**
	 * The list of input sources.
	 */
	private List<InputInfo> inputs = new ArrayList<>();
	/**
	 * The list of parameters.
	 */
	private List<ParameterInfo> parameters = new ArrayList<>();
	/**
	 * The necessary number of backward values to calculate the indicator, depends on the indicator implementation. If
	 * greater than zero, the system will call the calculate method after backward values are available.
	 */
	private int lookBackward = 0;
	/**
	 * The necessary number of forward values to calculate the indicator.
	 */
	private int lookForward = 0;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public IndicatorInfo(Session session, Indicator indicator) {
		super(session);
		this.indicator = indicator;
		setDataType(DataType.Indicator);
	}

	/**
	 * Returns the indicator this info is attached at.
	 * 
	 * @return The indicator.
	 */
	public Indicator getIndicator() {
		return indicator;
	}

	/**
	 * Adds an input source to the list of inputs.
	 * 
	 * @param input The input source.
	 */
	public void addInput(InputInfo input) {
		inputs.add(input);
	}

	/**
	 * Returns the number of input sources.
	 * 
	 * @return The number of input sources.
	 */
	public int getInputCount() {
		return inputs.size();
	}

	/**
	 * Returns the input source info at the given index.
	 * 
	 * @param index The index.
	 * @return The input source info.
	 */
	public InputInfo getInput(int index) {
		return inputs.get(index);
	}

	/**
	 * Adds a parameter to the list of parameters.
	 * 
	 * @param parameter The parameter.
	 */
	public void addParameter(ParameterInfo parameter) {
		parameters.add(parameter);
	}

	/**
	 * Returns the number of parameters.
	 * 
	 * @return The number of parameters.
	 */
	public int getParameterCount() {
		return parameters.size();
	}

	/**
	 * Returns the parameter with the given index.
	 * 
	 * @param index The parameter index.
	 * @return The parameter.
	 */
	public ParameterInfo getParameter(int index) {
		return parameters.get(index);
	}

	/**
	 * Returns the parameter with the given field name or alias, no case sensitive.
	 * 
	 * @param alias The parameter field alias.
	 * @return The parameter or null if not found.
	 */
	public ParameterInfo getParameter(String alias) {
		for (ParameterInfo parameter : parameters) {
			if (parameter.getField().getAlias().toUpperCase().equals(alias.toUpperCase())) {
				return parameter;
			}
		}
		return null;
	}

	/**
	 * Returns a boolean that indicates if this indicator has to be plotted in its on chart container.
	 * 
	 * @return A boolean that indicates if this indicator has to be plotted in its on chart container.
	 */
	public boolean isOwnChartContainer() {
		return ownChartContainer;
	}

	/**
	 * Sets a boolean that indicates if this indicator has to be plotted in its on chart container.
	 * 
	 * @param ownChartContainer A boolean that indicates if this indicator has to be plotted in its on chart container.
	 */
	public void setOwnChartContainer(boolean ownChartContainer) {
		this.ownChartContainer = ownChartContainer;
	}

	/**
	 * Returns the necessary number of backward values to calculate the indicator.
	 * 
	 * @return The necessary number of backward values to calculate the indicator.
	 */
	public int getLookBackward() {
		return lookBackward;
	}

	/**
	 * Sets the necessary number of backward values to calculate the indicator.
	 * 
	 * @param lookBackward The necessary number of backward values to calculate the indicator.
	 */
	public void setLookBackward(int lookBackward) {
		this.lookBackward = lookBackward;
	}

	/**
	 * Returns the necessary number of forward values to calculate the indicator.
	 * 
	 * @return The necessary number of forward values to calculate the indicator.
	 */
	public int getLookForward() {
		return lookForward;
	}

	/**
	 * Sets the necessary number of forward values to calculate the indicator.
	 * 
	 * @param lookForward The necessary number of forward values to calculate the indicator.
	 */
	public void setLookForward(int lookForward) {
		this.lookForward = lookForward;
	}

}
