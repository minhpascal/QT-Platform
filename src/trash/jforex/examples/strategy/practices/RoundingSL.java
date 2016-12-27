package trash.jforex.examples.strategy.practices;

import java.math.BigDecimal;
import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;


public class RoundingSL implements IStrategy {

    private Instrument instrument = Instrument.EURUSD;
    private IEngine engine;
    public IHistory history;

    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.history = context.getHistory();

        ITick tick = history.getLastTick(instrument);
        double slPrice = tick.getBid() - 0.0010001;
        engine.submitOrder("marketOrder", instrument, OrderCommand.BUY, 1, 0, 10, roundToPippette(slPrice, instrument), 0);
    }
    
    private static double roundToPippette(double amount, Instrument instrument) {
        return round(amount, instrument.getPipScale() + 1);
    }
    
    private static double round(double amount, int decimalPlaces) {
        return (new BigDecimal(amount)).setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onStop() throws JFException {    }

    public void onTick(Instrument instrument, final ITick tick) throws JFException {}
    

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }
    

    

}
