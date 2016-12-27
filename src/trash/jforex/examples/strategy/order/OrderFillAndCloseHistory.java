package trash.jforex.examples.strategy.order;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import static com.dukascopy.api.IMessage.Type.*;

/**
 * The strategy shows how to retrieve order fill and close history
 *
 */
public class OrderFillAndCloseHistory implements IStrategy {

    @Configurable("")
    public Instrument instrument = Instrument.CHFSGD;
    @Configurable(value="max order amount", description ="Please adjust according to your account limits")
    public double maxAmount = 500;
    @Configurable("")
    public int partialCloseIncrements = 5;
    
    private IConsole console;
    private IHistory history;
    private IEngine engine;
    private IOrder maxOrder;
    
    private static List<IMessage.Type> AMOUNT_CHANGE_TYPES = Arrays.asList(new IMessage.Type[] {ORDER_CHANGED_OK, ORDER_CLOSE_OK, ORDER_FILL_OK});
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        this.history = context.getHistory();
        this.engine = context.getEngine();
        context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(new Instrument[] {instrument})), true);
        double price = history.getLastTick(instrument).getBid() - instrument.getPipValue();
        //submit a max amount, 0-slippage order to increase probability of partial fill
        maxOrder = engine.submitOrder("maxOrder", instrument, OrderCommand.SELLLIMIT, maxAmount, price, 0, 0, 0);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument ){
            return;
        }
        if(
               //close order if it is fully filled or the last fill happened more than 10 secs ago
               maxOrder.getFillTime() + Period.TEN_SECS.getInterval() < tick.getTime() 
               //can't close more often than once per second
               && maxOrder.getCloseTime() + Period.ONE_SEC.getInterval() < tick.getTime()
               && maxOrder.getState() == IOrder.State.FILLED
          ){
            //cancel unfilled part
            if(Double.compare(maxOrder.getRequestedAmount(), maxOrder.getAmount()) > 0){
                maxOrder.setRequestedAmount(0);
                maxOrder.waitForUpdate(2000);
            }
            maxOrder.close(Math.min(maxOrder.getOriginalAmount() / partialCloseIncrements, maxOrder.getAmount()));
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder order = message.getOrder();
        if(order == null){
            return;
        }
        console.getOut().println(message);
        if(AMOUNT_CHANGE_TYPES.contains(message.getType())){
            printPartialOrders(order);
        }
    }
    
    private void printPartialOrders(IOrder order){
        for(IFillOrder pfOrder : order.getFillHistory()){
            console.getInfo().println("fill: " + order.getLabel() + " " + pfOrder);
        }
        for(ICloseOrder pfOrder : order.getCloseHistory()){
            console.getInfo().println("close: " + order.getLabel() + " " + pfOrder);
        }
    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
