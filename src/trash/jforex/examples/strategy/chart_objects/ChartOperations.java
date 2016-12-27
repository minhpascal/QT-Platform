package trash.jforex.examples.strategy.chart_objects;

import java.io.File;

import javax.imageio.ImageIO;

import com.dukascopy.api.*;
import com.dukascopy.api.util.*;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.*;

public class ChartOperations implements IStrategy {

    private IChart chart;
    private IContext context;
    private IConsole console;
    
    @Override
    public void onStart(IContext context) throws JFException {  
        this.context = context;
        this.console = context.getConsole();        
        
        IFeedDescriptor feedDescriptor = new TimePeriodAggregationFeedDescriptor(Instrument.EURUSD, Period.TEN_SECS, OfferSide.BID);
        chart = context.openChart(feedDescriptor);
        
        print("Chart's feed: " + chart.getFeedDescriptor()); //will print all feed's parameters
        print("Chart's instrument: " + chart.getFeedDescriptor().getInstrument()); //retrieve a specific feed parameter
        
        //wait until the feed gets loaded
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace(console.getErr());
        }
        
        print("Chart drawable coordinates: min(%s,%.5f); max(%s,%.5f)",
                DateUtils.format(chart.getMinTime()), chart.getMinPrice(),
                DateUtils.format(chart.getMaxTime()), chart.getMaxPrice()
            );
        
        try {
            File file = new File(context.getFilesDir().getPath() + File.separator + "ChartImage.png");
            ImageIO.write(chart.getImage(), "png", file);
            print("chart image saved in: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace(console.getErr());
        }
        
    }
    
    @Override
    public void onStop() throws JFException {
        context.closeChart(chart);
    }
    
    private void print(Object o){
        console.getOut().println(o);
    }
    
    private void print(String message, Object... args){
        print(String.format(message,args));
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}
}
