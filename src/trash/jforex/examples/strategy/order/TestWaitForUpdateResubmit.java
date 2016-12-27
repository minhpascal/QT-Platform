package trash.jforex.examples.strategy.order;


import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start creates an invalid market order and
 * on its rejection submits a valid version.
 *
 */
@RequiresFullAccess
public class TestWaitForUpdateResubmit implements IStrategy {
	private IEngine engine;
	private IConsole console;
	
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
		IOrder order = engine.submitOrder("order", Instrument.EURUSD, OrderCommand.BUY, -0.001);
		
		IMessage message = order.waitForUpdate(2000, IOrder.State.OPENED);//wait max 2 sec for OPENED
		//resubmit order on rejection
		if(message.getType() == IMessage.Type.ORDER_SUBMIT_REJECTED){
			order = engine.submitOrder("order", Instrument.EURUSD, OrderCommand.BUY, 0.001);
			message = order.waitForUpdate(2000, IOrder.State.OPENED);//wait max 2 sec for OPENED
		}

		print(String.format("After update: state=%s, message=%s", order.getState(), message));		
		 
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