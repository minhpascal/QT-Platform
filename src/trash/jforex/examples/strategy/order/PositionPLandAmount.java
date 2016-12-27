package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;

/**
 * The strategy on its start prints position aggregated P/L and amount for each instrument
 *
 */
public class PositionPLandAmount implements IStrategy {

    private IEngine engine;
    private IConsole console;
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();
        for(Instrument instrument : context.getSubscribedInstruments()){
            double profitLoss = 0;
            double amount = 0;
            int filledOrders = 0;
            for (IOrder order : engine.getOrders(instrument)) {
                if (order.getState() == IOrder.State.FILLED){
                    profitLoss += order.getProfitLossInUSD();
                    amount += order.isLong() ? order.getAmount() : -order.getAmount();
                    filledOrders++;
                }
            }
            if(filledOrders > 0){
                console.getOut().format("%s aggregated PL in USD =%.5f, amount=%.5f", instrument ,profitLoss, amount).println();
            }
        }
        context.stop(); //stop the strategy
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {}   
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException {}
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
}
