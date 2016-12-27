/**
 * 
 */
package trash.jforex.learning.strategies;

import java.util.Set;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.IUserInterface;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.TicksFeedDescriptor;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;

import trash.jforex.indicators.PercentagePriceAvgSpread;

/**
 * JForex test.
 * 
 * @author Miquel Sas
 */
//@Library("JForex-SDK-Library.jar")
public class MyFirstStrategy implements IStrategy {
	private IEngine engine;
	private IConsole console;
	private IHistory history;
	private IContext context;
	private IIndicators indicators;
	private IUserInterface userInterface;
	private IChart chart;

	/**
     * 
     */
	public MyFirstStrategy() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onStart(com.dukascopy.api.IContext)
	 */
	@Override
	public void onStart(IContext context) throws JFException {
		this.engine = context.getEngine();
		this.console = context.getConsole();
		this.history = context.getHistory();
		this.context = context;
		this.indicators = context.getIndicators();
		this.userInterface = context.getUserInterface();

		console.getOut().println("Hello strategy");
		IFeedDescriptor feedDescriptor = new TimePeriodAggregationFeedDescriptor(Instrument.EURUSD,
				Period.FIFTEEN_MINS, OfferSide.ASK, Filter.ALL_FLATS);
		chart = context.openChart(feedDescriptor);
//
//		console.getOut().println(context.getFilesDir());
//		indicators.registerCustomIndicator(TripleEMAIndicator.class);
//		indicators.registerCustomIndicator(SimpleIndicator.class);
//		indicators.registerCustomIndicator(CheckIndicatorInfo.class);
		indicators.registerCustomIndicator(PercentagePriceAvgSpread.class);
		
//		IIndicator indicatorTHREEEMA = indicators.getIndicator("THREEEMA");
//		IIndicator indicatorSIMPLEIND = indicators.getIndicator("SIMPLEIND");
//		IIndicator indicatorCheck = indicators.getIndicator("CHECKIND");
		IIndicator indicatorPerc = indicators.getIndicator("PPOPRICEAVG");
		
		Set<IChart> charts = context.getCharts();
		for (IChart chart : charts) {
//			chart.add(indicatorTHREEEMA);
//			chart.add(indicatorSIMPLEIND);
//			chart.add(indicatorCheck);
			chart.add(indicatorPerc);
		}
		for (Instrument instrument : context.getSubscribedInstruments()) {
			IFeedDescriptor feedDescriptor2 = new TicksFeedDescriptor(instrument);
			feedDescriptor2.setOfferSide(OfferSide.BID);// need to set due to platform requirements
			context.openChart(feedDescriptor2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onTick(com.dukascopy.api.Instrument, com.dukascopy.api.ITick)
	 */
	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onBar(com.dukascopy.api.Instrument, com.dukascopy.api.Period,
	 * com.dukascopy.api.IBar, com.dukascopy.api.IBar)
	 */
	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onMessage(com.dukascopy.api.IMessage)
	 */
	@Override
	public void onMessage(IMessage message) throws JFException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onAccount(com.dukascopy.api.IAccount)
	 */
	@Override
	public void onAccount(IAccount account) throws JFException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dukascopy.api.IStrategy#onStop()
	 */
	@Override
	public void onStop() throws JFException {
		for (IChart c : context.getCharts()) {
			context.closeChart(c);
		}
		if (chart != null)
			context.closeChart(chart);
	}

}
