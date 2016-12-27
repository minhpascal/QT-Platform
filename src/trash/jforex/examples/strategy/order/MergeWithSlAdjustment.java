package trash.jforex.examples.strategy.order;


import com.dukascopy.api.*;
import com.dukascopy.api.util.DateUtils;

import static com.dukascopy.api.IEngine.OrderCommand.*;

/**
 * The strategy creates 3 orders which have SL prices, 
 * since orders with SL can't be merged, 
 * both price conditions have to be removed and after successful merge 
 * set all over new to the merged order.
 * 
 * The strategy calculates the weighted average SL amount
 * of the mergeable orders and then set the calculated SL price
 * to the merged order
 *
 */
@RequiresFullAccess
public class MergeWithSlAdjustment implements IStrategy {

    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("order1 Amount")
    public double order1Amount = 0.01;
    @Configurable("order1 Is Long")
    public boolean order1IsLong = true;
    @Configurable("order1 Sl Pips")
    public int order1SlPips = 30;
    @Configurable("order2 Amount")
    public double order2Amount = 0.02;
    @Configurable("order2 Is Long")
    public boolean order2IsLong = false;
    @Configurable("order2 Sl Pips")
    public int order2SlPips = 10;
    @Configurable("order3 Amount")
    public double order3Amount = 0.03;
    @Configurable("order3 Is Long")
    public boolean order3IsLong = true;
    @Configurable("order3 Sl Pips")
    public int order3SlPips = 10;
    
    private IConsole console;
    private IEngine engine;
    private IHistory history;    
        
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        console.getOut().println("Start");
        
        double price = history.getLastTick(instrument).getBid();
        double pip = instrument.getPipValue();
        
        IOrder order1 = engine.submitOrder("order1", instrument, order1IsLong ? BUY : SELL, order1Amount, 0, 20, 
                order1IsLong ? price - order1SlPips * pip : price + order1SlPips * pip, 0); //SL price
        IOrder order2 = engine.submitOrder("order2", instrument, order2IsLong ? BUY : SELL, order2Amount, 0, 20, 
                order2IsLong ? price - order2SlPips * pip : price + order2SlPips * pip, 0); //SL price
        IOrder order3 = engine.submitOrder("order3", instrument, order3IsLong ? BUY : SELL, order3Amount, 0, 20, 
                order3IsLong ? price - order3SlPips * pip : price + order3SlPips * pip, 0); //SL price

        //wait for market orders to get OPENED and then FILLED
        order1.waitForUpdate(2000);// wait for OPENED 
        order1.waitForUpdate(2000);// wait for FILLED
        //order2 and order3 might have got FILLED while waiting for order1 update, so we wait only if there is no fill happened yet
        if(order2.getState() != IOrder.State.FILLED)
            order2.waitForUpdate(2000);        
        if(order2.getState() != IOrder.State.FILLED)
            order2.waitForUpdate(2000);
        if(order3.getState() != IOrder.State.FILLED)
            order3.waitForUpdate(2000);
        if(order3.getState() != IOrder.State.FILLED)
            order3.waitForUpdate(2000);
        
        try {
            mergeWithSlAndTp(order1, order2, order3);
        } catch (JFException e) {
            printErr("Merge failed: " + e.getMessage());
        }
        
        
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

      
    private void mergeWithSlAndTp(IOrder... orders) throws JFException{

        ITick tick = history.getLastTick(instrument);
        double slAmountWeightedTotal = 0; //SL amount - aggregation of market price distance to SL's and weighted by order amount
        double slAmountWeighted;
        int slCount = 0;
        
        //remove sl attached orders if any
        for(IOrder o: orders){
            double price = o.isLong() ? tick.getBid() : tick.getAsk();
            if(Double.compare(o.getStopLossPrice(),0) != 0){
                slAmountWeighted = Math.abs(price - o.getStopLossPrice()) * o.getAmount();
                slAmountWeightedTotal += slAmountWeighted; 
                print( String.format("%s remove stop loss. amount-weighted SL=%.8f, already aggregated SL amount=%.8f", 
                        o.getLabel(), slAmountWeighted, slAmountWeightedTotal));
                o.setStopLossPrice(0);
                
                o.waitForUpdate(2000);        
                slCount++;
            }
        }
        
        double slAmountWeightedAverage = slAmountWeightedTotal / slCount;
        
        IOrder mergedOrder = engine.mergeOrders("mergedOrder", orders);
        mergedOrder.waitForUpdate(2000);
        
        if(mergedOrder.getState() != IOrder.State.FILLED){
            return;
        }
        
        double slPriceDelta = slAmountWeightedAverage / mergedOrder.getAmount();
        double slPrice = mergedOrder.isLong() 
            ? tick.getBid() - slPriceDelta
            : tick.getAsk() + slPriceDelta;
        mergedOrder.setStopLossPrice(slPrice);
        mergedOrder.waitForUpdate(2000);
        
        print(String.format("mergedOrder sl=%.5f", mergedOrder.getStopLossPrice()));
        
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {

    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        // print all order related messsages
        if (message.getOrder() != null)
            print("<html><font color=\"gray\">"+ DateUtils.format(System.currentTimeMillis()) + " " + message+"</font>");
    }
    
    private void printErr(Object o){
        console.getErr().println(o);
    }

    private void print(Object o) {
        console.getOut().println(o);
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
        for (IOrder o : engine.getOrders())
            o.close();
    }

}
