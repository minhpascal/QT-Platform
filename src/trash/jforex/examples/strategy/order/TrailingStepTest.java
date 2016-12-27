package trash.jforex.examples.strategy.order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IMessage.Reason;

/**
 * The strategy keeps track of stop loss changes according to the set trailing step.
 * The strategy creates two orders in opposite directions such that one of them 
 * will get closed by stop loss while the other one - will have its stop loss updated
 * by trailing step.
 * 
 */
public class TrailingStepTest implements IStrategy {

    @Configurable("")
    public Instrument instrument = Instrument.EURNOK;
    @Configurable("")
    public int trailingStep = 10;
    
    private IConsole console;
    private IEngine engine;
    private IHistory history;
    
    private Map<IOrder, Integer> slMoves = new HashMap<IOrder, Integer>();
    

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        engine = context.getEngine();
        history = context.getHistory();
        
        context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(new Instrument[]{instrument})), true);
        
        ITick tick = history.getLastTick(instrument);

        IOrder order = engine.submitOrder("buyWithTrailing_"+tick.getTime(), instrument, OrderCommand.BUY, 0.001);
        order.waitForUpdate(2000, IOrder.State.FILLED);     
        slMoves.put(order, 0);
        order.setStopLossPrice(tick.getBid() - instrument.getPipValue() * 10, OfferSide.BID, trailingStep);

        // market BUY for unconditional close
        order = engine.submitOrder("sellWithTrailing_"+tick.getTime(), instrument, OrderCommand.SELL, 0.001);
        order.waitForUpdate(2000, IOrder.State.FILLED);
        slMoves.put(order, 0);
        order.setStopLossPrice(tick.getAsk() + instrument.getPipValue() * 10, OfferSide.ASK, trailingStep);

    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder order = message.getOrder();
        if (order == null || !slMoves.containsKey(order)) {
            //skip non-order messages and messages not related to our 2 orders
            return;
        }
        console.getOut().println(message);
        if (message.getType() == IMessage.Type.ORDER_CHANGED_OK && message.getReasons().contains(Reason.ORDER_CHANGED_SL)) {
            slMoves.put(order, slMoves.get(order) + 1);
            console.getInfo().format("%s stop loss changed to %.5f\n stop loss moves: %s", order.getLabel(), order.getStopLossPrice(), slMoves).println();
        } else if (message.getType() == IMessage.Type.ORDER_CLOSE_OK){
            slMoves.remove(order);
        }

    }
    

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument){
            return;
        }
        for(Map.Entry<IOrder, Integer> tsMove : slMoves.entrySet()){
            IOrder order = tsMove.getKey();
            int moveCount = tsMove.getValue();
            //first stop loss move is right after the order fill
            if(order.getProfitLossInPips() > trailingStep && moveCount < 2){
                console.getErr().format("%s profit is %.1%f pips but stop loss has not been moved! ",  order.getLabel(), order.getProfitLossInPips()).println();
            }
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }


    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    }
}
