package trash.jforex.examples.strategy.account;


import com.dukascopy.api.*;

public class AccountInfoOnAccount implements IStrategy {

    private IEngine engine;
    private IConsole console;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
    }
    
    @Override
    public void onAccount(IAccount account) throws JFException {
        double profitLoss = 0;
        double totalAmount = 0;  
        for (IOrder order : engine.getOrders()) {
            if(order.getState() == IOrder.State.FILLED){
                profitLoss += order.getProfitLossInUSD();
                totalAmount += order.getAmount();
            }
        }     
        //account.getEquity() gets updated every 5 seconds 
        //whereas history.getEquity() gets calculated according to the last tick prices
        console.getOut().format("last server equity=%.2f calculated equity=%.2f profit/loss=%.2f credit line=%.2f balance=%.2f total amount=%.3f", 
            account.getEquity(), history.getEquity(), profitLoss, account.getCreditLine(), account.getBalance(), totalAmount).println();
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onStop() throws JFException {}

    
}
