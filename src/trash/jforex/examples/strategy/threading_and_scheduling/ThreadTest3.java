package trash.jforex.examples.strategy.threading_and_scheduling;

import java.util.concurrent.Callable;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.util.DateUtils;

public class ThreadTest3 implements IStrategy {

    private IHistory history;
    private IEngine engine;
    private IConsole console;
    
    @Override
    public void onStart(IContext context) throws JFException {
        history = context.getHistory();
        engine = context.getEngine();
        console = context.getConsole();

        final BuyTask task = new BuyTask(Instrument.EURUSD, 40);
        
       //run the task in the same thread:
        context.executeTask(task);
        print("This should print after the task execution: " + DateUtils.format(System.currentTimeMillis()));
        
        //run the task in a different thread:
        final IContext finalContext = context;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalContext.executeTask(task);
                } catch (Exception e) {
                    console.getErr().println(Thread.currentThread().getName() + " " + e);
                }
            }
        });
        thread.start();
        print("This should print before the end of the task execution: " + DateUtils.format(System.currentTimeMillis()));
    }

    private class BuyTask implements Callable<IOrder> {
        private final Instrument instrument;
        private final double stopLossPips;

        public BuyTask(Instrument instrument, double stopLossPips) {
            this.instrument = instrument;
            this.stopLossPips = stopLossPips;
        }

        public IOrder call() throws Exception {
            double stopLossPrice = history.getLastTick(instrument).getBid() - stopLossPips * instrument.getPipValue();
            IOrder order = engine.submitOrder("Buy_order1"+System.nanoTime(), instrument, IEngine.OrderCommand.BUY, 0.001, 0, 20, stopLossPrice, 0);
            print("Created order: " + order.getLabel());
            return order;
        }
    }
    
    private void print (Object o){
        console.getOut().println(o);
    }


    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}