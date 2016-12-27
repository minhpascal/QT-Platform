package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy creates two orders in order to show IMessage.Reasons
 * for two cases:
 * - When order gets closed by meeting SL price condition
 * - When order gets closed by any other reason.
 * 
 */
public class OrderClosedBySL implements IStrategy {

    private IConsole console;
    private IEngine engine;
    private IHistory history;
    private Instrument instrument = Instrument.EURUSD;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        engine = context.getEngine();
        history = context.getHistory();

		context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
        IOrder order = engine.submitOrder("orderCloseBySL", instrument, OrderCommand.BUY, 0.001);
        order.waitForUpdate(2000, IOrder.State.FILLED);
        // on purpose put SL such that the order gets immediately closed on the SL
        double slPrice = history.getLastTick(instrument).getAsk() + instrument.getPipValue() * 10;
        order.setStopLossPrice(slPrice);

        // market BUY for unconditional close
        order = engine.submitOrder("orderUnconditionalClose", instrument, OrderCommand.BUY, 0.001);
        order.waitForUpdate(2000, IOrder.State.FILLED);
        order.close();
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        IOrder order = message.getOrder();
        if (order == null) {
            return;
        }
        console.getOut().println(message);
        if (message.getType() == IMessage.Type.ORDER_CLOSE_OK) {
            if (message.getReasons().contains(IMessage.Reason.ORDER_CLOSED_BY_SL)) {
                console.getInfo().println(order.getLabel() + " order closed by stop loss");
            } else {
                console.getInfo().println(order.getLabel() + " order closed by other reason than reaching stop loss");
            }
        }

    }
    

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }


    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    }
}
