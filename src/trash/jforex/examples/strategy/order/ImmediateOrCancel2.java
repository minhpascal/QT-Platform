package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy on its start creates an order and right after the order fill cancels the unfilled part
 */
public class ImmediateOrCancel2 implements IStrategy {

	@Configurable("")
	public Instrument instrument = Instrument.EURUSD;
	
	private IConsole console;
	private IEngine engine;
	private IHistory history;
	private IOrder order;
	
	private final String label = "iocOrder";
	
	@Override
	public void onStart(IContext context) throws JFException {
		engine = context.getEngine();
		console = context.getConsole();
		history = context.getHistory();
	    context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
		console.getOut().println("Start");
		
		//price 5 pips below the last bid price
		double price = history.getLastTick(instrument).getBid() - 5 * instrument.getPipValue();
		order = engine.submitOrder(label, instrument, OrderCommand.BUYLIMIT, 0.01, price, 0, 0, 0);
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

	@Override
	public void onMessage(IMessage message) throws JFException {
		//our order just got filled AND it has an unfilled part
		if(message.getOrder() == order && order.getState() == IOrder.State.FILLED 
				&& Double.compare(order.getRequestedAmount(), order.getAmount()) != 0){
			// cancel the unfilled part 
			order.setRequestedAmount(0);
		}
		
		//print all order related messages
		if(message.getOrder() != null){
			console.getOut().println(DateUtils.format(message.getCreationTime()) + " " +message);
		}

	}

	@Override
	public void onAccount(IAccount account) throws JFException {}

	//close order on stop
	@Override
	public void onStop() throws JFException {
		if (engine.getOrder(label) != null)
			engine.getOrder(label).close();

	}

}
