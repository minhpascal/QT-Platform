package trash.jforex.examples.strategy.strategies;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.MaType;

public class Stochastic implements IStrategy {
	private IEngine engine;
	private IIndicators indicators;
	private IConsole console;

	@Configurable("Amount")
	public double amount = 0.2;
	@Configurable("Period")
	public Period fixedPeriod = Period.ONE_MIN;
    @Configurable("Instrument")
    public Instrument selectedInstrument = Instrument.EURUSD;

	private OfferSide side = OfferSide.BID;
	private int fastKPeriod = 5;
	private MaType slowDMaType =  MaType.SMA;
	private int slowKPeriod = 3;
	private MaType slowKMaType =  MaType.SMA;
	private int slowDPeriod = 3;
	private int shift = 0;
	private int counter = 0;


	public void onStart(IContext context) throws JFException {
		engine = context.getEngine();
		indicators = context.getIndicators();
		console = context.getConsole();
		context.getChart(selectedInstrument).addIndicator(indicators.getIndicator("STOCH"));
	}

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
		if (selectedInstrument.equals(instrument) && period.equals(fixedPeriod)) {
			double[] stochastic = indicators.stoch(instrument, period, side, fastKPeriod, slowKPeriod , slowKMaType, 
					slowDPeriod, slowDMaType, shift );
			createOrderOnStochastic(instrument, bidBar, stochastic);
		}
	}

	private void createOrderOnStochastic(Instrument instrument, IBar bidBar, double[] stochastic) throws JFException {
		OrderCommand orderCommand;
		if ((stochastic[0] >= 80) && (stochastic[1] >= 80)) {
			orderCommand = OrderCommand.SELL;
			closeOppositeIfExist(orderCommand);
			createOrder(instrument, bidBar, orderCommand);
		} else if ((stochastic[0] <= 20) && (stochastic[1] <= 20) ) {
			orderCommand = OrderCommand.BUY;
            closeOppositeIfExist(orderCommand);
			createOrder(instrument, bidBar, orderCommand);
		}
	}

	private void closeOppositeIfExist(OrderCommand command) throws JFException {
		if (engine.getOrders().size() == 0) {
			return;
		}
		for (IOrder order: engine.getOrders(selectedInstrument)) {
			if (!order.getOrderCommand().equals(command)) {
				order.close();
			}
		}
	}

	private void createOrder(Instrument instrument, IBar bidBar, OrderCommand orderCommand) throws JFException {
	    if (engine.getOrders().size() > 0) {
	        return;
	    }
	    engine.submitOrder(getLabel(instrument), instrument, orderCommand, amount);
	}
	
	

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {

	}

	public void onStop() throws JFException {
		for (IOrder order : engine.getOrders(selectedInstrument)) {
			order.close();
		}
	}
	
	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	public void print(String string) {
		console.getOut().println(string);
	}
	
	protected String getLabel(Instrument instrument) {
		return (instrument.name() + (counter ++)).toUpperCase();
	}
}
