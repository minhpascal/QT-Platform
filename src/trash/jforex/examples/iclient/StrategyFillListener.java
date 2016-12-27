package trash.jforex.examples.iclient;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

/**
 * The strategy fills an order every 10 seconds for each subscribed instrument.
 * The strategy contains a listener which executes the caller program-defined logic on each order fill.
 *
 */
@RequiresFullAccess
public class StrategyFillListener implements IStrategy {
	
    private IConsole console; 
    private IEngine engine;
    private StrategyFillListener.ClientActions clientActions;
    int counter;
    String prefix = "strategyOrder";
    
    public interface ClientActions {
        void onOrderFill(IOrder order, IEngine engine);
    }  
    
    //for the launch from standalone
    public StrategyFillListener (StrategyFillListener.ClientActions clientActions){
        this.clientActions = clientActions;
    }
    
    //for the launch from the JForex client
    public StrategyFillListener (){        
        this.clientActions = new StrategyFillListener.ClientActions (){
            @Override
            public void onOrderFill(IOrder order, IEngine engine) {}            
        };
    }

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        engine = context.getEngine();
        
        print("start strategy");
    }
    
    private void print(Object o){
        console.getOut().println(o);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    	engine.submitOrder(prefix + counter++, instrument, OrderCommand.BUY, 0.001);
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    	IOrder order = message.getOrder();
    	if(order == null){
    		return;
    	}
    	//callback only on strategy-created order fills
        if(message.getType() == IMessage.Type.ORDER_FILL_OK && order.getLabel().startsWith(prefix)){
            clientActions.onOrderFill(order, engine);
        }
    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {
    	for(IOrder o : engine.getOrders()){
    		if (o.getState() != IOrder.State.CREATED){
    			o.close();
    		}
    	}
    }

}
