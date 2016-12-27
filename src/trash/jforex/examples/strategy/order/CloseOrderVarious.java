package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import static com.dukascopy.api.IOrder.State.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy creates an order and then partially closes it with various methods
 * and afterwards closes it in full.
 * 
 * The strategy also demonstrates how to manage the order state on closing or canceling the order.
 * 
 */
public class CloseOrderVarious implements IStrategy {

    private IConsole console;
    private IEngine engine;
    private IHistory history;
    private Instrument instrument = Instrument.EURUSD;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        engine = context.getEngine();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);

        IOrder order = engine.submitOrder("order", instrument, OrderCommand.BUY, 0.01);
        order.waitForUpdate(2000, FILLED);

        if (order.getState() != FILLED) {
            console.getOut().println("Can't close order - order not filled: " + order.getState());
            context.stop();
        }

        // unconditional partial close
        order.close(0.003);
        order.waitForUpdate(2000);

        // conditional partial close by price
        double lastBid = history.getLastTick(instrument).getBid();
        order.close(0.003, lastBid - 100 * instrument.getPipValue());
        order.waitForUpdate(2000);

        // conditional partial close by price and slippage
        order.close(0.003, lastBid - 100 * instrument.getPipValue(), 5);
        order.waitForUpdate(2000);

        // unconditional full order close
        order.close();
        order.waitForUpdate(2000, CLOSED);

        if (order.getState() != CLOSED) {
            console.getErr().println("Can't create next order with the same label - order not closed: " + order.getState());
            context.stop();
        }

        // place conditional order such that the price condition is not instantly met
        double price = history.getLastTick(instrument).getAsk() + 5 * instrument.getPipValue();
        order = engine.submitOrder("order", instrument, OrderCommand.BUYSTOP, 0.01, price);
        order.waitForUpdate(2000, OPENED);

        if (order.getState() != OPENED) {
            console.getErr().println("Can't cancel order - order : " + order.getState());
            context.stop();
        }
        order.close();
        order.waitForUpdate(2000, CANCELED);  
        
        //create both filled and opened orders
        price = history.getLastTick(instrument).getAsk();        
        for(int i = -5; i< 6; i++){
            String label = String.format("order_%sAsk%s", Math.abs(i),i < 0 ? "below" : "above");
            order = engine.submitOrder(label, instrument, OrderCommand.BUYSTOP, 0.01, price + i*instrument.getPipValue());
            order.waitForUpdate(2000, OPENED, FILLED);
        }
        
        for(IOrder o: engine.getOrders()){
            console.getNotif().println("active orders: " + engine.getOrders());
             if (o.getState() == OPENED || o.getState() == FILLED) {
                 o.close();
                 o.waitForUpdate(2000, CLOSED, CANCELED);
             }
        }
        
        context.stop();

    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder order = message.getOrder();
        if (order == null || message.getType() == IMessage.Type.NOTIFICATION) {
            return;
        }
        console.getOut().println(message);
        if (message.getType() == IMessage.Type.ORDER_CLOSE_OK) {
            console.getInfo().println(order + " " +
                    (order.getState() == CANCELED ? "cancelled" :
                    order.getState() == FILLED ? "partially closed" :
                    "fully closed")); //CLOSED
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
