package trash.jforex.examples.strategy.practices;

import com.dukascopy.api.*;

public class BroadcastStrategy implements IStrategy {
    private IEngine engine;
    private IConsole console;
   
    @Configurable("Name")
    public String name = "CLIENT 1";

    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        engine = context.getEngine();
        console.getOut().println("Started : " + name);
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
        if (message instanceof IStrategyBroadcastMessage) {
            console.getOut().println("Broadcast : " + message);
        }
    }

    public void onStop() throws JFException {
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if (period.equals(Period.TEN_SECS) && (instrument.equals(Instrument.EURUSD))) {
        try {
            engine.broadcast(name, "Broadcast Client 1 " + System.currentTimeMillis());
        } catch (Exception ex) {
            ex.printStackTrace();
            console.getErr().println("Error while broadcasting : " + ex);
        }
        }
    }
}