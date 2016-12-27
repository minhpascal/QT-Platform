package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IChartObject;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.drawings.IOhlcChartObject;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.util.DateUtils;

/**
 * The strategy calculates MACD and EMA values over the last candle stick bar
 * and prints the result to the strategy console.
 * 
 * Also the strategy plots the indicators on chart, if the
 * chart configuration matches the feed data. The strategy also adds
 * OHLC informer with indicator showing option
 *
 */
public class PlotEmaMacdWithOhlc implements IStrategy {
    private IConsole console;
    private IIndicators indicators;
    
    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("Period")
    public Period period = Period.TEN_SECS;
    @Configurable("Filter")
    public Filter filter = Filter.NO_FILTER;
    @Configurable("side")
    public OfferSide side = OfferSide.BID;
    @Configurable("emaTimePeriod")
    public int emaTimePeriod = 12;
    @Configurable("macdSignalPeriod")
    public int macdSignalPeriod = 9;
    @Configurable("macdSlowPeriod")
    public int macdSlowPeriod = 26;
    @Configurable("macdFastPeriod")
    public int macdFastPeriod = 12;
    
   
    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        this.indicators = context.getIndicators();
        
        IChart chart = null;
        for(IChart c : context.getCharts(instrument)){
            if(c.getSelectedOfferSide() == this.side
                    && c.getSelectedPeriod() == this.period
                    && c.getFilter() == this.filter){
                chart = c;
                break;
            }
            if(c.getFilter() != this.filter){
                console.getErr().println("Filter dismatch! Change in platform settings the filter to the same one that the strategy is using!");
                context.stop();
            }
        }
        if(chart == null){
            chart = context.openChart(new TimePeriodAggregationFeedDescriptor(instrument, period, side, filter));
        }
        
        chart.add(indicators.getIndicator("EMA"), new Object[] { emaTimePeriod });
        chart.add(indicators.getIndicator("MACD"), new Object[] { macdFastPeriod, macdSlowPeriod, macdSignalPeriod });

        IOhlcChartObject ohlc = null;
        for (IChartObject obj : chart.getAll()) {
            if (obj instanceof IOhlcChartObject) {
                ohlc = (IOhlcChartObject) obj;
            }
        }
        if (ohlc == null) {
            ohlc = chart.getChartObjectFactory().createOhlcInformer();
            chart.add(ohlc);
        }
        ohlc.setShowIndicatorInfo(true);
        
        //calculate on the previous bar over candle interval such that the filters get used
        long time = context.getHistory().getBar(instrument, period, side, 1).getTime();
        double[][] macd = indicators.macd(instrument, period, side, AppliedPrice.CLOSE, macdFastPeriod, macdSlowPeriod, macdSignalPeriod, filter, 1, time, 0);
        double[] ema = indicators.ema(instrument, period, side, AppliedPrice.CLOSE, emaTimePeriod, filter, 1, time, 0);
        
        console.getOut().format("%s - ema=%s, macd=%s (by candle interval)", DateUtils.format(time), arrayToString(ema), arrayToString(macd)).println();
    }
    
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if(instrument != this.instrument || period != this.period){
            return;
        }
        //calculate by shift (Filter.NO_FILTER gets used at all times) for the bar that just finished
        double[] macd = indicators.macd(instrument, period, side, AppliedPrice.CLOSE, macdFastPeriod, macdSlowPeriod, macdSignalPeriod, 1);
        double ema = indicators.ema(instrument, period, side, AppliedPrice.CLOSE, emaTimePeriod, 1);
        
        console.getOut().format("%s - ema=%.5f, macd=%s (by shift)", DateUtils.format(bidBar.getTime()), ema, arrayToString(macd)).println();
    }

    public void onAccount(IAccount account) throws JFException {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onStop() throws JFException {
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }
    


    private static String arrayToString(double[] arr) {
        StringBuilder str = new StringBuilder();
        for (int r = 0; r < arr.length; r++) {
            str.append(String.format("[%s] %.5f; ", r, arr[r]));
        }
        return str.toString();
    }
    
    private static String arrayToString(double[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < arr.length; r++) {
            for (int c = 0; c < arr[r].length; c++) {
                sb.append(String.format("[%s][%s] %.5f; ",r, c, arr[r][c]));
            }
            sb.append("; ");
        }
        return sb.toString();
    }
    
}