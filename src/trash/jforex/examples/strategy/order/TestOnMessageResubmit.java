package trash.jforex.examples.strategy.order;

import java.util.Map;
import java.util.HashMap;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start submits a market order with an invalid amount
 * such that the order gets rejected. In such case the order gets resubmitted
 * with an increased amount until either the order gets successfully submitted
 * or the maxOrderResubmitCount gets exceeded.
 *
 */
public class TestOnMessageResubmit implements IStrategy {
    private IEngine engine;
    private IConsole console; 
    
    @Configurable("")
    public int maxOrderResubmitCount = 5;
    
    private Map<IOrder,Integer> resubmitAttempts = new HashMap<IOrder,Integer>();
    
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        
        IOrder order = engine.submitOrder("order", Instrument.EURUSD, OrderCommand.BUY, - 0.002);
        resubmitAttempts.put(order, 0);
    }

    public void onMessage(IMessage message) throws JFException {

        IOrder order = message.getOrder();
        
        if(message.getType() == IMessage.Type.ORDER_SUBMIT_REJECTED){
            console.getOut().println(message);
            Integer attempts = resubmitAttempts.get(order);
            if(attempts == null){
                console.getWarn().println("Rejected order was not created by this strategy.");
            } else if (attempts > maxOrderResubmitCount){
                console.getWarn().println("Rejected order has exceeeded resubmit attempt count.");
            } else {
                resubmitAttempts.remove(order);
                IOrder newOrder = engine.submitOrder(order.getLabel(), order.getInstrument(), order.getOrderCommand(), order.getAmount() + 0.001);
                resubmitAttempts.put(newOrder, ++attempts);
                console.getOut().println("Resubmitted order: " + newOrder + " attempts left: " +(maxOrderResubmitCount - attempts + 1));
            }
        }        
    }

    //close all orders on strategy stop
    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
    }    

    public void onAccount(IAccount account) throws JFException {    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }


}