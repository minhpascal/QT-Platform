package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy on its start submits a market order with SL and
 * as soon as the order gets filled, the SL gets updated
 *
 */
@RequiresFullAccess
public class TestWaitForUpdateSL implements IStrategy {
	private IEngine engine;
	private IConsole console;
		
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
		double price = context.getHistory().getLastTick(Instrument.EURUSD).getBid();
		
		//submit order with SL and TP at 5 pip distance and slippage 20
		IOrder order = engine.submitOrder("orderValid", Instrument.EURUSD, OrderCommand.BUY, 0.001, 0, 20, 
				price - 0.0005, price + 0.0005);
		order.waitForUpdate(2000); //wait max 2 sec for OPENED
		order.waitForUpdate(2000); //wait max 2 sec for FILLED
		print(String.format("After fill: state=%s, open price=%.5f, fill time=%s, stop loss=%.5f",
				order.getState(),order.getOpenPrice(), DateUtils.format(order.getFillTime()), order.getStopLossPrice()));
		
		//adjust stop loss to be in 5 pip distance from open price
		order.setStopLossPrice(order.getOpenPrice() - 0.0005);
		order.waitForUpdate(2000); //wait max 2 sec for SL price to get updated
		print(String.format("After SL update: state=%s, open price=%.5f, fill time=%s, stop loss=%.5f",
				order.getState(),order.getOpenPrice(), DateUtils.format(order.getFillTime()), order.getStopLossPrice()));
		 
	}

	public void onMessage(IMessage message) throws JFException {  
		print("<html><font color=\"gray\">"+message+"</font>");
	}
	
	public void print(Object message) {
		console.getOut().println(message);
	}

	//close all orders on strategy stop
	public void onStop() throws JFException {
		for (IOrder order : engine.getOrders()) {
			order.close();
		}
	}	

	public void onAccount(IAccount account) throws JFException {
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}


}