package trash.jforex.examples.strategy.practices;



import java.util.List;
import java.util.Set;

import com.dukascopy.api.*;
import com.dukascopy.api.ICalendarMessage.Detail;
import com.dukascopy.api.IEngine.OrderCommand;

public class NewsStrategy implements IStrategy {
	private static final String _3_MONTH_MOVING_AVERAGE = "3 Month Moving Average";
	private static final String NATIONAL_ACTIVITY_INDEX = "National Activity Index";
	private static final String IEM_MARKET_COMPOSITE_INDEX = "Market Composite Index";
	private IEngine engine;
	private IConsole console;
	private INewsMessage news;
	private ICalendarMessage calendarNews;
	private int counter;


	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
		console.getOut().println("Started " );
		counter = 0;
	}

	public void onAccount(IAccount account) throws JFException {
	}

	public void onMessage(IMessage message) throws JFException {
		if (!message.getType().equals(IMessage.Type.CALENDAR) && (!message.getType().equals(IMessage.Type.NEWS))) {
			return;
		} 
		if (message.getType().equals(IMessage.Type.CALENDAR)) {
			news = (ICalendarMessage) message;
		} else {
			news = (INewsMessage) message;
		}
		if (message.getType().equals(IMessage.Type.NEWS)) {
			Set<String> currencies = news.getCurrencies();
			for (String currency: currencies) {
				if (currency.equals("EUR")) {
					engine.submitOrder(getLabel(Instrument.EURUSD), Instrument.EURUSD, OrderCommand.BUY , 0.01);
				}
			}
		} else {
			calendarNews = (ICalendarMessage) message;
			List<ICalendarMessage.Detail> calendarNewsDetails = calendarNews.getDetails();

			for (Detail newsDetail : calendarNewsDetails) {
				String actual = newsDetail.getActual();
				String previous = newsDetail.getPrevious();
				
				if ((newsDetail.getDescription() != null) && (newsDetail.getDescription().equals(NATIONAL_ACTIVITY_INDEX) || newsDetail.getDescription().equals(_3_MONTH_MOVING_AVERAGE))) {
					double previousValue = Double.valueOf(previous.trim()).doubleValue();
					OrderCommand command = (previousValue > 0) ? OrderCommand.BUY : OrderCommand.SELL;
					engine.submitOrder(getLabel(Instrument.EURUSD), Instrument.EURUSD, command, 0.01);
				}
					
				if (americanIEPValueReceived(calendarNews, newsDetail, actual, previous)) {
					double actualValue = Double.valueOf(actual.trim()).doubleValue();
					double previousValue = Double.valueOf(previous.trim()).doubleValue();
					OrderCommand command = (actualValue > previousValue) ? OrderCommand.BUY : OrderCommand.SELL;
					engine.submitOrder(getLabel(Instrument.EURUSD), Instrument.EURUSD, command, 0.01);
				}
			}
		}

	}

	private boolean americanIEPValueReceived(ICalendarMessage calendarNews, Detail newsDetail, String actualValue, String previousValue) {
		return (actualValue != null) && (previousValue != null) && calendarNews.getCountry().equals("US") 
				&& newsDetail.getDescription().trim().equals(IEM_MARKET_COMPOSITE_INDEX);
	}

	public void onStop() throws JFException {
		for (IOrder order : engine.getOrders()) {
			engine.getOrder(order.getLabel()).close();
		}
	}

	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}
	

	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

	protected String getLabel(Instrument instrument) {
		String label = instrument.name();
		label = label + (counter++);
		label = label.toUpperCase();
		return label;
	}
	
}