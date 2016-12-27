/**
 * 
 */
package com.qtplaf.library.trading.data.info;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.trading.data.DataType;

/**
 * Input source info for indicators and studies. Input sources can be of types <i>Price</i>, <i>Volume</i> or
 * <i>Indicator</i>.
 * 
 * @author Miquel Sas
 */
public class InputInfo {

	/**
	 * Small structure to define the accepted input sources.
	 */
	public class InputType {
		/**
		 * The data type, either <i>Price</i>, <i>Volume</i> or <i>Indicator</i>.
		 */
		private DataType dataType;
		/**
		 * The number of values to retrieve from the data type, if less equal than zero, the indicator decides the
		 * values to retrieve, if not the system will let the user select the value from the possible values of the
		 * type.
		 */
		private int values = 1;

		/**
		 * Constructor assigning type and number of values.
		 * 
		 * @param dataType The data type.
		 * @param values The number of values.
		 */
		InputType(DataType dataType, int values) {
			super();
			this.dataType = dataType;
			this.values = values;
		}

		/**
		 * Returns the data type.
		 * 
		 * @return The data type.
		 */
		public DataType getDataType() {
			return dataType;
		}

		/**
		 * Returns the number of values to provideif less equal than zero, the indicator decides the values to retrieve,
		 * if not the system will let the user select the value from the possible values of the type.
		 * 
		 * @return The number of values to provide.
		 */
		public int getValues() {
			return values;
		}

		/**
		 * Returns a boolean indicating if the argument object is equal to this input source.
		 * 
		 * @return A boolean indicating if the argument object is equal to this input source.
		 */
		public boolean equals(Object o) {
			if (o instanceof InputType) {
				InputType input = (InputType) o;
				return getDataType().equals(input.getDataType()) && getValues() == input.getValues();
			}
			return false;
		}
	}

	/**
	 * The list of possible input source types with the number of values required fro each type.
	 */
	private List<InputType> possibleInputSources = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public InputInfo() {
		super();
	}

	/**
	 * Adds a possible input source for a given data type and number of values.
	 * 
	 * @param dataType The data type.
	 * @param values The number of values.
	 */
	public void addPossibleInputSource(DataType dataType, int values) {
		InputType inputType = new InputType(dataType, values);
		if (possibleInputSources.contains(inputType)) {
			possibleInputSources.add(inputType);
		}
	}

	/**
	 * Returns the number of possible input sources.
	 * 
	 * @return The number of possible input sources.
	 */
	public int getPossibleInputSourcesCount() {
		return possibleInputSources.size();
	}

	/**
	 * Returns the possible input source at the given index.
	 * 
	 * @param index The index.
	 * @return The input source.
	 */
	public InputType getPossibleInputSource(int index) {
		return possibleInputSources.get(index);
	}
}
