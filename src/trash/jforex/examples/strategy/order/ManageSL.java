package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy demonstrates how one can manage order's stop loss:
 *  1) add stop loss at order creation
 *  2) add stop loss to an existing order
 *  3) remove stop loss
 *  4) modify stop loss
 *
 * The strategy prints all order changes and closes the order on strategy stop
 * 
 */
public class ManageSL implements IStrategy {
    
    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("Instrument")
    public Period period = Period.TEN_SECS;
    @Configurable("Order Command")
    public OrderCommand cmd = OrderCommand.BUY;
    @Configurable("Stop Loss In Pips")
    public int StopLossInPips = 5;
    
    private IConsole console;
    private IHistory history;
    private IEngine engine;
    private IOrder order;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        history = context.getHistory();
        engine = context.getEngine();
        
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
        double lastBidPrice = history.getLastTick(instrument).getBid();

        double amount = 0.001;
        int slippage = 5;
        double stopLossPrice = getSLPrice(lastBidPrice);
        double takeProfitPrice = 0; //no take profit
        
        //1) make an order with SL at the last bid price
        order = engine.submitOrder("order1", instrument, cmd, amount, lastBidPrice, slippage, stopLossPrice, takeProfitPrice);
        
    }
    
    private double getSLPrice (double price){
        return cmd.isLong() 
            ? price - instrument.getPipValue() * StopLossInPips
            : price + instrument.getPipValue() * StopLossInPips;
    }
    
    private void print(Object o){
        console.getOut().println(o);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if(instrument != this.instrument || period != this.period){
            return;
        }
        
        //We can change SL only for FILLED and OPENED orders
        if(order.getState() == IOrder.State.FILLED || order.getState() == IOrder.State.OPENED){
            
            //2) if order has no stop loss - we add it
            if (doubleEquals(order.getStopLossPrice(),0)){
                order.setStopLossPrice(getSLPrice(order.getOpenPrice()));
                return;
            }
            
            //3) if the stop loss increased more than twice - remove it
            if (Math.abs(order.getOpenPrice() - order.getStopLossPrice()) > StopLossInPips * instrument.getPipValue() * 2){
                order.setStopLossPrice(0);
                return;
            }
            
            //4) increase stop loss by 1 pip
            if (order.isLong()){
                order.setStopLossPrice(order.getStopLossPrice() - instrument.getPipValue());
            } else {
                order.setStopLossPrice(order.getStopLossPrice() + instrument.getPipValue());
            }
        }

    }
    
    //we need such function since floating point values are not exact
    private boolean doubleEquals(double d1, double d2){
        //0.1 pip is the smallest increment we work with in JForex
        return Math.abs(d1-d2) < instrument.getPipValue() / 10;
    }

    @Override
    public void onMessage(IMessage message) throws JFException {        
        //print only orders related to our order change
        if(message.getOrder() != null && message.getOrder() == order)
            print(message);
    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    //close all active orders on strategy stop
    @Override
    public void onStop() throws JFException {
        for(IOrder o : engine.getOrders()){
            o.close();
        }
    }

}
