package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy on its start creates an order.
 * Once the order profit reaches 5 pips, the order stop loss
 * gets moved to the open price
 *
 */
public class BreakEven implements IStrategy {
    
    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("")
    public OrderCommand orderCommand = OrderCommand.BUY;
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
    private IOrder order;
    private boolean breakEvenReached = false;

    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
                
        ITick tick = history.getLastTick(instrument);
        double stopLossPrice, takeProfitPrice;
        if(orderCommand.isLong()){
            stopLossPrice = tick.getBid() - stopLossPips * instrument.getPipValue();
            takeProfitPrice = tick.getBid() + takeProfitPips * instrument.getPipValue();
        } else {
            stopLossPrice = tick.getAsk() + stopLossPips * instrument.getPipValue();
            takeProfitPrice = tick.getAsk() - takeProfitPips * instrument.getPipValue();
        }
        //for simplicity make order at the last bid price. Change this for use with conditional orders
        double openPrice = tick.getBid(); 
        order = engine.submitOrder("breakEvenOrder", instrument, orderCommand, amount, openPrice, slippage,  stopLossPrice, takeProfitPrice);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if(instrument != this.instrument || order.getState() != IOrder.State.FILLED){
            return;
        }
        if( order.getProfitLossInPips() >= breakEvenPips && breakEvenReached == false){
            console.getOut().println("Order has profit of " + order.getProfitLossInPips() + " pips! Move the stop loss to break even." );
            order.setStopLossPrice(order.getOpenPrice()); 
            breakEvenReached = true;
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
