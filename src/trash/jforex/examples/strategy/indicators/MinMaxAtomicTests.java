package trash.jforex.examples.strategy.indicators;



import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.RequiresFullAccess;

/**
 * The following strategy calculates MinMax values by:
 * - shift
 * - candle interval
 * - time interval
 * 
 * The strategy uses two approaches for indicator calculation:
 * - MinMax indicator specific methods
 * - universal methods
 * 
 * The strategy logs the results such that user can check that 
 * the corresponding ones match by using different approaches.
 * 
 * For the sake of atomicity, each test on purpose uses 
 * as little as possible global variables.
 *
 */
@RequiresFullAccess
public class MinMaxAtomicTests implements IStrategy {
	
	private IConsole console;
	private IHistory history;
	private IIndicators indicators;

	@Override
	public void onStart(IContext context) throws JFException {
		console = context.getConsole();
		history = context.getHistory();
		indicators = context.getIndicators();
        context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
		
		testShift();
		testTimeInterval();
		testTimeIntervalTicks();
		testCandleInterval();
		testShiftUni();
		testTimeIntervalUni();
		testCandleIntervalUni();		
	}
	
	private void testShift() throws JFException {
		int shift = 1;
		double[] minMax = indicators.minMax(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, shift);
		console.getOut().println(String.format("Previous bar min=%1$.5f max=%2$.5f", minMax[0], minMax[1]));
	}

	private void testTimeInterval() throws JFException {
		long from = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 3).getTime();
		long to = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
		double[][] minMax = indicators.minMax(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, from, to);
		int last = minMax[0].length - 1;
		console.getOut().println(String.format(
		    "Current bar min=%1$.5f max=%2$.5f; Previous bar min=%3$.5f max=%4$.5f; Third to last bar min=%5$.5f max=%6$.5f",
		    minMax[0][last], minMax[1][last], minMax[0][last - 1], minMax[1][last - 1], minMax[0][0], minMax[1][0]));
	}
	
	private void testTimeIntervalTicks() throws JFException {
		long to = history.getTimeOfLastTick(Instrument.EURUSD);  
		long from = to - 1000 * 4; //4 secs before		
		double[][] minMax = indicators.minMax(Instrument.EURUSD, Period.TICK, OfferSide.ASK, AppliedPrice.CLOSE, 5, from, to);
		int last = minMax[0].length - 1;
		console.getOut().println(String.format(
		    "Current tick min=%1$.5f max=%2$.5f; Previous tick min=%3$.5f max=%4$.5f; Third to last tick min=%5$.5f max=%6$.5f",
		    minMax[0][last], minMax[1][last], minMax[0][last - 1], minMax[1][last - 1], minMax[0][0], minMax[1][0]));
	}

	private void testCandleInterval() throws JFException {
		int candlesBefore = 4, candlesAfter = 0; 
		long currBarTime = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
		double[][] minMax = indicators.minMax(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, Filter.NO_FILTER,
				candlesBefore, currBarTime, candlesAfter);
		int last = minMax[0].length - 1;
		console.getOut().println(String.format(
		    "Current bar min=%1$.5f max=%2$.5f; Previous bar min=%3$.5f max=%4$.5f; Third to last bar min=%5$.5f max=%6$.5f",
		    minMax[0][last], minMax[1][last], minMax[0][last - 1], minMax[1][last - 1], minMax[0][0], minMax[1][0]));
	}

	private void testShiftUni() throws JFException {
		int shift = 1;
		Object[] minMaxUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "MINMAX",
				new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, shift);
		double[] minMax = {(Double) minMaxUni[0], (Double) minMaxUni[1]};
		console.getOut().println(String.format("Previous bar min=%1$.5f max=%2$.5f", minMax[0], minMax[1]));
	}
	
	private void testTimeIntervalUni() throws JFException {
		long from = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 3).getTime();
		long to = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
		Object[] minMaxUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "MINMAX",
				new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, from, to);
		double[][] minMax = {(double[]) minMaxUni[0], (double[]) minMaxUni[1]};
		int last = minMax[0].length - 1;
		console.getOut().println(String.format(
		    "Current bar min=%1$.5f max=%2$.5f; Previous bar min=%3$.5f max=%4$.5f; Third to last bar min=%5$.5f max=%6$.5f",
		    minMax[0][last], minMax[1][last], minMax[0][last - 1], minMax[1][last - 1], minMax[0][0], minMax[1][0]));
		

	}
	
	private void testCandleIntervalUni() throws JFException {
		int candlesBefore = 4, candlesAfter = 0;
		long currBarTime = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
		Object[] minMaxUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "MINMAX",
				new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, Filter.NO_FILTER, candlesBefore, currBarTime, candlesAfter);
		double[][] minMax = {(double[]) minMaxUni[0], (double[]) minMaxUni[1]};
		int last = minMax[0].length - 1;
		console.getOut().println(String.format(
		    "Current bar min=%1$.5f max=%2$.5f; Previous bar min=%3$.5f max=%4$.5f; Third to last bar min=%5$.5f max=%6$.5f",
		    minMax[0][last], minMax[1][last], minMax[0][last - 1], minMax[1][last - 1], minMax[0][0], minMax[1][0]));
	}
	
	@Override
	public void onTick(Instrument instrument, ITick tick) throws JFException {}

	@Override
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

	@Override
	public void onMessage(IMessage message) throws JFException {}

	@Override
	public void onAccount(IAccount account) throws JFException {}

	@Override
	public void onStop() throws JFException {}

}
