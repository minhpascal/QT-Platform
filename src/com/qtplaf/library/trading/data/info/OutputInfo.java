package com.qtplaf.library.trading.data.info;

/**
 * Information about data output values.
 * 
 * @author Miquel Sas
 */
public class OutputInfo {

	/**
	 * The output name, for instance <b>Close</b> for the close value of an <b>OHLCV</b> instance.
	 */
	private String name;
	/**
	 * A short name to build a short information string, like for instance <b>C</b> for the close value of an
	 * <b>OHLCV</b> instance.
	 */
	private String shortName;
	/**
	 * An optional description.
	 */
	private String description;
	/**
	 * The index of this output in the data object.
	 */
	private int index;
	/**
	 * A boolean that indicates if this output has to be plotted, applies only to indicators.
	 */
	private boolean plot = true;

	/**
	 * Default constructor.
	 */
	public OutputInfo() {
		super();
	}

	/**
	 * Constructor assigning the name and the short name.
	 * 
	 * @param name The name.
	 * @param shortName The short name.
	 * @param index The data index.
	 */
	public OutputInfo(String name, String shortName, int index) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.index = index;
	}

	/**
	 * Constructor assigning the name and the short name.
	 * 
	 * @param name The name.
	 * @param shortName The short name.
	 * @param index The data index.
	 * @param plot A boolean indicating if this output has to be plotted.
	 */
	public OutputInfo(String name, String shortName, int index, boolean plot) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.index = index;
		this.plot = plot;
	}

	/**
	 * Constructor assigning the name, short name and description..
	 * 
	 * @param name The name.
	 * @param shortName The short name.
	 * @param index The data index.
	 * @param description The description.
	 */
	public OutputInfo(String name, String shortName, int index, String description) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.index = index;
		this.description = description;
	}

	/**
	 * Constructor assigning the name, short name and description..
	 * 
	 * @param name The name.
	 * @param shortName The short name.
	 * @param index The data index.
	 * @param description The description.
	 * @param plot A boolean indicating if this output has to be plotted.
	 */
	public OutputInfo(String name, String shortName, int index, String description, boolean plot) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.index = index;
		this.description = description;
		this.plot = plot;
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the short name.
	 * 
	 * @return The short name.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets the short name.
	 * 
	 * @param shortName The short name.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Returns the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * returns the data index.
	 * 
	 * @return The data index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the data index.
	 * 
	 * @param index The data index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Returns a boolean indicating if this output has to be plotted.
	 * 
	 * @return A boolean indicating if this output has to be plotted.
	 */
	public boolean isPlot() {
		return plot;
	}

	/**
	 * Sets a boolean indicating if this output has to be plotted.
	 * 
	 * @param plot A boolean indicating if this output has to be plotted.
	 */
	public void setPlot(boolean plot) {
		this.plot = plot;
	}

}
