package trash.jforex.examples.strategy.indicators;

import java.io.File;
import java.util.Arrays;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.util.RangeBarFeedDescriptor;
import com.dukascopy.api.indicators.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

@RequiresFullAccess
public class CalculateIndFeedPChannel implements IStrategy {

    private IConsole console;
    private IIndicators indicators;
    private IHistory history;
    
    @Configurable("Indicator name")
    public String indName = "PCHANNEL";
    @Configurable("Instrument")
    public Instrument instrument = Instrument.EURUSD;
    @Configurable("Period")
    public Period period = Period.ONE_MIN;
    @Configurable("AppliedPrice")
    public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
    @Configurable("OfferSide")
    public OfferSide side = OfferSide.BID;
    @Configurable("candle Count")
    public int candleCount = 10;


    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        this.indicators = context.getIndicators();      
        this.history = context.getHistory();
                
        //1 - input related parameters
        AppliedPrice[] inputTypes = new AppliedPrice[] {AppliedPrice.CLOSE};
        OfferSide[] offerSides = new OfferSide[] {OfferSide.BID};
        
        //2 - optional input related parameters - 0=TimePeriod       
        Object[] optParams = new Object[] { 14 };

        //3 - set up feed and calculate
        IFeedDescriptor feedDescriptor = new RangeBarFeedDescriptor(Instrument.EURUSD, PriceRange.valueOf(2),OfferSide.BID);        
        long currBarTime = history.getBar(instrument, period, side, 0).getTime();
        Object[] outputs = indicators.calculateIndicator(feedDescriptor, offerSides, indName, inputTypes, optParams, candleCount, currBarTime, 0);
        
        //4 - process outputs: 0=Up, 1=Down
        double [] ups = (double[])outputs[1];
        double [] lows = (double[])outputs[0];
        print("Up=%s", arrayToString(ups));  
        print("Down=%s", arrayToString(lows));  

       
    }
    
    private void print(String format, Object...args){
        print(String.format(format, args));
    }

    private void print(Object message) {
        console.getOut().println(message);
    }
    
    public static String arrayToString(double[] arr) {
        String str = "";
        for (int r = 0; r < arr.length; r++) {
            str += String.format("[%s] %.5f; ", r, arr[r]);
        }
        return str;
    }
    
    public void onAccount(IAccount account) throws JFException {    }
    public void onMessage(IMessage message) throws JFException {    }
    public void onStop() throws JFException {    }
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }
}