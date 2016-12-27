/**
 * 
 */
package com.qtplaf.library.trading.data;

/**
 * Listens to data lists changes add, remove, set and clear.
 * 
 * @author Miquel Sas
 */
public interface DataListListener {
	/**
	 * Call listeners to notify changes in the data list.
	 * 
	 * @param e The data list event.
	 */
	void dataListChanged(DataListEvent e);
}
