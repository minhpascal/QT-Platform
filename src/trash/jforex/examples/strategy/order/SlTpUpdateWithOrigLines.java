package trash.jforex.examples.strategy.order;

import java.awt.Color;
import java.text.DecimalFormat;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.drawings.IHorizontalLineChartObject;

/**
 * The strategy creates an order on its start and: 
 * 1) On every tick updates the stop loss and take profit levels to keep them 
 *    in a particular price distance  from the current market price.
 * 2) Draws lines for original SL and TP levels.
 * 3) Prints a message once the original SL and TP levels get broken. 
 */
public class SlTpUpdateWithOrigLines implements IStrategy {

	@Configurable("SL and TP price distance")
	public double priceDistance = 0.0003;
	@Configurable("Instrument")
	public Instrument instrument = Instrument.EURUSD;
	
	private IEngine engine;
	private IOrder order;
	private IHistory history;
	private IChart chart;
	private IContext context;
	private IConsole console;
	
	private IHorizontalLineChartObject slLine, tpLine;
	private boolean origTpBroken, origSlBroken;
	
	long lastTickTime = 0;
	DecimalFormat df = new DecimalFormat("0.00000");
	
	
	@Override
	public void onStart(IContext context) throws JFException {
		this.context= context;
		engine= context.getEngine();
		history = context.getHistory();
		console = context.getConsole();
        context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
		chart = context.getChart(instrument);
		
		double lastPrice = history.getLastTick(instrument).getBid();
		lastTickTime = history.getLastTick(instrument).getTime();

		double slPrice = lastPrice - priceDistance;
		double tpPrice = lastPrice + priceDistance;
		order = engine.submitOrder("order1", instrument, OrderCommand.BUY, 0.001, 0, 5, slPrice, tpPrice);
		
		//create lines for original SL and TP levels
		slLine = chart.getChartObjectFactory().createHorizontalLine();
		tpLine = chart.getChartObjectFactory().createHorizontalLine();
		slLine.setPrice(0, slPrice);
		tpLine.setPrice(0, tpPrice);
		slLine.setColor(Color.RED);
		tpLine.setColor(Color.GREEN);
		slLine.setText("Original SL");
		tpLine.setText("Original TP");
		chart.addToMainChartUnlocked(slLine);
		chart.addToMainChartUnlocked(tpLine);
		
	}

	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {
		//we can't update SL or TP more frequently than once per second
		if(instrument != this.instrument || tick.getTime() - lastTickTime < 1000){
			return;
		}
		//bid price has broke either SL or TP - the order has been closed. Or the order has been closed manually;
		if(!engine.getOrders().contains(order)){
			//stop the strategy
			context.stop();
			return;
		}
		
		order.setStopLossPrice(tick.getBid() - priceDistance);
		order.setTakeProfitPrice(tick.getBid() + priceDistance);
		
		if(!origSlBroken && tick.getBid() < slLine.getPrice(0)){
			console.getOut().println("Original Stop Loss price level broken, current price: " + df.format(tick.getBid()));
			origSlBroken = true;
		}
		if(!origTpBroken && tick.getBid() > tpLine.getPrice(0)){
			console.getOut().println("Original Take Profit price level broken, current price: " + df.format(tick.getBid()));
			origTpBroken = true;
		}
		
		lastTickTime = tick.getTime();
	}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

	@Override
	public void onMessage(IMessage message) throws JFException {}

	@Override
	public void onAccount(IAccount account) throws JFException {}

	@Override
	public void onStop() throws JFException {
		chart.removeAll();
		for(IOrder o:engine.getOrders()){
			o.close();
		}
	}

}
