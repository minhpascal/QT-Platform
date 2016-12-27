package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.util.DateUtils;


/**
 * The strategy on its start places a bid and as soon as the 
 * bid gets opened, the exipry time gets changed.
 *
 */
public class OneOrderPlaceBid2 implements IStrategy {

    private IConsole console;
    private IEngine engine;
    private IHistory history;
    private IOrder order;
    
    private final String label = "OneOrder";
    
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        console.getOut().println("Start");
        
        //withdraw bid after 10 secs
        long goodTillTime = history.getLastTick(Instrument.EURUSD).getTime() + 10 * 1000; 
        //price 5 pips below the last bid price
        double price = history.getLastTick(Instrument.EURUSD).getBid() - 5 * Instrument.EURUSD.getPipValue();
        order = engine.submitOrder(label, Instrument.EURUSD, OrderCommand.PLACE_BID, 0.01, 
                price, 0, 0, 0, goodTillTime);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {
        if(message.getType() == IMessage.Type.ORDER_SUBMIT_OK && message.getOrder() == this.order){
            //cancel in 2.5 secs after opening the order
            order.setGoodTillTime(history.getLastTick(Instrument.EURUSD).getTime() + 2500);
        }
        
        //print all order related messages
        if(message.getOrder() != null){
            console.getOut().println(DateUtils.format(message.getCreationTime()) + " " +message);
        }

    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {
        if (engine.getOrder(label) != null)
            engine.getOrder(label).close();

    }

}
