package trash.jforex.examples.strategy.threading_and_scheduling;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

import com.dukascopy.api.drawings.IHorizontalLineChartObject;

/**
 * The strategy on its start creates an order and a horizontal line.
 * Then it schedules a task which moves the horizontal line's price
 * 5 pips above the order's open price whenever the order price changes.
 * 
 * On strategy stop the order gets closed and the line removed from the chart.
 * Also the executor service gets stopped, otherwise it would continue the task
 * execution as scheduled
 *
 */
@RequiresFullAccess
public class HLineScheduledExecutor implements IStrategy {

    private IChart chart;
    private IHistory history;
    private IConsole console;
    private IEngine engine;
    
    private Instrument instrument = Instrument.EURUSD;
    private double pip = Instrument.EURUSD.getPipValue();
    
    @Configurable("Execution interval in milliseconds")
    public long execIntervalMillis = 1000;
    
    private ScheduledExecutorService executor;
    private IOrder order;
    private IHorizontalLineChartObject hLine;
    
    @Override
    public void onStart(final IContext context) throws JFException {
        this.chart = context.getChart(instrument);
        this.history = context.getHistory();
        this.console = context.getConsole();
        this.engine = context.getEngine();
        ITick tick = history.getLastTick(instrument);  
        
        //create hline at price 0 and a conditional order 
        hLine = chart.getChartObjectFactory().createHorizontalLine("hLineKey", 0);
        chart.addToMainChart(hLine);        
        order = engine.submitOrder("orderWithHLine", instrument, OrderCommand.BUYSTOP, 0.001, tick.getAsk() + pip * 10);
        
        //create and schedule the task to run in a separate thread
        final HLineTask hlineTask = new HLineTask(order, hLine);        
        executor = Executors.newSingleThreadScheduledExecutor();

        Runnable periodicTask = new Runnable() {
            public void run() {
                context.executeTask(hlineTask);
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, execIntervalMillis, TimeUnit.MILLISECONDS);
    }
    
    private class HLineTask implements Callable<Object>{
        private IOrder order;
        private IHorizontalLineChartObject hLine;      
        private double lastOpenPrice = 0;
        
        public HLineTask(IOrder order, IHorizontalLineChartObject hLine) {
            this.hLine = hLine;
            this.order = order;
        }
                        
        public Object call() throws Exception {
            //on open price change move hline 5 pips above the open price
            if(Double.compare(lastOpenPrice, order.getOpenPrice()) != 0){
                hLine.setPrice(0, order.getOpenPrice() + pip * 5);
                console.getOut().println(String.format("Adjust hline price from %.5f to %.5f", lastOpenPrice, order.getOpenPrice()));
                lastOpenPrice = order.getOpenPrice();
            }
            return null;
        }
    }
    
    @Override
    public void onStop() throws JFException {
        executor.shutdown();
        if(order.getState() == IOrder.State.FILLED || order.getState() == IOrder.State.OPENED){
            order.close();
        }
        chart.remove(hLine);
        
    }
    
    @Override
    public void onMessage(IMessage message) throws JFException {}
    
    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}



}
