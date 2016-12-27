package trash.jforex.examples.strategy.order;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;

/**
 * The strategy on its start creates an order with minimum order amount
 * for each of the instruments in the instruments set.
 * The strategy closes all orders on its stop.
 *
 */
@SuppressWarnings("serial")
public class MinAmounts implements IStrategy {

	private IEngine engine;	
	private IConsole console;
	private IContext context;	

	public Set<Instrument> instruments = new HashSet<Instrument>() {{
		add(Instrument.EURUSD);
		add(Instrument.XAUUSD);		
		add(Instrument.XAGUSD);	
	}};
	
	@Override
	public void onStart(IContext context) throws JFException {
		engine = context.getEngine();
		console = context.getConsole();
		this.context = context;
		
		//in order to trade, we have to make sure that the instruments have been subscribed
		subscribeToInstruments(instruments);
		
		//make an order for each of the instruments with their minimum amount
		for (Iterator<Instrument> it = instruments.iterator(); it.hasNext();) {
			Instrument instrument = it.next();
			String label = instrument.getPrimaryCurrency() +"order";
			engine.submitOrder(label, instrument, OrderCommand.BUY, getMinAmount(instrument));
		}
	}
	
	//minimum order amount is 1000 units, except for XAUUSD and XAGUSD to which it is 1 and 50 ounces respectively 
	private double getMinAmount(Instrument instrument){
		switch (instrument){
		case XAUUSD : return 0.000001;
		case XAGUSD : return 0.00005;
		default : return 0.001;
		}
	}
	
	private void subscribeToInstruments(Set<Instrument> instruments){
		 
		context.setSubscribedInstruments(instruments);
		 
		// wait max 1 second for the instruments to get subscribed
		int i = 10;
		while (!context.getSubscribedInstruments().containsAll(instruments)) {
		    try {
		        Thread.sleep(100);
		    } catch (InterruptedException e) {
		        console.getOut().println(e.getMessage());
		    }
		    i--;
		}
	}
	
	//close all orders on strategy stop
	@Override
	public void onStop() throws JFException {
		for (IOrder o : engine.getOrders()){
			o.close();
		}		
	}
	public void onTick(Instrument instrument, ITick tick) throws JFException {}
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
	public void onMessage(IMessage message) throws JFException {}
	public void onAccount(IAccount account) throws JFException {}


}
