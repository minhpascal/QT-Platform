package trash.jforex.examples.strategy.order;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start submits a market order with SL
 * as soon as the order gets filled, the SL gets updated.
 * 
 * The strategy enqueues orders that have to be updated and
 * processes them on next onTick
 *
 */
@RequiresFullAccess
public class TestOnMessageSLQueue implements IStrategy {
	private IEngine engine;
	private IConsole console; 
	private IOrder order;
	
	private Queue<IOrder> justFilledOrders = new ConcurrentLinkedQueue<IOrder>();
	
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
		double price = context.getHistory().getLastTick(Instrument.EURUSD).getBid();
		
		//submit order with SL and TP at 5 pip distance and slippage 20
		order = engine.submitOrder("orderValid", Instrument.EURUSD, OrderCommand.BUY, 0.001, 0, 20, 
				price - 0.0005, price + 0.0005);
	}

	public void onMessage(IMessage message) throws JFException {

		//in JForex-API 2.6.38 one can't change orders in onMessage, so we enqueue order change request and process it in next onTick
		if(message.getOrder() == order && message.getType() == IMessage.Type.ORDER_FILL_OK){
			justFilledOrders.add(message.getOrder());
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

	public void onAccount(IAccount account) throws JFException {	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {				
    	//process just filled orders
    	while(!justFilledOrders.isEmpty()){
    		IOrder filledOrder = justFilledOrders.poll();
    		if(filledOrder == order){
    			//update SL to be at 5 pips from open price
    			order.setStopLossPrice(order.getOpenPrice() - 0.0005);  
    		}
    	}
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {	}


}