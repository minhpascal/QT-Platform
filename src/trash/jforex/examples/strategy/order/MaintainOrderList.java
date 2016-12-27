package trash.jforex.examples.strategy.order;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy maintains its own-created orders in a specific list
 * and removes orders from it on their close or cancel.
 * 
 * The strategy also prints all order change messages and shows
 * how to determine if the changed order was from the strategy or it was another order
 *
 */
public class MaintainOrderList implements IStrategy {    
    
    private IEngine engine;
    private IConsole console;
    private List<IOrder> myOrders = new ArrayList<IOrder>();
    
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        IOrder order = engine.submitOrder("myOrder", Instrument.EURUSD, OrderCommand.BUY, 0.001);
        myOrders.add(order);
    }
    
    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder order = message.getOrder();
        if(order == null){
            return;
        }
        if(!myOrders.contains(order)){
            //casual log messages for non-this-strategy order changes
            console.getOut().println(message);
            List<IOrder> otherOrders = new ArrayList<IOrder>(engine.getOrders());
            otherOrders.removeAll(myOrders);
            console.getOut().println("all other orders: " + otherOrders);
        } else {
            //warning log messages for this-strategy order changes
            console.getWarn().println(message);
            console.getWarn().println("all this strategy orders: " + myOrders);
            if(order.getState() == IOrder.State.CLOSED || order.getState() == IOrder.State.CANCELED){
                myOrders.remove(order);
            }
        }     
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
