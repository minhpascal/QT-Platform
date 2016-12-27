package trash.jforex.examples.strategy.strategies;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;

public class SMASimpleStrategy implements IStrategy {
	private IEngine engine;
	private IHistory history;
	private IIndicators indicators;
	private int counter = 0;

	@Configurable("Instrument")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("Period")
	public Period selectedPeriod = Period.FIFTEEN_MINS;
	@Configurable("SMA filter")
	public Filter indicatorFilter = Filter.NO_FILTER;
	@Configurable("Amount")
	public double amount = 0.02;
	@Configurable("Stop loss")
	public int stopLossPips = 10;
	@Configurable("Take profit")
	public int takeProfitPips = 90;

	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.history = context.getHistory();
		this.indicators = context.getIndicators();
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
	}

	public void onStop() throws JFException {
		//close all orders
		for (IOrder order : engine.getOrders()) {
			engine.getOrder(order.getLabel()).close();
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {

		if (!instrument.equals(Instrument.EURUSD) || !period.equals(Period.FIFTEEN_MINS))
			return;

		IBar prevBar = history.getBar(instrument, selectedPeriod, OfferSide.BID, 1);

		int smaTimePeriod = 50;
		int candlesBefore = 2;
		int candlesAfter = 0;

		double sma = indicators.sma(instrument, selectedPeriod, OfferSide.BID, AppliedPrice.CLOSE, smaTimePeriod, indicatorFilter,
				candlesBefore, prevBar.getTime(), candlesAfter)[0];

		// SMA crossed previous green candle
		if (prevBar.getOpen() < sma && prevBar.getClose() > sma) {
			submitOrder(OrderCommand.BUY);
		}
		// SMA crossed previous red candle
		if (prevBar.getOpen() > sma && prevBar.getClose() < sma) {
			submitOrder(OrderCommand.SELL);
		}
	}

	private IOrder submitOrder(OrderCommand orderCmd) throws JFException {

		double stopLossPrice, takeProfitPrice;

		// Calculating stop loss and take profit prices
		if (orderCmd == OrderCommand.BUY) {
			stopLossPrice = history.getLastTick(this.instrument).getBid() - getPipPrice(this.stopLossPips);
			takeProfitPrice = history.getLastTick(this.instrument).getBid() + getPipPrice(this.takeProfitPips);
		} else {
			stopLossPrice = history.getLastTick(this.instrument).getAsk() + getPipPrice(this.stopLossPips);
			takeProfitPrice = history.getLastTick(this.instrument).getAsk() - getPipPrice(this.takeProfitPips);
		}

		// Submitting an order for the specified instrument at the current market price
		return engine.submitOrder(getLabel(instrument), this.instrument, orderCmd, this.amount, 0, 20, stopLossPrice, takeProfitPrice);
	}

	protected String getLabel(Instrument instrument) {
		String label = instrument.name();
		label = label + (counter++);
		label = label.toUpperCase();
		return label;
	}

	private double getPipPrice(int pips) {
		return pips * this.instrument.getPipValue();
	}

}
