/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import com.dukascopy.api.ICloseOrder;
import com.dukascopy.api.IFillOrder;
import com.qtplaf.library.trading.server.OrderHistory;

/**
 * Dukascopy implementation of the <i>OrderHistory</i> interface.
 * 
 * @author Miquel Sas
 */
public class DkOrderHistory implements OrderHistory {
	
	/**
	 * IClose order reference.
	 */
	private ICloseOrder closeOrder;
	/**
	 * IFillOrder reference.
	 */
	private IFillOrder fillOrder;
	
	/**
	 * Constructor assigning the ICloseOrder.
	 * 
	 * @param closeOrder The ICloseOrder.
	 */
	public DkOrderHistory(ICloseOrder closeOrder) {
		super();
		this.closeOrder = closeOrder;
	}

	/**
	 * Constructor assigning the IFillOrder.
	 * 
	 * @param fillOrder The IFillOrder.
	 */
	public DkOrderHistory(IFillOrder fillOrder) {
		super();
		this.fillOrder = fillOrder;
	}

	/**
	 * Returns the amount of the closed part.
	 * 
	 * @return The amount of the closed part.
	 */
	public double getAmount() {
		return (closeOrder != null ? closeOrder.getAmount() : fillOrder.getAmount());
	}

	/**
	 * Returns the close price.
	 * 
	 * @return The close price.
	 */
	public double getPrice() {
		return (closeOrder != null ? closeOrder.getPrice() : fillOrder.getPrice());
	}

	/**
	 * Returns the time when the order was closed.
	 * 
	 * @return The time when the order was closed.
	 */
	public long getTime() {
		return (closeOrder != null ? closeOrder.getTime() : fillOrder.getTime());
	}
}
