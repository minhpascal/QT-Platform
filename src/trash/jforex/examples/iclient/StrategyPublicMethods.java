package trash.jforex.examples.iclient;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

@RequiresFullAccess
public class StrategyPublicMethods implements IStrategy {
	
    private IConsole console; 
    private IEngine engine;
    private StrategyPublicMethods.ClientActions clientActions;
    
    public interface ClientActions {
        void onOrderClose(IOrder order);
        void onOrderFill(IOrder order);
    }  
    
    //for the launch from standalone
    public StrategyPublicMethods (StrategyPublicMethods.ClientActions clientActions){
        this.clientActions = clientActions;
    }
    
    //for the launch from the JForex client
    public StrategyPublicMethods (){        
        this.clientActions = new StrategyPublicMethods.ClientActions (){
            @Override
            public void onOrderClose(IOrder order) {}
            @Override
            public void onOrderFill(IOrder order) {}            
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
        if(engine.getOrders().size() == 0){
            engine.submitOrder("order", instrument, OrderCommand.BUY, 0.001);
        } else {
            for (IOrder order : engine.getOrders()){
                if (order.getState() == IOrder.State.FILLED){
                    order.close();
                }
            }
        }
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        if(message.getType() == IMessage.Type.ORDER_FILL_OK){
            clientActions.onOrderFill(message.getOrder());
        }
        if(message.getType() == IMessage.Type.ORDER_CLOSE_OK){
            clientActions.onOrderClose(message.getOrder());
        }
    }

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
