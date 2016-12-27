package trash.jforex.examples.strategy.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import com.dukascopy.api.*;

import static com.dukascopy.api.Instrument.*;
import static com.dukascopy.api.IEngine.OrderCommand.*;

/**
 * The strategy on its start creates 4 orders of two instruments.
 * In every 10 seconds, the strategy prints lists of:
 * - active orders,
 * - pending orders,
 * - profitable orders,
 * - losing orders.
 * 
 * Depending on strategy parameters the strategy closes the orders of a certain group.
 *
 */
public class OrderManagementPL implements IStrategy {
    
    @Configurable("Close all active orders")
    public boolean closeAllActive = false;
    @Configurable("Close all pending orders")
    public boolean closeAllPending = false;
    @Configurable("Close all profit orders")
    public boolean closeAllProfit = false;
    @Configurable("Close all loss orders")
    public boolean closeAllLoss = false;
    
    private IConsole console;
    private IEngine engine;
    private Set<Instrument> instruments = new HashSet<Instrument>(Arrays.asList(new Instrument[] {EURUSD, USDCAD}));

    @Override
    public void onStart(IContext context) throws JFException {
        
        console = context.getConsole();
        engine = context.getEngine();    
        
        context.setSubscribedInstruments(instruments, true);
        
        engine.submitOrder("Order_BUY_EURUSD", EURUSD, BUY, 0.1);
        engine.submitOrder("Order_SELL_EURUSD", EURUSD, SELL, 0.2);
        engine.submitOrder("Order_BUY_USDCAD", USDCAD, BUY, 0.1);
        engine.submitOrder("Order_SELL_USDCAD", USDCAD, SELL, 0.2);

    }
    
    private List<IOrder> getProfitOrders() throws JFException{
        List<IOrder> orders = new ArrayList<IOrder>();
        for(IOrder o : engine.getOrders()){
            if(o.getProfitLossInUSD() >= 0){
                orders.add(o);
            }
        }
        return orders;
    }
    
    private List<IOrder> getLossOrders() throws JFException{
        List<IOrder> orders = engine.getOrders();
        orders.removeAll(getProfitOrders());
        return orders;
    }
    
    private List<IOrder> getPendingOrders() throws JFException{
        List<IOrder> orders = engine.getOrders();
        for(IOrder o : engine.getOrders()){
            if(o.getState() == IOrder.State.OPENED){
                orders.add(o);
            }
        }
        return orders;
    }
    
    
    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if(instrument != EURUSD || period != Period.TEN_SECS){
            return;
        }
        console.getOut().println("Active orders: " + engine.getOrders());
        console.getOut().println("Pending orders: " + getPendingOrders());
        console.getOut().println("Orders in profit: " + getProfitOrders());
        console.getOut().println("Orders in loss: " + getLossOrders()); 
        
        if(closeAllActive) closeOrders(engine.getOrders());
        if(closeAllPending) closeOrders(getPendingOrders());
        if(closeAllProfit) closeOrders(getProfitOrders());
        if(closeAllLoss) closeOrders(getLossOrders());
    }
    
    private void closeOrders(List<IOrder> orders) throws JFException{
        for(IOrder o: orders){
            if(o.getState() == IOrder.State.FILLED || o.getState() == IOrder.State.OPENED){
                o.close();
            }
        }
    }
    
    
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {
        //close all orders on strategy stop
        for(IOrder o : engine.getOrders()){
            o.close();
        }
    }

}
