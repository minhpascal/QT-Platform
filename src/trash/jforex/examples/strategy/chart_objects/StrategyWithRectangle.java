package trash.jforex.examples.strategy.chart_objects;

import java.math.BigDecimal;
import java.util.UUID;

import javax.swing.SwingConstants;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.drawings.IRectangleChartObject;

public class StrategyWithRectangle implements IStrategy {
    private IEngine engine;
    private IConsole console;
    private IHistory history;
    private IChart chart;

    private IRectangleChartObject rectangle;
    private ITick formerTick;
    private int counter = 0;

    @Configurable("Amount")
    public double amount = 0.02;

    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;

    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();
        this.history = context.getHistory();
        this.chart = context.getChart(instrument);
        this.formerTick = history.getLastTick(instrument);
        
        ITick tick = history.getLastTick(instrument);
        double askBidDiff = Math.abs(tick.getAsk() - tick.getBid());

        // draw rectangle
        this.rectangle = chart.getChartObjectFactory().createRectangle(getKey("rectangle"), tick.getTime() + Period.TEN_SECS.getInterval(),
                tick.getBid() - askBidDiff, tick.getTime() + 3 * Period.TEN_SECS.getInterval(), tick.getBid() + 3 * askBidDiff);

        chart.addToMainChartUnlocked(rectangle);
    }

    private String getKey(String str) {
        return str + UUID.randomUUID().toString().replace('-', '0');
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onStop() throws JFException {
        
        // remove rectangle on exit
        chart.remove(rectangle.getKey());

        // close all orders
        for (IOrder order : engine.getOrders()) {
            engine.getOrder(order.getLabel()).close();
        }
    }

    private boolean isInsideRectTime(ITick tick) {
        return tick.getTime() >= rectangle.getTime(0) && tick.getTime() <= rectangle.getTime(1);
    }

    private boolean bidInsideRect(ITick tick) {
        return tick.getBid() >= rectangle.getPrice(0) && tick.getBid() <= rectangle.getPrice(1);
    }

    private boolean askInsideRect(ITick tick) {
        return tick.getAsk() >= rectangle.getPrice(0) && tick.getAsk() <= rectangle.getPrice(1);
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {

        if (!instrument.equals(this.instrument))
            return;

        ITick lastTick = tick;

        // if the last tick has come into the rectangle from the left side
        if (!isInsideRectTime(formerTick) && isInsideRectTime(lastTick) && askInsideRect(lastTick)) {
            // take profit price = rectangle top
            // stop loss = rectangle bottom
            engine.submitOrder(getLabel(instrument), this.instrument, OrderCommand.BUY, this.amount, 0, 0, roundToTenthPip( rectangle.getPrice(0) ), roundToTenthPip(rectangle.getPrice(1)) );
        }

        // if the former tick has already been within the time frame of the rectangle but not within the price range 
        // and the last tick has come into the rectangle from the top
        if (isInsideRectTime(lastTick) && bidInsideRect(lastTick) && !bidInsideRect(formerTick) && formerTick.getBid() > rectangle.getPrice(1)) {
            // take profit if price
            engine.submitOrder(getLabel(instrument), this.instrument, OrderCommand.SELL, this.amount);
        }

        // if the former tick has already been within the time frame of the rectangle but not within the price range
        // and the last tick has come into the rectangle from the bottom
        if (isInsideRectTime(lastTick) && askInsideRect(lastTick) && !askInsideRect(formerTick) && formerTick.getAsk() < rectangle.getPrice(0)) {
            // take profit if price
            engine.submitOrder(getLabel(instrument), this.instrument, OrderCommand.BUY, this.amount);
        }

        formerTick = lastTick;
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    private void print(Object o) {
        console.getOut().println(o);
    }

    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label + (counter++);
        label = label.toUpperCase();
        return label;
    }
    
    // round price to the pip scale
    private double roundToTenthPip(double price) {
        BigDecimal bd = new BigDecimal(price);
        bd = bd.setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}