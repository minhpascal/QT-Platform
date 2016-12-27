package trash.jforex.examples.strategy.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy creates 10 orders onStart and on every onBar merges first two of positions
 * in the list, with limit up to 5 positions per each merged position.
 *
 */
public class Merge10WLimit implements IStrategy {

    private IConsole console;
    private IEngine engine;

    private Instrument instrument = Instrument.EURUSD;
    
    private Map<IOrder, Integer> mergeCounts = new HashMap<IOrder, Integer>();  
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
        console.getOut().println("Start");

        // create 5 buy orders and 5 sell orders
        for (int i = 0; i < 10; i++) {
            IOrder order = engine.submitOrder('o' + String.valueOf((char)((int)'A' + i)), instrument, i % 2 == 1 ? OrderCommand.BUY : OrderCommand.SELL, 0.001 * (i + 1));
            order.waitForUpdate(IOrder.State.FILLED);
        }

        while (engine.getOrders().size() > 1){
            int previousMergeCountTotal = 0;
            String label = "";
            List<IOrder> mergeableOrders = Arrays.asList(engine.getOrders().get(0), engine.getOrders().get(1));
            for(IOrder mergeableOrder : mergeableOrders){
                Integer previousMergeCount = mergeCounts.get(mergeableOrder);
                if(previousMergeCount != null){
                    previousMergeCountTotal += previousMergeCount;
                }
                label += mergeableOrder.getLabel();
            }
            if(previousMergeCountTotal >= 5){
                console.getWarn().println("Aggregated merge count can't exceed 5!");
                break;
            } else {
                IOrder mergedOrder = engine.mergeOrders(label,mergeableOrders.toArray(new IOrder[]{}));
                IMessage message = mergedOrder.waitForUpdate(2, TimeUnit.SECONDS);
                if(message.getType() == IMessage.Type.ORDERS_MERGE_OK){
                    mergeCounts.put(mergedOrder, previousMergeCountTotal+1);
                }
                console.getInfo().println(mergeCounts);
            }
            
        }
    }
    
    @Override
    public void onMessage(IMessage message) throws JFException {
        // print all order related messsages
        if (message.getOrder() != null)
            print(message);
    }

    private void print(Object o) {
        console.getOut().println(o);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}



    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
        for (IOrder o : engine.getOrders())
            o.close();
    }

}
