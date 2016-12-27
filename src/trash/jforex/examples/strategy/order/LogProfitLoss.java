package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy every 10 seconds prints the total profit loss of the filled positions
 *
 */
public class LogProfitLoss implements IStrategy {

    private IEngine engine;
    private IConsole console;
    
    @Configurable("")
    public Instrument printInstrument = Instrument.EURUSD;
    @Configurable("")
    public Period printPeriod = Period.TEN_SECS;
        
    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(printInstrument), true);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if(instrument != this.printInstrument || period != this.printPeriod)
            return;
        
        double profitLoss = 0;
        for (IOrder order : engine.getOrders()) {
            if (order.getState() == IOrder.State.FILLED){
                profitLoss += order.getProfitLossInUSD();
            }
        }        
        console.getOut().format("%s PL in USD =%.5f ", DateUtils.format(askBar.getTime() + period.getInterval()),profitLoss).println();
        
    }
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException {}
}
