package trash.jforex.examples.strategy.practices;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

/**
 * The strategy demonstrates how to use an external library in a strategy.
 * Namely it uses the commons-lang3-3.0.1.jar library, which can be downloaded here:
 * http://mvnrepository.com/artifact/org.apache.commons/commons-lang3/3.0.1
 * 
 * The strategy itself prints the last 10 values and reversed values of sma and as 
 * well finds min/max values.
 * 
 * Before running the strategy place the commons-lang3-3.0.1.jar in {@link IContext#getFilesDir()} directory
 * 
 */
@Library("commons-lang3-3.0.1.jar")
public class SmaFlipMinMax implements IStrategy {

    private IConsole console;

    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        long time = context.getHistory().getBar(Instrument.EURUSD, Period.TEN_MINS, OfferSide.BID, 1).getTime();
        double[] sma = context.getIndicators().sma(Instrument.EURUSD, Period.TEN_MINS, OfferSide.BID, AppliedPrice.CLOSE, 12, Filter.NO_FILTER, 10, time, 0);
        double[] smaReversed = ArrayUtils.clone(sma);
        ArrayUtils.reverse(smaReversed);
        double min = NumberUtils.min(sma);
        double max = NumberUtils.max(sma);
        console.getOut().format("sma for last 10 candles \nasc: %s \ndesc:%s \nmax=%.5f min=%.5f", toString(sma), toString(smaReversed), max, min).println();
        context.stop();
    }

    private static String toString(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < arr.length; r++) {
            sb.append(String.format("[%s] %.5f; ", r, arr[r]));
        }
        return sb.toString();
    }

    @Override
    public void onStop() throws JFException {    }
    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
    @Override
    public void onMessage(IMessage message) throws JFException {}
    @Override
    public void onAccount(IAccount account) throws JFException {}

}
