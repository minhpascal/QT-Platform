package trash.jforex.examples.strategy.indicators;

import java.util.Arrays;
import java.util.HashSet;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.feed.util.*;
import com.dukascopy.api.indicators.IIndicator;

/**
 * The example strategy shows how to work with an arbitrary feed:
 * - subscribe and print the latest completed feed data
 * - retrieve history by feed descriptor
 * - calculate an indicator by feed descriptor
 * - open a chart of the feed
 * 
 */
public class FeedEmaMacdMinMax implements IStrategy, IFeedListener {

    private IConsole console;
    private IIndicators indicators;

    @Configurable("")
    public IFeedDescriptor feedDescriptor = new RenkoFeedDescriptor(Instrument.EURUSD, PriceRange.ONE_PIP, OfferSide.BID);
    @Configurable("Feed data count for indicator calculation")
    public int dataCount = 10;
    @Configurable("")
    public int minMaxTimePeriod = 5;
    @Configurable("")
    public int emaTimePeriod = 12;
    @Configurable("")
    public int macdSignalPeriod = 9;
    @Configurable("")
    public int macdSlowPeriod = 26;
    @Configurable("")
    public int macdFastPeriod = 12;
    @Configurable("open chart")
    public boolean openChart = false;
    
    private static int MAX = 0, MIN = 1;
   

    @Override
    public void onStart(IContext context) throws JFException {

        console = context.getConsole();
        indicators = context.getIndicators();

        context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(new Instrument[] { feedDescriptor.getInstrument() })), true);

        if (openChart) {
            context.openChart(feedDescriptor);
        }

        print("subscribe to feed=" + feedDescriptor);
        context.subscribeToFeed(feedDescriptor, this);

    }
    
    @Override
    public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
        console.getInfo().println("completed " + feedData + " of feed: " + feedDescriptor);
        try {            
            //calculate MINMAX by shift     
            double[] minMax = indicators.minMax(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(), minMaxTimePeriod).calculate(1);
            print("Previous %s max=%.5f min=%.5f", feedDescriptor.getDataType(), minMax[MAX], minMax[MIN]);
            
            //calculate EMA by shift  
            double emaPrev = indicators.ema(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(), emaTimePeriod).calculate(1);
            print("Previous %s ema=%.5f", feedDescriptor.getDataType(), emaPrev);

            // calculate EMA for the last 10 feed data
            double[] ema =indicators.ema(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(), emaTimePeriod).calculate(dataCount, feedData.getTime(), 0);
            print("ema output line for the last %s %s: %s", dataCount, feedDescriptor.getDataType(), arrayToString(ema));

            // calculate MACD for the last 10 feed data
            double[][] macd = indicators.macd(feedDescriptor, AppliedPrice.CLOSE, feedDescriptor.getOfferSide(),  macdFastPeriod, macdSlowPeriod, macdSignalPeriod)
                    .calculate(dataCount, feedData.getTime(), 0);

            IIndicator macdIndicator = indicators.getIndicator("MACD");
            for (int i = 0; i < macdIndicator.getIndicatorInfo().getNumberOfOutputs(); i++) {
                String outputName = macdIndicator.getOutputParameterInfo(i).getName();
                print("macd output %s for the last %s %s: %s", outputName, dataCount, feedDescriptor.getDataType(), arrayToString(macd[i]));
            }
        } catch (JFException e) {
            console.getErr().println(e);
            e.printStackTrace();
        }
    }

    private void print(Object o) {
        console.getOut().println(o);
    }

    private void print(String format, Object... args) {
        console.getOut().format(format, args).println();
    }
    
    private static String arrayToString(double[] arr) {
        String str = "";
        for (int r = 0; r < arr.length; r++) {
            str += String.format("[%s] %.5f; ", r, arr[r]);
        }
        return str;
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
    }

}
