package trash.jforex.examples.strategy.indicators;

import java.util.Arrays;

import com.dukascopy.api.*;
import static com.dukascopy.api.IIndicators.AppliedPrice.*;
import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.TimePeriodAggregationFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;

//pass file name if the indicator is located in context.getFilesDir, otherwise - full file path
@CustomIndicators("Indicator.jfx")
public class CutomIndicatorPackageInJfx implements IStrategy {

    private IConsole console;
    private IIndicators indicators;
    private IHistory history;
    
    @Override
    public void onStart(IContext context) throws JFException {
        console = context.getConsole();
        indicators = context.getIndicators();
        history = context.getHistory();
        String indPath = getClass().getAnnotation(CustomIndicators.class).value(); //for multiple indicators split the path with File.pathSeparator 
        IIndicator indicator = indicators.getIndicatorByPath(indPath);
        if(indicator == null){
            console.getErr().println("Indicator by path "+indPath+" not registered!");
            context.stop();
        }
        String indName = indicator.getIndicatorInfo().getName();
        //add indicator to the last active chart if there is one
        IChart chart = context.getLastActiveChart();
        if(chart != null){
            chart.add(indicator);
        }
        //take feed from chart if there is one
        IFeedDescriptor feedDescriptor = chart!= null 
                ? chart.getFeedDescriptor()
                : new TimePeriodAggregationFeedDescriptor(Instrument.EURUSD, Period.TEN_SECS, OfferSide.BID, Filter.NO_FILTER);
                
        //use default value - just like the chart does
        int timePeriod = (Integer) indicator.getOptInputParameterInfo(0).getDescription().getOptInputDefaultValue();
        
        //calculate with shift 1
        Object[] resultByShift =  indicators.calculateIndicator(
                feedDescriptor, new  OfferSide[] {OfferSide.BID}, indName, new AppliedPrice[]{CLOSE}, new Object[]{timePeriod}, 1);
        double prevValue = (Double) resultByShift[0];
        
       //calculate last 5
        long lastTime = history.getFeedData(feedDescriptor, 0).getTime();
        Object[] resultByCandleInterval =  indicators.calculateIndicator(
                feedDescriptor, new  OfferSide[] {OfferSide.BID}, indName, new AppliedPrice[]{CLOSE}, new Object[]{timePeriod}, 5, lastTime, 0);
        double[] values = (double[]) resultByCandleInterval[0];
        
        console.getOut().format("previous=%.7f last 5=%s on feed=%s", prevValue, Arrays.toString(values), feedDescriptor).println();
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {}
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}
    public void onMessage(IMessage message) throws JFException {}
    public void onAccount(IAccount account) throws JFException {}
    public void onStop() throws JFException {}
}
