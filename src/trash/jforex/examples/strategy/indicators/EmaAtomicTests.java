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

/**
 * The following strategy calculates Ema values by:
 * - shift
 * - candle interval
 * - time interval
 * 
 * The strategy uses two approaches for indicator calculation:
 * - Ema indicator specific methods
 * - universal methods
 * 
 * The strategy logs the results such that user can check that 
 * the corresponding ones match by using different approaches.
 * 
 * For the sake of atomicity, each test on purpose uses 
 * as little as possible global variables.
 *
 */
public class EmaAtomicTests implements IStrategy {
    
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
        double ema = indicators.ema(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, shift);
        console.getOut().format("Previous bar ema=%.5f", ema).println();
    }

    private void testTimeInterval() throws JFException {
        long from = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 3).getTime();
        long to = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
        double[] ema = indicators.ema(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, from, to);
        int last = ema.length - 1;
        console.getOut().format("Current bar ema=%.5f; Previous bar ema=%.5f; Third to last bar ema=%.5f",
            ema[last], ema[last - 1], ema[0]).println();
    }
    
    private void testTimeIntervalTicks() throws JFException {  
        long to = history.getTimeOfLastTick(Instrument.EURUSD);  
        long from = to - 1000 * 4; //4 secs before      
        double[] ema = indicators.ema(Instrument.EURUSD, Period.TICK, OfferSide.ASK, AppliedPrice.CLOSE, 5, from, to);
        int last = ema.length - 1;
        console.getOut().format("Current tick ema=%.5f; Previous tick ema=%.5f; Third to last tick ema=%.5f",
            ema[last], ema[last - 1], ema[0]).println();
    }

    private void testCandleInterval() throws JFException {
        int candlesBefore = 4, candlesAfter = 0; 
        long currBarTime = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
        double[] ema = indicators.ema(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, AppliedPrice.CLOSE, 5, Filter.NO_FILTER,
                candlesBefore, currBarTime, candlesAfter);
        int last = ema.length - 1;
        console.getOut().format("Current bar ema=%.5f; Previous bar ema=%.5f; Third to last bar ema=%.5f",
            ema[last], ema[last - 1], ema[0]).println();
    }

    private void testShiftUni() throws JFException {
        int shift = 1;
        Object[] emaUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "EMA",
                new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, shift);
        double ema = (Double) emaUni[0];
        console.getOut().format("Previous bar ema=%.5f", ema).println();
    }
    
    private void testTimeIntervalUni() throws JFException {
        long from = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 3).getTime();
        long to = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
        Object[] emaUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "EMA",
                new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, from, to);
        double[] ema  = (double[]) emaUni[0];
        int last = ema.length - 1;
        console.getOut().format("Current bar ema=%.5f; Previous bar ema=%.5f; Third to last bar ema=%.5f",
                ema[last], ema[last - 1], ema[0]).println();    
    }
    
    private void testCandleIntervalUni() throws JFException {
        int candlesBefore = 4, candlesAfter = 0;
        long currBarTime = history.getBar(Instrument.EURUSD, Period.ONE_MIN, OfferSide.ASK, 0).getTime();
        Object[] emaUni = indicators.calculateIndicator(Instrument.EURUSD, Period.ONE_MIN, new OfferSide[] { OfferSide.ASK }, "EMA",
                new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { 5 }, Filter.NO_FILTER, candlesBefore, currBarTime, candlesAfter);
        double[] ema  = (double[]) emaUni[0];
        int last = ema.length - 1;
        console.getOut().format("Current bar ema=%.5f; Previous bar ema=%.5f; Third to last bar ema=%.5f",
                ema[last], ema[last - 1], ema[0]).println();    
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
