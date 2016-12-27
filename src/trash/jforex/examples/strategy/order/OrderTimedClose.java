package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start creates an order and closes it
 * after a designated time interval.
 *
 */
public class OrderTimedClose implements IStrategy {
	
    private IConsole console;
    private IEngine engine;    
    IOrder order;
    
    @Configurable(value = "Close order after (secs)", description = "Time interval after which the order gets closed.")
    public int orderCloseSecs = 10;
    
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
        order = engine.submitOrder("order1", Instrument.EURUSD, OrderCommand.BUY, 0.01);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(order.getState() == IOrder.State.FILLED 
                && order.getFillTime() + orderCloseSecs * 1000 < tick.getTime()){
            order.close();
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        console.getOut().println(message);
    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {
        if(order.getState() == IOrder.State.FILLED){
            order.close();
        }

    }

}
