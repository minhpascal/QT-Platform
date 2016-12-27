package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import static com.dukascopy.api.IEngine.OrderCommand.*;
/**
 * The strategy on its start creates an order for every order type
 * and thereafter prints the info about the created orders depending on their type
 *
 */
public class SwitchOrderCommand implements IStrategy {

    private IEngine engine;
    private IConsole console;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();        
        this.history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
        
        //price used for conditional orders - just the last bid price
        double price = history.getLastTick(Instrument.EURUSD).getBid();
        
        //make an order for every order command
        OrderCommand[] orderCommands = OrderCommand.values();
        for (int i = 0; i < orderCommands.length; i++){
            try{
                //market orders
                if (orderCommands[i] == BUY || orderCommands[i] == SELL){
                    engine.submitOrder("order"+i, Instrument.EURUSD, orderCommands[i], 0.001);
                //conditional orders - for conditional orders we need to assign a >0 price
                } else { 
                    engine.submitOrder("order"+i, Instrument.EURUSD, orderCommands[i], 0.001, price);
                }
            } catch (JFException e) {
                console.getErr().println(orderCommands[i] + " " + e);
            }
        }
        
        //iterate through all active orders
        for (IOrder order : engine.getOrders()) {
            switch (order.getOrderCommand()) {
            case BUY:
                print(order.getLabel() + " is a LONG market order");
                break;
            case SELL:
                print(order.getLabel() + " is a SHORT market order");
                break;
            default:
                print(order.getLabel() + " is a " + (order.isLong() ? "LONG" : "SHORT") + " " + order.getOrderCommand()
                        + " conditional order");
            }
        }
        for (IOrder order : engine.getOrders()) {
            print(order.getLabel() + " " + order.getOrderCommand());
        }

    }
    
    //close all active orders on startegy stop
    @Override
    public void onStop() throws JFException {
        for (IOrder o : engine.getOrders()){
            o.close();
        }
    }
    
    private void print(Object o){
        console.getOut().println(o);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if(instrument == Instrument.EURUSD && period == Period.TEN_SECS){
            for (IOrder order : engine.getOrders()) {
                print(order.getLabel() + " " + order.getOrderCommand());
            }
        }
    }
    @Override
    public void onMessage(IMessage message) throws JFException {}
    @Override
    public void onAccount(IAccount account) throws JFException {}

}
