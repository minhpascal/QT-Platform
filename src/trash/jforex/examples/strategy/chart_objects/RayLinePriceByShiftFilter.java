package trash.jforex.examples.strategy.chart_objects;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.RequiresFullAccess;
import com.dukascopy.api.IChartObject.ATTR_INT;
import com.dukascopy.api.drawings.IRayLineChartObject;
import com.dukascopy.api.drawings.IShortLineChartObject;

/**
 * The strategy on its start creates a ray line
 * and on every bar it caclulates the price on the line for:
 * - last formed bar,
 * - 20 bars to last formed bar.
 * For both price values the strategy draws a small marker.
 * 
 * The strategy applies special logic for handling bar filters.
 *
 */
@RequiresFullAccess
public class RayLinePriceByShiftFilter implements IStrategy {

    @Configurable("Instrument") 
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("Chart filter")
    public Filter chartFilter = Filter.WEEKENDS;
    @Configurable("log values")
    public boolean log = false;
    
    private IChart chart; 
    private IHistory history;
    private IConsole console;

    private IRayLineChartObject rayLine;
    private int counter;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") {
        {
            setTimeZone(TimeZone.getTimeZone("GMT"));
        }
    };
    public static DecimalFormat df = new DecimalFormat("0.00000");

    @Override
    public void onStart(IContext context) throws JFException {
        this.chart = context.getChart(Instrument.EURUSD);
        this.history = context.getHistory();
        this.console = context.getConsole();
        IBar bar = history.getBar(chart.getInstrument(), chart.getSelectedPeriod(), chart.getSelectedOfferSide(), 0);
        
        rayLine = chart.getChartObjectFactory().createRayLine();
        rayLine.setPrice(0, bar.getClose());
        rayLine.setTime(0, bar.getTime());        
        rayLine.setPrice(1, bar.getClose() - 0.0005); //5 pips below
        rayLine.setTime(1, bar.getTime() - chart.getSelectedPeriod().getInterval() * 5); // 5 bars behind
        chart.addToMainChartUnlocked(rayLine);

    }

    private double getPriceByShift(IRayLineChartObject rayLine, int shift) throws JFException {
        double result = 0;

        // 2 coordinates of the ray line
        long timePivot = history.getBarStart(chart.getSelectedPeriod(), rayLine.getTime(0));
        double pricePivot = rayLine.getPrice(0);
        long timeHandle = history.getBarStart(chart.getSelectedPeriod(),rayLine.getTime(1));
        double priceHandle = rayLine.getPrice(1);

        // get triangle coordinates and get tangent
        long barInterval = chart.getSelectedPeriod().getInterval();
        double priceDelta = pricePivot - priceHandle;
        double timeDelta = timePivot - timeHandle - getFilteredTime(timeHandle , timePivot);
        double tangent = priceDelta / timeDelta;
        // for logging
        double timeDeltaInBars = timeDelta / barInterval;

        // get shifted triangle coordinates
        IBar barShifted = getChartBar(chartFilter, shift);
        double timeDeltaShifted = barShifted.getTime() - timeHandle - getFilteredTime(timeHandle, barShifted.getTime());
        double priceDeltaShifted = timeDeltaShifted * tangent;
        // for logging
        double timeDeltaShiftedInBars = timeDeltaShifted / barInterval;

        result = priceHandle + priceDeltaShifted;
        log(String.format("shift=%s, result=%.5f, priceDelta=%.5f, timeDeltaInBars=%.5f, tangent=%.10f, "
                + "timeDeltaShiftedInBars=%.5f, priceDeltaShifted=%.5f, barTime=%s", 
                shift, result, priceDelta,  timeDeltaInBars, tangent, 
                timeDeltaShiftedInBars, priceDeltaShifted, sdf.format(barShifted.getTime())));

        return result;
    }
    
    //filter helper methods
    
    private long getFilteredTime(long from, long to) throws JFException{           
        //adjust time since IHistory.getBars require 1) to >= from 2) to and from can't be in the future
        long lastBarTime = history.getBar(chart.getInstrument(), chart.getSelectedPeriod(), chart.getSelectedOfferSide(), 0).getTime();
        long minTime = Math.min(Math.min(from, to),lastBarTime);
        long maxTime = Math.min(Math.max(from, to),lastBarTime);
        
        long filteredTime = getFilteredBarCount(minTime, maxTime) * chart.getSelectedPeriod().getInterval() * (to > from ? 1 : -1);
        log(String.format("filtered bars from %s to %s - %s ", sdf.format(from), sdf.format(to), filteredTime/chart.getSelectedPeriod().getInterval()));
        return filteredTime;
    }
    
    private int getFilteredBarCount(long from, long to) throws JFException{
        List<IBar> barsWithFilter = getChartBars(chartFilter, from, to);
        List<IBar> barsWithoutFilter = getChartBars(Filter.NO_FILTER, from, to);
        return barsWithoutFilter.size() - barsWithFilter.size();
    }
    
    private int getFilteredBarCount(int shift) throws JFException{
        
        long lastBarTime = getChartBar(0).getTime();
        long shiftedBarTime = getChartBar(shift).getTime();

        int filteredBarCount = getFilteredBarCount(shiftedBarTime , lastBarTime);
        log(String.format("filtered bars from %s to %s - %s ", sdf.format(shiftedBarTime), sdf.format(lastBarTime), filteredBarCount));
        return filteredBarCount;
    }
    
    private boolean isFiltered(long time, int shift) throws JFException{        
        time = time - shift * chart.getSelectedPeriod().getInterval();
        List<IBar> barsWithFilter = getChartBars(chartFilter, time, time);
        boolean  isFiltered = barsWithFilter.size() == 0;
        log(String.format("isFiltered %s - %s ", sdf.format(time), isFiltered));
        return isFiltered;
    }
    
    //chart bar helper methods
    
    private IBar getChartBar(int shift) throws JFException{
        return history.getBar(chart.getInstrument(), chart.getSelectedPeriod(), chart.getSelectedOfferSide(), shift);
    }
    
    private IBar getChartBar(Filter filter, int shift) throws JFException{
        return getChartBars(filter, shift + 1).get(0);
}
    
    private List<IBar> getChartBars(Filter filter, long from, long to) throws JFException{
         return history.getBars(chart.getInstrument(), chart.getSelectedPeriod(), chart.getSelectedOfferSide(), 
                 filter, from, to);
    }
    
    private List<IBar> getChartBars(Filter filter, int count) throws JFException{
        long lastBarTime = getChartBar(0).getTime();
            return history.getBars(chart.getInstrument(), chart.getSelectedPeriod(), chart.getSelectedOfferSide(), 
                filter, count, lastBarTime, 0);
   }    
    



    private void print(Object o) {
        console.getOut().println(o);
    }
    
    private void log(Object o) {
        if (log){
            print(o);
        }
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if (instrument != chart.getInstrument() || period != chart.getSelectedPeriod()) {
            return;
        }
        
        IBar bar = chart.getSelectedOfferSide() == OfferSide.BID ? bidBar : askBar;

        int shift1 = 0;
        int shift2 = 20;
        
        int filtered1 = getFilteredBarCount(shift1);
        int filtered2 = getFilteredBarCount(shift2);
        
        if(!isFiltered(bidBar.getTime(), shift1)){
            double price1 = getPriceByShift(rayLine, shift1);
            drawMarker(price1, bar.getTime() - period.getInterval() * (shift1 - 1 + filtered1)); 
        }
        
        if(!isFiltered(bidBar.getTime(), shift2)){
            double price2 = getPriceByShift(rayLine, shift2);
            drawMarker(price2, bar.getTime() - period.getInterval() * (shift2 - 1 + filtered2));
        }
    }

    private void drawMarker(double price, long time) {

        Color color = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        double height = Math.max(chart.getInstrument().getPipValue(), 
                chart.getSelectedPeriod().getInterval() / Period.TEN_MINS.getInterval() * chart.getInstrument().getPipValue());
        IShortLineChartObject line = chart.getChartObjectFactory().createShortLine();
        line.setPrice(0, price - height);
        line.setTime(0, time);
        line.setPrice(1, price + height);
        line.setTime(1, time);
        line.setColor(color);
        line.setAttrInt(ATTR_INT.WIDTH, 3);
        chart.addToMainChart(line);

        IShortLineChartObject line2 = chart.getChartObjectFactory().createShortLine();
        line2.setPrice(0, price);
        line2.setTime(0, time - chart.getSelectedPeriod().getInterval());
        line2.setPrice(1, price);
        line2.setTime(1, time + chart.getSelectedPeriod().getInterval());
        line2.setColor(color);
        line2.setAttrInt(ATTR_INT.WIDTH, 3);
        line2.setText(String.valueOf(++counter));
        chart.addToMainChart(line2);

    }

    @Override
    public void onMessage(IMessage message) throws JFException {
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onStop() throws JFException {
        //remove all chart objects on stop
        //chart.removeAll();
    }

}
