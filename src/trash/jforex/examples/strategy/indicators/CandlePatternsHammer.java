package trash.jforex.examples.strategy.indicators;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IFeedListener;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IndicatorInfo;

/**
 * The strategy demonstrates how to use hammer candlestick patter indicator:
 * - On its start (i.e. in the onStart method) finds hammer pattern occurrences 
 *   over the last 100 candlesticks and prints to console the latest occurrence by shift.
 * - On every candlestick (i.e. in the onBar method) checks if it is of hammer pattern.
 * Also the strategy plots the indicator on chart.
 *
 */

public class CandlePatternsHammer implements IStrategy, IFeedListener {
    
    @Configurable("")
    public IFeedDescriptor feedDescriptor =   
            new TimePeriodAggregationFeedDescriptor(
                    Instrument.EURUSD, 
                    Period.TEN_MINS, 
                    OfferSide.BID, 
                    Filter.NO_FILTER
            );
    @Configurable("")
    public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
    @Configurable("")
    public int candleCount = 100;
    @Configurable("PlotterOld on chart?")
    public boolean plotOnChart = true;
    
    private String indName = "CDLHAMMER";
    
    private IIndicators indicators;
    private IConsole console;
    private IHistory history;
    private IContext context;
    private IChart chart;    

    @Override
    public void onStart(IContext context) throws JFException {
        indicators = context.getIndicators();
        console = context.getConsole();
        history = context.getHistory();
        chart = context.getChart(feedDescriptor.getInstrument());
        this.context = context;
                
        int candlesBefore = candleCount, candlesAfter = 0;
        long currBarTime = history.getFeedData(feedDescriptor, 0).getTime();
        
        IIndicator indicator = context.getIndicators().getIndicator(indName);

        Object[] patternUni = indicators.calculateIndicator(feedDescriptor, new OfferSide[] { feedDescriptor.getOfferSide() }, indName,
                new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] { }, candlesBefore, currBarTime, candlesAfter);

        //all candle patterns have just one output - we're good with 1-dimensional array
        int[] values = (int[]) patternUni[0];
        Set<Integer> occurrences = new LinkedHashSet<Integer>();

        for(int i=0; i < values.length; i++){
            int shift = values.length - 1 - i;
            if(values[i] != 0){
                occurrences.add(shift);
            }
        }
        int lastOccurrence = occurrences.isEmpty() 
            ? -1 
            : Collections.min(occurrences);
        
        console.getOut().format("%s pattern occurances over last %s bars=%s; last occurrence shift=%s; all occurences: %s",
                indicator.getIndicatorInfo().getTitle(), candleCount,occurrences.size(), lastOccurrence, occurrences.toString()
                ).println();            
        
        if (plotOnChart && chart != null) {
            if(chart == null){
                console.getWarn().println("can't add to chart - chart is null!");
                return;
            }
            chart.add(indicator);
        }

    }
    
    @Override
    public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {

        try {
            IIndicator indicator = context.getIndicators().getIndicator(indName);
            IndicatorInfo info = indicator.getIndicatorInfo();

            // shift of just finished feed element
            int shift = 1;

            Object[] patternUni = indicators.calculateIndicator(feedDescriptor, new OfferSide[] { feedDescriptor.getOfferSide() }, indName,
                    new AppliedPrice[] { IIndicators.AppliedPrice.CLOSE }, new Object[] {}, shift);

            // all candle patterns have just one output - we're good with
            // 1-dimensional array
            int patternValue = (Integer) patternUni[0];

            if (patternValue != 0) {
                console.getOut().format("%s pattern of value %s occurred at feed data: %s", info.getTitle(), patternValue, feedData).println();
            }
        } catch (JFException e) {
            e.printStackTrace();
        }
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
