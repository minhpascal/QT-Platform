package trash.jforex.examples.strategy.strategies;

import static com.dukascopy.api.IEngine.OrderCommand.BUY;
import static com.dukascopy.api.IEngine.OrderCommand.SELL;
import static com.dukascopy.api.IOrder.State.CLOSED;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.*;

/**
 * The strategy employs the Martingale system in making the bets i.e. orders:
 * - if the order loses then the next order's amount gets doubled,
 * - if the order is in profit then the next order's amount gets reset to starting amount.
 * The order direction gets decided by using values of two MA indicators
 * If for somewhat reason the order gets canceled, it gets resubmitted with the same data.
 */
public class MartingaleWtihMA implements IStrategy {
    
    @Configurable("Maximum level")
    public int maxLevel = 10; //sets that the maximum order amount = startingAmount * 2^maxLevel
    @Configurable("Starting amount")
    public double startingAmount = 0.001;
    @Configurable("Starting direction")
    public OrderCommand startingDirection = BUY;
    
    //order parameters
    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("Stop loss")
    public int stopLossPips = 10;
    @Configurable("Take profit")
    public int takeProfitPips = 10;
    
    //indicator parameters
    @Configurable("MA period")
    public int MAPeriod=100;
    @Configurable("MA period 2") 
    public int MAPeriod2=10;
    @Configurable("MA period step")
    public Period tf = Period.ONE_HOUR;
    
    //private params
    private int slippage = 20;
    
    private IEngine engine;
    private IHistory history;
    private IIndicators indicators;
    
    private int currentLevel;
    private int orderCounter;
    private IOrder order;
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.history = context.getHistory();
        this.indicators = context.getIndicators();    
        
        //make the first order
        submitOrder();
    }
    
    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        
        if(engine.getOrders().contains(order))
            return;
        
        // the order has been either CLOSED or CANCELED
        if (order.getState() == CLOSED) {
            // on profit reset the level, on loss increase the level
            currentLevel = order.getProfitLossInUSD() > 0 ? 0 : currentLevel + 1;
            submitOrder();
        } else {
            //if order had been canceled - just resubmit the order with the same data
            resubmitOrder();
        }
    }
    
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {    }
    
    @Override
    public void onStop() throws JFException {
        //close the order
        if(engine.getOrders().contains(order))
            order.close();
    }
    
    /**
     * Get the order direction depending on MA value.
     */
    private OrderCommand getOrderCommand() throws JFException{
        
        double ma5_0 = indicators.ma(instrument, tf,OfferSide.BID,IIndicators.AppliedPrice.CLOSE,MAPeriod,IIndicators.MaType.SMA,0);
        double ma5_1 = indicators.ma(instrument, tf,OfferSide.BID,IIndicators.AppliedPrice.CLOSE,MAPeriod2,IIndicators.MaType.SMA,0);
        
        double price = history.getLastTick(instrument).getBid();

        return (price < ma5_0 || price < ma5_1) ? SELL : BUY;
    }
    
    private void submitOrder() throws JFException{

        double stopLossPrice, takeProfitPrice, orderAmount;
        
        //Calculating amount
        if(currentLevel == 0){
            //reset to the starting amount
            orderAmount = startingAmount;
        } else if(currentLevel == maxLevel){
            //can't exceed the max level's amount
            orderAmount = order.getAmount();
        } else {
            //double the last order's amount if we went the same direction (the level got incresased instead of getting nullified)
            orderAmount = order.getAmount() * 2;
        }
        
        //Calculating stop loss and take profit prices
        OrderCommand orderCommand = getOrderCommand();
        if (orderCommand == OrderCommand.BUY) {
            stopLossPrice = history.getLastTick(this.instrument).getBid() - stopLossPips * this.instrument.getPipValue();
            takeProfitPrice = history.getLastTick(this.instrument).getBid() + takeProfitPips * this.instrument.getPipValue();
        } else {
            stopLossPrice = history.getLastTick(this.instrument).getAsk() + stopLossPips * this.instrument.getPipValue();
            takeProfitPrice = history.getLastTick(this.instrument).getAsk() - takeProfitPips * this.instrument.getPipValue();
        }
        
        order = engine.submitOrder("order" + orderCounter++, instrument, orderCommand, orderAmount, 0, slippage, stopLossPrice, takeProfitPrice);
    }
    
    private void resubmitOrder() throws JFException{
        
        order = engine.submitOrder("order" + orderCounter++, instrument, order.getOrderCommand(), order.getAmount(), 0, slippage, 
                order.getStopLossPrice(), order.getTakeProfitPrice());
    }
}
