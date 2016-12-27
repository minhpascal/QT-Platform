package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

public class ConditionalOrder implements IStrategy {

    private IEngine engine;
    private IConsole console;
    private IOrder order;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        engine = context.getEngine();
        console = context.getConsole();
        history = context.getHistory();
		context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        double lastAsk = history.getLastTick(Instrument.EURUSD).getAsk();
        double price = lastAsk + Instrument.EURUSD.getPipValue() * 2;
        order = engine.submitOrder("BuyStopOrder", Instrument.EURUSD, OrderCommand.BUYSTOP, 0.1, price);        
    }
    
    public void onMessage(IMessage message) throws JFException {
        if(message.getOrder() == order){//print only our-order messages
            console.getOut().println(message);
        }
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException {}
}
