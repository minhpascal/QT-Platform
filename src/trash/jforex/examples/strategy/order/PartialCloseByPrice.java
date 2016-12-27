package trash.jforex.examples.strategy.order;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy on its start opens an order and twice attempts
 * to partially close the order by a price.
 * Eventually the order gets closed in full without a price condition
 *
 */
public class PartialCloseByPrice implements IStrategy {
	
	private IOrder order;
	private IEngine engine;
	private IConsole console;
	private IHistory history;
	private Instrument instrument = Instrument.EURUSD;
	

	public void onStart(IContext context) throws JFException {
		engine = context.getEngine();
		console = context.getConsole();
		history = context.getHistory();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
		
		double lastBid = history.getLastTick(instrument).getBid();
		
		order = engine.submitOrder("orderPartialClose", instrument, OrderCommand.BUY, 1);
		order.waitForUpdate(2000, IOrder.State.FILLED); 
		
		//should fail with ORDER_CLOSE_REJECTED, since it is unlikely to have liquidity at such price
		order.close(0.3, lastBid + 100 * instrument.getPipValue(), 5);
		order.waitForUpdate(2000); //wait for order change
		
		//should succeed with ORDER_CLOSE_OK, since jforex allows positive slippage 
		order.close(0.3, lastBid - 100 * instrument.getPipValue(), 5);
		order.waitForUpdate(2000); //wait for order change
		
		order.close(); //close in full
		context.stop();//stop the strategy
	}

	public void onMessage(IMessage message) throws JFException {
		console.getOut().format("%s\n%s closePrice=%.5f, amount=%.3f, requestedAmount=%.3f, closeTime=%s, state=%s", 
				message, order.getLabel(), order.getClosePrice(), order.getAmount(), order.getRequestedAmount(), 
					DateUtils.format(order.getCloseTime()), order.getState()).println();

	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {}
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
	public void onAccount(IAccount account) throws JFException {}
	public void onStop() throws JFException {}

}
