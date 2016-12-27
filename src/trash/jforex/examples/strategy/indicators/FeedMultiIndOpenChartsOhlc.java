package trash.jforex.examples.strategy.indicators;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.dukascopy.api.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.drawings.IOhlcChartObject;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.feed.util.*;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.OutputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * The following strategy demonstrates how one can bind an arbitrary set of indicators
 * with corresponding feed descriptors, such that each indicator with its feed descriptor 
 * can be used both for indicator calculation and indicator plotting on the chart.
 *
 */

public class FeedMultiIndOpenChartsOhlc implements IStrategy {


    private IConsole console;
    private IIndicators indicators;
    private IContext context;

    @Configurable("")
    public Instrument instrument = Instrument.EURUSD;

    class IndDataAndFeed{
        
        private IFeedDescriptor feedDescriptor;
        private String indicatorName;
        private Object[] optionalInputs;
        private int outputIndex;
        private IIndicator indicator;
        private IChart chart;
        
        public IndDataAndFeed(String indicatorName, Object[] optionalInputs, int outputIndex, IFeedDescriptor feedDescriptor) {
            this.feedDescriptor = feedDescriptor;
            this.indicatorName = indicatorName;
            this.optionalInputs = optionalInputs;
            this.outputIndex = outputIndex;
        }
        
        public void openChartAddIndicator(){
            for(IChart openedChart : context.getCharts(feedDescriptor.getInstrument())){
                IFeedDescriptor chartFeed = openedChart.getFeedDescriptor();
                if(chartFeed.getPeriod() == feedDescriptor.getPeriod() && chartFeed.getOfferSide() == feedDescriptor.getOfferSide()){
                    chart = openedChart;
                }
            }
            if(chart == null){
                chart = context.openChart(feedDescriptor);
            }
            if(chart.getFeedDescriptor().getFilter() != feedDescriptor.getFilter()){
                console.getErr().println("Chart filter " + chart.getFeedDescriptor().getFilter() + " does not match indicator feed filter " 
                        +  feedDescriptor.getFilter() + " please adjust the platform settings");
                context.stop();
            }
            indicator = indicators.getIndicator(indicatorName);
            
            int outputCount = indicator.getIndicatorInfo().getNumberOfOutputs();
            Color[] colors = new Color[outputCount];
            DrawingStyle[] styles = new DrawingStyle[outputCount];
            int[] widths = new int[outputCount];
            for(int outIdx = 0; outIdx< outputCount; outIdx++){
                OutputParameterInfo outInfo = indicator.getOutputParameterInfo(outIdx);
                if(outInfo == null){
                    console.getErr().println(indicatorName + " "  + outIdx + "is null");
                    continue;
                }
                //make colors darker
                colors[outIdx] = new Color(new Random().nextInt(256 * 256 * 256));
                //make solid-line inputs dashed
                styles[outIdx] = outInfo.getDrawingStyle() == DrawingStyle.LINE ? DrawingStyle.DASH_LINE : outInfo.getDrawingStyle();
                //thicken the 1-width lines
                widths[outIdx] = 2;
            }
            
            chart.add(indicator, optionalInputs, colors, styles, widths);
            
            //show indicator values in ohlc
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
        }
        
        public double getCurrentValue() throws JFException{
            Object[] outputs = indicators.calculateIndicator(feedDescriptor, new OfferSide[] { feedDescriptor.getOfferSide() },indicatorName,
                    new AppliedPrice[] { AppliedPrice.CLOSE }, optionalInputs, 0);
            double value = (Double) outputs[outputIndex];
            return value;
        }
        
        public void removeFromChart(){
            if(chart != null && indicator != null){
                chart.removeIndicator(indicator);
            }
        }
        
        @Override 
        public String toString(){
            return String.format("%s %s on %s %s feed", indicatorName, Arrays.toString(optionalInputs), feedDescriptor.getOfferSide(), feedDescriptor.getPeriod());
        }
        
    }
    
    private List<IndDataAndFeed> calculatableIndicators = new ArrayList<IndDataAndFeed>(Arrays.asList(new IndDataAndFeed[]{
            new IndDataAndFeed("MACD", new Object[] {12,26,9}, 0, new TimePeriodAggregationFeedDescriptor(instrument, Period.FIVE_MINS, OfferSide.BID, Filter.WEEKENDS)),
            new IndDataAndFeed("RSI", new Object[] {50}, 0, new TimePeriodAggregationFeedDescriptor(instrument, Period.FIVE_MINS, OfferSide.BID, Filter.WEEKENDS)),
            new IndDataAndFeed("RSI", new Object[] {50}, 0, new TimePeriodAggregationFeedDescriptor(instrument, Period.ONE_HOUR, OfferSide.BID, Filter.WEEKENDS)),
            new IndDataAndFeed("CCI", new Object[] {14}, 0, new TimePeriodAggregationFeedDescriptor(instrument, Period.FIFTEEN_MINS, OfferSide.BID, Filter.WEEKENDS)),
            new IndDataAndFeed("CCI", new Object[] {14}, 0, new TimePeriodAggregationFeedDescriptor(instrument, Period.ONE_HOUR, OfferSide.BID, Filter.WEEKENDS))
    }));
    

    @Override
    public void onStart(IContext context) throws JFException {

        if(!context.getSubscribedInstruments().contains(instrument)){
            context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(new Instrument [] {instrument})), true);
        }
        
        this.context = context;
        console = context.getConsole();
        indicators = context.getIndicators();
        
        for(IndDataAndFeed indDataAndFeed : calculatableIndicators){
            indDataAndFeed.openChartAddIndicator();
        }
    }
    
    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (instrument != this.instrument) {
            return;
        }
        for (IndDataAndFeed indDataAndFeed : calculatableIndicators) {
            double value = indDataAndFeed.getCurrentValue();
            print("%s current value=%.5f", indDataAndFeed, value);
        }
    }
    
    @Override
    public void onStop() throws JFException {
        for(IndDataAndFeed indDataAndFeed : calculatableIndicators){
            indDataAndFeed.removeFromChart();
        }
    }

    private void print(Object o) {
        console.getOut().println(o);
    }

    private void print(String format, Object... args) {
        print(String.format(format, args));
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



}
