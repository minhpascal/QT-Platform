/**
 * 
 */
package com.qtplaf.library.trading.server.servers.dukascopy;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.IEngine;
import com.dukascopy.api.IOrder;
import com.qtplaf.library.trading.data.Instrument;
import com.qtplaf.library.trading.server.Order;
import com.qtplaf.library.trading.server.OrderCommand;
import com.qtplaf.library.trading.server.OrderManager;
import com.qtplaf.library.trading.server.ServerException;

/**
 * Dukascopy order manager implementation.
 * 
 * @author Miquel Sas
 */
public class DkOrderManager implements OrderManager {

	/**
	 * Server reference.
	 */
	private DkServer server;

	/**
	 * Constructor assigning the reference server.
	 * 
	 * @param server The Dukascopy server.
	 */
	public DkOrderManager(DkServer server) {
		super();
		this.server = server;
	}

	/**
	 * Returns an order identified by the id.
	 * 
	 * @param id The identifier.
	 * @return The order if not an error occurs.
	 * @throws ServerException
	 */
	public Order getOrderById(String id) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder order = engine.getOrderById(id);
			return new DkOrder(order);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Returns an order identified by the label.
	 * 
	 * @param label The label.
	 * @return The order if not an error occurs.
	 * @throws ServerException
	 */
	public Order getOrderByLabel(String label) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder order = engine.getOrder(label);
			return new DkOrder(order);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Returns a list with all currently active orders.
	 * 
	 * @return The list of orders.
	 * @throws ServerException
	 */
	public List<Order> getOrders() throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			List<IOrder> dkOrders = engine.getOrders();
			List<Order> orders = new ArrayList<>();
			for (IOrder dkOrder : dkOrders) {
				orders.add(new DkOrder(dkOrder));
			}
			return orders;
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Returns a list with all currently active orders for the given instrument.
	 * 
	 * @param instrument The instrument to check.
	 * @return The list of orders.
	 * @throws ServerException
	 */
	public List<Order> getOrders(Instrument instrument) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			List<IOrder> dkOrders = engine.getOrders(DkUtilities.toDkInstrument(instrument));
			List<Order> orders = new ArrayList<>();
			for (IOrder dkOrder : dkOrders) {
				orders.add(new DkOrder(dkOrder));
			}
			return orders;
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order at market price.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price, slippage);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price, slippage, stopLossPrice, 0);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price, slippage, stopLossPrice, takeProfitPrice);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order with full parameters, except the comment.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @param expirationTime A long indicating how long the order should be alive if not executed. For market orders must
	 *        be zero.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice,
		long expirationTime) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price, slippage, stopLossPrice, takeProfitPrice, expirationTime);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

	/**
	 * Submits a request for an order with full parameters.
	 * 
	 * @param label The order label. It is recommended to set an unique identifier as a label, since it is a way to
	 *        retrieve the order, for instance once lost the identifier that the server sets.
	 * @param instrument The instrument.
	 * @param orderCommand The order command.
	 * @param amount the amount.
	 * @param price The required price.
	 * @param slippage The accepted slippage.
	 * @param stopLossPrice The stop loss price.
	 * @param takeProfitPrice The take profit price.
	 * @param expirationTime A long indicating how long the order should be alive if not executed. For market orders must
	 *        be zero.
	 * @param comment An optional comment.
	 * @return The order if succefully created.
	 * @throws ServerException
	 */
	public Order submitOrder(
		String label,
		Instrument instrument,
		OrderCommand orderCommand,
		double amount,
		double price,
		double slippage,
		double stopLossPrice,
		double takeProfitPrice,
		long expirationTime,
		String comment) throws ServerException {
		try {
			IEngine engine = server.getContext().getEngine();
			IOrder dkOrder = engine.submitOrder(
				label, 
				DkUtilities.toDkInstrument(instrument), 
				DkUtilities.toDkOrderCommand(orderCommand), 
				amount, price, slippage, stopLossPrice, takeProfitPrice, expirationTime, comment);
			return new DkOrder(dkOrder);
		} catch (Exception cause) {
			throw new ServerException(cause);
		}
	}

}
