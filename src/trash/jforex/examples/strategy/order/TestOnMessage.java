package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start submits two market orders - the second one 
 * on purpose has an invalid amount.
 * In onMessage method the strategy prints messages depending on
 * the received message type
 *
 */
public class TestOnMessage implements IStrategy {
	private IEngine engine;
	private IConsole console;
	
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        
		engine.submitOrder("orderValid", Instrument.EURUSD, OrderCommand.BUY, 0.001);
		engine.submitOrder("orderInvalid", Instrument.EURUSD, OrderCommand.BUY, -0.001);
	}

	public void onMessage(IMessage message) throws JFException {
		
		switch(message.getType()){
			case ORDER_SUBMIT_OK : 
				print("Order opened: " + message.getOrder());
				break;
			case ORDER_SUBMIT_REJECTED : 
				print("Order open failed: " + message.getOrder());
				break;
			case ORDER_FILL_OK : 
				print("Order filled: " + message.getOrder());
				break;
			case ORDER_FILL_REJECTED : 
				print("Order cancelled: " + message.getOrder());
				break;
			default:
				break;
		}
		print("<html><font color=\"gray\">"+message+"</font>");
	}
	
	public void print(String message) {
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