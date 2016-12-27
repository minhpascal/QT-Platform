package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

public class MarketOrder implements IStrategy {

	private IEngine engine;
	private IConsole console;
	private IOrder order;
	
	@Override
	public void onStart(IContext context) throws JFException {
		engine = context.getEngine();
		console = context.getConsole();
		context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		order = engine.submitOrder("MaketBuyOrder1", Instrument.EURUSD, OrderCommand.BUY, 0.1);		
	}
	
	public void onMessage(IMessage message) throws JFException {
		if(message.getOrder() == order){//print only our-order messages
			console.getOut().println(message);
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {}
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
	public void onAccount(IAccount account) throws JFException {}
	public void onStop() throws JFException {}}
