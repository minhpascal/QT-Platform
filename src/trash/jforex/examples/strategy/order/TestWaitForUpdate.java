package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy on its start creates a market order and on order updates
 * prints the relevant order information
 *
 */
public class TestWaitForUpdate implements IStrategy {
	private IEngine engine;
	private IConsole console;	
	
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
		IOrder order = engine.submitOrder("orderValid", Instrument.EURUSD, OrderCommand.BUY, 0.001);
		print(String.format("After submit: state=%s, open price=%.5f, creation time=%s",
				order.getState(), order.getOpenPrice(), DateUtils.format(order.getCreationTime())));
		
		order.waitForUpdate(2000); //wait max 2 sec for OPENED
		print(String.format("After update: state=%s, open price=%.5f, creation time=%s",
				order.getState(), order.getOpenPrice(), DateUtils.format(order.getCreationTime())));
		
		order.waitForUpdate(2000); //wait max 2 sec for FILLED
		print(String.format("After update: state=%s, open price=%.5f, fill time=%s",
				order.getState(),order.getOpenPrice(), DateUtils.format(order.getFillTime())));
		 
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