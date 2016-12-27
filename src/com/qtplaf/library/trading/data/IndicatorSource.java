/**
 * 
 */
package com.qtplaf.library.trading.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicator data source. Indicators can be calculated over a list of data lists, using a set of indexes for each data
 * list. The indicator source packs data list and the set of indexes to apply.
 * 
 * @author Miquel Sas
 */
public class IndicatorSource {

	/**
	 * The data list.
	 */
	private DataList dataList;
	/**
	 * The list of indexes.
	 */
	private List<Integer> indexes;

	/**
	 * Constructor assigning fields.
	 * 
	 * @param dataList The data list.
	 * @param indexes The indexes.
	 */
	public IndicatorSource(DataList dataList, List<Integer> indexes) {
		super();
		this.dataList = dataList;
		this.indexes = indexes;
	}

	/**
	 * Constructor assigning fields.
	 * 
	 * @param dataList The data list.
	 * @param indexes The indexes.
	 */
	public IndicatorSource(DataList dataList, int... indexes) {
		super();
		this.dataList = dataList;
		setIndexes(indexes);
	}

	/**
	 * Returns the data list.
	 * @return the dataList
	 */
	public DataList getDataList() {
		return dataList;
	}

	/**
	 * Sets the data list.
	 * @param dataList The data list.
	 */
	public void setDataList(DataList dataList) {
		this.dataList = dataList;
	}

	/**
	 * Returns the list of indexes.
	 * @return The list of indexes.
	 */
	public List<Integer> getIndexes() {
		return indexes;
	}

	/**
	 * Sets the list of indexes.
	 * @param indexes The list of indexes.
	 */
	public void setIndexes(List<Integer> indexes) {
		this.indexes = indexes;
	}

	/**
	 * Sets the list of indexes.
	 * @param indexes The list of indexes.
	 */
	public void setIndexes(int... indexes) {
		this.indexes = new ArrayList<>();
		for (int index : indexes) {
			this.indexes.add(index);
		}
	}

}
