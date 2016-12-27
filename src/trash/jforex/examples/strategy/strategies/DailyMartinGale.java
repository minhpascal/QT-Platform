package trash.jforex.examples.strategy.strategies;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;

public class DailyMartinGale implements IStrategy {
	private IEngine engine;
	private IConsole console;
	private IContext context;
	private IAccount account;
	private SimpleDateFormat sdf;

	private ArrayList<String> orderLabelList	= new ArrayList<String>();

	@Configurable("Currency")
	public Instrument currencyInstrument = Instrument.EURUSD;
	@Configurable("Start rate")
	public double startRate = 1.37;
	@Configurable("Amount")
	public double amount = 0.2;
	@Configurable("Take profit")
	public int takeProfit	= 10;
	@Configurable("Stop Loss")
	public int stopLoss = 5;
	@Configurable("Multiplier")
	public int multiplier = 2;
	@Configurable("Trade start hour GMT")
	public String startTime = "10:00";
	@Configurable("Trade close hour GMT")
	public String endTime = "22:55";
	
	private double amountDelta = 0;
	private int counter = 0;
	String label;

	private double takeProfitPrice;
	private double stopLossPrice;
	private String parsedStartTime;
	private String parsedEndTime;
	
	
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.context = context;
		amountDelta = amount;
		sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		parsedStartTime = startTime.replace(":", "");
		parsedEndTime = endTime.replace(":", "");
	}

	public void onAccount(IAccount account) throws JFException {
		this.account = account;
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
		print(" Stop stragegy");
		for (IOrder order : engine.getOrders()) {
			order.close();
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
		if (instrument.equals(currencyInstrument) && isValidTime(tick.getTime())) {
			monitorOrder(instrument, tick);
		}
	}

	private void monitorOrder(Instrument instrument, ITick tick) throws JFException { 
		double useOfLeverage = account.getUseOfLeverage();
		if ((useOfLeverage < 100) ) {
		    // Create first order
			if (orderLabelList.size() < 1) {
				createFirstOrders(tick, instrument, amountDelta);
			}
			recreateOrderIfClosed(instrument, tick);
		} else {
			print(" Maximum use of Leverage exceed " + useOfLeverage + " strategy stopping");
			context.stop();
		}
	}

	private void recreateOrderIfClosed(Instrument instrument, ITick tick) throws JFException {
		double currentBidPrice = tick.getBid();
		double currentAskPrice = tick.getAsk();
		IOrder prevOrder = engine.getOrder(orderLabelList.get(0));
		
		// If any order was created during strategy run
		if (prevOrder != null) {
			if (prevOrder.isLong()) {
				recreateOrderAfterBuy(instrument, tick, currentAskPrice, prevOrder);
			} else {
				recreateOrderAfterSell(instrument, tick, currentBidPrice, prevOrder);
			}
		}
	}

	private void recreateOrderAfterSell(Instrument instrument, ITick tick, double currentPrice, IOrder order) throws JFException {
		takeProfitPrice = order.getOpenPrice() - (instrument.getPipValue() * takeProfit);
		stopLossPrice = order.getOpenPrice() + (instrument.getPipValue() * stopLoss);

		if ((currentPrice < takeProfitPrice)) {
			print(String.valueOf(currentPrice > takeProfit));
			print( "Current price: " + currentPrice + "; Take profit: " + takeProfitPrice + " is triggered, next order amount(default): " + amount);
			deleteOrderFromList(instrument, tick, order.getLabel());
			amountDelta = amount;
			createOrders(tick, instrument, amountDelta, IEngine.OrderCommand.SELL);
		} else if (currentPrice >= stopLossPrice) {
			print( "Amount " + amountDelta + " multiplier: " + multiplier);
			print("Current price: " + currentPrice + " ;Stop Loss: " + stopLossPrice + " is triggered, next order amount: " + amountDelta);
			deleteOrderFromList(instrument, tick, order.getLabel());
			amountDelta *= multiplier;
			createOrders(tick, instrument, amountDelta, IEngine.OrderCommand.SELL);
		}
	}

	private void recreateOrderAfterBuy(Instrument instrument, ITick tick, double currentPrice, IOrder order) throws JFException {
		takeProfitPrice = order.getOpenPrice() + (instrument.getPipValue() * takeProfit);
		stopLossPrice = order.getOpenPrice() - (instrument.getPipValue() * stopLoss);

		if ((currentPrice > takeProfitPrice)) {
			deleteOrderFromList(instrument, tick, order.getLabel());
			amountDelta = amount;
			print( "Current price: " + currentPrice + "; Take profit: " + takeProfitPrice + " is triggered, next order amount(default): " + amountDelta);
			createOrders(tick, instrument, amountDelta, IEngine.OrderCommand.BUY);
		} else if (currentPrice <= stopLossPrice) {
			deleteOrderFromList(instrument, tick, order.getLabel());
			amountDelta *= multiplier;
			print("Current price: " + currentPrice + " ;Stop Loss: " + stopLossPrice + " is triggered, next order amount: " + amountDelta);
			createOrders(tick, instrument, amountDelta, IEngine.OrderCommand.BUY);
		}
	}

	private void createFirstOrders(ITick tick, Instrument instrument, double orderAmount ) throws JFException {
		double currentBidPrice = tick.getBid();
		double currentAskPrice = tick.getAsk();
		if ((currentBidPrice < startRate )) {
			createOrders(tick, instrument, orderAmount, IEngine.OrderCommand.BUY);
		} else if ((currentAskPrice >= startRate)) {
			createOrders(tick, instrument, orderAmount, IEngine.OrderCommand.SELL);
		}
	}

	private void deleteOrderFromList(Instrument instrument, ITick tick, String orderLabel) throws JFException {
		engine.getOrder(orderLabel).close();
		orderLabelList.remove(orderLabel);
	}
	
	private void createOrders(ITick tick, Instrument instrument, double orderAmount, OrderCommand orderCommand) throws JFException {
		String label = getLabel(instrument);
		engine.submitOrder(label, instrument, orderCommand, orderAmount, tick.getBid());
		print("Created order " + label + " price " + tick.getBid() + " amount: " + orderAmount);
		orderLabelList.add(label);
	}

	protected String getLabel(Instrument instrument) {
		String label = instrument.name();
		label = label + (counter ++);
		label = label.toUpperCase();
		return label;
	}
	
	/**
	 * 
	 * @param tickTime market tick time in milliseconds
	 * @return
	 */
	private boolean isValidTime(long tickTime) {
		
		String formattedTickTime = sdf.format(tickTime); 
		formattedTickTime = formattedTickTime.replace(":", "");
		
		int tickTimeValue = Integer.parseInt(formattedTickTime);
		int startTimeValue = Integer.parseInt(parsedStartTime);
		int endTimeValue = Integer.parseInt(parsedEndTime);
		
		if (startTimeValue < endTimeValue){
			if ((tickTimeValue > startTimeValue) && (tickTimeValue < endTimeValue)){
				return true;
			}
		// Else swap time range and calculate valid time 
		} else {
			int tmpTimeValue = startTimeValue;
			startTimeValue = endTimeValue;
			endTimeValue = tmpTimeValue;
			if ((tickTimeValue < startTimeValue) || (tickTimeValue >= endTimeValue)){
				return true;
			}
		}
		return false;
	}
	private void print(String message) {
		console.getOut().println(message);
	}
}
