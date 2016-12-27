package trash.jforex.examples.strategy.order;

import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.*;

import static com.dukascopy.api.IEngine.OrderCommand.*;

/**
 * The strategy on its start creates two orders.
 * Once the order profit reaches 5 pips, the order stop loss
 * gets moved to the open price
 *
 */
public class BreakEvenMultipleOrders implements IStrategy {
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    double amount = 0.001;
    @Configurable("")
    public int slippage = 20;
    @Configurable("")
    public int stopLossPips = 10;
    @Configurable("")
    public int takeProfitPips = 40;
    @Configurable("")
    public int breakEvenPips = 5;
    
    private IConsole console;
    private IEngine engine;
    private IHistory history;
    private int counter;
    
    public List<IOrder> orderBeNOTReached = new ArrayList<IOrder>();

    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
                
        submitBeMarketOrder(BUY);
        submitBeMarketOrder(SELL);

    }
    
    private void submitBeMarketOrder(IEngine.OrderCommand orderCommand) throws JFException {
        ITick tick = history.getLastTick(instrument);
        double openPrice = tick.getBid(); 
        double slPrice, tpPrice;
        if (orderCommand.isLong()) {
            slPrice = tick.getBid() - stopLossPips * instrument.getPipValue();
            tpPrice = tick.getBid() + takeProfitPips * instrument.getPipValue();
        } else {
            slPrice = tick.getAsk() + stopLossPips * instrument.getPipValue();
            tpPrice = tick.getAsk() - takeProfitPips * instrument.getPipValue();
        }
        IOrder order = engine.submitOrder("breakEvenOrder"+counter++, instrument, orderCommand, amount, openPrice, slippage, slPrice, tpPrice);
        orderBeNOTReached.add(order);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument 
                || orderBeNOTReached.isEmpty() //all BE's reached, nothing to check
            ){
            return;
        }
        List<IOrder> ordersBeReached = new ArrayList<IOrder>();
        for(IOrder order  : orderBeNOTReached){
            if( order.getProfitLossInPips() >= breakEvenPips){
                console.getOut().println( order.getLabel() + " has profit of " + order.getProfitLossInPips() + " pips! Move the stop loss to break even." );
                order.setStopLossPrice(order.getOpenPrice()); 
                ordersBeReached.add(order);
            }
        }
        for(IOrder order  : ordersBeReached){
            orderBeNOTReached.remove(order);
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
